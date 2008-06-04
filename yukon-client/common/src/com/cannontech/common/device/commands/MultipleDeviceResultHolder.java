package com.cannontech.common.device.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dynamic.PointValueHolder;

public interface MultipleDeviceResultHolder {

    public Set<YukonDevice> getAllDevices();
    
    public Set<YukonDevice> getSuccessfulDevices();
    
    public Set<YukonDevice> getFailedDevices();

    public boolean isSuccessful(YukonDevice device);

    public boolean isUnsuccessful(YukonDevice device);
    
    public List<PointValueHolder> getValues(YukonDevice device);
    
    public Map<YukonDevice, List<PointValueHolder>> getValues();

    public boolean isComplete();

    public Map<YukonDevice, DeviceErrorDescription> getErrors();

    public Map<YukonDevice, String> getResultStrings();

}