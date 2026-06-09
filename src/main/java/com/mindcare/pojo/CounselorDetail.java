package com.mindcare.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 咨询师详情展示对象。
 *
 * <p>这里没有直接复用 counselor 表的单表实体，而是单独设计一个详情对象，
 * 原因是当前详情页除了咨询师表字段外，还需要展示：
 * 1. 咨询师账号姓名
 * 2. 联系电话
 * 3. 可预约时间段列表 scheduleList
 *
 * <p>因此使用独立的详情对象更符合“一个对象服务一个查询场景”的设计思路，
 * 也更便于后续前端详情页直接消费。</p>
 */
@Data
public class CounselorDetail {

    private Long id;
    private Long userId;
    private String counselorName;
    private String phone;
    private String specialty;
    private String title;
    private Integer yearsOfExperience;
    private String introduction;
    private Long avatarFileId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 咨询师可预约时间段列表。
     *
     * <p>该字段对应 counselor_schedule 表中的多条记录，
     * 是当前 resultMap + collection 映射的核心落点。</p>
     */
    private List<Schedule> scheduleList;
}
