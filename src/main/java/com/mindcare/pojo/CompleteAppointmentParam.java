package com.mindcare.pojo;

import lombok.Data;

/**
 * 完成预约业务参数。
 *
 * <p>该对象专门服务于“完成预约并写入咨询记录”的事务场景，
 * 不和普通咨询记录新增参数混在一起，便于明确这个接口的业务语义。</p>
 */
@Data
public class CompleteAppointmentParam {

    /**
     * 预约主键。
     */
    private Long appointmentId;

    /**
     * 咨询师主键。
     */
    private Long counselorId;

    /**
     * 咨询摘要。
     */
    private String summary;

    /**
     * 后续建议。
     */
    private String suggestion;

    /**
     * 可选的附件主键。
     */
    private Long attachmentFileId;
}
