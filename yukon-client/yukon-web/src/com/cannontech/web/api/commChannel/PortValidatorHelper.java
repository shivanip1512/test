package com.cannontech.web.api.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortSharing;
import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.device.port.SharedPortType;
import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.stars.util.ServletUtils;

public class PortValidatorHelper {

    private final static String key = "yukon.web.api.error.";
    @Autowired private PortDao portDao;

    public void validatePortTimingFields(Errors errors, PortTiming timing) {
        YukonValidationUtils.checkRange(errors, "timing.preTxWait", timing.getPreTxWait(), 0, 10000000, false);
        YukonValidationUtils.checkRange(errors, "timing.rtsToTxWait", timing.getRtsToTxWait(), 0, 10000000, false);
        YukonValidationUtils.checkRange(errors, "timing.postTxWait", timing.getPostTxWait(), 0, 10000000, false);
        YukonValidationUtils.checkRange(errors, "timing.receiveDataWait", timing.getReceiveDataWait(), 0, 1000, false);
        YukonValidationUtils.checkRange(errors, "timing.extraTimeOut", timing.getExtraTimeOut(), 0, 999, false);
    }
    
    public void validatePortSharingFields(Errors errors, PortSharing sharing) {
        if ((sharing.getSharedPortType() != null && sharing.getSharedPortType() != SharedPortType.NONE)
                && sharing.getSharedSocketNumber() != null) {
            YukonValidationUtils.validatePort(errors, "sharing.sharedSocketNumber",
                    String.valueOf(sharing.getSharedSocketNumber()));
        }

        if (sharing.getSharedSocketNumber() != null) {
            if ((sharing.getSharedPortType() == null || sharing.getSharedPortType() == SharedPortType.NONE)
                    && sharing.getSharedSocketNumber() != CommPort.DEFAULT_SHARED_SOCKET_NUMBER) {
                errors.rejectValue("sharing.sharedSocketNumber", key + "udpPort.invalidSocketNumber");
            }
        }
    }

    /**
     * Validate Socket is unique or not.
     */
    public void validateDuplicateSocket(Errors errors, String ipAddress, Integer portNumber) {
        Integer existingPortId = portDao.findUniquePortTerminalServer(ipAddress, portNumber);
        String portIdString = ServletUtils.getPathVariable("id");
        Integer portId = portIdString != null ? Integer.valueOf(portIdString) : null;

        if (existingPortId != null && !(existingPortId.equals(portId))) {
            errors.reject(key + "duplicateSocket", new Object[] { ipAddress, portNumber }, "");
        }
    }
}
