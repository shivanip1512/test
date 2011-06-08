package com.cannontech.common.device.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestRouteAndDevice;
import com.cannontech.common.device.commands.impl.CommandRequestRouteAndDeviceExecutorImpl;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Route;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.database.data.lite.LiteYukonUser;

public class RouteBroadcastServiceImpl implements RouteBroadcastService{
    
    CommandRequestRouteAndDeviceExecutorImpl executor;
    
    @Override
    public void broadcastCommand(final String command, List<Route> routes, DeviceRequestType type, final RouteBroadcastService.CompletionCallback callback, LiteYukonUser user) {
        
        ObjectMapper<Route, CommandRequestRouteAndDevice> objectMapper = new ObjectMapper<Route, CommandRequestRouteAndDevice>() {
            public CommandRequestRouteAndDevice map(Route from) throws ObjectMappingException {
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
            public void receivedLastError(CommandRequestRouteAndDevice command, SpecificDeviceErrorDescription error) {
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
        
        List<CommandRequestRouteAndDevice> commands = new MappingList<Route, CommandRequestRouteAndDevice>(routes, objectMapper);
        
        executor.execute(commands, dummyCallback, type, user);
        
    }
    
    private CommandRequestRouteAndDevice buildRequest(Route route, String command) {
        CommandRequestRouteAndDevice request = new CommandRequestRouteAndDevice();
        request.setDevice(new SimpleDevice(0, PaoType.SYSTEM));
        request.setRouteId(route.getId());
        
        String commandStr = command;
        request.setCommand(commandStr);
        return request;
    }
    
    @Autowired
    public void setCommandRequestRouteAndDeviceExecutor(CommandRequestRouteAndDeviceExecutorImpl executor) {
        this.executor = executor;
    }

}
