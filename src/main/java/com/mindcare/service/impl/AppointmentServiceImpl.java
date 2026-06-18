package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.AppointmentMapper;
import com.mindcare.pojo.Appointment;
import com.mindcare.pojo.AppointmentDetail;
import com.mindcare.pojo.AppointmentQueryParam;
import com.mindcare.pojo.AppointmentStatusUpdateParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.service.AppointmentService;
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
@Service
public class AppointmentServiceImpl implements AppointmentService {

    /**
     * 预约状态：待确认。
     */
    private static final int STATUS_PENDING = 1;

    /**
     * 预约状态：已确认。
     */
    private static final int STATUS_CONFIRMED = 2;

    /**
     * 预约状态：已完成。
     */
    private static final int STATUS_COMPLETED = 3;

    /**
     * 预约状态：已取消。
     */
    private static final int STATUS_CANCELED = 4;

    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentMapper appointmentMapper) {
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public void add(Appointment appointment) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        // 1. 校验目标时间段是否已经被未取消预约占用。
        // 这里不做复杂的时间重叠算法，而是直接按 schedule_id 判断，
        // 与当前项目前面约定的数据模型保持一致。
        Integer usedCount = appointmentMapper.countActiveAppointmentByScheduleId(appointment.getScheduleId());
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException("该时间段已被预约，请重新选择");
        }

        // 2. 生成预约单号并补齐默认字段。
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setStatus(STATUS_PENDING);
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        // 3. 保存预约记录。
        appointmentMapper.insert(appointment);
    }

    @Override
    public PageResult<AppointmentDetail> page(AppointmentQueryParam queryParam) {
        // 对分页参数做兜底处理，避免前端未传时直接报错。
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
        // 1. 查询原预约，确保记录存在。
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }

        // 2. 已取消的预约不允许重复取消。
        if (Objects.equals(appointment.getStatus(), STATUS_CANCELED)) {
            throw new BusinessException("该预约已经取消，无需重复操作");
        }

        // 3. 已完成的预约不允许再取消，避免破坏业务闭环。
        if (Objects.equals(appointment.getStatus(), STATUS_COMPLETED)) {
            throw new BusinessException("已完成的预约不允许取消");
        }

        appointmentMapper.cancelById(id, StringUtils.hasText(cancelReason) ? cancelReason : "用户取消");
    }

    @Override
    public void updateStatus(AppointmentStatusUpdateParam param) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        Appointment appointment = appointmentMapper.selectById(param.getId());
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }

        Integer oldStatus = appointment.getStatus();
        Integer newStatus = param.getStatus();

        // 通用状态修改接口不处理“取消预约”，取消动作需要单独记录取消原因，
        // 因此统一要求前端走 /appointments/cancel/{id}。
        if (Objects.equals(newStatus, STATUS_CANCELED)) {
            throw new BusinessException("取消预约请调用专用取消接口");
        }

        validateStatusFlow(oldStatus, newStatus);
        appointmentMapper.updateStatusById(param.getId(), newStatus);
    }

    /**
     * 校验预约状态流转是否合法。
     *
     * <p>当前项目约定的状态流转比较固定：
     * - 待确认 -> 已确认
     * - 已确认 -> 已完成
     *
     * <p>如果后续想支持更复杂的状态变更，再在这里统一扩展。</p>
     *
     * @param oldStatus 原状态
     * @param newStatus 新状态
     */
    private void validateStatusFlow(Integer oldStatus, Integer newStatus) {
        if (Objects.equals(oldStatus, newStatus)) {
            throw new BusinessException("预约状态未发生变化");
        }

        if (Objects.equals(oldStatus, STATUS_PENDING) && Objects.equals(newStatus, STATUS_CONFIRMED)) {
            return;
        }
        if (Objects.equals(oldStatus, STATUS_CONFIRMED) && Objects.equals(newStatus, STATUS_COMPLETED)) {
            return;
        }

        throw new BusinessException("当前预约状态不允许这样修改");
    }

    /**
     * 生成预约单号。
     *
     * <p>单号规则保持简单可讲解：
     * - 固定前缀 AP
     * - 当前时间到秒
     * - 随机三位数
     *
     * <p>这类实现已经足够支撑教学型项目和本地演示。</p>
     *
     * @return 预约单号
     */
    private String generateAppointmentNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = 100 + new Random().nextInt(900);
        return "AP" + timePart + randomPart;
    }
}
