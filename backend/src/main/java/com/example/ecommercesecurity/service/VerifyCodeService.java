package com.example.ecommercesecurity.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class VerifyCodeService {

    // 临时存储验证码（生产环境建议用 Redis，这里先用 Map）
    private final ConcurrentHashMap<String, VerifyCode> codeMap = new ConcurrentHashMap<>();

    public String generateCode(String username) {
        // 生成 6 位随机码
        String code = String.format("%06d", new Random().nextInt(999999));

        // 存入 Map（5分钟有效）
        VerifyCode vc = new VerifyCode(code, System.currentTimeMillis() + 5 * 60 * 1000);
        codeMap.put(username, vc);

        System.out.println("【模拟短信】用户名 " + username + " 的验证码是: " + code);
        return code;
    }

    public boolean verifyCode(String username, String inputCode) {
        VerifyCode vc = codeMap.get(username);
        if (vc == null) return false;

        // 检查是否过期
        if (System.currentTimeMillis() > vc.expireTime) {
            codeMap.remove(username);
            return false;
        }

        // 验证成功后立即删除（一次性使用）
        boolean success = vc.code.equals(inputCode);
        if (success) codeMap.remove(username);

        return success;
    }

    // 内部类
    private record VerifyCode(String code, long expireTime) {}
}