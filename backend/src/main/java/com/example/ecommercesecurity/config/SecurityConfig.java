package com.example.ecommercesecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)   // 前后端分离项目必须禁用 CSRF
                .authorizeHttpRequests(auth -> auth
                        // ==================== 公开允许的接口 ====================
                        .requestMatchers("/api/auth/**").permitAll()      // 登录、注册、验证码
                        .requestMatchers("/api/products/**").permitAll()  // 商品浏览（公开）
                        .requestMatchers("/api/cart/**").permitAll()      // 购物车操作
                        .requestMatchers("/api/orders/**").permitAll()    // 订单下单、支付、查询（本次新增）

                        // 其他所有接口暂时需要登录（后面可以逐步加上权限控制）
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();   // 使用 BCrypt 加密密码
    }
}