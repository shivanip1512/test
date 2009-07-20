package com.cannontech.common.device.commands.impl;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.core.dynamic.PointValueHolder;

public class VerifyConfigCommandCompletionCallback implements CommandCompletionCallback<CommandRequestDevice> {
    
    VerifyConfigCommandResult results = new VerifyConfigCommandResult();
    
    public VerifyConfigCommandCompletionCallback(Iterable<? extends YukonDevice> devices) {
        results.initializeResults(devices);
    }
    
    @Override
    public void receivedIntermediateError(CommandRequestDevice command, DeviceErrorDescription error) {
        YukonDevice device = command.getDevice();
        results.getResults().get(device).getDiscrepancies().add(error.getPorter());
    }

    @Override
    public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
        addToResults(command.getDevice(), value);
    }

    @Override
    final public void complete() {}

    @Override
    public void cancel() {}

    @Override
    public void processingExceptionOccured(String reason) {}

    @Override
    public void receivedLastError(CommandRequestDevice command, DeviceErrorDescription error) {
        YukonDevice device = command.getDevice();
        results.getResults().get(device).getDiscrepancies().add(error.getPorter());
        results.handleFailure(device);
    }

    @Override
    public void receivedLastResultString(CommandRequestDevice command, String value) {
        YukonDevice device = command.getDevice();
        addToResults(device, value);
        if(results.getResults().get(device).getDiscrepancies().isEmpty()) {
            results.handleSuccess(device);
        }else {
            results.handleFailure(device);
        }
    }

    @Override
    public void receivedValue(CommandRequestDevice command, PointValueHolder value) {}

    private void addToResults(YukonDevice device, String value) {
        if(value.contains("is NOT current")) {
            results.getResults().get(device).getDiscrepancies().add(value);
        } else {
            results.getResults().get(device).getMatching().add(value);
        }
    }
    
    public VerifyConfigCommandResult getResults(){
        return results;
    }
    
}