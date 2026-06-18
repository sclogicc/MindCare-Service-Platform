package com.mindcare.controller;

import com.mindcare.annotation.PreventDuplicate;
import com.mindcare.constant.UserRole;
import com.mindcare.interceptor.TokenInterceptor;
import com.mindcare.mapper.CounselorMapper;
import com.mindcare.pojo.Appointment;
import com.mindcare.pojo.AppointmentDetail;
import com.mindcare.pojo.AppointmentDetail;
import com.mindcare.pojo.AppointmentQueryParam;
import com.mindcare.pojo.AppointmentStatusUpdateParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.pojo.Result;
import com.mindcare.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 预约管理控制器。
 *
 * <p>该控制器只负责接收请求、打印必要日志、调用 Service 完成业务处理，
 * 不在 Controller 中直接编写数据库查询或状态校验逻辑，
 * 以保持和传统 Spring Boot 后台管理项目一致的分层风格。</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CounselorMapper counselorMapper;
    private final HttpServletRequest request;

    public AppointmentController(AppointmentService appointmentService,
                                 CounselorMapper counselorMapper,
                                 HttpServletRequest request) {
        this.appointmentService = appointmentService;
        this.counselorMapper = counselorMapper;
        this.request = request;
    }

    /** 读取拦截器存入的当前用户角色 */
    private Integer currentRole() {
        return (Integer) request.getAttribute(TokenInterceptor.ATTR_ROLE);
    }

    /** 读取拦截器存入的当前用户 ID */
    private Long currentUserId() {
        return (Long) request.getAttribute(TokenInterceptor.ATTR_USER_ID);
    }

    /**
     * 新增预约。
     *
     * <p>前端提交预约表单后进入该接口。
     * Service 层会负责：
     * 1. 生成预约单号
     * 2. 校验时间段是否已经被未取消预约占用
     * 3. 保存预约数据</p>
     *
     * @param appointment 预约表单数据
     * @return 统一成功结果
     */
    @PreventDuplicate
    @PostMapping
    public Result add(@Valid @RequestBody Appointment appointment) {
        log.info("新增预约: {}", appointment);
        appointmentService.add(appointment);
        return Result.success();
    }

    /**
     * 分页查询预约列表。
     *
     * <p>支持分页和条件查询，动态 SQL 主要写在 MyBatis XML 中。
     * 当前接口适合管理员查看全量预约，也可通过传入 userId、counselorId 做视角收敛。</p>
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result page(AppointmentQueryParam queryParam) {
        applyRoleFilter(queryParam);
        log.info("分页查询预约列表: role={}, {}", currentRole(), queryParam);
        PageResult<AppointmentDetail> pageResult = appointmentService.page(queryParam);
        return Result.success(pageResult);
    }

    /**
     * 根据当前用户角色，强制限定数据可见范围。
     *
     * <p>普通用户只能看自己的预约；咨询师只能看分配给自己的预约；
     * 管理员不做额外限制。</p>
     */
    private void applyRoleFilter(AppointmentQueryParam queryParam) {
        Integer role = currentRole();
        Long userId = currentUserId();
        if (role == null || userId == null) {
            return;
        }
        if (role == UserRole.USER.getCode()) {
            queryParam.setUserId(userId);
        } else if (role == UserRole.COUNSELOR.getCode()) {
            Long counselorId = counselorMapper.selectIdByUserId(userId);
            if (counselorId != null) {
                queryParam.setCounselorId(counselorId);
            }
        }
    }

    /**
     * 查询预约详情。
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        AppointmentDetail detail = appointmentService.getById(id);
        checkAppointmentAccess(detail);
        log.info("查询预约详情: id={}, role={}", id, currentRole());
        return Result.success(detail);
    }

    /**
     * 校验当前用户是否有权限查看该预约。
     *
     * <p>普通用户只能查看自己的预约；咨询师只能查看分配给自己的预约；
     * 管理员可查看所有。</p>
     */
    private void checkAppointmentAccess(AppointmentDetail detail) {
        if (detail == null) {
            return;
        }
        Integer role = currentRole();
        Long userId = currentUserId();
        if (role == null || userId == null) {
            return;
        }
        if (role == UserRole.USER.getCode() && !userId.equals(detail.getUserId())) {
            throw new com.mindcare.exception.BusinessException("无权访问该预约");
        }
        if (role == UserRole.COUNSELOR.getCode()) {
            Long counselorId = counselorMapper.selectIdByUserId(userId);
            if (counselorId == null || !counselorId.equals(detail.getCounselorId())) {
                throw new com.mindcare.exception.BusinessException("无权访问该预约");
            }
        }
    }

    /**
     * 取消预约。
     *
     * <p>该接口单独保留，是因为“取消预约”不仅仅是普通状态修改，
     * 还需要额外记录取消原因，因此和通用状态更新接口分开更清晰。</p>
     *
     * @param id     预约主键
     * @param param  取消原因参数
     * @return 统一成功结果
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id, @RequestBody AppointmentStatusUpdateParam param) {
        log.info("取消预约: id={}, param={}", id, param);
        appointmentService.cancel(id, param.getCancelReason());
        return Result.success();
    }

    /**
     * 修改预约状态。
     *
     * <p>该接口用于通用状态流转，例如：
     * - 待确认 -> 已确认
     * - 已确认 -> 已完成
     *
     * 业务层会校验状态是否允许流转，避免前端随意把预约改成非法状态。</p>
     *
     * @param param 状态修改参数
     * @return 统一成功结果
     */
    @PutMapping("/status")
    public Result updateStatus(@Valid @RequestBody AppointmentStatusUpdateParam param) {
        log.info("修改预约状态: {}", param);
        appointmentService.updateStatus(param);
        return Result.success();
    }
}
