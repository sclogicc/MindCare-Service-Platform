package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mindcare.constant.AppointmentStatus;
import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.AppointmentMapper;
import com.mindcare.mapper.ConsultationRecordMapper;
import com.mindcare.mapper.UploadFileMapper;
import com.mindcare.pojo.Appointment;
import com.mindcare.pojo.CompleteAppointmentParam;
import com.mindcare.pojo.ConsultationRecord;
import com.mindcare.pojo.ConsultationRecordPageItem;
import com.mindcare.pojo.ConsultationRecordQueryParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.service.ConsultationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 咨询记录业务实现类。
 *
 * <p>当前实现的核心事务场景是：
 * 完成预约时，同时写入咨询记录并修改预约状态。</p>
 */
@Slf4j
@Service
public class ConsultationRecordServiceImpl implements ConsultationRecordService {

    private final ConsultationRecordMapper consultationRecordMapper;
    private final AppointmentMapper appointmentMapper;
    private final UploadFileMapper uploadFileMapper;

    public ConsultationRecordServiceImpl(ConsultationRecordMapper consultationRecordMapper,
                                         AppointmentMapper appointmentMapper,
                                         UploadFileMapper uploadFileMapper) {
        this.consultationRecordMapper = consultationRecordMapper;
        this.appointmentMapper = appointmentMapper;
        this.uploadFileMapper = uploadFileMapper;
    }

    @Override
    public PageResult<ConsultationRecordPageItem> page(ConsultationRecordQueryParam queryParam) {
        Integer page = queryParam.getPage() == null || queryParam.getPage() < 1 ? 1 : queryParam.getPage();
        Integer pageSize = queryParam.getPageSize() == null || queryParam.getPageSize() < 1 ? 10 : queryParam.getPageSize();

        Page<ConsultationRecordPageItem> pageInfo = PageHelper.startPage(page, pageSize);
        List<ConsultationRecordPageItem> rows = consultationRecordMapper.selectPageList(queryParam);
        return new PageResult<>(pageInfo.getTotal(), rows);
    }

    @Override
    public ConsultationRecordPageItem getDetailByAppointmentId(Long appointmentId) {
        if (appointmentId == null) {
            throw new BusinessException("预约ID不能为空");
        }

        ConsultationRecordPageItem detail = consultationRecordMapper.selectDetailByAppointmentId(appointmentId);
        if (detail == null) {
            throw new BusinessException("预约记录不存在");
        }

        return detail;
    }

    /**
     * 完成预约并写入咨询记录。
     *
     * <p>事务处理流程：
     * 1. 查询预约记录，确认预约存在且状态为"已确认"
     * 2. 校验该预约是否已经存在咨询记录，避免重复完成
     * 3. 如果携带附件，则校验附件是否存在
     * 4. 新增咨询记录
     * 5. 更新预约状态为"已完成"
     *
     * <p>任意一步失败，整个事务都会回滚。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeAppointment(CompleteAppointmentParam param) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        // 1. 查询原预约记录
        Appointment appointment = appointmentMapper.selectById(param.getAppointmentId());
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }

        // 2. 只有"已确认"的预约，才允许进入"已完成"状态
        AppointmentStatus currentStatus;
        try {
            currentStatus = AppointmentStatus.fromCode(appointment.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("预约状态异常");
        }

        if (currentStatus != AppointmentStatus.CONFIRMED) {
            throw new BusinessException("当前预约状态不允许完成");
        }

        // 3. 校验咨询师匹配
        if (!Objects.equals(appointment.getCounselorId(), param.getCounselorId())) {
            throw new BusinessException("咨询师信息与预约记录不匹配");
        }

        // 4. 一次预约只允许一条咨询记录
        Integer recordCount = consultationRecordMapper.countByAppointmentId(param.getAppointmentId());
        if (recordCount != null && recordCount > 0) {
            throw new BusinessException("该预约已存在咨询记录，请勿重复提交");
        }

        // 5. 附件存在性校验
        if (param.getAttachmentFileId() != null) {
            Integer fileCount = uploadFileMapper.countById(param.getAttachmentFileId());
            if (fileCount == null || fileCount == 0) {
                throw new BusinessException("咨询附件不存在");
            }
        }

        // 6. 组装咨询记录并入库
        ConsultationRecord record = new ConsultationRecord();
        record.setAppointmentId(param.getAppointmentId());
        record.setCounselorId(param.getCounselorId());
        record.setSummary(param.getSummary());
        record.setSuggestion(param.getSuggestion());
        record.setAttachmentFileId(param.getAttachmentFileId());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        consultationRecordMapper.insert(record);

        // 7. 同步把预约状态改成"已完成"（事务内，失败一起回滚）
        appointmentMapper.updateStatusById(param.getAppointmentId(), AppointmentStatus.COMPLETED.getCode());
        log.info("咨询记录已生成并完成预约: appointmentId={}, counselorId={}",
                param.getAppointmentId(), param.getCounselorId());
    }

}
