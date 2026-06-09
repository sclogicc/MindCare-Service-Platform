package com.mindcare.service;

import com.mindcare.pojo.CompleteAppointmentParam;
import com.mindcare.pojo.ConsultationRecordPageItem;
import com.mindcare.pojo.ConsultationRecordQueryParam;
import com.mindcare.pojo.PageResult;

/**
 * 咨询记录业务接口。
 */
public interface ConsultationRecordService {

    /**
     * 分页查询咨询记录列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    PageResult<ConsultationRecordPageItem> page(ConsultationRecordQueryParam queryParam);

    /**
     * 根据预约主键查询咨询记录详情。
     *
     * @param appointmentId 预约主键
     * @return 详情对象
     */
    ConsultationRecordPageItem getDetailByAppointmentId(Long appointmentId);

    /**
     * 完成预约并写入咨询记录。
     *
     * <p>这是一个事务场景：
     * 1. 新增 consultation_record
     * 2. 更新 appointment 状态为已完成
     *
     * 上述两步必须同时成功或同时失败。</p>
     *
     * @param param 完成预约参数
     */
    void completeAppointment(CompleteAppointmentParam param);
}
