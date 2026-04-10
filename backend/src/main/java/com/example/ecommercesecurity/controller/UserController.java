package com.example.ecommercesecurity.controller;

import com.example.ecommercesecurity.common.ApiResponse;
import com.example.ecommercesecurity.dto.LoginRequest;
import com.example.ecommercesecurity.dto.RegisterRequest;
import com.example.ecommercesecurity.service.UserService;
import com.example.ecommercesecurity.service.VerifyCodeService;   // ← 新增这行 import
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerifyCodeService verifyCodeService;   // ← 正确的注入位置

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        String result = userService.register(request);

        if ("注册成功".equals(result)) {
            return ResponseEntity.status(201).body(ApiResponse.success("注册成功", null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(result));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        String result = userService.login(request);

        if ("登录成功".equals(result)) {
            return ResponseEntity.ok(ApiResponse.success("登录成功", null));
        } else {
            return ResponseEntity.status(401).body(ApiResponse.error(401, result));
        }
    }

    // 新增验证码发送接口（登录成功后调用）
    @PostMapping("/verify-code")
    public String sendVerifyCode(@RequestParam String username) {
        String code = verifyCodeService.generateCode(username);
        return "验证码已发送（模拟短信）: " + code;
    }

    // 新增验证码验证接口（二次验证）
    @PostMapping("/verify")
    public String verifyCode(@RequestParam String username, @RequestParam String code) {
        boolean success = verifyCodeService.verifyCode(username, code);
        return success ? "2FA验证成功" : "验证码错误或已过期";
    }
}