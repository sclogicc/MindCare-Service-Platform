package com.mindcare.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约实体类，对应 appointment 表。
 */
@Data
public class Appointment {

    private Long id;
    private String appointmentNo;

    @NotNull(message = "预约用户不能为空")
    private Long userId;

    @NotNull(message = "咨询师不能为空")
    private Long counselorId;

    @NotNull(message = "预约时间段不能为空")
    private Long scheduleId;

    private Integer status;
    private String contactPhone;
    private String remark;
    private String cancelReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
