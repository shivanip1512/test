package com.cannontech.common.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortSharing;
import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.device.port.SharedPortType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.util.Validator;

public class PortValidatorHelper {

    private final static String key = "yukon.web.api.error.";

    public static void validatePortTimingFields(Errors errors, PortTiming timing) {
        if (!errors.hasFieldErrors("timing.preTxWait")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.preTxWait", timing.getPreTxWait(), 0, 10000000,
                    "Pre Tx Wait", false);
        }
        if (!errors.hasFieldErrors("timing.rtsToTxWait")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.rtsToTxWait", timing.getRtsToTxWait(), 0,
                    10000000, "RTS To Tx Wait", false);
        }
        if (!errors.hasFieldErrors("timing.postTxWait")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.postTxWait", timing.getPostTxWait(), 0, 10000000,
                    "Post Tx Wait", false);
        }
        if (!errors.hasFieldErrors("timing.receiveDataWait")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.receiveDataWait", timing.getReceiveDataWait(), 0,
                    1000, "Receive Data Wait", false);
        }
        if (!errors.hasFieldErrors("timing.extraTimeOut")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.extraTimeOut", timing.getExtraTimeOut(), 0, 999,
                    "Additional Time Out", false);
        }
    }
    
    public static void validatePortSharingFields(Errors errors, PortSharing sharing) {
        if (sharing.getSharedPortType() != null && sharing.getSharedPortType() != SharedPortType.NONE) {
            YukonValidationUtils.validatePort(errors, "sharing.sharedSocketNumber",
                    String.valueOf(sharing.getSharedSocketNumber()), "Socket Number" );
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
    public static void validateDuplicateSocket(Errors errors, String ipAddress, Integer portNumber, Integer existingPortId,
            String portIdString, PaoType portType) {
        Integer portId = portIdString != null ? Integer.valueOf(portIdString) : null;

        if (existingPortId != null && !(existingPortId.equals(portId))) {
            if (PaoType.TSERVER_SHARED == portType) {
                errors.reject(key + "field.combinationError", new Object[] { "IP Address", ipAddress, "Port Number", portNumber.toString() }, "");
            } else {
                errors.reject(key + "field.uniqueError", new Object[] { "Port Number", portNumber.toString() }, "");
            }
        }
    }

    /**
     * Validate Port and IP Address combination is Unique
     */
    public static void validateUniquePortAndIpAddress(Errors errors, Integer portNumber, String ipAddress, Integer existingPortId,
            String portIdString, PaoType portType) {
        if (portNumber != null && !errors.hasFieldErrors("portNumber") && !errors.hasFieldErrors("ipAddress")) {
            // Checks for unique IP Address and Port number
            PortValidatorHelper.validateDuplicateSocket(errors, ipAddress, portNumber, existingPortId, portIdString, portType);
        }
    }

    /**
     * Validate IP Address
     */
    public static void validateIPAddress(Errors errors, String ipAddress, boolean isIpAddressRequired) {
        if (isIpAddressRequired) {
            YukonValidationUtils.checkIfFieldRequired("ipAddress", errors, ipAddress, "IP Address");
        } else {
            YukonValidationUtils.checkIsEmpty(errors, "ipAddress", ipAddress, "IP Address");
        }
        if (!errors.hasFieldErrors("ipAddress")) {
            YukonValidationUtils.ipHostNameValidator(errors, "ipAddress", ipAddress);
        }
    }

    /**
     * Validate Carrier Detect Wait
     */
    public static void validateCarrierDetectWait(Errors errors, Integer carrierDetectWaitInMilliseconds) {
        if (carrierDetectWaitInMilliseconds != null) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "carrierDetectWaitInMilliseconds",
                    carrierDetectWaitInMilliseconds, 0, 999, "Carrier Detect Wait", false);
        }
    }

    /**
     * Validate Physical Port
     */
    public static void validatePhysicalPort(Errors errors, String fieldName) {
        YukonValidationUtils.checkIsEmpty(errors, "physicalPort", fieldName, "Physical Port");
        if (!errors.hasFieldErrors("physicalPort")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "physicalPort", fieldName, 8);
        }
    }

    /**
     * Validate Encryption Key
     */
    public static void validateEncryptionKey(Errors errors, String keyInHex) {
        if (StringUtils.isNotEmpty(keyInHex)) {
            if (!Validator.isHex(keyInHex)) {
                errors.rejectValue("keyInHex", "yukon.web.api.error.udpPort.invalidHexFormat");
            }
            if (!errors.hasFieldErrors("keyInHex")) {
                if (keyInHex.length() != 32) {
                    errors.rejectValue("keyInHex", "yukon.web.api.error.udpPort.invalidHexLength");
                }
            }
        }
    }
}
