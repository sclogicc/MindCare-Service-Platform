package com.mindcare.constant;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通用启用/停用状态枚举。
 *
 * <p>适用于：咨询师状态、时间段状态、用户账号状态等
 * 需要在“启用/停用”之间切换的业务实体。</p>
 */
public enum EnableStatus {

    DISABLED(0, "停用"),
    ENABLED(1, "启用");

    private final int code;
    private final String description;

    EnableStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    /**
     * 判断状态码是否为合法的启用/停用值。
     */
    public static boolean isValid(int code) {
        return code == ENABLED.code || code == DISABLED.code;
    }

    /**
     * 根据状态码查找对应的枚举值。
     */
    public static EnableStatus fromCode(int code) {
        for (EnableStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("非法的启用/停用状态码: " + code);
    }

    /**
     * 所有合法的状态码集合。
     */
    public static Set<Integer> validCodes() {
        return Arrays.stream(values())
                .map(EnableStatus::getCode)
                .collect(Collectors.toSet());
    }
}
