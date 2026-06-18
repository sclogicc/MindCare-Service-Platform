package com.mindcare.controller;

import com.mindcare.annotation.RequireRole;
import com.mindcare.pojo.PageResult;
import com.mindcare.pojo.Result;
import com.mindcare.pojo.Schedule;
import com.mindcare.pojo.SchedulePageItem;
import com.mindcare.pojo.ScheduleQueryParam;
import com.mindcare.pojo.ScheduleStatusUpdateParam;
import com.mindcare.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 时间段管理控制器。
 *
 * <p>当前模块主要服务于后台排班管理场景：
 * 管理员或咨询师可以查看、维护可预约时间段，
 * 从而为后续预约模块提供可选资源。</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * 新增时间段。
     *
     * @param schedule 时间段参数
     * @return 统一成功结果
     */
    @RequireRole(RequireRole.ADMIN)
    @PostMapping
    public Result add(@Valid @RequestBody Schedule schedule) {
        log.info("新增时间段: {}", schedule);
        scheduleService.add(schedule);
        return Result.success();
    }

    /**
     * 分页查询时间段列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result page(ScheduleQueryParam queryParam) {
        log.info("分页查询时间段列表: {}", queryParam);
        PageResult<SchedulePageItem> pageResult = scheduleService.page(queryParam);
        return Result.success(pageResult);
    }

    /**
     * 查询时间段详情。
     *
     * @param id 时间段主键
     * @return 时间段详情
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        log.info("查询时间段详情: id={}", id);
        return Result.success(scheduleService.getById(id));
    }

    /**
     * 修改时间段。
     *
     * @param schedule 时间段参数
     * @return 统一成功结果
     */
    @RequireRole(RequireRole.ADMIN)
    @PutMapping
    public Result update(@Valid @RequestBody Schedule schedule) {
        log.info("修改时间段: {}", schedule);
        scheduleService.update(schedule);
        return Result.success();
    }

    /**
     * 删除时间段。
     *
     * <p>若时间段已关联预约记录，则不允许删除，
     * 避免破坏预约历史和统计口径。</p>
     *
     * @param id 时间段主键
     * @return 统一成功结果
     */
    @RequireRole(RequireRole.ADMIN)
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        log.info("删除时间段: id={}", id);
        scheduleService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改时间段状态。
     *
     * @param param 状态参数
     * @return 统一成功结果
     */
    @RequireRole(RequireRole.ADMIN)
    @PutMapping("/status")
    public Result updateStatus(@Valid @RequestBody ScheduleStatusUpdateParam param) {
        log.info("修改时间段状态: {}", param);
        scheduleService.updateStatus(param);
        return Result.success();
    }
}
