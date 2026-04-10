package com.example.ecommercesecurity.service;

import com.example.ecommercesecurity.dto.LoginRequest;
import com.example.ecommercesecurity.dto.RegisterRequest;
import com.example.ecommercesecurity.entity.User;
import com.example.ecommercesecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            return "用户名已存在";
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // 加密新密码
                .email(request.getEmail())
                .build();

        userRepository.save(user);
        return "注册成功";
    }

    // 【关键修复】登录逻辑：兼容明文密码（旧账号）和加密密码（新账号）
    public String login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            return "用户名或密码错误";
        }

        User user = userOptional.get();
        String storedPassword = user.getPassword();
        String inputPassword = request.getPassword();

        // 检查数据库中的密码是否是 BCrypt 加密格式（以 $2a$ 开头）
        if (isPasswordEncoded(storedPassword)) {
            if (passwordEncoder.matches(inputPassword, storedPassword)) {
                return "登录成功";
            } else {
                return "用户名或密码错误";
            }
        } else {
            // 旧账号：密码是明文，直接比较
            if (inputPassword.equals(storedPassword)) {
                return "登录成功";
            } else {
                return "用户名或密码错误";
            }
        }
    }

    // 辅助方法：判断密码是否是 BCrypt 加密格式
    private boolean isPasswordEncoded(String password) {
        return password != null && password.startsWith("$2a$");
    }
}
