package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public abstract class CollectingCommandCompletionCallback implements
        CommandCompletionCallback, CommandResultHolder {
    private List<DeviceErrorDescription> errors = new ArrayList<DeviceErrorDescription>();
    private List<PointValueHolder> values = new ArrayList<PointValueHolder>();
    private List<String> resultStrings = new ArrayList<String>();

    public void receivedError(DeviceErrorDescription error) {
        errors.add(error);
    }

    public void receivedValue(PointValueHolder value) {
        values.add(value);
    }
    
    public void receivedResultString(String resultString) {
        resultStrings.add(resultString);
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

}
