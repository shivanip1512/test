package com.cannontech.web.contextualMenu.model.menuEntry;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.contextualMenu.CollectionCategory;
import com.google.common.collect.Maps;

public class DeviceCollectionMenuAction extends DeviceMenuAction {
    
    public DeviceCollectionMenuAction(String baseUrl) {
        this(baseUrl, defaultDeviceIdParamName, null);
    }

    public DeviceCollectionMenuAction(String baseUrl, YukonRole requiredRole) {
        this(baseUrl, defaultDeviceIdParamName, requiredRole);
    }

    public DeviceCollectionMenuAction(String baseUrl, YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, null, requiredRoleProperties);
    }
    
    public DeviceCollectionMenuAction(String baseUrl, YukonRole requiredRole,
                                     YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, defaultDeviceIdParamName, requiredRole, requiredRoleProperties);
    }

    public DeviceCollectionMenuAction(String baseUrl, String inputParamName, YukonRole requiredRole,
                                     YukonRoleProperty... requiredRoleProperties) {
        super(baseUrl, true, defaultDeviceIdParamName, defaultDeviceIdParamName, requiredRole, requiredRoleProperties);
    }

    /**
     * Returns a url that is 'safe', with parameters encoded, and escaped for html output.
     * 
     * Either we call this method with inputs containing DEVICE_COLLECTION-specific parameters
     * (things like "collectionType", "idList.ids", etc)
     * or inputs contains the deviceId that needs to be turned into a device collection-specific url
     * ("idList.ids={deviceId}", etc)
     */
    @Override
    public String getUrl(CollectionCategory collectionCategory, HttpServletRequest req) {
        
        Map<String, String> parameters = ServletUtil.getParameterMap(req);
        
        if (collectionCategory == CollectionCategory.PAO_ID) {
            
            String paoId = parameters.get(inputParamName);
            parameters = Maps.newHashMap();
            parameters.put("collectionType", DeviceCollectionType.idList.name());
            parameters.put(DeviceCollectionType.idList.getParameterName("ids"), paoId);
            
        } 
        
        Map<String, String> encodedParameters = ServletUtil.urlEncode(parameters);
        String url = baseUrl + ServletUtil.buildQueryStringFromMap(encodedParameters, true);
        String safeUrl = ServletUtil.createSafeUrl(req, url);
        
        return safeUrl;
    }

}
