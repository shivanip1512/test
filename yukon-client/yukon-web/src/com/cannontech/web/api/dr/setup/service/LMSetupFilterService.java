package com.cannontech.web.api.dr.setup.service;

import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;

public interface LMSetupFilterService {

    /**
     * Calls dao to filter based on filter criteria.
     */
    SearchResults<LMPaoDto> filter(FilterCriteria<LMSetupFilter> filterCriteria);

}
