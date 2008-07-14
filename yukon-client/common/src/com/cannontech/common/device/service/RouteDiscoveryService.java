package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RouteDiscoveryService {

    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, SimpleCallback<Integer> callback, LiteYukonUser user);

}
