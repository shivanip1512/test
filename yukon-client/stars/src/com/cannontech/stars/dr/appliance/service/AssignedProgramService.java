package com.cannontech.stars.dr.appliance.service;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.user.YukonUserContext;

public interface AssignedProgramService {
    public SearchResult<AssignedProgram> filter(int applianceCategoryId,
            UiFilter<AssignedProgram> filter, boolean sortByName,
            boolean sortDescending, int startIndex, int count,
            YukonUserContext userContext);
}
