package com.cannontech.stars.dr.appliance.service;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper.SortBy;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;

public interface AssignedProgramService {
    public SearchResult<AssignedProgram> filter(Iterable<Integer> applianceCategoryIds,
            UiFilter<AssignedProgram> filter, SortBy sortBy, boolean sortDescending,
            int startIndex, int count);

    public SearchResult<AssignedProgram> filter(int applianceCategoryId,
            UiFilter<AssignedProgram> filter, SortBy sortBy, boolean sortDescending,
            int startIndex, int count);
}
