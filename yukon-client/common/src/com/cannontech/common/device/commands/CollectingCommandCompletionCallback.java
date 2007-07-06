package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public abstract class CollectingCommandCompletionCallback implements
        CommandCompletionCallback, CommandResultHolder {
    private List<DeviceErrorDescription> errors = new ArrayList<DeviceErrorDescription>();
    private List<PointValueHolder> values = new ArrayList<PointValueHolder>();
    private List<String> lastResultStrings = new ArrayList<String>();
    private List<String> resultStrings = new ArrayList<String>();

    public void receivedError(DeviceErrorDescription error) {
        errors.add(error);
    }

    public void receivedValue(PointValueHolder value) {
        values.add(value);
    }
    
    public void receivedResultString(String value) {
        resultStrings.add(value);
    }
    
    public void receivedLastResultString(String resultString) {
        lastResultStrings.add(resultString);
    }
    
    public boolean isErrorsExist() {
        return !errors.isEmpty();
    }

    public List<DeviceErrorDescription> getErrors() {
        return errors;
    }

    public List<PointValueHolder> getValues() {
        return values;
    }
    
    public List<String> getResultStrings() {
        return resultStrings;
    }
    
    public List<String> getLastResultStrings() {
        return lastResultStrings;
    }
    
    public String getLastResultString() {
        
        if(lastResultStrings.size() > 0){
            return lastResultStrings.get(lastResultStrings.size() - 1);
        }
        
        return "";
    }

}
