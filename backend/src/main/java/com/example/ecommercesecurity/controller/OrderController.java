package com.example.ecommercesecurity.controller;

import com.example.ecommercesecurity.common.ApiResponse;
import com.example.ecommercesecurity.entity.Order;
import com.example.ecommercesecurity.entity.User;
import com.example.ecommercesecurity.repository.UserRepository;
import com.example.ecommercesecurity.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestParam String username) {
        try {
            Order order = orderService.createOrderFromCart(username);
            return ResponseEntity.ok(ApiResponse.success("订单创建成功", order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<Order>> simulatePay(@RequestParam Long orderId) {
        try {
            Order order = orderService.simulatePay(orderId);
            return ResponseEntity.ok(ApiResponse.success("支付成功", order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByUsername(@RequestParam String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            List<Order> orders = orderService.findByUserId(user.getId());
            return ResponseEntity.ok(ApiResponse.success("获取订单列表成功", orders));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}