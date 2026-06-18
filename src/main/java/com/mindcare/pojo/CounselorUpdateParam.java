package com.mindcare.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 咨询师信息修改参数。
 *
 * <p>这里先只开放 counselor 业务表字段，
 * 不在当前阶段引入账号信息联动修改，避免把收尾工作重新做成重型模块。</p>
 */
@Data
public class CounselorUpdateParam {

    @NotNull(message = "咨询师主键不能为空")
    private Long id;

    @NotBlank(message = "擅长方向不能为空")
    private String specialty;

    @NotBlank(message = "职称不能为空")
    private String title;

    @NotNull(message = "从业年限不能为空")
    @Min(value = 0, message = "从业年限不能小于0")
    private Integer yearsOfExperience;

    private String introduction;
    private Long avatarFileId;
    private Integer status;
}
