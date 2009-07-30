package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RouteDiscoveryService {

    public void routeDiscovery(SimpleDevice device, List<Integer> routeIds, SimpleCallback<Integer> callback, LiteYukonUser user);

    public void cancelRouteDiscovery(List<SimpleCallback<Integer>> routeFoundCallbacks, 
                                     final LiteYukonUser user);
}
