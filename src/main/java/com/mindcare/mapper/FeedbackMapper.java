package com.mindcare.mapper;

import com.mindcare.pojo.Feedback;
import com.mindcare.pojo.FeedbackPageItem;
import com.mindcare.pojo.FeedbackQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 反馈评价 Mapper。
 */
@Mapper
public interface FeedbackMapper {

    /**
     * 分页查询反馈列表。
     *
     * @param queryParam 查询参数
     * @return 列表结果
     */
    List<FeedbackPageItem> selectPageList(FeedbackQueryParam queryParam);

    /**
     * 根据反馈主键查询详情。
     *
     * @param id 反馈主键
     * @return 详情
     */
    FeedbackPageItem selectDetailById(@Param("id") Long id);

    /**
     * 根据预约主键查询反馈详情。
     *
     * @param appointmentId 预约主键
     * @return 详情
     */
    FeedbackPageItem selectDetailByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * 根据预约主键统计反馈数量。
     *
     * @param appointmentId 预约主键
     * @return 数量
     */
    Integer countByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * 新增反馈。
     *
     * @param feedback 反馈实体
     */
    void insert(Feedback feedback);

    /**
     * 删除反馈。
     *
     * @param id 反馈主键
     */
    void deleteById(@Param("id") Long id);
}
