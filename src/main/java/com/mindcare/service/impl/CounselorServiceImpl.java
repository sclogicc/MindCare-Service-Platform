package com.mindcare.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mindcare.constant.EnableStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 咨询师业务实现类。
 */
@Slf4j
@Service
public class CounselorServiceImpl implements CounselorService {

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
        log.info("咨询师信息已更新: id={}", param.getId());
    }

    @Override
    public void updateStatus(CounselorStatusUpdateParam param) {
        // 基础字段非空校验已由 Controller 层 @Valid 完成

        if (!EnableStatus.isValid(param.getStatus())) {
            throw new BusinessException("咨询师状态非法");
        }

        CounselorDetail detail = counselorMapper.selectDetailWithSchedulesById(param.getId());
        if (detail == null) {
            throw new BusinessException("咨询师不存在");
        }

        EnableStatus newStatus = EnableStatus.fromCode(param.getStatus());
        counselorMapper.updateStatusById(param.getId(), newStatus.getCode());
        log.info("咨询师状态已变更: id={}, status={}", param.getId(), newStatus.getDescription());
    }

    /**
     * 校验咨询师修改业务规则（非空校验已由 Controller 层 @Valid 完成）。
     */
    private void validateUpdateParam(CounselorUpdateParam param) {
        if (param.getStatus() != null && !EnableStatus.isValid(param.getStatus())) {
            throw new BusinessException("咨询师状态非法");
        }
    }
}
