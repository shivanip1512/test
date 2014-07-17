package com.cannontech.amr.demandreset.service;

import org.joda.time.Instant;

import com.cannontech.common.device.model.SimpleDevice;

public abstract class DemandResetVerificationCallback implements DemandResetCallback{

    @Override
    public void verified(SimpleDevice device, Instant pointDataTimeStamp) {
    }

    @Override
    public void failed(SimpleDevice device, String reason) {
    }

    @Override
    public void cannotVerify(SimpleDevice device, String reason) {
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
