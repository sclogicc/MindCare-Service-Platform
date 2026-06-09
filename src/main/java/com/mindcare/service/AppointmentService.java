package com.mindcare.service;

import com.mindcare.pojo.Appointment;
import com.mindcare.pojo.AppointmentDetail;
import com.mindcare.pojo.AppointmentQueryParam;
import com.mindcare.pojo.AppointmentStatusUpdateParam;
import com.mindcare.pojo.PageResult;

/**
 * 预约管理业务接口。
 */
public interface AppointmentService {

    /**
     * 新增预约。
     *
     * @param appointment 预约数据
     */
    void add(Appointment appointment);

    /**
     * 分页查询预约列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    PageResult<AppointmentDetail> page(AppointmentQueryParam queryParam);

    /**
     * 查询预约详情。
     *
     * @param id 预约主键
     * @return 预约详情
     */
    AppointmentDetail getById(Long id);

    /**
     * 取消预约。
     *
     * @param id           预约主键
     * @param cancelReason 取消原因
     */
    void cancel(Long id, String cancelReason);

    /**
     * 修改预约状态。
     *
     * @param param 状态更新参数
     */
    void updateStatus(AppointmentStatusUpdateParam param);
}
