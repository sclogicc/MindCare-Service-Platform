package com.mindcare.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 咨询师状态修改参数。
 */
@Data
public class CounselorStatusUpdateParam {

    @NotNull(message = "咨询师ID不能为空")
    private Long id;

    @NotNull(message = "咨询师状态不能为空")
    private Integer status;
}
