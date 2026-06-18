package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mindcare.exception.BusinessException;
import com.mindcare.mapper.CounselorMapper;
import com.mindcare.pojo.CounselorDetail;
import com.mindcare.pojo.CounselorOption;
import com.mindcare.pojo.CounselorPageItem;
import com.mindcare.pojo.CounselorQueryParam;
import com.mindcare.pojo.CounselorStatusUpdateParam;
import com.mindcare.pojo.CounselorUpdateParam;
import com.mindcare.pojo.PageResult;
import com.mindcare.service.CounselorService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 咨询师业务实现类。
 *
 * <p>这里不承担新增、修改等后台管理逻辑，
 * 先专注于给前端预约表单提供稳定的数据源。</p>
 */
@Service
public class CounselorServiceImpl implements CounselorService {

    /**
     * 咨询师状态：停用。
     */
    private static final int STATUS_DISABLED = 0;

    /**
     * 咨询师状态：启用。
     */
    private static final int STATUS_ENABLED = 1;

    private final CounselorMapper counselorMapper;

    public CounselorServiceImpl(CounselorMapper counselorMapper) {
        this.counselorMapper = counselorMapper;
    }

    @Override
    public PageResult<CounselorPageItem> page(CounselorQueryParam queryParam) {
        Integer page = queryParam.getPage() == null || queryParam.getPage() < 1 ? 1 : queryParam.getPage();
        Integer pageSize = queryParam.getPageSize() == null || queryParam.getPageSize() < 1 ? 10 : queryParam.getPageSize();

        Page<CounselorPageItem> pageInfo = PageHelper.startPage(page, pageSize);
        List<CounselorPageItem> rows = counselorMapper.selectPageList(queryParam);
        return new PageResult<>(pageInfo.getTotal(), rows);
    }

    @Override
    public List<CounselorOption> listOptions() {
        return counselorMapper.selectActiveOptions();
    }

    @Override
    public CounselorDetail getDetailById(Long id) {
        if (id == null) {
            throw new BusinessException("咨询师主键不能为空");
        }

        CounselorDetail detail = counselorMapper.selectAvailableDetailWithSchedulesById(id);
        if (detail == null) {
            throw new BusinessException("咨询师不存在或已停用");
        }

        return detail;
    }

    @Override
    public CounselorDetail getManageDetailById(Long id) {
        if (id == null) {
            throw new BusinessException("咨询师主键不能为空");
        }

        CounselorDetail detail = counselorMapper.selectDetailWithSchedulesById(id);
        if (detail == null) {
            throw new BusinessException("咨询师不存在");
        }

        return detail;
    }

    @Override
    public void update(CounselorUpdateParam param) {
        validateUpdateParam(param);

        CounselorDetail detail = counselorMapper.selectDetailWithSchedulesById(param.getId());
        if (detail == null) {
            throw new BusinessException("咨询师不存在");
        }

        counselorMapper.updateById(param);
    }

    @Override
    public void updateStatus(CounselorStatusUpdateParam param) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        if (!Objects.equals(param.getStatus(), STATUS_ENABLED) && !Objects.equals(param.getStatus(), STATUS_DISABLED)) {
            throw new BusinessException("咨询师状态非法");
        }

        CounselorDetail detail = counselorMapper.selectDetailWithSchedulesById(param.getId());
        if (detail == null) {
            throw new BusinessException("咨询师不存在");
        }

        counselorMapper.updateStatusById(param.getId(), param.getStatus());
    }

    /**
     * 校验咨询师修改业务规则（非空校验已由 Controller 层 @Valid 完成）。
     *
     * @param param 修改参数
     */
    private void validateUpdateParam(CounselorUpdateParam param) {
        if (param.getStatus() != null
                && !Objects.equals(param.getStatus(), STATUS_ENABLED)
                && !Objects.equals(param.getStatus(), STATUS_DISABLED)) {
            throw new BusinessException("咨询师状态非法");
        }
    }
}
