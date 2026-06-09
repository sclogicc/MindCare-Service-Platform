package com.mindcare.service;

import com.mindcare.pojo.Feedback;
import com.mindcare.pojo.FeedbackPageItem;
import com.mindcare.pojo.FeedbackQueryParam;
import com.mindcare.pojo.PageResult;

/**
 * 反馈评价业务接口。
 */
public interface FeedbackService {

    /**
     * 新增反馈评价。
     *
     * @param feedback 反馈参数
     */
    void add(Feedback feedback);

    /**
     * 分页查询反馈列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    PageResult<FeedbackPageItem> page(FeedbackQueryParam queryParam);

    /**
     * 查询反馈详情。
     *
     * @param id 反馈主键
     * @return 详情
     */
    FeedbackPageItem getById(Long id);

    /**
     * 根据预约主键查询反馈详情。
     *
     * @param appointmentId 预约主键
     * @return 详情
     */
    FeedbackPageItem getByAppointmentId(Long appointmentId);

    /**
     * 删除反馈。
     *
     * @param id 反馈主键
     */
    void deleteById(Long id);
}
