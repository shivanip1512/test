package com.cannontech.web.api.dr.setup.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.dao.impl.LMProgramConstraintSetupDaoImpl;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;

public class LMProgramConstraintSetupFilterServiceImpl implements LMSetupFilterService<ProgramConstraint> {
    @Autowired private LMProgramConstraintSetupDaoImpl setupDao;

    @Override
    public SearchResults<ProgramConstraint> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext) {
        List<ProgramConstraint> filteredResults = setupDao.getDetails(filterCriteria);
        int totalHitCount = setupDao.getTotalCount(filterCriteria);
        SearchResults<ProgramConstraint> searchResults = SearchResults.pageBasedForSublist(filteredResults, filterCriteria.getPagingParameters(), totalHitCount);
        return searchResults;
    }

    @Override
    public LmSetupFilterType getFilterType() {
        return LmSetupFilterType.PROGRAM_CONSTRAINT;
    }

}
