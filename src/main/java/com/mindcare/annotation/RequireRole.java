package com.mindcare.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解。
 *
 * <p>标注在 Controller 方法上，声明允许访问的角色列表。
 * 角色常量：1=管理员, 2=咨询师, 3=普通用户</p>
 *
 * <p>使用示例：
 * <pre>{@code
 * @RequireRole(1)
 * public Result deleteById(@PathVariable Long id) { ... }
 *
 * @RequireRole({1, 2})
 * public Result page(@RequestBody QueryParam param) { ... }
 * }</pre></p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * 允许访问的角色列表。
     *
     * @return 角色 ID 数组，默认空数组表示不限制
     */
    int[] value() default {};

    /**
     * 角色常量。
     */
    int ADMIN = 1;
    int COUNSELOR = 2;
    int USER = 3;
}
