package com.cannontech.web.contextualMenu.model.menuEntry;

import java.util.Map;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.contextualMenu.CollectionCategory;

public abstract class DeviceMenuEntry extends BaseMenuEntry {
    
    protected final static String defaultDeviceIdParamName = "deviceId";
    
    protected String inputParamName;
    protected String outputParamName;

    public DeviceMenuEntry(String baseUrl, String inputParamName, String outputParamName,
                           YukonRole requiredRole,
                           YukonRoleProperty... requiredRoleProperties) {
        super(baseUrl, requiredRole, requiredRoleProperties);
        this.inputParamName = inputParamName;
        this.outputParamName = outputParamName;
    }

    public Integer getDeviceId(CollectionCategory collectionCategory, Map<String, String> inputs) {
        if (collectionCategory == CollectionCategory.PAO_ID) {
            return Integer.valueOf(inputs.get(inputParamName));
        }
        return null;
    }
}
