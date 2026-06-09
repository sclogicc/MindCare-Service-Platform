package com.mindcare.pojo;

import lombok.Data;

/**
 * 通用图表统计对象。
 *
 * <p>适用于饼图、柱状图等“名称 + 数值”结构的报表数据。</p>
 */
@Data
public class ReportOption {

    private String name;
    private Long value;
}
