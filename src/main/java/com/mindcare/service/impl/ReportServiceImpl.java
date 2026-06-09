package com.mindcare.service.impl;

import com.mindcare.mapper.ReportMapper;
import com.mindcare.pojo.AppointmentStatusCount;
import com.mindcare.pojo.MonthlyAppointmentOption;
import com.mindcare.pojo.ReportOption;
import com.mindcare.pojo.ReportQueryParam;
import com.mindcare.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表业务实现类。
 *
 * <p>当前报表模块的设计思路是：
 * 1. Mapper 负责写聚合 SQL
 * 2. Service 负责把数据库原始统计结果整理成前端图表更好消费的结构
 * 3. 对“月份缺失”“评分缺失”这类情况进行补零处理，避免前端图表出现断层</p>
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    public ReportServiceImpl(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public List<ReportOption> appointmentStatusReport(ReportQueryParam queryParam) {
        AppointmentStatusCount count = reportMapper.selectAppointmentStatusCount(queryParam);
        if (count == null) {
            count = new AppointmentStatusCount();
        }

        List<ReportOption> result = new ArrayList<>();
        result.add(buildReportOption("待确认", count.getPendingCount()));
        result.add(buildReportOption("已确认", count.getConfirmedCount()));
        result.add(buildReportOption("已完成", count.getCompletedCount()));
        result.add(buildReportOption("已取消", count.getCanceledCount()));
        return result;
    }

    @Override
    public List<ReportOption> counselorWorkloadReport(ReportQueryParam queryParam) {
        return reportMapper.selectCounselorWorkload(queryParam);
    }

    @Override
    public List<MonthlyAppointmentOption> monthlyAppointmentReport(ReportQueryParam queryParam) {
        int year = queryParam.getYear() == null ? LocalDate.now().getYear() : queryParam.getYear();

        List<MonthlyAppointmentOption> dbList = reportMapper.selectMonthlyAppointmentCount(year);
        Map<Integer, Long> monthValueMap = new LinkedHashMap<>();
        for (MonthlyAppointmentOption item : dbList) {
            monthValueMap.put(item.getMonth(), item.getValue());
        }

        // 补齐 1~12 月，避免没有数据的月份在图表中缺失。
        List<MonthlyAppointmentOption> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            MonthlyAppointmentOption option = new MonthlyAppointmentOption();
            option.setMonth(month);
            option.setMonthLabel(month + "月");
            option.setValue(monthValueMap.getOrDefault(month, 0L));
            result.add(option);
        }
        return result;
    }

    @Override
    public List<ReportOption> feedbackScoreReport(ReportQueryParam queryParam) {
        List<ReportOption> dbList = reportMapper.selectFeedbackScoreCount(queryParam);
        Map<String, Long> scoreValueMap = new LinkedHashMap<>();
        for (ReportOption item : dbList) {
            scoreValueMap.put(item.getName(), item.getValue());
        }

        // 评分固定 1~5 分，前端图表通常需要完整维度，因此这里统一补齐。
        List<ReportOption> result = new ArrayList<>();
        for (int score = 1; score <= 5; score++) {
            result.add(buildReportOption(score + "分", scoreValueMap.getOrDefault(score + "分", 0L)));
        }
        return result;
    }

    /**
     * 构造通用报表选项对象。
     *
     * @param name  图表展示名称
     * @param value 图表值
     * @return 报表对象
     */
    private ReportOption buildReportOption(String name, Long value) {
        ReportOption option = new ReportOption();
        option.setName(name);
        option.setValue(value == null ? 0L : value);
        return option;
    }
}
