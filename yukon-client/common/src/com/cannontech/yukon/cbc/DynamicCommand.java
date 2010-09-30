package com.cannontech.yukon.cbc;

import java.util.Map;

import com.cannontech.message.util.Message;
import com.google.common.collect.Maps;

public class DynamicCommand extends Message {
	
    public enum Parameter implements SerializableObject {
        DEVICE_ID(0),
        POINT_ID(1),
        POINT_RESPONSE_DELTA(2);
        
        private int parameterId;
        
        private Parameter(int parameterId) {
        	this.parameterId = parameterId;
        }
        
        public int getSerializeId() {
        	return parameterId;
        }
    }
    
    public enum CommandType implements SerializableObject {
        UNDEFINED(0),
        DELTA(1);
        
        private int commandTypeId;
        
        private CommandType(int commandTypeId) {
        	this.commandTypeId = commandTypeId;
        }
        
        public int getSerializeId() {
        	return commandTypeId;
        }
    }
    
    private Map<Parameter,Integer> intParameters = Maps.newHashMap();
    private Map<Parameter,Double> doubleParameters = Maps.newHashMap();
    
    private CommandType commandType;
    
    public DynamicCommand(CommandType commandType) {
        this.commandType = commandType;
    }
    
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
