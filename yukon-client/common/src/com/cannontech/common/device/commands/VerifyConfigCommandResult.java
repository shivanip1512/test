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
        if(value.contains("is NOT current")) {
            verifyResultsMap.get(device).getDiscrepancies().add(value);
        } else {
            verifyResultsMap.get(device).getMatching().add(value);
        }
    }
    
    public void addError(YukonDevice device, String value) {
        verifyResultsMap.get(device).getDiscrepancies().add(value);
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
