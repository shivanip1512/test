package com.cannontech.common.device.routeLocate;

import java.util.LinkedHashMap;
import java.util.List;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.user.YukonUserContext;

public interface RouteLocationService {

    int locate(LinkedHashMap<String, String> inputs, DeviceCollection deviceCollection, List<Integer> routeIds, boolean autoUpdateRoute, String command,
            SimpleCallback<CollectionActionResult> alertCallback, YukonUserContext context);
    
    List<DeviceRouteLocation> getLocations(int cacheKey);
}
