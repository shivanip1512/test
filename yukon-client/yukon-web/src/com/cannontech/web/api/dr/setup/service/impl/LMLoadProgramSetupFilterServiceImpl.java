package com.cannontech.web.api.dr.setup.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.dao.impl.LMLoadProgramSetupDaoImpl;
import com.cannontech.web.api.dr.setup.model.LoadProgramFilteredResult;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;

public class LMLoadProgramSetupFilterServiceImpl implements LMSetupFilterService<LoadProgramFilteredResult> {
    @Autowired private LMLoadProgramSetupDaoImpl setupDao;

    @Override
    public SearchResults<LoadProgramFilteredResult> filter(FilterCriteria<LMSetupFilter> filterCriteria,
            YukonUserContext userContext) {
        List<LoadProgramFilteredResult> filteredResults = setupDao.getDetails(filterCriteria);
        filteredResults.stream().forEach(result -> {
            result.setGears(setupDao.getGearsOrderByGearNumber(result.getProgram().getId()));
            result.setLoadGroups(setupDao.getLoadGroupsOrderByGroupOrder(result.getProgram().getId()));
        });
        int totalHitCount = setupDao.getTotalCount(filterCriteria);
        SearchResults<LoadProgramFilteredResult> searchResults = SearchResults.pageBasedForSublist(filteredResults,
                filterCriteria.getPagingParameters(), totalHitCount);
        return searchResults;
    }

    @Override
    public LmSetupFilterType getFilterType() {
        return LmSetupFilterType.LOAD_PROGRAM;
    }

}
