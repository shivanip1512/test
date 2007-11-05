package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandRequestExecutor {
    
    public CommandResultHolder execute(YukonDevice device, String command, LiteYukonUser user) throws Exception;

    public CommandResultHolder execute(CommandRequest command, LiteYukonUser user) throws CommandCompletionException, PaoAuthorizationException ;
    
    public CommandResultHolder execute(List<CommandRequest> commands, LiteYukonUser user) throws CommandCompletionException, PaoAuthorizationException ;

    public void execute(List<CommandRequest> commands, CommandCompletionCallback callback, LiteYukonUser user) throws PaoAuthorizationException;
}
