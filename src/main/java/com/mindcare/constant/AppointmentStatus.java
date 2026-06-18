package com.mindcare.constant;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 预约状态枚举。
 *
 * <p>统一管理预约模块的状态码和状态流转规则，
 * 替代原来分散在各 Service 中的 private static final int 常量。</p>
 *
 * <p>状态流转规则（状态机）：
 * <pre>
 * 待确认(1) → 已确认(2)   ← 确认操作
 * 已确认(2) → 已完成(3)   ← 完成咨询 + 写入记录
 * 待确认(1) → 已取消(4)   ← 取消操作
 * 已确认(2) → 已取消(4)   ← 取消操作
 * 已完成(3) → 任何状态    ✗ 终态，不可逆
 * 已取消(4) → 任何状态    ✗ 终态，不可逆
 * </pre></p>
 */
public enum AppointmentStatus {

    PENDING(1, "待确认", false),
    CONFIRMED(2, "已确认", false),
    COMPLETED(3, "已完成", true),
    CANCELED(4, "已取消", true);

    private final int code;
    private final String description;
    private final boolean terminal;   // 是否为终态（不可再变更）

    AppointmentStatus(int code, String description, boolean terminal) {
        this.code = code;
        this.description = description;
        this.terminal = terminal;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }
    public boolean isTerminal() { return terminal; }

    /**
     * 根据状态码查找对应的枚举值。
     */
    public static AppointmentStatus fromCode(int code) {
        for (AppointmentStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("非法的预约状态码: " + code);
    }

    /**
     * 判断从当前状态流转到目标状态是否合法。
     *
     * <p>合法路径：
     * PENDING → CONFIRMED / CANCELED
     * CONFIRMED → COMPLETED / CANCELED</p>
     */
    public boolean canTransitionTo(AppointmentStatus target) {
        if (this == target) return false;
        if (this.terminal) return false;
        if (target == CANCELED) return this == PENDING || this == CONFIRMED;
        if (this == PENDING && target == CONFIRMED) return true;
        if (this == CONFIRMED && target == COMPLETED) return true;
        return false;
    }

    /**
     * 所有终态的状态码集合。
     */
    public static Set<Integer> terminalCodes() {
        return Arrays.stream(values())
                .filter(AppointmentStatus::isTerminal)
                .map(AppointmentStatus::getCode)
                .collect(Collectors.toSet());
    }
}
