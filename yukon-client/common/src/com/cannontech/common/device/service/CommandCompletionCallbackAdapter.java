package com.cannontech.common.device.service;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.core.dynamic.PointValueHolder;

public class CommandCompletionCallbackAdapter<T> implements
        CommandCompletionCallback<T> {

    @Override
    public void complete() {
    }

    @Override
    public void receivedIntermediateError(T command,
            DeviceErrorDescription error) {
    }

    @Override
    public void receivedIntermediateResultString(T command, String value) {
    }

    @Override
    public void receivedLastError(T command, DeviceErrorDescription error) {
    }

    @Override
    public void receivedLastResultString(T command, String value) {
    }

    @Override
    public void receivedValue(T command, PointValueHolder value) {
    }
    
    @Override
    public void cancel() {
    }

}
