package com.mindcare.pojo;

import lombok.Data;

/**
 * 咨询记录分页查询参数。
 *
 * <p>当前咨询记录页面采用“预约驱动”的展示方式，
 * 因此查询对象既包含 appointment 维度的筛选字段，
 * 也包含“是否已填写咨询记录”这一业务状态字段。</p>
 */
@Data
public class ConsultationRecordQueryParam {

    private Integer page;
    private Integer pageSize;
    private String appointmentNo;
    private Long userId;
    private Long counselorId;
    private Integer status;

    /**
     * 是否已存在咨询记录。
     * 1 表示已有记录，0 表示未填写记录。
     */
    private Integer hasRecord;
}
