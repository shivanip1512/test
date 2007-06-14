package com.cannontech.common.device.commands;

import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public interface CommandResultHolder {

    public List<DeviceErrorDescription> getErrors();

    public List<PointValueHolder> getValues();
    
    public boolean isErrorsExist();

}