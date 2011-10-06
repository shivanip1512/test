package com.cannontech.common.device.commands;

public interface CommandCallback {

    /**
     * This method creates a command using the parameterDto that that can be used by porter.
     */
    public String generateCommand(CommandRequestExecutionParameterDto parameterDto);
    
}