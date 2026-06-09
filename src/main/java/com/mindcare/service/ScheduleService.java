package com.mindcare.service;

import com.mindcare.pojo.PageResult;
import com.mindcare.pojo.Schedule;
import com.mindcare.pojo.SchedulePageItem;
import com.mindcare.pojo.ScheduleQueryParam;
import com.mindcare.pojo.ScheduleStatusUpdateParam;

/**
 * 时间段业务接口。
 */
public interface ScheduleService {

    /**
     * 新增时间段。
     *
     * @param schedule 时间段参数
     */
    void add(Schedule schedule);

    /**
     * 分页查询时间段列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    PageResult<SchedulePageItem> page(ScheduleQueryParam queryParam);

    /**
     * 查询时间段详情。
     *
     * @param id 时间段主键
     * @return 详情
     */
    SchedulePageItem getById(Long id);

    /**
     * 修改时间段。
     *
     * @param schedule 时间段参数
     */
    void update(Schedule schedule);

    /**
     * 删除时间段。
     *
     * @param id 时间段主键
     */
    void deleteById(Long id);

    /**
     * 修改时间段状态。
     *
     * @param param 状态参数
     */
    void updateStatus(ScheduleStatusUpdateParam param);
}
