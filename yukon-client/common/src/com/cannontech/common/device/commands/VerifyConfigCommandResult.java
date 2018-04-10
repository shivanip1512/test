package com.cannontech.common.device.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.model.SimpleDevice;

public class VerifyConfigCommandResult {
    
    Map<SimpleDevice, VerifyResult> verifyResultsMap = new HashMap<>();

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
      
    public Map<SimpleDevice, VerifyResult> getVerifyResultsMap(){
        return verifyResultsMap;
    }
}
