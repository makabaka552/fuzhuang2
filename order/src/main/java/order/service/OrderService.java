package order.service;

import model.Order;
import model.OrderStatus;
import model.PageBeam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface OrderService {
    void AddOrder(String username, Order order);



    List<Order> GetAllOrder(String username);

    void cancel(String username, Long id);

    void DeleteOrder(String username, Long id);


    Order GetOrder(String username, Long id);

    Order updateOrderStatus(String orderNo, OrderStatus orderStatus);

    Order handlePaymentSuccess(String orderNo, String paymentMethod, LocalDateTime payTime);

    Order getOrderById(String orderNo);

    Order updateOrder(Order order);

    PageBeam GetAllOrder(Integer page, Integer pageSize);

    Map<String, Object> getOrderStats();

    void cancelOrder(String orderid);
}
