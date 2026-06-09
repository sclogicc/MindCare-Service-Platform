package com.mindcare.service;

import com.mindcare.pojo.CounselorDetail;
import com.mindcare.pojo.CounselorOption;
import com.mindcare.pojo.CounselorPageItem;
import com.mindcare.pojo.CounselorQueryParam;
import com.mindcare.pojo.CounselorStatusUpdateParam;
import com.mindcare.pojo.CounselorUpdateParam;
import com.mindcare.pojo.PageResult;

import java.util.List;

/**
 * 咨询师业务接口。
 *
 * <p>当前阶段先聚焦前端新增预约所需的两个查询能力：
 * 1. 查询可选咨询师列表
 * 2. 查询咨询师详情并携带可预约时间段列表</p>
 */
public interface CounselorService {

    /**
     * 分页查询咨询师列表。
     *
     * @param queryParam 查询参数
     * @return 分页结果
     */
    PageResult<CounselorPageItem> page(CounselorQueryParam queryParam);

    /**
     * 查询启用状态的咨询师下拉列表。
     *
     * @return 咨询师选项集合
     */
    List<CounselorOption> listOptions();

    /**
     * 查询咨询师详情，并附带当前可预约时间段列表。
     *
     * @param id 咨询师主键
     * @return 咨询师详情
     */
    CounselorDetail getDetailById(Long id);

    /**
     * 查询后台管理场景下的咨询师详情。
     *
     * @param id 咨询师主键
     * @return 咨询师详情
     */
    CounselorDetail getManageDetailById(Long id);

    /**
     * 修改咨询师业务信息。
     *
     * @param param 修改参数
     */
    void update(CounselorUpdateParam param);

    /**
     * 修改咨询师状态。
     *
     * @param param 状态参数
     */
    void updateStatus(CounselorStatusUpdateParam param);
}
