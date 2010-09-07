package com.cannontech.yukon.cbc;

import java.util.Map;

import com.cannontech.message.util.Message;
import com.google.common.collect.Maps;

public class DynamicCommand extends Message {
    
    public enum Parameter {
        DEVICE_ID,
        POINT_ID,
        POINT_RESPONSE_DELTA
    }
    
    public enum CommandType {
        UNDEFINED,
        DELTA
    }
    
    private Map<Parameter,Integer> intParameters = Maps.newHashMap();
    private Map<Parameter,Double> doubleParameters = Maps.newHashMap();
    
    private CommandType commandType;
    
    public DynamicCommand(CommandType commandType) {
        this.commandType = commandType;
    }
    
    
    //TODO Based on command type. Only allow certain parameters to be inserted without exception.
    public void addParameter(Parameter parameter, int value) {
        intParameters.put(parameter, value);
    }
    
    public void addParameter(Parameter parameter, double value) {
        doubleParameters.put(parameter, value);
    } 
    
    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public Map<Parameter, Integer> getIntParameters() {
        return intParameters;
    }

    public Map<Parameter, Double> getDoubleParameters() {
        return doubleParameters;
    }
}
