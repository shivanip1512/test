package com.cannontech.common.device.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandRequestRouteAndDevice;
import com.cannontech.common.device.commands.impl.CommandRequestRouteAndDeviceExecutorImpl;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.database.data.lite.LiteYukonUser;

public class RouteBroadcastServiceImpl implements RouteBroadcastService{
    
    CommandRequestRouteAndDeviceExecutorImpl executor;
    
    @Override
    public void broadcastCommand(final String command, List<Integer> routeIds, CommandRequestExecutionType type, final RouteBroadcastService.CompletionCallback callback, LiteYukonUser user) {
        
        ObjectMapper<Integer, CommandRequestRouteAndDevice> objectMapper = new ObjectMapper<Integer, CommandRequestRouteAndDevice>() {
            public CommandRequestRouteAndDevice map(Integer from) throws ObjectMappingException {
                return buildRequest(from, command);
            }
        };
        
        CommandCompletionCallbackAdapter<CommandRequestRouteAndDevice> dummyCallback = new CommandCompletionCallbackAdapter<CommandRequestRouteAndDevice>() {
            private boolean failed = false;
            private String errorMsg = "";
            @Override
            public void processingExceptionOccured(String reason) {
                errorMsg = reason;
                failed = true;
            }
            @Override
            public void receivedLastError(CommandRequestRouteAndDevice command, DeviceErrorDescription error) {
                errorMsg = error.getPorter();
                failed = true;
            }
            
            @Override
            public void complete() {
                if (failed) {
                    callback.failure(errorMsg);
                } else {
                    callback.success();
                }
            }
        };
        
        List<CommandRequestRouteAndDevice> commands = new MappingList<Integer, CommandRequestRouteAndDevice>(routeIds, objectMapper);
        
        executor.execute(commands, dummyCallback, type, user);
        
    }
    
    private CommandRequestRouteAndDevice buildRequest(Integer routeId, String command) {
        CommandRequestRouteAndDevice request = new CommandRequestRouteAndDevice();
        request.setDevice(new SimpleDevice(0, PaoType.SYSTEM));
        request.setRouteId(routeId);
        
        String commandStr = command;
        request.setCommand(commandStr);
        return request;
    }
    
    @Autowired
    public void setCommandRequestRouteAndDeviceExecutor(CommandRequestRouteAndDeviceExecutorImpl executor) {
        this.executor = executor;
    }

}
