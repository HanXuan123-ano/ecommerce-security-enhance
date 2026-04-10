package com.example.ecommercesecurity.controller;

import com.example.ecommercesecurity.common.ApiResponse;
import com.example.ecommercesecurity.entity.Cart;
import com.example.ecommercesecurity.entity.CartItem;
import com.example.ecommercesecurity.entity.Product;
import com.example.ecommercesecurity.entity.User;
import com.example.ecommercesecurity.repository.CartItemRepository;
import com.example.ecommercesecurity.repository.ProductRepository;
import com.example.ecommercesecurity.repository.UserRepository;
import com.example.ecommercesecurity.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Cart>> getCartByUsername(@RequestParam String username) {
        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取或创建购物车
        Cart cart = cartService.getOrCreateCartForUser(user.getId());

        // 加载购物车项（现在 Cart 已经有 items 集合）
        return ResponseEntity.ok(ApiResponse.success("获取购物车成功", cart));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addToCart(
            @RequestParam String username,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        if (quantity <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("数量必须大于0"));
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        Cart cart = cartService.getOrCreateCartForUser(user.getId());

        // 检查是否已存在该商品
        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());
        CartItem existingItem = items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            // 新增条目
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cartItemRepository.save(newItem);
        }

        return ResponseEntity.ok(ApiResponse.success("商品已成功添加到购物车"));
    }
}