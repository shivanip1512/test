package com.cannontech.amr.device.search.dao;

import java.util.List;

import com.cannontech.amr.device.search.model.DeviceSearchResultEntry;
import com.cannontech.amr.device.search.model.FilterBy;
import com.cannontech.amr.device.search.model.OrderByField;
import com.cannontech.amr.device.search.model.SearchField;
import com.cannontech.common.search.SearchResult;

public interface DeviceSearchDao {
    /**
     * Method used to search for a list of paos
     * 
     * @param filters List of columns and criteria to filter the search with
     * @param orderBy column to order the search by
     * @param start Index of the first pao we want from the sorted, filtered list
     * @param count Number of paos we want from the sorted, filtered list
     * @return A sorted list of paos that match the filter criteria
     */
    SearchResult<DeviceSearchResultEntry> search(List<SearchField> fields, List<FilterBy> filters, OrderByField orderBy, int start, int count);
}
