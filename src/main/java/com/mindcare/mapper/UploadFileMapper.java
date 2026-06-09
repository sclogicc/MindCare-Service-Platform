package com.mindcare.mapper;

import com.mindcare.pojo.UploadFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 附件 Mapper。
 *
 * <p>当前事务场景中仅用于校验附件是否存在，
 * 因此保持最小化接口即可。</p>
 */
@Mapper
public interface UploadFileMapper {

    /**
     * 根据主键统计附件数量。
     *
     * @param id 附件主键
     * @return 数量
     */
    Integer countById(@Param("id") Long id);

    /**
     * 新增附件记录。
     *
     * @param uploadFile 附件实体
     */
    void insert(UploadFile uploadFile);
}
