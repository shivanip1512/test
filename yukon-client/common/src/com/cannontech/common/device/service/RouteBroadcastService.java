package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RouteBroadcastService {
    
    public void broadcastDetectionCommand(final String command, List<Integer> routeIds, CommandRequestExecutionType type, final CompletionCallback callback, LiteYukonUser user);
    
    public static interface CompletionCallback{
        public void success();
        public void failure(String errorReason);
    }
}
