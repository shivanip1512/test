
package com.cannontech.web.common.resources.service;

import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.data.ResourceType;
import com.cannontech.web.common.resources.service.data.ResourceBundleFilterServiceType;

public interface PackageFilterService {
    
    /**
     * validateResourceBundle As the name suggests, make sure the model {@link ResourceBundle} doesn't have any syntax errors, or 
     * cruft that doesn't belong there, before the filter is applied. 
     * 
     * @see {@link ResourceBundleFilterServiceType}
     * @see {@link ResourceBundle}
     * @see {@link ResourceType}
     *
     * @param bundle - the model {@link ResourceBundle} to be processed.
     * @exception {@link Exception} Anything that causes the process to flunk. 
     * @return void
     */
    void validateResourceBundle(ResourceBundle bundle) throws Exception;
    
    /**
     * processResourceBundle As the name suggests, apply a filter to the model {@link ResourceBundle}.
     * 
     * @see {@link ResourceBundleFilterServiceType}
     * @see {@link ResourceBundle}
     * @see {@link ResourceType}
     *
     * @param bundle - the model {@link ResourceBundle} to be processed.
     * @exception {@link Exception} Anything that causes the process to flunk. 
     * @return void
     */    
    
    void processResourceBundle(ResourceBundle bundle) throws Exception;
    
    /**
     * getFitlerType retrieve the {@link ResourceBundleFilterServiceType} described for this filter service interface.
     * 
     * @see {@link ResourceBundleFilterServiceType}
     * @see {@link ResourceBundle}
     * @see {@link ResourceType}
     * @return ResourceBundleFilterServiceType
     */    
    ResourceBundleFilterServiceType getFilterType();
}

