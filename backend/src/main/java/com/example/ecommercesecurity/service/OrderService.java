package com.example.ecommercesecurity.service;

import com.example.ecommercesecurity.entity.*;
import com.example.ecommercesecurity.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrderFromCart(String username) {
        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取用户的购物车
        Cart cart = cartService.getOrCreateCartForUser(user.getId());

        // 获取购物车项
        List<CartItem> cartItems = cartItemRepository.findByCart_Id(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空，无法创建订单");
        }

        // 计算总金额
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 生成订单号
        String orderNo = "ORD" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 创建订单
        Order order = Order.builder()
                .user(user)
                .orderNo(orderNo)
                .totalAmount(totalAmount)
                .status("PENDING")
                .build();
        Order savedOrder = orderRepository.save(order);

        // 创建订单项并减少商品库存
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // 检查库存
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("商品 " + product.getName() + " 库存不足");
            }

            // 减少库存
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // 创建订单项
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // 清空购物车
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }

    @Transactional
    public Order simulatePay(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不是PENDING，无法支付");
        }

        order.setStatus("PAID");
        return orderRepository.save(order);
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUser_Id(userId);
    }
}