package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RouteDiscoveryService {

    /**
     * This method takes list of routes and a device, and tries to figure out if the supplied 
     * device is located on any of the supplied routes.  If the device has been found on a 
     * route in the past, this method will try and find the device on that route before trying 
     * the supplied list of routes.
     * 
     * @param device
     * @param routeIds
     * @param callback
     * @param user
     */
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, SimpleCallback<Integer> callback, LiteYukonUser user);

    void routeDiscovery(YukonDevice device, List<Integer> routeIds, SimpleCallback<Integer> routeFoundCallback,
            LiteYukonUser user, String command, CollectionActionResult result);

}
