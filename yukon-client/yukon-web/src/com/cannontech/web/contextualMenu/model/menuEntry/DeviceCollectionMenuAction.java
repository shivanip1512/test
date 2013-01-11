package com.cannontech.web.contextualMenu.model.menuEntry;

import java.util.Map;

import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
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
        super(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, requiredRole, requiredRoleProperties);
    }

    /*
     * Either we call this method with inputs containing DEVICE_COLLECTION-specific input parameters
     * (things like "collectionType", "idList.ids", etc)
     * or inputs contains the deviceId that needs to be turned into a device collection-specific url
     * ("idList.ids={deviceId}", etc)
     */
    @Override
    public String getUrl(CollectionCategory collectionCategory, Map<String, String> inputs) {
        Map<String, String> queryParams = null;
        if (collectionCategory == CollectionCategory.DEVICE_COLLECTION) {
            queryParams = inputs;
            // build device collection
            // we need to escape this at some point, too (not doing this yet)
            // also add the request into it ServletUtil.createSafeUrl(request, url);
            return baseUrl + ServletUtil.buildQueryStringFromMap(inputs, false);

        } else if (collectionCategory == CollectionCategory.PAO_ID) {
            queryParams = Maps.newHashMap();
            queryParams.put("collectionType", DeviceCollectionType.idList.name());
            queryParams.put(DeviceCollectionType.idList.getParameterName("ids"), inputs.get(inputParamName));
            // build device collection
            // we need to escape this at some point, too (not doing this yet)
            // also add the request into it ServletUtil.createSafeUrl(request, url);
        } else {
            throw new RuntimeException("this url doesn't support " + collectionCategory);
        }
        return baseUrl + ServletUtil.buildQueryStringFromMap(queryParams, false);
    }

}
