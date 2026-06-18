package com.mindcare.controller;

import com.mindcare.annotation.RequireRole;
import com.mindcare.pojo.Feedback;
import com.mindcare.pojo.FeedbackPageItem;
import com.mindcare.pojo.FeedbackQueryParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.pojo.Result;
import com.mindcare.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 反馈评价控制器。
 *
 * <p>当前模块同样采用“预约驱动”的页面组织方式，
 * 既能查看已评价记录，也能找出尚未评价的已完成预约。</p>
 */
@Slf4j
@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * 新增反馈评价。
     *
     * @param feedback 反馈参数
     * @return 统一成功结果
     */
    @PostMapping
    public Result add(@RequestBody Feedback feedback) {
        log.info("新增反馈评价: {}", feedback);
        feedbackService.add(feedback);
        return Result.success();
    }

    /**
     * 分页查询反馈列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result page(FeedbackQueryParam queryParam) {
        log.info("分页查询反馈列表: {}", queryParam);
        PageResult<FeedbackPageItem> pageResult = feedbackService.page(queryParam);
        return Result.success(pageResult);
    }

    /**
     * 查询反馈详情。
     *
     * @param id 反馈主键
     * @return 详情
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        log.info("查询反馈详情: id={}", id);
        FeedbackPageItem detail = feedbackService.getById(id);
        return Result.success(detail);
    }

    /**
     * 根据预约主键查询反馈详情。
     *
     * @param appointmentId 预约主键
     * @return 详情
     */
    @GetMapping("/appointment/{appointmentId}")
    public Result getByAppointmentId(@PathVariable Long appointmentId) {
        log.info("根据预约查询反馈详情: appointmentId={}", appointmentId);
        FeedbackPageItem detail = feedbackService.getByAppointmentId(appointmentId);
        return Result.success(detail);
    }

    /**
     * 删除反馈。
     *
     * @param id 反馈主键
     * @return 统一成功结果
     */
    @RequireRole(RequireRole.ADMIN)
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        log.info("删除反馈: id={}", id);
        feedbackService.deleteById(id);
        return Result.success();
    }
}
