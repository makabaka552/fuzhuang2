package payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.entity.PaymentRecord;
import payment.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付超时处理服务
 * 处理超时未支付的订单
 */
@Service
public class PaymentTimeoutService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentTimeoutService.class);

    // 支付超时时间，单位：分钟
    private static final int PAYMENT_TIMEOUT_MINUTES = 30;

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    private OrderPaymentMappingService orderPaymentMappingService;

    /**
     * 定时任务：检查支付超时订单
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkPaymentTimeout() {
        logger.info("开始检查支付超时订单...");

        // 查询所有待支付状态的支付记录
        List<PaymentRecord> pendingRecords = paymentRecordService.findByStatus(PaymentStatus.PENDING.getCode());
        LocalDateTime now = LocalDateTime.now();

        // 筛选出超时的记录
        List<PaymentRecord> timeoutRecords = pendingRecords.stream()
                .filter(record -> ChronoUnit.MINUTES.between(record.getCreatedTime(), now) >= PAYMENT_TIMEOUT_MINUTES)
                .collect(Collectors.toList());

        logger.info("找到{}个支付超时订单", timeoutRecords.size());

        // 处理超时记录
        for (PaymentRecord record : timeoutRecords) {
            try {
                // 更新支付记录状态为已取消
                record.setStatus(PaymentStatus.CANCELLED.getCode());
                paymentRecordService.save(record);

                // 更新订单状态
                orderPaymentMappingService.updateOrderStatus(record);

                logger.info("支付超时自动取消: 订单号={}", record.getOrderNo());
            } catch (Exception e) {
                logger.error("处理支付超时订单异常: " + record.getOrderNo(), e);
            }
        }

        logger.info("支付超时订单检查完成");
    }

    /**
     * 手动检查指定订单是否超时
     *
     * @param orderNo 订单号
     * @return 是否超时
     */
    public boolean isPaymentTimeout(String orderNo) {
        List<PaymentRecord> records = paymentRecordService.findByOrderNo(orderNo);
        if (records.isEmpty()) {
            return false;
        }

        PaymentRecord record = records.get(0);
        if (!PaymentStatus.PENDING.getCode().equals(record.getStatus())) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.MINUTES.between(record.getCreatedTime(), now) >= PAYMENT_TIMEOUT_MINUTES;
    }
}