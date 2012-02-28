package com.cannontech.amr.demandreset.service;

import com.cannontech.common.device.commands.MultipleDeviceResultHolder;

public interface DemandResetCallback {
    public void completed(MultipleDeviceResultHolder results);
}
