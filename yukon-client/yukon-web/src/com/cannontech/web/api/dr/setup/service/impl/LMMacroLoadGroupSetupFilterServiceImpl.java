package com.cannontech.web.api.dr.setup.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.dao.impl.LMMacroLoadGroupSetupDaoImpl;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;

public class LMMacroLoadGroupSetupFilterServiceImpl implements LMSetupFilterService <LMPaoDto> {
    @Autowired private LMMacroLoadGroupSetupDaoImpl setupDao;

    @Override
    public SearchResults<LMPaoDto> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext) {
        return setupDao.getDetails(filterCriteria, List.of(PaoType.MACRO_GROUP));
    }

    @Override
    public LmSetupFilterType getFilterType() {
        return LmSetupFilterType.MACRO_LOAD_GROUP;
    }

}
