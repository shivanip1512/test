package com.cannontech.services.rfn.endpoint;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.device.RfnDeviceDeleteConfirmationReply;

public class RfnDeviceDeleteConfirmationListener {
    private static final Logger log = YukonLogManager.getLogger(RfnDeviceDeleteConfirmationListener.class);

    public void handleRfnDeviceDeletionConfirmation(RfnDeviceDeleteConfirmationReply rfnDeviceDeleteConfirmationReply) {

        log.info("{} - received reply ({}) from NM ", rfnDeviceDeleteConfirmationReply.getRfnIdentifier(),
                rfnDeviceDeleteConfirmationReply.getReplyType());
    }
}
