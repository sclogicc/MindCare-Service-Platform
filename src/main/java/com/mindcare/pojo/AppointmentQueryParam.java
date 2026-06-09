package com.mindcare.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 预约分页与条件查询参数。
 *
 * <p>保留单独的查询参数对象，可以避免把分页参数和实体类字段混在一起，
 * 同时也更适合后续在 MyBatis XML 中编写动态 SQL。</p>
 */
@Data
public class AppointmentQueryParam {

    private Integer page;
    private Integer pageSize;
    private String appointmentNo;
    private Long userId;
    private Long counselorId;
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
