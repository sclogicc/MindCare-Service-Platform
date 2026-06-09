package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 咨询记录列表/详情展示对象。
 *
 * <p>由于页面需要同时展示预约信息、咨询记录信息和附件信息，
 * 直接复用单表实体会导致字段不完整，因此单独定义一个展示对象。</p>
 */
@Data
public class ConsultationRecordPageItem {

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

    private Long recordId;
    private String summary;
    private String suggestion;
    private Long attachmentFileId;
    private String attachmentOriginalName;
    private String attachmentFileUrl;
    private LocalDateTime recordCreateTime;
    private LocalDateTime recordUpdateTime;
}
