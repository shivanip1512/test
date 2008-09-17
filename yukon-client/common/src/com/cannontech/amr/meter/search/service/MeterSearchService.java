package com.cannontech.amr.meter.search.service;

import java.util.List;

import com.cannontech.amr.meter.search.model.ExtendedMeter;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.OrderBy;
import com.cannontech.common.search.SearchResult;

/**
 * Service class for meter search
 */
public interface MeterSearchService {

    /**
     * Method used to search for a list of paos
     * @param filterByList - List of columns and criteria to filter the search with
     * @param orderBy - List of columns to order the search by
     * @param start - Index of the first pao we want from the sorted, filtered list
     * @param count - Number of paos we want from the sorted, filtered list
     * @return A sorted list of paos that match the filter criteria
     */
    public SearchResult<ExtendedMeter> search(List<FilterBy> filterByList, OrderBy orderBy, int start,
            int count);

}
