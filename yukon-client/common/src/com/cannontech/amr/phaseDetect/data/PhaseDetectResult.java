package com.cannontech.amr.phaseDetect.data;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.util.Completable;
import com.cannontech.enums.Phase;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PhaseDetectResult implements Completable {

    private String errorMsg = "";
    private String key;
    
    private Map<Phase, StoredDeviceGroup> phaseToGroupMap = Maps.newHashMap();
    private Map<SimpleDevice, String> failureGroupMap = Maps.newHashMap();
    private Map<Integer, Map<String, PhaseDetectVoltageReading>> deviceReadingsMap = Maps.newHashMap();
    private CommandRequestExecutionIdentifier commandRequestExecutionIdentifier;
    private CommandCompletionCallbackAdapter<CommandRequestDevice> callback;
    private Map<SimpleDevice, DetectedPhase> detectedPhaseResultMap = Maps.newHashMap();
    private List<SimpleDevice> inputDeviceList = Lists.newArrayList();
    private PhaseDetectData testData;
    
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
        return true;
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

    public Map<Integer, Map<String, PhaseDetectVoltageReading>> getDeviceReadingsMap() {
        return deviceReadingsMap;
    }

    public CommandCompletionCallbackAdapter<CommandRequestDevice> getCallback() {
        return callback;
    }

    public void setCallback(CommandCompletionCallbackAdapter<CommandRequestDevice> callback) {
        this.callback = callback;
    }

    public Map<SimpleDevice, DetectedPhase> getDetectedPhaseResult() {
        return detectedPhaseResultMap;
    }
    
    public void handleDetectResult(SimpleDevice device, DetectedPhase phase){
        detectedPhaseResultMap.put(device, phase);
    }

    public void setInputDeviceList(List<SimpleDevice> devices) {
        this.inputDeviceList = devices;
    }
    
    public List<SimpleDevice> getInputDeviceList(){
        return inputDeviceList;
    }

    public void setTestData(PhaseDetectData phaseDetectData) {
        this.testData = phaseDetectData;
    }

    public PhaseDetectData getTestData() {
        return testData;
    }
}
