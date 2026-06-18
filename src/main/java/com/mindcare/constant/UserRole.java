package com.mindcare.constant;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户角色枚举。
 *
 * <p>统一管理角色定义，与 {@link com.mindcare.annotation.RequireRole}
 * 中的常量值保持一致。</p>
 */
public enum UserRole {

    ADMIN(1, "管理员"),
    COUNSELOR(2, "咨询师"),
    USER(3, "普通用户");

    private final int code;
    private final String description;

    UserRole(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    /**
     * 根据角色码查找对应的枚举值。
     */
    public static UserRole fromCode(int code) {
        for (UserRole r : values()) {
            if (r.code == code) return r;
        }
        throw new IllegalArgumentException("非法的角色码: " + code);
    }

    /**
     * 所有合法的角色码集合。
     */
    public static Set<Integer> validCodes() {
        return Arrays.stream(values())
                .map(UserRole::getCode)
                .collect(Collectors.toSet());
    }
}
