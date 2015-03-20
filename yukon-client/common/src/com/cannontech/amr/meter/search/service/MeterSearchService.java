package com.cannontech.amr.meter.search.service;

import java.util.List;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;

/**
 * Service class for meter search
 */
public interface MeterSearchService {

    /**
     * Method used to search for a list of paos.
     * @param filters - List of columns and criteria to filter the search with.
     * @param orderBy - List of columns to order the search by.
     * @param paging - Used to determine where to start and how many to return in the resulting list.
     * @return A sorted list of paos that match the filter criteria.
     */
    SearchResults<YukonMeter> search(List<FilterBy> filters, MeterSearchOrderBy orderBy, PagingParameters paging);
    
}