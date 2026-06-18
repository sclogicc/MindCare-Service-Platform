package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mindcare.constant.AppointmentStatus;
import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.AppointmentMapper;
import com.mindcare.mapper.FeedbackMapper;
import com.mindcare.pojo.Appointment;
import com.mindcare.pojo.Feedback;
import com.mindcare.pojo.FeedbackPageItem;
import com.mindcare.pojo.FeedbackQueryParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 反馈评价业务实现类。
 *
 * <p>当前模块的核心规则：
 * 1. 只有已完成预约允许评价
 * 2. 一次预约只允许提交一条反馈
 * 3. 反馈用户和预约用户必须一致</p>
 */
@Slf4j
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;
    private final AppointmentMapper appointmentMapper;

    public FeedbackServiceImpl(FeedbackMapper feedbackMapper, AppointmentMapper appointmentMapper) {
        this.feedbackMapper = feedbackMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public void add(Feedback feedback) {
        // 基础字段非空/范围校验已由 Controller 层 @Valid 完成
        if (feedback.getIsAnonymous() == null) {
            feedback.setIsAnonymous(0);
        }

        Appointment appointment = appointmentMapper.selectById(feedback.getAppointmentId());
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }

        AppointmentStatus currentStatus;
        try {
            currentStatus = AppointmentStatus.fromCode(appointment.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("预约状态异常");
        }

        if (currentStatus != AppointmentStatus.COMPLETED) {
            throw new BusinessException("只有已完成的预约才能提交评价");
        }

        if (!Objects.equals(appointment.getUserId(), feedback.getUserId())) {
            throw new BusinessException("评价用户与预约记录不匹配");
        }

        if (!Objects.equals(appointment.getCounselorId(), feedback.getCounselorId())) {
            throw new BusinessException("咨询师信息与预约记录不匹配");
        }

        Integer count = feedbackMapper.countByAppointmentId(feedback.getAppointmentId());
        if (count != null && count > 0) {
            throw new BusinessException("该预约已提交评价，请勿重复操作");
        }

        feedback.setCreateTime(LocalDateTime.now());
        feedback.setUpdateTime(LocalDateTime.now());
        feedbackMapper.insert(feedback);
        log.info("评价提交成功: appointmentId={}, userId={}, score={}",
                feedback.getAppointmentId(), feedback.getUserId(), feedback.getScore());
    }

    @Override
    public PageResult<FeedbackPageItem> page(FeedbackQueryParam queryParam) {
        Integer page = queryParam.getPage() == null || queryParam.getPage() < 1 ? 1 : queryParam.getPage();
        Integer pageSize = queryParam.getPageSize() == null || queryParam.getPageSize() < 1 ? 10 : queryParam.getPageSize();

        Page<FeedbackPageItem> pageInfo = PageHelper.startPage(page, pageSize);
        List<FeedbackPageItem> rows = feedbackMapper.selectPageList(queryParam);
        return new PageResult<>(pageInfo.getTotal(), rows);
    }

    @Override
    public FeedbackPageItem getById(Long id) {
        if (id == null) {
            throw new BusinessException("反馈主键不能为空");
        }

        FeedbackPageItem detail = feedbackMapper.selectDetailById(id);
        if (detail == null) {
            throw new BusinessException("反馈记录不存在");
        }

        return detail;
    }

    @Override
    public FeedbackPageItem getByAppointmentId(Long appointmentId) {
        if (appointmentId == null) {
            throw new BusinessException("预约ID不能为空");
        }

        FeedbackPageItem detail = feedbackMapper.selectDetailByAppointmentId(appointmentId);
        if (detail == null) {
            throw new BusinessException("预约记录不存在");
        }

        return detail;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new BusinessException("反馈主键不能为空");
        }

        FeedbackPageItem detail = feedbackMapper.selectDetailById(id);
        if (detail == null) {
            throw new BusinessException("反馈记录不存在");
        }

        feedbackMapper.deleteById(id);
        log.info("评价已删除: id={}", id);
    }

}
