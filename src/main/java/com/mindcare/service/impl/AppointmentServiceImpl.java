package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mindcare.constant.AppointmentStatus;
import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.AppointmentMapper;
import com.mindcare.pojo.Appointment;
import com.mindcare.pojo.AppointmentDetail;
import com.mindcare.pojo.AppointmentQueryParam;
import com.mindcare.pojo.AppointmentStatusUpdateParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 预约管理业务实现类。
 *
 * <p>当前模块的核心逻辑集中在这里：
 * 1. 新增预约时做时间段冲突校验
 * 2. 分页查询时调用 PageHelper
 * 3. 修改状态时校验预约是否存在、状态流转是否合法
 *
 * <p>这样做可以保证：
 * - Controller 足够轻
 * - Mapper 只负责数据访问
 * - 业务规则都集中在 Service 层，便于后续讲解和维护</p>
 */
@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentMapper appointmentMapper) {
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public void add(Appointment appointment) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        // 1. 校验目标时间段是否已经被未取消预约占用。
        Integer usedCount = appointmentMapper.countActiveAppointmentByScheduleId(appointment.getScheduleId());
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException("该时间段已被预约，请重新选择");
        }

        // 2. 生成预约单号并补齐默认字段。
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setStatus(AppointmentStatus.PENDING.getCode());
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        // 3. 保存预约记录。
        appointmentMapper.insert(appointment);
        log.info("预约创建成功: appointmentNo={}, userId={}, counselorId={}, scheduleId={}",
                appointment.getAppointmentNo(), appointment.getUserId(),
                appointment.getCounselorId(), appointment.getScheduleId());
    }

    @Override
    public PageResult<AppointmentDetail> page(AppointmentQueryParam queryParam) {
        Integer page = queryParam.getPage() == null || queryParam.getPage() < 1 ? 1 : queryParam.getPage();
        Integer pageSize = queryParam.getPageSize() == null || queryParam.getPageSize() < 1 ? 10 : queryParam.getPageSize();

        Page<AppointmentDetail> pageInfo = PageHelper.startPage(page, pageSize);
        List<AppointmentDetail> rows = appointmentMapper.selectPageList(queryParam);
        return new PageResult<>(pageInfo.getTotal(), rows);
    }

    @Override
    public AppointmentDetail getById(Long id) {
        AppointmentDetail detail = appointmentMapper.selectDetailById(id);
        if (detail == null) {
            throw new BusinessException("预约记录不存在");
        }
        return detail;
    }

    @Override
    public void cancel(Long id, String cancelReason) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }

        AppointmentStatus currentStatus;
        try {
            currentStatus = AppointmentStatus.fromCode(appointment.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("预约状态异常");
        }

        // 终态不允许取消
        if (currentStatus.isTerminal()) {
            if (currentStatus == AppointmentStatus.CANCELED) {
                throw new BusinessException("该预约已经取消，无需重复操作");
            }
            if (currentStatus == AppointmentStatus.COMPLETED) {
                throw new BusinessException("已完成的预约不允许取消");
            }
        }

        String reason = StringUtils.hasText(cancelReason) ? cancelReason : "用户取消";
        appointmentMapper.cancelById(id, reason);
        log.info("预约已取消: id={}, 原状态={}, 原因={}", id, currentStatus.getDescription(), reason);
    }

    @Override
    public void updateStatus(AppointmentStatusUpdateParam param) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        Appointment appointment = appointmentMapper.selectById(param.getId());
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }

        AppointmentStatus oldStatus;
        AppointmentStatus newStatus;
        try {
            oldStatus = AppointmentStatus.fromCode(appointment.getStatus());
            newStatus = AppointmentStatus.fromCode(param.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("预约状态值非法");
        }

        // 取消预约必须走专用取消接口（需记录取消原因）
        if (newStatus == AppointmentStatus.CANCELED) {
            throw new BusinessException("取消预约请调用专用取消接口");
        }

        // 使用枚举内置的状态机校验
        if (!oldStatus.canTransitionTo(newStatus)) {
            throw new BusinessException("当前预约状态不允许这样修改");
        }

        appointmentMapper.updateStatusById(param.getId(), newStatus.getCode());
        log.info("预约状态变更: id={}, {} → {}", param.getId(),
                oldStatus.getDescription(), newStatus.getDescription());
    }

    /**
     * 生成预约单号。
     */
    private String generateAppointmentNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = 100 + new Random().nextInt(900);
        return "MC" + timePart + randomPart;
    }
}
