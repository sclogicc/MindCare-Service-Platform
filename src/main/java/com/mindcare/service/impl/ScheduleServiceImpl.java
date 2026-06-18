package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.ScheduleMapper;
import com.mindcare.pojo.PageResult;
import com.mindcare.pojo.Schedule;
import com.mindcare.pojo.SchedulePageItem;
import com.mindcare.pojo.ScheduleQueryParam;
import com.mindcare.pojo.ScheduleStatusUpdateParam;
import com.mindcare.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 时间段业务实现类。
 *
 * <p>该类集中处理时间段管理的核心业务规则：
 * 1. 分页查询
 * 2. 时间重叠冲突校验
 * 3. 删除前预约引用校验
 * 4. 基础状态切换</p>
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    /**
     * 时间段状态：停用。
     */
    private static final int STATUS_DISABLED = 0;

    /**
     * 时间段状态：启用。
     */
    private static final int STATUS_ENABLED = 1;

    private final ScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public void add(Schedule schedule) {
        validateBaseParam(schedule, false);
        validateConflict(schedule);

        schedule.setCreateTime(LocalDateTime.now());
        schedule.setUpdateTime(LocalDateTime.now());
        if (schedule.getStatus() == null) {
            schedule.setStatus(STATUS_ENABLED);
        }

        scheduleMapper.insert(schedule);
    }

    @Override
    public PageResult<SchedulePageItem> page(ScheduleQueryParam queryParam) {
        Integer page = queryParam.getPage() == null || queryParam.getPage() < 1 ? 1 : queryParam.getPage();
        Integer pageSize = queryParam.getPageSize() == null || queryParam.getPageSize() < 1 ? 10 : queryParam.getPageSize();

        Page<SchedulePageItem> pageInfo = PageHelper.startPage(page, pageSize);
        List<SchedulePageItem> rows = scheduleMapper.selectPageList(queryParam);
        return new PageResult<>(pageInfo.getTotal(), rows);
    }

    @Override
    public SchedulePageItem getById(Long id) {
        SchedulePageItem detail = scheduleMapper.selectDetailById(id);
        if (detail == null) {
            throw new BusinessException("时间段不存在");
        }
        return detail;
    }

    @Override
    public void update(Schedule schedule) {
        validateBaseParam(schedule, true);

        Schedule existed = scheduleMapper.selectById(schedule.getId());
        if (existed == null) {
            throw new BusinessException("时间段不存在");
        }

        validateConflict(schedule);
        scheduleMapper.updateById(schedule);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new BusinessException("时间段主键不能为空");
        }

        Schedule existed = scheduleMapper.selectById(id);
        if (existed == null) {
            throw new BusinessException("时间段不存在");
        }

        Integer appointmentCount = scheduleMapper.countAppointmentByScheduleId(id);
        if (appointmentCount != null && appointmentCount > 0) {
            throw new BusinessException("该时间段已有预约记录，不允许删除");
        }

        scheduleMapper.deleteById(id);
    }

    @Override
    public void updateStatus(ScheduleStatusUpdateParam param) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        if (!Objects.equals(param.getStatus(), STATUS_ENABLED) && !Objects.equals(param.getStatus(), STATUS_DISABLED)) {
            throw new BusinessException("时间段状态非法");
        }

        Schedule existed = scheduleMapper.selectById(param.getId());
        if (existed == null) {
            throw new BusinessException("时间段不存在");
        }

        scheduleMapper.updateStatusById(param.getId(), param.getStatus());
    }

    /**
     * 校验基础业务规则（非空校验已由 Controller 层 @Valid 完成）。
     *
     * @param schedule 时间段参数
     * @param requireId 是否要求主键必填（id 字段在新增时为空，因此由该参数控制）
     */
    private void validateBaseParam(Schedule schedule, boolean requireId) {
        if (requireId && schedule.getId() == null) {
            throw new BusinessException("时间段主键不能为空");
        }
        if (schedule.getScheduleDate().isBefore(LocalDate.now())) {
            throw new BusinessException("不能创建过去日期的时间段");
        }
        if (!schedule.getStartTime().isBefore(schedule.getEndTime())) {
            throw new BusinessException("开始时间必须早于结束时间");
        }
        if (schedule.getStatus() != null
                && !Objects.equals(schedule.getStatus(), STATUS_ENABLED)
                && !Objects.equals(schedule.getStatus(), STATUS_DISABLED)) {
            throw new BusinessException("时间段状态非法");
        }
    }

    /**
     * 校验时间段冲突。
     *
     * <p>这里是时间段管理模块的关键业务规则：
     * 同一咨询师在同一天内，不能存在时间重叠的两个时间段。
     * 这样可以保证后续用户基于 schedule_id 发起预约时，底层时间资源本身就是干净的。</p>
     *
     * @param schedule 时间段参数
     */
    private void validateConflict(Schedule schedule) {
        Integer conflictCount = scheduleMapper.countConflictSchedule(schedule);
        if (conflictCount != null && conflictCount > 0) {
            throw new BusinessException("该咨询师在当前日期已存在时间重叠的时间段");
        }
    }
}
