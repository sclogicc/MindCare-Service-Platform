package com.mindcare.controller;

import com.mindcare.pojo.MonthlyAppointmentOption;
import com.mindcare.pojo.ReportOption;
import com.mindcare.pojo.ReportQueryParam;
import com.mindcare.pojo.Result;
import com.mindcare.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计报表控制器。
 *
 * <p>该控制器统一承接报表查询请求，风格保持和传统后台项目一致：
 * - Controller 只负责接收参数和返回结果
 * - 具体统计逻辑放在 Service 层
 * - 聚合 SQL 放在 Mapper XML 中</p>
 */
@Slf4j
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 预约状态分布统计。
     *
     * @param queryParam 查询参数，可传日期范围
     * @return 状态分布列表
     */
    @GetMapping("/appointmentStatus")
    public Result appointmentStatus(ReportQueryParam queryParam) {
        log.info("查询预约状态分布统计: {}", queryParam);
        List<ReportOption> data = reportService.appointmentStatusReport(queryParam);
        return Result.success(data);
    }

    /**
     * 咨询师工作量统计。
     *
     * @param queryParam 查询参数，可传日期范围
     * @return 咨询师工作量列表
     */
    @GetMapping("/counselorWorkload")
    public Result counselorWorkload(ReportQueryParam queryParam) {
        log.info("查询咨询师工作量统计: {}", queryParam);
        List<ReportOption> data = reportService.counselorWorkloadReport(queryParam);
        return Result.success(data);
    }

    /**
     * 每月预约数量统计。
     *
     * @param queryParam 查询参数，主要使用 year
     * @return 月度预约数量列表
     */
    @GetMapping("/monthlyAppointments")
    public Result monthlyAppointments(ReportQueryParam queryParam) {
        log.info("查询每月预约数量统计: {}", queryParam);
        List<MonthlyAppointmentOption> data = reportService.monthlyAppointmentReport(queryParam);
        return Result.success(data);
    }

    /**
     * 反馈评分统计。
     *
     * @param queryParam 查询参数，可传日期范围
     * @return 评分分布列表
     */
    @GetMapping("/feedbackScore")
    public Result feedbackScore(ReportQueryParam queryParam) {
        log.info("查询反馈评分统计: {}", queryParam);
        List<ReportOption> data = reportService.feedbackScoreReport(queryParam);
        return Result.success(data);
    }
}
