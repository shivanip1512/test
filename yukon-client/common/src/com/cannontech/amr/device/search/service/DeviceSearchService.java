package com.cannontech.amr.device.search.service;

import java.util.List;

import com.cannontech.amr.device.search.model.DeviceSearchCategory;
import com.cannontech.amr.device.search.model.DeviceSearchResultEntry;
import com.cannontech.amr.device.search.model.FilterBy;
import com.cannontech.amr.device.search.model.OrderByField;
import com.cannontech.amr.device.search.model.SearchField;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface DeviceSearchService {
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

    /**
     * Method used to get the list of fields to display
     * 
     * @param category Category of device
     * @return A list of fields
     */
    List<SearchField> getFieldsForCategory(DeviceSearchCategory category);

    /**
     * Method used to get the filter for the specified category
     * 
     * @param category Category of device to get filters for for
     * @return A list of filters
     */
    FilterBy getFiltersForCategory(DeviceSearchCategory category);

    /**
     * Method user to get a pao
     * 
     * @param deviceId The id of the device
     * @return A pao
     */
    LiteYukonPAObject getDevice(int deviceId);

    /**
     * Method used to get the category of a device
     * 
     * @param deviceId The id of the device
     * @return A device category
     */
    DeviceSearchCategory getDeviceCategory(int deviceId);

    /**
     * Method used to see if meter detail display is supported
     * 
     * @param lPao the pao
     * @return if the display is supported or not
     */
    boolean isMeterDetailsSupported(LiteYukonPAObject lPao);
}
