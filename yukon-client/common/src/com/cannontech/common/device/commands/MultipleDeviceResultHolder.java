package com.cannontech.common.device.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dynamic.PointValueHolder;

public interface MultipleDeviceResultHolder {

    public Set<SimpleDevice> getAllDevices();
    
    public Set<SimpleDevice> getSuccessfulDevices();
    
    public Set<SimpleDevice> getFailedDevices();

    public boolean isSuccessful(SimpleDevice device);

    public boolean isUnsuccessful(SimpleDevice device);
    
    public List<PointValueHolder> getValues(SimpleDevice device);
    
    public Map<SimpleDevice, List<PointValueHolder>> getValues();

    public boolean isComplete();
    
    public boolean isCanceled();

    public Map<SimpleDevice, SpecificDeviceErrorDescription> getErrors();

    public Map<SimpleDevice, String> getResultStrings();

}