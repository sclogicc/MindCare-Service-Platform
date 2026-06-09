package com.mindcare.pojo;

import lombok.Data;

/**
 * 月度预约数量统计对象。
 *
 * <p>单独拆分类而不是直接复用 ReportOption，
 * 是因为月度图表通常除了 value 之外，还需要稳定的 month、monthLabel 两个字段，
 * 这样前端折线图或柱状图对接会更直观。</p>
 */
@Data
public class MonthlyAppointmentOption {

    private Integer month;
    private String monthLabel;
    private Long value;
}
