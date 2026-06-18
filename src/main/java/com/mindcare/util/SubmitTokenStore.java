package com.mindcare.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交令牌存储（基于内存）。
 *
 * <p>每个令牌一次使用后即失效（原子删除）。
 * 后台线程每 60 秒清理一次过期令牌。
 * 生产环境中可替换为 Redis 实现。</p>
 */
@Slf4j
@Component
public class SubmitTokenStore {

    /** token → 过期时间戳(ms) */
    private final ConcurrentHashMap<String, Long> store = new ConcurrentHashMap<>();

    public SubmitTokenStore() {
        // 定期清理过期令牌
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "submit-token-cleaner");
            t.setDaemon(true);
            return t;
        }).scheduleWithFixedDelay(this::cleanExpired, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 生成一个新的提交令牌。
     *
     * @param ttlSeconds 令牌有效期（秒）
     * @return 新生成的唯一令牌
     */
    public String generate(int ttlSeconds) {
        String token = UUID.randomUUID().toString().replace("-", "");
        long expireAt = System.currentTimeMillis() + ttlSeconds * 1000L;
        store.put(token, expireAt);
        log.debug("生成提交令牌: ttl={}s", ttlSeconds);
        return token;
    }

    /**
     * 验证并消费令牌（原子操作，一次有效）。
     *
     * @param token 待验证的令牌
     * @return true 令牌有效且未被使用过 / false 令牌无效或已被使用
     */
    public boolean validateAndConsume(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        Long expireAt = store.remove(token);   // 原子删除，确保一次有效
        if (expireAt == null) {
            return false;                       // 令牌不存在或已被消费
        }
        if (System.currentTimeMillis() > expireAt) {
            return false;                       // 令牌已过期
        }
        return true;
    }

    /**
     * 清理过期的令牌。
     */
    private void cleanExpired() {
        long now = System.currentTimeMillis();
        store.entrySet().removeIf(entry -> {
            if (now > entry.getValue()) {
                log.debug("清理过期令牌: {}", entry.getKey().substring(0, 8));
                return true;
            }
            return false;
        });
    }
}
