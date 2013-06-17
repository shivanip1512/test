package com.cannontech.message.capcontrol.model;

import java.util.Map;

import com.google.common.collect.Maps;

public class DynamicCommand extends CapControlCommand {
	
    public enum Parameter implements SerializableByIdentifier {
        DEVICE_ID(0),
        POINT_ID(1),
        POINT_RESPONSE_DELTA(2),
        POINT_RESPONSE_STATIC_DELTA(3);
        
        private int parameterId;
        
        private Parameter(int parameterId) {
        	this.parameterId = parameterId;
        }
        
        public int getSerializeId() {
        	return parameterId;
        }
    }
    
    public enum DynamicCommandType implements SerializableByIdentifier {
        UNDEFINED(0),
        DELTA(1);
        
        private int commandTypeId;
        
        private DynamicCommandType(int commandTypeId) {
        	this.commandTypeId = commandTypeId;
        }
        
        public int getSerializeId() {
        	return commandTypeId;
        }
    }
    
    private Map<Parameter,Integer> intParameters = Maps.newHashMap();
    private Map<Parameter,Double> doubleParameters = Maps.newHashMap();
    
    private DynamicCommandType dynamicCommandType;
    
    public DynamicCommand(DynamicCommandType dynamicCommandType) {
        this.dynamicCommandType = dynamicCommandType;
    }
    
    public void addParameter(Parameter parameter, int value) {
        intParameters.put(parameter, value);
    }
    
    public void addParameter(Parameter parameter, double value) {
        doubleParameters.put(parameter, value);
    } 
    
    public DynamicCommandType getCommandType() {
        return dynamicCommandType;
    }

    public void setCommandType(DynamicCommandType dynamicCommandType) {
        this.dynamicCommandType = dynamicCommandType;
    }

    public Map<Parameter, Integer> getIntParameters() {
        return intParameters;
    }

    public Map<Parameter, Double> getDoubleParameters() {
        return doubleParameters;
    }
}
