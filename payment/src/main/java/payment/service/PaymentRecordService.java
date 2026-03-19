package payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payment.entity.PaymentRecord;
import payment.repository.PaymentRecordRepository;

import java.util.List;
import java.util.Optional;

/**
 * 支付记录服务类
 * 提供支付记录的增删改查功能
 */
@Service
public class PaymentRecordService {

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    /**
     * 保存支付记录
     *
     * @param paymentRecord 支付记录对象
     * @return 保存后的支付记录
     */
    public PaymentRecord save(PaymentRecord paymentRecord) {
        return paymentRecordRepository.save(paymentRecord);
    }

    /**
     * 根据订单号查询支付记录
     *
     * @param orderNo 订单号
     * @return 支付记录列表
     */
    public List<PaymentRecord> findByOrderNo(String orderNo) {
        return paymentRecordRepository.findByOrderNo(orderNo);
    }

    /**
     * 根据ID查询支付记录
     *
     * @param id 支付记录ID
     * @return 支付记录（可能为空）
     */
    public Optional<PaymentRecord> findById(Long id) {
        return paymentRecordRepository.findById(id);
    }

    /**
     * 更新支付记录状态
     *
     * @param id     支付记录ID
     * @param status 新状态
     * @return 更新后的支付记录
     */
    public PaymentRecord updateStatus(Long id, String status) {
        Optional<PaymentRecord> optionalRecord = paymentRecordRepository.findById(id);
        if (optionalRecord.isPresent()) {
            PaymentRecord record = optionalRecord.get();
            record.setStatus(status);
            return paymentRecordRepository.save(record);
        }
        throw new RuntimeException("支付记录不存在: " + id);
    }

    /**
     * 根据用户ID查询支付记录
     *
     * @param userId 用户ID
     * @return 支付记录列表
     */
    public List<PaymentRecord> findByUserId(Long userId) {
        return paymentRecordRepository.findByUserId(userId);
    }

    /**
     * 根据支付状态查询支付记录
     *
     * @param status 支付状态
     * @return 支付记录列表
     */
    public List<PaymentRecord> findByStatus(String status) {
        return paymentRecordRepository.findByStatus(status);
    }
}