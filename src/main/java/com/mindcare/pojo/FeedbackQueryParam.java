package com.mindcare.pojo;

import lombok.Data;

/**
 * 反馈评价分页查询参数。
 *
 * <p>当前反馈页面按“已完成预约 + 反馈信息”进行展示，
 * 因此查询参数同时覆盖预约侧与反馈侧的筛选需求。</p>
 */
@Data
public class FeedbackQueryParam {

    private Integer page;
    private Integer pageSize;
    private String appointmentNo;
    private Long userId;
    private Long counselorId;
    private Integer score;
    private Integer isAnonymous;

    /**
     * 1 表示已有反馈，0 表示待评价。
     */
    private Integer hasFeedback;
}
