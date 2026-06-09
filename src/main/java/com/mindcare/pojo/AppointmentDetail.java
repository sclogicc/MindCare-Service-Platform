package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约详情展示对象。
 *
 * <p>该对象不是直接映射单张表，而是承载预约模块联表查询后的展示结果，
 * 这样列表页和详情页都能直接使用统一的数据结构。</p>
 */
@Data
public class AppointmentDetail {

    private Long id;
    private String appointmentNo;
    private Long userId;
    private String userName;
    private Long counselorId;
    private String counselorName;
    private Long scheduleId;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer status;
    private String contactPhone;
    private String remark;
    private String cancelReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
