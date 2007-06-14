package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public abstract class CollectingCommandCompletionCallback implements
        CommandCompletionCallback, CommandResultHolder {
    private List<DeviceErrorDescription> errors;
    private List<PointValueHolder> values;

    public void receivedError(DeviceErrorDescription error) {
        errors.add(error);
    }

    public void receivedValue(PointValueHolder value) {
        values.add(value);
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

}
