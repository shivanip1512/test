package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.device.YukonDevice;

public interface RouteDiscoveryService {

    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, Boolean doPutconfig) throws Exception;
}
