
package com.cannontech.web.common.resources.service;
import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.data.ResourceType;
import com.cannontech.web.common.resources.service.error.*;
import com.cannontech.web.common.resources.service.impl.ResourceBundleServiceImpl;

public interface ResourceBundleService {
    
    /**
     * getResourceBundle Get a {@link ResourceBundle} by specific string identifier and {@link ResourceType}
     * <p>
     * This is the main of the service. Its purpose is to expose the retrieved resource, apply encoding via
     * {@link #combineResourceContent(ResourceBundle)} and then apply filtering through 
     * {@link #processFilterList(ResourceBundle)}.
     * 
     * @see {@link ResourceBundleServiceImpl}
     * @see {@link ResourceBundle}
     * @see {@link ResourceType}
     * @see {@link PackageFilterService}
     *
     * @param packageName - the name of the package request minus the exention ~i.e. .js, .css ext.
     * @param type - {@ResourceType}.
     * @throws ResourceBundleException 
     * @return ResourceBundle
     */
    ResourceBundle getResourceBundle(String packageName, ResourceType type) throws ResourceBundleException;
    
    /**
     * combineResourceContent - Combine the list of files described by bean factory of the model ResourceBundle - 
     * file resource-bundle-manifest.xml These files are intent to be encoded by the specified type {@link ResourceType} 
     * and it is assumed the files are of the same type. When the process is done, the 
     * {@link ResourceBundle#setResourceResult(String)} is called to fill the model with the result. This is done at runtime 
     * by request from the client.  
     * @see {@link ResourceBundle}
     * @see {@link ResourceType}
     * @param resource - the model {@link ResourceBundle} to be consumed.
     * @throws ResourceBundleException
     * @return void
     */
    void combineResourceContent(ResourceBundle resource) throws ResourceBundleException;
    
    /**
     * processFilterList - Apply a transform to the model {@link ResourceBundle} listed by bean factory
     * List<{@link PackageFilterService}> - file resource-bundle-manifest.xml. The filter does anything from parsing Less
     * to Css, CSS compression, javascript compression and linting. 
     * Once it is done, it stores the result in {@link ResourceBundle#setResourceResult(String)}
     * @see {@link ResourceBundle}
     * @see {@link PackageFilterService}
     * @param resource - the model {@link ResourceBundle} to be consumed.
     * @return void
     * @throws PackageResourceFilterException 
     */
    void processFilterList(ResourceBundle resource) throws PackageResourceFilterException;

}

