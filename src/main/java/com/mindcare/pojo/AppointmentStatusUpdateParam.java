package com.mindcare.pojo;

import lombok.Data;

/**
 * 预约状态修改参数。
 *
 * <p>该对象同时服务于：
 * - 通用状态修改
 * - 取消预约时的取消原因提交
 *
 * <p>虽然两个接口不会同时使用所有字段，但这样可以减少重复参数类，
 * 对当前教学型项目来说更直接。</p>
 */
@Data
public class AppointmentStatusUpdateParam {

    private Long id;
    private Integer status;
    private String cancelReason;
}
