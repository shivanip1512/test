package com.cannontech.web.contextualMenu.model.menuEntry;

import java.util.Map;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.contextualMenu.CollectionCategory;

public class SingleDeviceMenuEntry extends DeviceMenuEntry {
    
    public SingleDeviceMenuEntry(String baseUrl) {
        this(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, null);
    }

    public SingleDeviceMenuEntry(String baseUrl, YukonRole requiredRole) {
        this(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, requiredRole);
    }

    public SingleDeviceMenuEntry(String baseUrl, YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, null, requiredRoleProperties);
    }

    public SingleDeviceMenuEntry(String baseUrl, YukonRole requiredRole,
                              YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, requiredRole, requiredRoleProperties);
    }

    public SingleDeviceMenuEntry(String baseUrl, String inputParamName,
                              YukonRole requiredRole,
                              YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, inputParamName, inputParamName, requiredRole, requiredRoleProperties);
    }

    public SingleDeviceMenuEntry(String baseUrl, String inputParamName, String outputParamName,
                              YukonRole requiredRole,
                              YukonRoleProperty... requiredRoleProperties) {
        super(baseUrl, inputParamName, outputParamName, requiredRole, requiredRoleProperties);
    }

    /* 
     * This method only supports inputs containing the deviceId that needs to be turned into a single-device-specific url
     * ("deviceId={deviceId}")
     */
    @Override
    public String getUrl(CollectionCategory collectionCategory, Map<String, String> inputs) {
        if (collectionCategory == CollectionCategory.PAO_ID) {
            return baseUrl + outputParamName + "=" + inputs.get(inputParamName);
        }
        throw new RuntimeException("this url doesn't support " + collectionCategory);
    }

}
