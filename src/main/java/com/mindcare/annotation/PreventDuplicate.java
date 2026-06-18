package com.mindcare.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防重复提交注解。
 *
 * <p>标注在 Controller 方法上，声明该方法需要防重复提交保护。
 * 前端在调用标注了此注解的 POST/PUT 接口前，需先从
 * {@code GET /anti-duplicate/token} 获取一次性提交令牌，
 * 并在请求头中携带 {@code X-Submit-Token}。</p>
 *
 * <p>使用示例：
 * <pre>{@code
 * @PreventDuplicate
 * @PostMapping
 * public Result add(@Valid @RequestBody Appointment appointment) { ... }
 * }</pre></p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventDuplicate {

    /**
     * 令牌过期时间（秒），默认 300 秒（5分钟）。
     */
    int ttlSeconds() default 300;
}
