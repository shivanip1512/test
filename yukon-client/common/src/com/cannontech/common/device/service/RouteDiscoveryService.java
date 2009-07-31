package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RouteDiscoveryService {

    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, SimpleCallback<Integer> callback, LiteYukonUser user);

    public void cancelRouteDiscovery(List<SimpleCallback<Integer>> routeFoundCallbacks, 
                                     final LiteYukonUser user);
}
