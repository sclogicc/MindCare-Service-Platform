package com.mindcare.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 时间段状态修改参数。
 */
@Data
public class ScheduleStatusUpdateParam {

    @NotNull(message = "时间段ID不能为空")
    private Long id;

    @NotNull(message = "时间段状态不能为空")
    private Integer status;
}
