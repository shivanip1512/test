package com.cannontech.common.device.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.model.SimpleDevice;

public class VerifyConfigCommandResult {
    
    private Map<SimpleDevice, VerifyResult> verifyResultsMap = new HashMap<>();
    
    public VerifyConfigCommandResult(Map<SimpleDevice, VerifyResult> verifyResultsMap) {
        this.verifyResultsMap = verifyResultsMap;
    }
    
    public VerifyConfigCommandResult() {
    }
    
    public void addResultString(SimpleDevice device, String value) {
        verifyResultsMap.get(device).getMatching().add(value);
    }
    
    public void addDiscrepancy(SimpleDevice device, String value) {
        List<String> desc = verifyResultsMap.get(device).getDiscrepancies();
        if(value.contains("is NOT current")) {
            // Porter's verify result string contains category mismatch summaries of the form
            //   Config CATEGORYNAME is NOT current
            String[] words = value.split(" ");
            //  Grab CATEGORYNAME out of the category summary message
            value = words[1];
        }
        desc.add(value);
    }
      
    public void setError(SimpleDevice device, DeviceError error) {
        verifyResultsMap.get(device).setError(error);
    }
    
    public Map<SimpleDevice, VerifyResult> getVerifyResultsMap(){
        return verifyResultsMap;
    }
}
