package com.example.ecommercesecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");   // 允许前端端口
        config.addAllowedOriginPattern("*");                // 开发阶段可放开所有（后面可改）
        config.addAllowedMethod("*");                       // 允许所有请求方法
        config.addAllowedHeader("*");                       // 允许所有请求头
        config.setAllowCredentials(true);                   // 允许携带 cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);    // 所有接口都生效

        return new CorsFilter(source);
    }
}