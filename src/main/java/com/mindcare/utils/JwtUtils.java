package com.mindcare.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类。
 *
 * <p>风格尽量贴近 tlias-web-management：
 * - 提供生成 token 的静态方法
 * - 提供解析 token 的静态方法
 * - 当前先使用固定密钥和固定过期时间，便于教学和联调
 *
 * <p>后续如果项目继续完善，可以把密钥和过期时间提取到配置文件中。</p>
 */
public class JwtUtils {

    /**
     * JWT 签名密钥。
     *
     * <p>当前直接写在工具类中，是为了尽量保持和参考项目一致的实现风格。
     * 后续若需要提升可配置性，可以迁移到 application.yml 中。</p>
     */
    private static final String SECRET_KEY = "bWluZGNhcmUtc2VydmljZS1wbGF0Zm9ybQ==";

    /**
     * token 过期时间，单位毫秒。
     * 当前设置为 12 小时。
     */
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000L;

    private JwtUtils() {
    }

    /**
     * 生成 JWT。
     *
     * @param claims token 中存放的业务字段
     * @return JWT 字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .compact();
    }

    /**
     * 解析 JWT。
     *
     * @param token JWT 字符串
     * @return token 中的 claims
     * @throws Exception token 无效或过期时抛出异常
     */
    public static Claims parseToken(String token) throws Exception {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
