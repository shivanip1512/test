package com.cannontech.amr.phaseDetect.data;

import java.util.Map;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.Completable;
import com.google.common.collect.Maps;

public class PhaseDetectResult implements Completable {

    private String errorMsg = "";
    private String key;
    
    private Map<Phase, StoredDeviceGroup> phaseToGroupMap = Maps.newHashMap();
    private Map<SimpleDevice, String> failureGroupMap = Maps.newHashMap();
    private Map<Integer, Map<String, String>> deviceReadingsMap = Maps.newHashMap();
    private CommandRequestExecutionIdentifier commandRequestExecutionIdentifier;
    
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public CommandRequestExecutionIdentifier getCommandRequestExecutionIdentifier() {
        return commandRequestExecutionIdentifier;
    }
    
    public void setCommandRequestExecutionIdentifier(CommandRequestExecutionIdentifier commandRequestExecutionIdentifier) {
        this.commandRequestExecutionIdentifier = commandRequestExecutionIdentifier;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    public Map<Phase, StoredDeviceGroup> getPhaseToGroupMap() {
        return phaseToGroupMap;
    }

    public void setPhaseToGroupMap(Map<Phase, StoredDeviceGroup> phaseToGroupMap) {
        this.phaseToGroupMap = phaseToGroupMap;
    }

    public Map<SimpleDevice, String> getFailureGroupMap() {
        return failureGroupMap;
    }

    public Map<Integer, Map<String, String>> getDeviceReadingsMap() {
        return deviceReadingsMap;
    }
    
}
