package com.mindcare.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类。
 *
 * <p>基于 BCrypt 算法对密码进行哈希处理。
 * BCrypt 内置 salt，每次加密结果不同，安全性高于 MD5/SHA 等简单哈希。</p>
 *
 * <p>使用方式：
 * <pre>{@code
 * // 加密（注册/新增用户时）
 * String hashed = PasswordUtil.encode("123456");
 *
 * // 验证（登录时）
 * boolean matched = PasswordUtil.matches("123456", hashed);
 * }</pre></p>
 */
public final class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {
        // 工具类不实例化
    }

    /**
     * 对明文密码进行 BCrypt 加密。
     *
     * @param rawPassword 明文密码
     * @return BCrypt 哈希后的密文
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 校验明文密码与 BCrypt 密文是否匹配。
     *
     * @param rawPassword     明文密码
     * @param encodedPassword BCrypt 密文
     * @return true 匹配 / false 不匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}
