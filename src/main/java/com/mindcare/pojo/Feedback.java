package com.mindcare.pojo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈评价实体类，对应 feedback 表。
 */
@Data
public class Feedback {

    private Long id;

    @NotNull(message = "预约ID不能为空")
    private Long appointmentId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "咨询师ID不能为空")
    private Long counselorId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分范围必须是1到5分")
    @Max(value = 5, message = "评分范围必须是1到5分")
    private Integer score;

    @NotBlank(message = "评价内容不能为空")
    private String content;

    private Integer isAnonymous;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
