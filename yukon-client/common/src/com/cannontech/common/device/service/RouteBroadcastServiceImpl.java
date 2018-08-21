package com.cannontech.common.device.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestRouteAndDevice;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Route;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.database.data.lite.LiteYukonUser;

public class RouteBroadcastServiceImpl implements RouteBroadcastService{
    
    @Autowired private CommandExecutionService executionService;
    
    @Override
    public void broadcastCommand(final String command, List<Route> routes, DeviceRequestType type, final RouteBroadcastService.CompletionCallback callback, LiteYukonUser user) {
        
        ObjectMapper<Route, CommandRequestRouteAndDevice> objectMapper = new ObjectMapper<Route, CommandRequestRouteAndDevice>() {
            @Override
            public CommandRequestRouteAndDevice map(Route from) throws ObjectMappingException {
                return new CommandRequestRouteAndDevice(command , new SimpleDevice(0, PaoType.SYSTEM), from.getId());
            }
        };
        
        CommandCompletionCallback<CommandRequestRouteAndDevice> dummyCallback = new CommandCompletionCallback<CommandRequestRouteAndDevice>() {
            private boolean failed = false;
            private String errorMsg = "";
            @Override
            public void processingExceptionOccurred(String reason) {
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
        
        List<CommandRequestRouteAndDevice> commands = new MappingList<>(routes, objectMapper);
          
        executionService.execute(commands, dummyCallback, type, user);

    }
}
