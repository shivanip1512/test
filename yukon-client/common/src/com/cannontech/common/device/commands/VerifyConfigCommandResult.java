package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.model.VerifyResult;

public class VerifyConfigCommandResult {
    
    Map<YukonDevice, VerifyResult> verifyResultsMap = new HashMap<YukonDevice, VerifyResult>();
    private List<YukonDevice> successList = new ArrayList<YukonDevice>();
    private List<YukonDevice> failureList = new ArrayList<YukonDevice>();

    public void addResultString(YukonDevice device, String value) {
        verifyResultsMap.get(device).getMatching().add(value);
    }
    
    public void addError(YukonDevice device, String value) {
        List<String> desc = verifyResultsMap.get(device).getDiscrepancies();
        if(value.contains("is NOT current")) {
            String[] words = value.split(" ");
            value = desc.isEmpty() ? words[1] : ", " + words[1];
        }
        desc.add(value);
    }
    
    public void handleSuccess(YukonDevice device) {
        successList.add(device);
    }
    
    public void handleFailure(YukonDevice device) {
        failureList.add(device);
    }
    
    public List<YukonDevice> getFailureList() {
        return failureList;
    }
    
    public List<YukonDevice> getSuccessList() {
        return successList;
    }
    
    public Map<YukonDevice, VerifyResult> getVerifyResultsMap(){
        return verifyResultsMap;
    }
}
