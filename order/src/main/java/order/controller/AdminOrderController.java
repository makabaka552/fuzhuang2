package order.controller;

import lombok.extern.slf4j.Slf4j;
import model.Order;
import model.OrderStatus;
import model.PageBeam;
import model.Result;
import order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/orders/admin")
public class AdminOrderController {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public Result getOrderList(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize){
        PageBeam pageBeam = orderService.GetAllOrder(page,pageSize);
        return  Result.success(pageBeam);
    }
    @GetMapping("/stats")
    public Result getOrderStatus(){
        Map<String,Object> result = orderService.getOrderStats();
        return Result.success(result);
    }
    @PostMapping("/{orderno}/cancel")
    public Result cancelOrder(@PathVariable String orderno){
        orderService.cancelOrder(orderno);
        return Result.success();
    }
    @PostMapping("/ship")
    public Result shipOrder(@RequestParam String orderNo) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("手动发货请求: 订单号={}", orderNo);

            // 查询订单
            Order order = orderService.getOrderById(orderNo);

            // 检查订单状态
            if (!OrderStatus.PAID.getCode().equals(order.getStatus())) {
                response.put("success", false);
                response.put("message", "只有已支付的订单才能发货，当前状态: " + order.getStatus());
                return Result.success(response);
            }

            // 更新订单状态为已发货
            order.setStatus(OrderStatus.SHIPPED.getCode());
            order.setShipTime(LocalDateTime.now());
            Order updatedOrder = orderService.updateOrder(order);

            response.put("success", true);
            response.put("message", "订单发货成功");
            response.put("orderNo", updatedOrder.getOrderNo());
            response.put("newStatus", updatedOrder.getStatus());

            log.info("订单发货成功: 订单号={}", orderNo);
        } catch (Exception e) {
            log.error("订单发货失败: 订单号={}, 错误={}", orderNo, e.getMessage());
            response.put("success", false);
            response.put("message", "订单发货失败: " + e.getMessage());
        }

        return Result.success(response);
    }
}





















