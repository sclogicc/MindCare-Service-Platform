package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 反馈评价列表/详情展示对象。
 */
@Data
public class FeedbackPageItem {

    private Long appointmentId;
    private String appointmentNo;
    private Long userId;
    private String userName;
    private Long counselorId;
    private String counselorName;
    private Long scheduleId;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer status;

    private Long feedbackId;
    private Integer score;
    private String content;
    private Integer isAnonymous;
    private LocalDateTime feedbackCreateTime;
    private LocalDateTime feedbackUpdateTime;
}
