package com.mindcare.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 时间段分页与条件查询参数。
 *
 * <p>独立的查询参数对象更适合承载分页字段和筛选条件，
 * 也便于 MyBatis XML 使用动态 SQL 进行 where 条件拼接。</p>
 */
@Data
public class ScheduleQueryParam {

    private Integer page;
    private Integer pageSize;
    private Long counselorId;
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;
}
