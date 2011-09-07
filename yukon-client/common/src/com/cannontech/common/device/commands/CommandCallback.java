package com.cannontech.common.device.commands;

public interface CommandCallback {

    /**
     * 
     * @param parameterDto
     * @return
     */
    public String generateCommand(CommandRequestExecutionParameterDto parameterDto);
    
}