package com.cannontech.web.api.dr.setup.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.dao.impl.LMLoadProgramSetupDaoImpl;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;

public class LMLoadProgramSetupFilterServiceImpl implements LMSetupFilterService <LMPaoDto> {
    @Autowired private LMLoadProgramSetupDaoImpl setupDao;

    @Override
    public SearchResults<LMPaoDto> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext) {
        LMSetupFilter setupFilter = filterCriteria.getFilteringParameters();
        if (setupFilter.getTypes() != null && !setupFilter.getTypes().isEmpty()) {
            return setupDao.getDetails(filterCriteria, setupFilter.getTypes());
        } else {
            return setupDao.getDetails(filterCriteria, new ArrayList<PaoType>(PaoType.getDirectLMProgramTypes()));
        }

    }

    @Override
    public LmSetupFilterType getFilterType() {
        return LmSetupFilterType.LOAD_PROGRAM;
    }

}
