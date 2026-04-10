package com.example.ecommercesecurity.controller;

import com.example.ecommercesecurity.common.ApiResponse;
import com.example.ecommercesecurity.entity.Product;
import com.example.ecommercesecurity.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(ApiResponse.success("获取商品列表成功", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok(ApiResponse.success("获取商品详情成功", product)))
                .orElse(ResponseEntity.badRequest().body(ApiResponse.error("商品不存在")));
    }
}