package payment.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payment.config.AlipayConfig;
import payment.entity.PaymentRecord;
import payment.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付宝通知服务类
 * 处理支付宝的回调通知
 */
@Service
public class AlipayNotifyService {

    private static final Logger logger = LoggerFactory.getLogger(AlipayNotifyService.class);

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    private PaymentNotificationService paymentNotificationService;

    @Autowired
    private OrderPaymentMappingService orderPaymentMappingService;

    /**
     * 验证支付宝异步通知的签名
     *
     * @param params 支付宝回调参数
     * @return 验签结果
     */
    public boolean verifySignature(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSignType());
        } catch (AlipayApiException e) {
            logger.error("支付宝验签异常", e);
            return false;
        }
    }

    /**
     * 处理支付宝异步通知
     * 
     * @param params 通知参数
     * @return 处理结果
     */
    public boolean handleAlipayNotify(Map<String, String> params) {
        // 从支付宝通知参数中获取必要参数
        String orderNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        logger.info("收到支付宝异步通知: 订单号={}, 交易号={}, 交易状态={}", orderNo, tradeNo, tradeStatus);
        logger.info("完整的通知参数: {}", params);

        // 验证参数完整性
        if (orderNo == null || orderNo.trim().isEmpty()) {
            logger.error("支付宝异步通知缺少订单号(out_trade_no)参数");
            return false;
        }

        // 交易成功
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            // 查询订单对应的支付记录
            List<PaymentRecord> records = paymentRecordService.findByOrderNo(orderNo);

            if (records.isEmpty()) {
                logger.error("未找到订单号对应的支付记录: {}", orderNo);

                // 尝试创建新的支付记录
                try {
                    PaymentRecord newRecord = new PaymentRecord();
                    newRecord.setOrderNo(orderNo);
                    newRecord.setTradeNo(tradeNo);
                    newRecord.setStatus(PaymentStatus.SUCCESS.getCode());
                    newRecord.setPaymentMethod("ALIPAY");
                    newRecord.setPaymentTime(LocalDateTime.now());
                    newRecord.setExtraInfo(mapToJson(params));

                    // 如果有金额参数，设置金额
                    if (params.containsKey("total_amount")) {
                        try {
                            double amount = Double.parseDouble(params.get("total_amount"));
                            newRecord.setAmount(new java.math.BigDecimal(amount));
                        } catch (NumberFormatException e) {
                            logger.warn("解析金额失败: {}", params.get("total_amount"));
                        }
                    }

                    // 保存新记录
                    PaymentRecord savedRecord = paymentRecordService.save(newRecord);
                    logger.info("创建了新的支付记录: {}", savedRecord.getId());

                    // 通知其他系统（会处理订单状态更新、优惠券使用、积分添加）
                    paymentNotificationService.notifyPaymentResultByEvent(savedRecord);
                    boolean notifyResult = paymentNotificationService.notifyOrderSystem(savedRecord);
                    logger.info("新支付记录通知订单系统结果: {}", notifyResult);

                    return true;
                } catch (Exception e) {
                    logger.error("创建新支付记录失败", e);
                    return false;
                }
            }

            // 更新现有支付记录
            PaymentRecord record = records.get(0);
            record.setTradeNo(tradeNo);
            record.setStatus(PaymentStatus.SUCCESS.getCode());
            record.setPaymentTime(LocalDateTime.now());
            record.setExtraInfo(mapToJson(params));

            PaymentRecord updatedRecord = paymentRecordService.save(record);
            logger.info("更新支付记录成功: {}", updatedRecord.getId());

            // 通知其他系统支付成功（会处理订单状态更新、优惠券使用、积分添加）
            paymentNotificationService.notifyPaymentResultByEvent(updatedRecord);
            boolean notifyResult = paymentNotificationService.notifyOrderSystem(updatedRecord);
            logger.info("订单系统通知结果: {}", notifyResult);

            return true;
        } else if ("TRADE_CLOSED".equals(tradeStatus)) {
            // 交易关闭
            List<PaymentRecord> records = paymentRecordService.findByOrderNo(orderNo);
            if (!records.isEmpty()) {
                PaymentRecord record = records.get(0);
                record.setStatus(PaymentStatus.CANCELLED.getCode());
                PaymentRecord updatedRecord = paymentRecordService.save(record);
                logger.info("交易关闭，更新支付记录状态为已取消: {}", updatedRecord.getId());

                // 更新订单状态
                boolean updateResult = orderPaymentMappingService.updateOrderStatus(updatedRecord);
                logger.info("交易关闭，订单状态更新结果: {}", updateResult);
            } else {
                logger.warn("交易关闭，但未找到支付记录: {}", orderNo);
            }
            return true;
        } else {
            logger.info("其他交易状态，不处理: {}", tradeStatus);
        }

        return false;
    }

    /**
     * 将Map转换为JSON字符串
     */
    private String mapToJson(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":\"")
                    .append(entry.getValue().replace("\"", "\\\"")).append("\",");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }
}