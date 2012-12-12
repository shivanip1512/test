package com.cannontech.amr.device.search.service;

import java.util.List;

import com.cannontech.amr.device.search.model.DeviceSearchCategory;
import com.cannontech.amr.device.search.model.DeviceSearchField;
import com.cannontech.amr.device.search.model.DeviceSearchFilterBy;
import com.cannontech.amr.device.search.model.DeviceSearchOrderBy;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface DeviceSearchService {
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
    
    /**
     * Method used to get the list of fields to display
     * 
     * @param category Category of device
     * @return A list of fields
     */
    List<DeviceSearchField> getFieldsForCategory(DeviceSearchCategory category);
    
    /**
     * Method used to get the list of filters
     * 
     * @param fields Fields for which a filter is required
     * @return A list of filters
     */
    List<DeviceSearchFilterBy> getFiltersForFields(List<DeviceSearchField> fields);
    
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
