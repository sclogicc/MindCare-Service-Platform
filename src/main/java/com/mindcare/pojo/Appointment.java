package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约实体类，对应 appointment 表。
 */
@Data
public class Appointment {

    private Long id;
    private String appointmentNo;
    private Long userId;
    private Long counselorId;
    private Long scheduleId;
    private Integer status;
    private String contactPhone;
    private String remark;
    private String cancelReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
