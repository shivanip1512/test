package com.cannontech.common.device.commands.impl;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.commands.CommandCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionParameterDto;

public class CommandCallbackBase implements CommandCallback {

    private final String commandString;

    public CommandCallbackBase(String commandString){
        this.commandString = commandString;
    }
    
    @Override
    public String generateCommand(CommandRequestExecutionParameterDto parameterDto) {
        String generatedCommand = commandString;
        
        if (parameterDto.isNoqueue() && !StringUtils.containsIgnoreCase(commandString, " noqueue")) {
            generatedCommand += " noqueue";
        }
        
        return generatedCommand;
    }
    
    @Override
    public String toString() {
        return commandString;
    }
    
}