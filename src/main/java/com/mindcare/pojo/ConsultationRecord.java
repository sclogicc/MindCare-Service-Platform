package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 咨询记录实体类，对应 consultation_record 表。
 *
 * <p>该实体用于承载咨询师在预约完成后填写的业务记录，
 * 包括咨询摘要、后续建议以及可选的附件信息。</p>
 */
@Data
public class ConsultationRecord {

    private Long id;
    private Long appointmentId;
    private Long counselorId;
    private String summary;
    private String suggestion;
    private Long attachmentFileId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
