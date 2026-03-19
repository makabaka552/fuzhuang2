package payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import payment.entity.PaymentRecord;

import java.util.List;

/**
 * 支付记录数据访问接口
 */
@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    /**
     * 根据订单号查询支付记录
     *
     * @param orderNo 订单号
     * @return 支付记录列表
     */
    List<PaymentRecord> findByOrderNo(String orderNo);

    /**
     * 根据支付方式查询支付记录
     *
     * @param paymentMethod 支付方式
     * @return 支付记录列表
     */
    List<PaymentRecord> findByPaymentMethod(String paymentMethod);

    /**
     * 根据用户ID查询支付记录
     *
     * @param userId 用户ID
     * @return 支付记录列表
     */
    List<PaymentRecord> findByUserId(Long userId);

    /**
     * 根据订单号和支付状态查询支付记录
     *
     * @param orderNo 订单号
     * @param status  支付状态
     * @return 支付记录列表
     */
    List<PaymentRecord> findByOrderNoAndStatus(String orderNo, String status);

    /**
     * 根据支付状态查询支付记录
     *
     * @param status 支付状态
     * @return 支付记录列表
     */
    List<PaymentRecord> findByStatus(String status);
}