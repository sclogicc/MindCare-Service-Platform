package com.mindcare.mapper;

import com.mindcare.pojo.CounselorDetail;
import com.mindcare.pojo.CounselorOption;
import com.mindcare.pojo.CounselorPageItem;
import com.mindcare.pojo.CounselorQueryParam;
import com.mindcare.pojo.CounselorUpdateParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 咨询师 Mapper。
 *
 * <p>当前只实现一个典型的一对多查询场景：
 * 查询咨询师详情，同时把该咨询师名下的可预约时间段列表一起查出来。</p>
 */
@Mapper
public interface CounselorMapper {

    /**
     * 分页查询咨询师列表。
     *
     * @param queryParam 查询参数
     * @return 列表结果
     */
    List<CounselorPageItem> selectPageList(CounselorQueryParam queryParam);

    /**
     * 查询启用状态的咨询师下拉选项。
     *
     * @return 咨询师选项列表
     */
    List<CounselorOption> selectActiveOptions();

    /**
     * 查询咨询师详情，并携带可预约时间段列表。
     *
     * @param id 咨询师主键
     * @return 咨询师详情
     */
    CounselorDetail selectDetailWithSchedulesById(@Param("id") Long id);

    /**
     * 查询咨询师详情，并仅返回当前仍可预约的时间段列表。
     *
     * <p>这里主要服务于前端新增预约表单：
     * - 只保留启用中的咨询师
     * - 只保留启用中的时间段
     * - 过滤掉已被未取消预约占用的时间段</p>
     *
     * @param id 咨询师主键
     * @return 咨询师详情
     */
    CounselorDetail selectAvailableDetailWithSchedulesById(@Param("id") Long id);

    /**
     * 修改咨询师业务信息。
     *
     * @param param 修改参数
     */
    void updateById(CounselorUpdateParam param);

    /**
     * 修改咨询师状态。
     *
     * @param id 咨询师主键
     * @param status 目标状态
     */
    void updateStatusById(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据用户 ID 查询咨询师主键。
     *
     * @param userId 系统用户主键
     * @return 咨询师主键，若不存在则返回 null
     */
    Long selectIdByUserId(@Param("userId") Long userId);
}
