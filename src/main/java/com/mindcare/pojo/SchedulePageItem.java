package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 时间段分页列表展示对象。
 *
 * <p>该对象主要服务于后台时间段管理列表页，
 * 因此在基础时间段字段上补充了咨询师姓名和关联预约数量等展示信息。</p>
 */
@Data
public class SchedulePageItem {

    private Long id;
    private Long counselorId;
    private String counselorName;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer status;
    private String remark;
    private Integer appointmentCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
