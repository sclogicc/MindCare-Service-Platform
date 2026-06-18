package com.mindcare.controller;

import com.mindcare.annotation.RequireRole;
import com.mindcare.pojo.CounselorDetail;
import com.mindcare.pojo.CounselorOption;
import com.mindcare.pojo.CounselorPageItem;
import com.mindcare.pojo.CounselorQueryParam;
import com.mindcare.pojo.CounselorStatusUpdateParam;
import com.mindcare.pojo.CounselorUpdateParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.pojo.Result;
import com.mindcare.service.CounselorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 咨询师控制器。
 *
 * <p>当前控制器主要服务于预约表单联调：
 * 前端先拉取咨询师列表，再根据咨询师主键查询详情和可预约时间段。</p>
 */
@Slf4j
@RestController
@RequestMapping("/counselors")
public class CounselorController {

    private final CounselorService counselorService;

    public CounselorController(CounselorService counselorService) {
        this.counselorService = counselorService;
    }

    /**
     * 分页查询咨询师列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result page(CounselorQueryParam queryParam) {
        log.info("分页查询咨询师列表: {}", queryParam);
        PageResult<CounselorPageItem> pageResult = counselorService.page(queryParam);
        return Result.success(pageResult);
    }

    /**
     * 查询启用状态的咨询师下拉列表。
     *
     * @return 咨询师选项集合
     */
    @GetMapping("/options")
    public Result listOptions() {
        log.info("查询咨询师下拉列表");
        List<CounselorOption> list = counselorService.listOptions();
        return Result.success(list);
    }

    /**
     * 查询咨询师详情，同时带出当前可预约时间段列表。
     *
     * <p>这里复用了 MyBatis resultMap + collection 的一对多装配能力，
     * 适合直接给前端详情卡片和时间段下拉框使用。</p>
     *
     * @param id 咨询师主键
     * @return 咨询师详情
     */
    @GetMapping("/{id}")
    public Result getDetailById(@PathVariable Long id) {
        log.info("查询咨询师详情: id={}", id);
        CounselorDetail detail = counselorService.getDetailById(id);
        return Result.success(detail);
    }

    /**
     * 查询后台管理场景下的咨询师详情。
     *
     * @param id 咨询师主键
     * @return 详情对象
     */
    @RequireRole(RequireRole.ADMIN)
    @GetMapping("/manage/{id}")
    public Result getManageDetailById(@PathVariable Long id) {
        log.info("查询后台咨询师详情: id={}", id);
        CounselorDetail detail = counselorService.getManageDetailById(id);
        return Result.success(detail);
    }

    /**
     * 修改咨询师业务信息。
     *
     * @param param 修改参数
     * @return 统一成功结果
     */
    @RequireRole(RequireRole.ADMIN)
    @PutMapping
    public Result update(@RequestBody CounselorUpdateParam param) {
        log.info("修改咨询师业务信息: {}", param);
        counselorService.update(param);
        return Result.success();
    }

    /**
     * 修改咨询师状态。
     *
     * @param param 状态参数
     * @return 统一成功结果
     */
    @RequireRole(RequireRole.ADMIN)
    @PutMapping("/status")
    public Result updateStatus(@RequestBody CounselorStatusUpdateParam param) {
        log.info("修改咨询师状态: {}", param);
        counselorService.updateStatus(param);
        return Result.success();
    }
}
