package com.mindcare.pojo;

import lombok.Data;

/**
 * 预约状态聚合结果对象。
 *
 * <p>该对象专门用于承接 SQL 中通过 case when 统计出来的一行结果。</p>
 */
@Data
public class AppointmentStatusCount {

    private Long pendingCount;
    private Long confirmedCount;
    private Long completedCount;
    private Long canceledCount;
}
