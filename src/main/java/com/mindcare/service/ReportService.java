package com.mindcare.service;

import com.mindcare.pojo.MonthlyAppointmentOption;
import com.mindcare.pojo.ReportOption;
import com.mindcare.pojo.ReportQueryParam;

import java.util.List;

/**
 * 统计报表业务接口。
 */
public interface ReportService {

    /**
     * 预约状态分布统计。
     *
     * @param queryParam 查询参数
     * @return 状态分布数据
     */
    List<ReportOption> appointmentStatusReport(ReportQueryParam queryParam);

    /**
     * 咨询师工作量统计。
     *
     * @param queryParam 查询参数
     * @return 工作量数据
     */
    List<ReportOption> counselorWorkloadReport(ReportQueryParam queryParam);

    /**
     * 每月预约数量统计。
     *
     * @param queryParam 查询参数
     * @return 月度预约数量数据
     */
    List<MonthlyAppointmentOption> monthlyAppointmentReport(ReportQueryParam queryParam);

    /**
     * 反馈评分统计。
     *
     * @param queryParam 查询参数
     * @return 评分分布数据
     */
    List<ReportOption> feedbackScoreReport(ReportQueryParam queryParam);
}
