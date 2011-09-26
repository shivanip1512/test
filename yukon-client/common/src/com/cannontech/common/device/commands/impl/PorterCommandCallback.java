package com.cannontech.common.device.commands.impl;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.commands.CommandCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionParameterDto;

public class PorterCommandCallback implements CommandCallback {

    private final String commandString;

    public PorterCommandCallback(String commandString){
        this.commandString = commandString;
    }
    
    @Override
    public String generateCommand(CommandRequestExecutionParameterDto parameterDto) {
        String generatedCommand = commandString;
        
        if (parameterDto.isNoqueue() && !StringUtils.containsIgnoreCase(commandString, " noqueue")) {
            generatedCommand += " noqueue";
        }
        
        if (parameterDto.isUpdate() && !StringUtils.containsIgnoreCase(commandString, " update")) {
            generatedCommand += " update";
        }
        
        return generatedCommand;
    }
    
    @Override
    public String toString() {
        return commandString;
    }
    
}