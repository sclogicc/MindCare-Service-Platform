package com.mindcare.mapper;

import com.mindcare.pojo.Appointment;
import com.mindcare.pojo.AppointmentDetail;
import com.mindcare.pojo.AppointmentQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预约 Mapper。
 *
 * <p>预约模块的数据访问主要集中在这里，包括：
 * 1. 新增预约
 * 2. 分页和条件查询
 * 3. 联表查询详情
 * 4. 时间段占用校验
 * 5. 状态更新和取消预约</p>
 */
@Mapper
public interface AppointmentMapper {

    /**
     * 新增预约。
     *
     * @param appointment 预约数据
     */
    void insert(Appointment appointment);

    /**
     * 分页查询预约列表。
     *
     * @param queryParam 查询参数
     * @return 预约列表
     */
    List<AppointmentDetail> selectPageList(AppointmentQueryParam queryParam);

    /**
     * 根据主键查询预约详情。
     *
     * @param id 预约主键
     * @return 预约详情
     */
    AppointmentDetail selectDetailById(@Param("id") Long id);

    /**
     * 根据主键查询预约基础信息。
     *
     * @param id 预约主键
     * @return 预约基础信息
     */
    Appointment selectById(@Param("id") Long id);

    /**
     * 根据时间段主键统计未取消预约数量。
     *
     * <p>该方法是“时间段冲突校验”的核心。
     * 只要同一个 schedule_id 下还存在待确认、已确认、已完成的预约，
     * 就说明这个时间段已经被占用，不能再次预约。</p>
     *
     * @param scheduleId 时间段主键
     * @return 占用数量
     */
    Integer countActiveAppointmentByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * 取消预约。
     *
     * @param id           预约主键
     * @param cancelReason 取消原因
     */
    void cancelById(@Param("id") Long id, @Param("cancelReason") String cancelReason);

    /**
     * 修改预约状态。
     *
     * @param id     预约主键
     * @param status 目标状态
     */
    void updateStatusById(@Param("id") Long id, @Param("status") Integer status);
}
