package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.model.Route;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RouteBroadcastService {
    
    public void broadcastCommand(final String command, List<Route> routeIds, DeviceRequestType type, final CompletionCallback callback, LiteYukonUser user);
    
    public static interface CompletionCallback{
        public void success();
        public void failure(String errorReason);
    }

}
