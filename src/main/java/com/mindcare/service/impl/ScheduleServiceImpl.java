package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mindcare.constant.EnableStatus;
import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.ScheduleMapper;
import com.mindcare.pojo.PageResult;
import com.mindcare.pojo.Schedule;
import com.mindcare.pojo.SchedulePageItem;
import com.mindcare.pojo.ScheduleQueryParam;
import com.mindcare.pojo.ScheduleStatusUpdateParam;
import com.mindcare.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 时间段业务实现类。
 *
 * <p>该类集中处理时间段管理的核心业务规则：
 * 1. 分页查询
 * 2. 时间重叠冲突校验
 * 3. 删除前预约引用校验
 * 4. 基础状态切换</p>
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

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
            schedule.setStatus(EnableStatus.ENABLED.getCode());
        }

        scheduleMapper.insert(schedule);
        log.info("时间段新增成功: counselorId={}, date={}, {}~{}",
                schedule.getCounselorId(), schedule.getScheduleDate(),
                schedule.getStartTime(), schedule.getEndTime());
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
        log.info("时间段已修改: id={}", schedule.getId());
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
        log.info("时间段已删除: id={}", id);
    }

    @Override
    public void updateStatus(ScheduleStatusUpdateParam param) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        if (!EnableStatus.isValid(param.getStatus())) {
            throw new BusinessException("时间段状态非法");
        }

        Schedule existed = scheduleMapper.selectById(param.getId());
        if (existed == null) {
            throw new BusinessException("时间段不存在");
        }

        EnableStatus newStatus = EnableStatus.fromCode(param.getStatus());
        scheduleMapper.updateStatusById(param.getId(), newStatus.getCode());
        log.info("时间段状态已变更: id={}, status={}", param.getId(), newStatus.getDescription());
    }

    /**
     * 校验基础业务规则（非空校验已由 Controller 层 @Valid 完成）。
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
        if (schedule.getStatus() != null && !EnableStatus.isValid(schedule.getStatus())) {
            throw new BusinessException("时间段状态非法");
        }
    }

    /**
     * 校验时间段冲突。
     */
    private void validateConflict(Schedule schedule) {
        Integer conflictCount = scheduleMapper.countConflictSchedule(schedule);
        if (conflictCount != null && conflictCount > 0) {
            throw new BusinessException("该咨询师在当前日期已存在时间重叠的时间段");
        }
    }
}
