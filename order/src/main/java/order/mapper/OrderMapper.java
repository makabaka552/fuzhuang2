package order.mapper;

import model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;



public interface OrderMapper extends JpaRepository<Order,Long> {
    List<Order> findByuserId(Long id);


    Order findByOrderNo(String orderNo);


    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    Double sumTotalAmount();
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();
}
