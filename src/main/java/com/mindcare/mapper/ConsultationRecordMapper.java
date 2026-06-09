package com.mindcare.mapper;

import com.mindcare.pojo.ConsultationRecord;
import com.mindcare.pojo.ConsultationRecordPageItem;
import com.mindcare.pojo.ConsultationRecordQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 咨询记录 Mapper。
 *
 * <p>当前事务场景下只需要两个核心能力：
 * 1. 根据 appointmentId 判断是否已经存在咨询记录
 * 2. 新增咨询记录</p>
 */
@Mapper
public interface ConsultationRecordMapper {

    /**
     * 分页查询咨询记录列表。
     *
     * <p>这里的“列表”本质上是按预约维度查询，
     * 并通过 left join consultation_record 的方式判断是否已经填写咨询记录。</p>
     *
     * @param queryParam 查询参数
     * @return 列表结果
     */
    List<ConsultationRecordPageItem> selectPageList(ConsultationRecordQueryParam queryParam);

    /**
     * 根据预约主键查询咨询记录详情。
     *
     * @param appointmentId 预约主键
     * @return 详情对象
     */
    ConsultationRecordPageItem selectDetailByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * 根据预约主键统计咨询记录数量。
     *
     * @param appointmentId 预约主键
     * @return 记录数量
     */
    Integer countByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * 新增咨询记录。
     *
     * @param consultationRecord 咨询记录
     */
    void insert(ConsultationRecord consultationRecord);
}
