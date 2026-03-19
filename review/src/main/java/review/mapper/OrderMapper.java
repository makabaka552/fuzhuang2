package review.mapper;

import model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderMapper extends JpaRepository<Order,Long> {


    List<Order> findByUserIdAndStatus(Long id, String status);
}
