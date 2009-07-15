package com.cannontech.common.device.commands.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.spring.YukonSpringHook;

public class VerifyConfigCommandCompletionCallback implements CommandCompletionCallback<CommandRequestDevice> {
    
    Map<YukonDevice, VerifyResult> results = new HashMap<YukonDevice, VerifyResult>();
    
    public VerifyConfigCommandCompletionCallback(List<YukonDevice> devices) {
        initializeResults(devices);
    }
    
    private void initializeResults(List<YukonDevice> devices) {
        MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
        DeviceConfigurationDao deviceConfigurationDao = YukonSpringHook.getBean("deviceConfigurationDao", DeviceConfigurationDao.class);
        for(YukonDevice device : devices) {
            Meter meter = meterDao.getForYukonDevice(device);
            VerifyResult result = new VerifyResult(meter);
            result.setConfig(deviceConfigurationDao.getConfigurationForDevice(device.getDeviceId()));
            results.put(device, result);
        }
    }
    
    @Override
    public void receivedIntermediateError(CommandRequestDevice command, DeviceErrorDescription error) {
        YukonDevice device = command.getDevice();
        results.get(device).getDiscrepancies().add(error.getPorter());
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
        results.get(device).getDiscrepancies().add(error.getPorter());
    }

    @Override
    public void receivedLastResultString(CommandRequestDevice command, String value) {
        addToResults(command.getDevice(), value);
    }

    @Override
    public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
        
    }

    private void addToResults(YukonDevice device, String value) {
        if(value.contains("is NOT current")) {
            results.get(device).getDiscrepancies().add(value);
        } else {
            results.get(device).getMatching().add(value);
        }
    }
    
    public Map<YukonDevice, VerifyResult> getResults(){
        return results;
    }
}