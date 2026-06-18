package com.mindcare.controller;

import com.mindcare.annotation.PreventDuplicate;
import com.mindcare.annotation.RequireRole;
import com.mindcare.pojo.CompleteAppointmentParam;
import com.mindcare.pojo.ConsultationRecordPageItem;
import com.mindcare.pojo.ConsultationRecordQueryParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.pojo.Result;
import com.mindcare.service.ConsultationRecordService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 咨询记录控制器。
 *
 * <p>当前模块采用“预约驱动”的设计：
 * 页面既能查看已完成且已有记录的预约，
 * 也能查看已确认但尚未填写记录、待完成闭环的预约。</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/consultationRecords")
public class ConsultationRecordController {

    private final ConsultationRecordService consultationRecordService;

    public ConsultationRecordController(ConsultationRecordService consultationRecordService) {
        this.consultationRecordService = consultationRecordService;
    }

    /**
     * 分页查询咨询记录列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result page(ConsultationRecordQueryParam queryParam) {
        log.info("分页查询咨询记录列表: {}", queryParam);
        PageResult<ConsultationRecordPageItem> pageResult = consultationRecordService.page(queryParam);
        return Result.success(pageResult);
    }

    /**
     * 根据预约主键查询咨询记录详情。
     *
     * @param appointmentId 预约主键
     * @return 详情对象
     */
    @GetMapping("/appointment/{appointmentId}")
    public Result getDetailByAppointmentId(@PathVariable Long appointmentId) {
        log.info("查询咨询记录详情: appointmentId={}", appointmentId);
        ConsultationRecordPageItem detail = consultationRecordService.getDetailByAppointmentId(appointmentId);
        return Result.success(detail);
    }

    /**
     * 完成预约并填写咨询记录。
     *
     * <p>该接口直接复用服务层中的事务场景，
     * 保证“写入咨询记录”和“修改预约状态”为同一个业务动作。</p>
     *
     * @param param 完成预约参数
     * @return 统一成功结果
     */
    @PreventDuplicate
    @RequireRole({RequireRole.ADMIN, RequireRole.COUNSELOR})
    @PostMapping("/complete")
    public Result completeAppointment(@Valid @RequestBody CompleteAppointmentParam param) {
        log.info("完成预约并填写咨询记录: {}", param);
        consultationRecordService.completeAppointment(param);
        return Result.success();
    }
}
