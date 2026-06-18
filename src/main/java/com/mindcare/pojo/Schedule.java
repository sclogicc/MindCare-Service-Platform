package com.mindcare.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 可预约时间段实体类，对应 counselor_schedule 表。
 *
 * <p>该类既可以作为时间段管理模块的基础实体，
 * 也可以作为咨询师详情中的子集合对象使用。</p>
 */
@Data
public class Schedule {

    private Long id;

    @NotNull(message = "咨询师不能为空")
    private Long counselorId;

    @NotNull(message = "预约日期不能为空")
    private LocalDate scheduleDate;

    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
