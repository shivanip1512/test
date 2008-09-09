package com.cannontech.common.device.commands;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CommandCompletionCallback<T> {
    public void receivedValue(T command, PointValueHolder value);
    public void receivedIntermediateError(T command, DeviceErrorDescription error);
    public void receivedLastError(T command, DeviceErrorDescription error);
    public void receivedIntermediateResultString(T command, String value);
    public void receivedLastResultString(T command, String value);
    public void complete();
    public void cancel();
}