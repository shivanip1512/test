package com.cannontech.stars.dr.appliance.service;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;

public interface AssignedProgramService {
    
    /**
     * Get search results for a collection of appliance categories. 
     * If paging is null, the results will not be paged.
     * If sorting is null, results are sorted by program order, descending.
     */
    SearchResults<AssignedProgram> filter(Iterable<Integer> categoryIds,
            UiFilter<AssignedProgram> filter, SortingParameters sorting, PagingParameters paging);
    
    /**
     * Get search results for a single appliance category. 
     * If paging is null, the results will not be paged.
     * If sorting is null, results are sorted by program order, descending.
     */
    SearchResults<AssignedProgram> filter(int categoryId,
            UiFilter<AssignedProgram> filter, SortingParameters sorting, PagingParameters paging);
    
}