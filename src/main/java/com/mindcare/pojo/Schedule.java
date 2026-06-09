package com.mindcare.pojo;

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
    private Long counselorId;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
