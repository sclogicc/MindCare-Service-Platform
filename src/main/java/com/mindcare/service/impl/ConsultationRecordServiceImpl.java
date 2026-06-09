package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 咨询记录业务实现类。
 *
 * <p>当前实现的核心事务场景是：
 * 完成预约时，同时写入咨询记录并修改预约状态。
 *
 * <p>为什么要把这两个动作放在同一个事务中：
 * 1. 如果咨询记录写入成功，但预约状态没有改成“已完成”，数据会不一致
 * 2. 如果预约状态先改成“已完成”，但咨询记录插入失败，也会导致业务闭环缺失
 * 3. 因此这两步必须保证“要么一起成功，要么一起失败”</p>
 */
@Service
public class ConsultationRecordServiceImpl implements ConsultationRecordService {

    /**
     * 预约状态：已确认。
     */
    private static final int STATUS_CONFIRMED = 2;

    /**
     * 预约状态：已完成。
     */
    private static final int STATUS_COMPLETED = 3;

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
     * 1. 校验参数完整性
     * 2. 查询预约记录，确认预约存在且状态为“已确认”
     * 3. 校验该预约是否已经存在咨询记录，避免重复完成
     * 4. 如果携带附件，则校验附件是否存在
     * 5. 新增咨询记录
     * 6. 更新预约状态为“已完成”
     *
     * <p>任意一步失败，整个事务都会回滚。</p>
     *
     * @param param 完成预约业务参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeAppointment(CompleteAppointmentParam param) {
        validateCompleteParam(param);

        // 1. 查询原预约记录，确认主业务数据存在。
        Appointment appointment = appointmentMapper.selectById(param.getAppointmentId());
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }

        // 2. 只有“已确认”的预约，才允许进入“已完成”状态。
        // 这样可以防止前端跳过确认环节，直接强行完成预约。
        if (!Objects.equals(appointment.getStatus(), STATUS_CONFIRMED)) {
            throw new BusinessException("当前预约状态不允许完成");
        }

        // 2.1 防止前端把咨询记录误提交到其他咨询师名下。
        if (!Objects.equals(appointment.getCounselorId(), param.getCounselorId())) {
            throw new BusinessException("咨询师信息与预约记录不匹配");
        }

        // 3. 一次预约只允许生成一条咨询记录。
        // 如果已经写过记录，说明该预约已经完成或已经被处理，不允许重复提交。
        Integer recordCount = consultationRecordMapper.countByAppointmentId(param.getAppointmentId());
        if (recordCount != null && recordCount > 0) {
            throw new BusinessException("该预约已存在咨询记录，请勿重复提交");
        }

        // 4. 如果前端携带了附件主键，则先校验附件是否真实存在。
        // 这一步虽然不是必须写进事务中的数据操作，但放在同一个业务流程中更完整，
        // 可以避免出现“咨询记录引用了不存在附件”的脏数据。
        if (param.getAttachmentFileId() != null) {
            Integer fileCount = uploadFileMapper.countById(param.getAttachmentFileId());
            if (fileCount == null || fileCount == 0) {
                throw new BusinessException("咨询附件不存在");
            }
        }

        // 5. 组装咨询记录并入库。
        ConsultationRecord record = new ConsultationRecord();
        record.setAppointmentId(param.getAppointmentId());
        record.setCounselorId(param.getCounselorId());
        record.setSummary(param.getSummary());
        record.setSuggestion(param.getSuggestion());
        record.setAttachmentFileId(param.getAttachmentFileId());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        consultationRecordMapper.insert(record);

        // 6. 同步把预约状态改成“已完成”。
        // 如果这里失败，前面已经插入的咨询记录也必须回滚。
        appointmentMapper.updateStatusById(param.getAppointmentId(), STATUS_COMPLETED);
    }

    /**
     * 校验完成预约业务参数。
     *
     * @param param 业务参数
     */
    private void validateCompleteParam(CompleteAppointmentParam param) {
        if (param == null) {
            throw new BusinessException("完成预约参数不能为空");
        }
        if (param.getAppointmentId() == null) {
            throw new BusinessException("预约ID不能为空");
        }
        if (param.getCounselorId() == null) {
            throw new BusinessException("咨询师ID不能为空");
        }
        if (!StringUtils.hasText(param.getSummary())) {
            throw new BusinessException("咨询摘要不能为空");
        }
    }
}
