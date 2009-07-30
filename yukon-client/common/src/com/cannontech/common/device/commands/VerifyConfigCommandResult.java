package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.model.SimpleDevice;

public class VerifyConfigCommandResult {
    
    Map<SimpleDevice, VerifyResult> verifyResultsMap = new HashMap<SimpleDevice, VerifyResult>();
    private List<SimpleDevice> successList = new ArrayList<SimpleDevice>();
    private List<SimpleDevice> failureList = new ArrayList<SimpleDevice>();

    public void addResultString(SimpleDevice device, String value) {
        verifyResultsMap.get(device).getMatching().add(value);
    }
    
    public void addError(SimpleDevice device, String value) {
        List<String> desc = verifyResultsMap.get(device).getDiscrepancies();
        if(value.contains("is NOT current")) {
            String[] words = value.split(" ");
            value = words[1];
        }
        desc.add(value);
    }
    
    public void handleSuccess(SimpleDevice device) {
        successList.add(device);
    }
    
    public void handleFailure(SimpleDevice device) {
        failureList.add(device);
    }
    
    public List<SimpleDevice> getFailureList() {
        return failureList;
    }
    
    public List<SimpleDevice> getSuccessList() {
        return successList;
    }
    
    public Map<SimpleDevice, VerifyResult> getVerifyResultsMap(){
        return verifyResultsMap;
    }
}
