package com.mindcare.mapper;

import com.mindcare.pojo.AppointmentStatusCount;
import com.mindcare.pojo.MonthlyAppointmentOption;
import com.mindcare.pojo.ReportOption;
import com.mindcare.pojo.ReportQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计报表 Mapper。
 *
 * <p>该 Mapper 只负责统计查询，不承载普通 CRUD。
 * 所有复杂聚合 SQL 都集中放在这里，方便后续统一维护报表逻辑。</p>
 */
@Mapper
public interface ReportMapper {

    /**
     * 查询预约状态统计结果。
     *
     * @param queryParam 查询参数
     * @return 单行状态计数结果
     */
    AppointmentStatusCount selectAppointmentStatusCount(ReportQueryParam queryParam);

    /**
     * 查询咨询师工作量统计。
     *
     * @param queryParam 查询参数
     * @return 工作量列表
     */
    List<ReportOption> selectCounselorWorkload(ReportQueryParam queryParam);

    /**
     * 查询某年每月预约数量。
     *
     * @param year 年份
     * @return 月度统计结果
     */
    List<MonthlyAppointmentOption> selectMonthlyAppointmentCount(@Param("year") Integer year);

    /**
     * 查询反馈评分统计。
     *
     * @param queryParam 查询参数
     * @return 评分分布数据
     */
    List<ReportOption> selectFeedbackScoreCount(ReportQueryParam queryParam);
}
