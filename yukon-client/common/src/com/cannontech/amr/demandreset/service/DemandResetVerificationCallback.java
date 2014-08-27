package com.cannontech.amr.demandreset.service;

import org.joda.time.Instant;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;

public abstract class DemandResetVerificationCallback implements DemandResetCallback{

    @Override
    public void verified(SimpleDevice device, Instant pointDataTimeStamp) {
    }

    @Override
    public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
    }

    @Override
    public void cannotVerify(SimpleDevice device, SpecificDeviceErrorDescription error) {
    }

    @Override
    public void complete() {
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public void canceled(SimpleDevice device) {
    }

    @Override
    public void processingExceptionOccured(String reason) {
    }
}
