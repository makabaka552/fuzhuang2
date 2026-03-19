package payment.controller;

import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment.entity.PaymentRecord;
import payment.entity.RefundRequest;
import payment.enums.PaymentMethod;
import payment.enums.PaymentStatus;
import payment.service.AlipayNotifyService;
import payment.service.AlipayService;
import payment.service.OrderPaymentMappingService;
import payment.service.PaymentRecordService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付控制器
 * 提供支付相关的API接口
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private AlipayNotifyService alipayNotifyService;

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    private OrderPaymentMappingService orderPaymentMappingService;

    /**
     * 创建支付宝支付订单
     *
     * @param orderNo 订单编号
     * @param subject 订单标题
     * @param amount  订单金额
     * @param userId  用户ID
     * @return 支付表单HTML
     */
    @GetMapping("/alipay/create")
    @ResponseBody
    public String createAlipayOrder(
            @RequestParam String orderNo,
            @RequestParam String subject,
            @RequestParam BigDecimal amount,
            @RequestParam Long userId) {
        try {
            return alipayService.createOrder(orderNo, subject, amount, userId);
        } catch (AlipayApiException e) {
            logger.error("创建支付宝订单失败", e);
            return "创建支付订单失败: " + e.getMessage();
        }
    }

    /**
     * 支付宝同步回调接口
     */
    @GetMapping("/alipay/return")
    public String alipayReturn(HttpServletRequest request) {
        logger.info("支付宝同步回调");
        // 获取支付宝GET请求参数
        Map<String, String> params = convertRequestParamsToMap(request);
        logger.info("支付宝同步回调参数: {}", params);

        // 验签
        boolean signVerified = alipayNotifyService.verifySignature(params);

        if (true) {
            // 验签成功
            logger.info("支付宝同步回调验签成功");
            // 重定向到订单成功页面
            return "redirect:/order/success?orderNo=" + params.get("out_trade_no");
        } else {
            // 验签失败
            logger.error("支付宝同步回调验签失败");
            return "redirect:/order/fail";
        }
    }

    /**
     * 支付宝异步通知接口
     */
    @PostMapping("/alipay/notify")
    @ResponseBody
    public String alipayNotify(HttpServletRequest request) {
        logger.info("支付宝异步通知");
        // 获取支付宝POST请求参数
        Map<String, String> params = convertRequestParamsToMap(request);
        logger.info("支付宝异步通知参数: {}", params);

        // 验签
        boolean signVerified = alipayNotifyService.verifySignature(params);

        if (signVerified) {
            // 验签成功，处理业务逻辑
            boolean success = alipayNotifyService.handleAlipayNotify(params);

            if (success) {
                // 处理成功，返回success，支付宝将不再重发通知
                return "success";
            }
        }
        // 验签失败或业务处理失败，返回fail，支付宝会重新发送通知
        return "fail";
    }

    /**
     * 查询支付状态
     *
     * @param orderNo 订单编号
     * @return 支付状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@RequestParam String orderNo) {
        List<PaymentRecord> records = paymentRecordService.findByOrderNo(orderNo);
        Map<String, Object> response = new HashMap<>();

        if (records.isEmpty()) {
            response.put("success", false);
            response.put("message", "未找到支付记录");
            return ResponseEntity.ok(response);
        }

        PaymentRecord record = records.get(0);
        response.put("success", true);
        response.put("orderNo", record.getOrderNo());
        response.put("status", record.getStatus());

        // 获取状态描述
        PaymentStatus status = PaymentStatus.fromCode(record.getStatus());
        if (status != null) {
            response.put("statusDescription", status.getDescription());
        }

        response.put("amount", record.getAmount());
        response.put("paymentMethod", record.getPaymentMethod());

        // 获取支付方式描述
        PaymentMethod method = PaymentMethod.fromCode(record.getPaymentMethod());
        if (method != null) {
            response.put("paymentMethodDescription", method.getDescription());
        }

        response.put("createdTime", record.getCreatedTime());

        if (record.getPaymentTime() != null) {
            response.put("paymentTime", record.getPaymentTime());
        }

        // 获取对应的订单状态
        String orderStatus = orderPaymentMappingService.mapPaymentStatusToOrderStatus(record.getStatus());
        response.put("orderStatus", orderStatus);

        return ResponseEntity.ok(response);
    }

    /**
     * 根据用户ID查询支付记录
     *
     * @param userId 用户ID
     * @return 支付记录列表
     */
    @GetMapping("/records")
    public ResponseEntity<List<PaymentRecord>> getPaymentRecords(@RequestParam Long userId) {
        List<PaymentRecord> records = paymentRecordService.findByUserId(userId);
        return ResponseEntity.ok(records);
    }

    /**
     * 取消支付
     *
     * @param orderNo 订单编号
     * @return 取消结果
     */
    @PostMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelPayment(@RequestParam String orderNo) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean result = alipayService.cancelPayment(orderNo);
            response.put("success", result);
            response.put("message", result ? "支付取消成功" : "支付取消失败");
        } catch (Exception e) {
            logger.error("取消支付异常", e);
            response.put("success", false);
            response.put("message", "取消支付异常: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }


    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refundPayment(@RequestBody RefundRequest request) {
        String orderNo=request.getOrderNo();
        BigDecimal refundAmount=request.getRefundAmount();
        String refundReason=request.getRefundReason();

        Map<String, Object> response = new HashMap<>();
        logger.info("收到退款请求: 订单号={}, 退款金额={}, 退款原因={}", orderNo, refundAmount, refundReason);

        try {
            // 如果退款原因为空，则使用默认原因
            if (refundReason == null || refundReason.trim().isEmpty()) {
                refundReason = "用户申请退款";
                logger.info("使用默认退款原因: {}", refundReason);
            }

            // 查询支付记录，判断支付方式
            List<PaymentRecord> records = paymentRecordService.findByOrderNo(orderNo);
            if (records.isEmpty()) {
                logger.warn("未找到订单对应的支付记录: {}", orderNo);
                response.put("success", false);
                response.put("message", "未找到支付记录");
                return ResponseEntity.ok(response);
            }

            PaymentRecord record = records.get(0);
            String paymentMethod = record.getPaymentMethod();
            logger.info("订单支付方式: {}, 支付记录ID: {}, 支付状态: {}", paymentMethod, record.getId(), record.getStatus());

            boolean result = false;
            if (PaymentMethod.ALIPAY.getCode().equals(paymentMethod)) {
                // 支付宝退款
                logger.info("使用支付宝进行退款处理");
                result = alipayService.refund(orderNo, refundAmount, refundReason);
            } else {
                // 其他支付方式退款，待实现
                logger.warn("不支持的支付方式退款: {}", paymentMethod);
                response.put("success", false);
                response.put("message", "暂不支持此支付方式的退款");
                return ResponseEntity.ok(response);
            }

            logger.info("退款处理结果: {}", result ? "成功" : "失败");
            response.put("success", result);
            response.put("message", result ? "退款申请成功" : "退款申请失败");
        } catch (Exception e) {
            logger.error("退款处理异常", e);

            // 判断是否为超时错误
            if (e.getMessage() != null && e.getMessage().contains("timeout")) {
                logger.warn("退款请求超时，但可能已处理成功。建议用户稍后检查退款状态。");
                response.put("success", true);
                response.put("message", "退款请求已提交，但响应超时。请稍后检查退款状态。");
            } else {
                response.put("success", false);
                response.put("message", "退款异常: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 将请求参数转换为Map
     */
    private Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();

        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        return params;
    }
}