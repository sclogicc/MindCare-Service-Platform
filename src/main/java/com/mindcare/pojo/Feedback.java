package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈评价实体类，对应 feedback 表。
 */
@Data
public class Feedback {

    private Long id;
    private Long appointmentId;
    private Long userId;
    private Long counselorId;
    private Integer score;
    private String content;
    private Integer isAnonymous;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
