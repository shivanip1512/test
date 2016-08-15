
package com.cannontech.web.common.mapping.service;

public interface MappingService {
    
    /**
     * Gets the Mapping Url with key to use with the specified view type
     *
     * @param viewType - the view type to get the mapping url for (STREET, SATELLITE)
     * @return String - the Mapping Url for the specified view type
     */
    String getMappingUrl(String viewType);

}

