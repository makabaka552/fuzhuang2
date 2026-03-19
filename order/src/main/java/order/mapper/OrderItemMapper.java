package order.mapper;

import model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemMapper extends JpaRepository<OrderItem,Long> {
}
