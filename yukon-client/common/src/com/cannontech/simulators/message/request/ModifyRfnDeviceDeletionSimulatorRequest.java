package com.cannontech.simulators.message.request;

import com.cannontech.common.rfn.simulation.SimulatedRfnDeviceDeletionSettings;
import com.cannontech.simulators.SimulatorType;

public class ModifyRfnDeviceDeletionSimulatorRequest implements SimulatorRequest {

    private static final long serialVersionUID = 1L;
    private boolean stopRfnDeviceDeletionReply;
    private SimulatedRfnDeviceDeletionSettings deletionSettings;

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_DEVICE_DELETE;
    }

    public boolean isStopDeletionReply() {
        return stopRfnDeviceDeletionReply;
    }

    public void setStopDeletionReply(boolean stopRfnDeviceDeletionReply) {
        this.stopRfnDeviceDeletionReply = stopRfnDeviceDeletionReply;
    }

    public SimulatedRfnDeviceDeletionSettings getDeletionSettings() {
        return deletionSettings;
    }

    public void setDeletionSettings(SimulatedRfnDeviceDeletionSettings deletionSettings) {
        this.deletionSettings = deletionSettings;
    }

    @Override
    public String toString() {
        return "ModifyRfnDeviceDeletionSimulatorRequest [stopRfnDeviceDeletionReply=" + stopRfnDeviceDeletionReply
                + ", deletionSettings=" + deletionSettings + "]";
    }
}
