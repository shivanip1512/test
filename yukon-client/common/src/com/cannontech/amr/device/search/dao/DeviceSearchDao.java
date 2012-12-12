package com.cannontech.amr.device.search.dao;

import java.util.List;

import com.cannontech.amr.device.search.model.DeviceSearchCategory;
import com.cannontech.amr.device.search.model.DeviceSearchFilterBy;
import com.cannontech.amr.device.search.model.DeviceSearchOrderBy;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface DeviceSearchDao {
    /**
     * Method used to search for a list of paos
     * 
     * @param category Category of device to search for
     * @param filterBy List of columns and criteria to filter the search with
     * @param orderBy column to order the search by
     * @param orderByDescending ordering direction
     * @param start Index of the first pao we want from the sorted, filtered list
     * @param count Number of paos we want from the sorted, filtered list
     * @return A sorted list of paos that match the filter criteria
     */
    SearchResult<LiteYukonPAObject> search(DeviceSearchCategory category, List<DeviceSearchFilterBy> filterBy, DeviceSearchOrderBy orderBy, Boolean orderByDescending, int start, int count);
}
