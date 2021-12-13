package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.model.RfnDeviceDeleteConfirmationReplyType;
import com.cannontech.common.rfn.model.RfnDeviceDeleteInitialReplyType;

public class SimulatedRfnDeviceDeletionSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private RfnDeviceDeleteInitialReplyType initialReply;
    private RfnDeviceDeleteConfirmationReplyType confirmationReply;

    public RfnDeviceDeleteInitialReplyType getInitialReply() {
        return initialReply;
    }

    public void setInitialReply(RfnDeviceDeleteInitialReplyType initialReply) {
        this.initialReply = initialReply;
    }

    public RfnDeviceDeleteConfirmationReplyType getConfirmationReply() {
        return confirmationReply;
    }

    public void setConfirmationReply(RfnDeviceDeleteConfirmationReplyType confirmationReply) {
        this.confirmationReply = confirmationReply;
    }
}
