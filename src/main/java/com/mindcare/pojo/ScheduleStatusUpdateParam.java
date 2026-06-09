package com.mindcare.pojo;

import lombok.Data;

/**
 * 时间段状态修改参数。
 */
@Data
public class ScheduleStatusUpdateParam {

    private Long id;
    private Integer status;
}
