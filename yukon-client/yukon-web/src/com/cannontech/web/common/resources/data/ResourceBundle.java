
package com.cannontech.web.common.resources.data;

import java.util.List;
import org.joda.time.DateTime;
import com.cannontech.web.common.resources.service.data.ResourceBundleFilterServiceType;

/** ResourceBundle 
*
*   ResouceBundle is a model used to consume a list of resource files that are to be serialized for an asset pipeline to
*   a browser. Even if the browser has cache-control,in possible absense the model has a TTL that will let the embeded CACHE 
*   to look up the model in case there are any changes.
*   The model is specific to a resourceType, so no mixing is allowed at this time until there is a suitable client parser to
*   consume a mixed package of filetypes. It has also included a list for Filter instructions for pre-process/final transform
*   before delivery to the browser as a response. 
*/
public class ResourceBundle {
    
    private String name;
    private DateTime timestamp;
    private Long TTL;
    private ResourceType resourceType;
    private List<String> resourcesPathList;
    private String resourceResult;
    private List<ResourceBundleFilterServiceType> filterServiceList;
    private boolean defaultLessPackage;
    
    public String getName() {
    
        return name;
    }
    public void setName(String name) {
    
        this.name = name;
    }
    public DateTime getTimestamp() {
    
        return timestamp;
    }
    public void setTimestamp(DateTime timestamp) {
    
        this.timestamp = timestamp;
    }
    public ResourceType getResourceType() {
    
        return resourceType;
    }
    public void setResourceType(ResourceType resourceType) {
    
        this.resourceType = resourceType;
    }
    public List<String> getResourcesPathList() {
    
        return resourcesPathList;
    }
    public void setResourcesPathList(List<String> resourcesPathList) {
    
        this.resourcesPathList = resourcesPathList;
    }
    public String getResourceResult() {
    
        return resourceResult;
    }
    public void setResourceResult(String resourceResult) {
    
        this.resourceResult = resourceResult;
    }
    public Long getTTL() {
        
        return TTL;
    }
    public void setTTL(Long TTL) {
    
        this.TTL = TTL;
    }
    public List<ResourceBundleFilterServiceType> getFilterServiceList() {
        
        return filterServiceList;
    }
    public void setFilterServiceList(List<ResourceBundleFilterServiceType> filterServiceList) {
    
        this.filterServiceList = filterServiceList;
    }
    public boolean isDefaultLessPackage() {
        
        return defaultLessPackage;
        
    }
    public void setDefaultLessPackage(boolean defaultLessPackage) {
        
        this.defaultLessPackage = defaultLessPackage;
        
    }
 
}

