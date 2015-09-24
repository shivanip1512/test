
package com.cannontech.web.common.resources.data;

import java.util.Map;
/** ResourcePackageList 
*
*   Used for the beanFactory creation of the ResourcePackageList @see resource-bundle-manifest.xml
*   This exposes the ResourceBundleService by dependency injection a list of consumable bundles.
*/
public class ResourcePackageList {
    private Map<Object,ResourceBundle> packages;

    public Map<Object, ResourceBundle> getPackages() {
        return packages;
    }
    public void setPackages(Map<Object, ResourceBundle> packages) {
    
        this.packages = packages;
    }
}

