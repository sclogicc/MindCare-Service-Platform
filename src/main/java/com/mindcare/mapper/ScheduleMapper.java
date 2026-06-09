package com.mindcare.mapper;

import com.mindcare.pojo.Schedule;
import com.mindcare.pojo.SchedulePageItem;
import com.mindcare.pojo.ScheduleQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 时间段 Mapper。
 *
 * <p>主要负责 counselor_schedule 表的基础 CRUD 和联表分页查询，
 * 复杂筛选和时间冲突相关 SQL 统一放在 XML 中维护。</p>
 */
@Mapper
public interface ScheduleMapper {

    /**
     * 新增时间段。
     *
     * @param schedule 时间段数据
     */
    void insert(Schedule schedule);

    /**
     * 分页查询时间段列表。
     *
     * @param queryParam 查询参数
     * @return 列表结果
     */
    List<SchedulePageItem> selectPageList(ScheduleQueryParam queryParam);

    /**
     * 根据主键查询时间段基础信息。
     *
     * @param id 主键
     * @return 时间段
     */
    Schedule selectById(@Param("id") Long id);

    /**
     * 根据主键查询时间段详情。
     *
     * @param id 主键
     * @return 时间段详情
     */
    SchedulePageItem selectDetailById(@Param("id") Long id);

    /**
     * 修改时间段。
     *
     * @param schedule 时间段数据
     */
    void updateById(Schedule schedule);

    /**
     * 删除时间段。
     *
     * @param id 主键
     */
    void deleteById(@Param("id") Long id);

    /**
     * 修改时间段状态。
     *
     * @param id 主键
     * @param status 目标状态
     */
    void updateStatusById(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 统计同一咨询师同一天是否存在时间重叠的时间段。
     *
     * <p>用于新增和修改时间段时的冲突校验。
     * overlap 规则为：
     * existing.start_time < current.end_time 且 existing.end_time > current.start_time</p>
     *
     * @param schedule 时间段参数
     * @return 冲突数量
     */
    Integer countConflictSchedule(Schedule schedule);

    /**
     * 统计某个时间段下是否已有预约记录。
     *
     * <p>只要存在关联预约，就不建议物理删除，以免破坏历史数据链路。</p>
     *
     * @param scheduleId 时间段主键
     * @return 预约数量
     */
    Integer countAppointmentByScheduleId(@Param("scheduleId") Long scheduleId);
}
