package com.cannontech.web.api.dr.setup.service;

import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;

public interface LMSetupFilterService <T> {

    /**
     * Calls dao to filter based on filter criteria.
     */
    SearchResults<T> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext );

    /**
     * Return Filter type.
     */
    LmSetupFilterType getFilterType();

}
