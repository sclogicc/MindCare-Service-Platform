package com.mindcare.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 报表查询参数。
 *
 * <p>为了统一报表接口风格，把常见的日期范围和年份查询参数收敛到一个对象中，
 * 这样 Controller 层接参会更整齐，Mapper XML 动态 SQL 也更容易复用。</p>
 */
@Data
public class ReportQueryParam {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer year;
}
