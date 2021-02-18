package com.cannontech.common.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortSharing;
import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.device.port.SharedPortType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.util.Validator;

public class PortValidatorHelper {
    
    private final static String key = "yukon.web.api.error.";

    public static void validatePortTimingFields(Errors errors, PortTiming timing) {
        if (!errors.hasFieldErrors("timing.preTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonValidationUtils.checkRange(errors, "timing.preTxWait", "Pre Tx Wait", timing.getPreTxWait(), range, false);
        }
        if (!errors.hasFieldErrors("timing.rtsToTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonValidationUtils.checkRange(errors, "timing.rtsToTxWait", "RTS To Tx Wait", timing.getRtsToTxWait(), range,
                    false);
        }
        if (!errors.hasFieldErrors("timing.postTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonValidationUtils.checkRange(errors, "timing.postTxWait", "Post Tx Wait", timing.getPostTxWait(), range, false);
        }
        if (!errors.hasFieldErrors("timing.receiveDataWait")) {
            Range<Integer> range = Range.inclusive(0, 1000);
            YukonValidationUtils.checkRange(errors, "timing.receiveDataWait", "Receive Data Wait", timing.getReceiveDataWait(),
                    range, false);
        }
        if (!errors.hasFieldErrors("timing.extraTimeOut")) {
            Range<Integer> range = Range.inclusive(0, 999);
            YukonValidationUtils.checkRange(errors, "timing.extraTimeOut", "Additional Time Out", timing.getExtraTimeOut(), range,
                    false);
        }
        if (!errors.hasFieldErrors("timing.postCommWait")) {
            Range<Integer> range = Range.inclusive(0, 100_000);
            YukonValidationUtils.checkRange(errors, "timing.postCommWait", "Post Comm Wait", timing.getPostCommWait(), range,
                    false);
        }
    }
    
    public static void validatePortSharingFields(Errors errors, PortSharing sharing, String fieldName) {
        if (sharing.getSharedPortType() != null && sharing.getSharedPortType() != SharedPortType.NONE) {
            YukonValidationUtils.validatePort(errors, "sharing.sharedSocketNumber", fieldName,
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
    public static void validateDuplicateSocket(Errors errors, String ipAddress, Integer portNumber, Integer existingPortId,
            String portIdString, PaoType portType) {
        Integer portId = portIdString != null ? Integer.valueOf(portIdString) : null;

        if (existingPortId != null && !(existingPortId.equals(portId))) {
            if (PaoType.TSERVER_SHARED == portType) {
                errors.reject(key + "field.combinationError",
                        new Object[] { "IP Address", ipAddress, "Port Number", portNumber.toString() }, "");
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
    public static void validateIPAddress(Errors errors, String ipAddress, String fieldName, boolean isIpAddressRequired) {
        if (isIpAddressRequired) {
            YukonValidationUtils.checkIfFieldRequired("ipAddress", errors, ipAddress, fieldName);
        } else {
            YukonValidationUtils.checkIsBlank(errors, "ipAddress", ipAddress, fieldName, false);
        }
        if (!errors.hasFieldErrors("ipAddress")) {
            YukonValidationUtils.ipHostNameValidator(errors, "ipAddress", ipAddress);
        }
    }
    
    /**
     * Validate Rfn Address field
     */
    public static void validateRfnAddressField(Errors errors, String path, String fieldValue, String fieldName, boolean isRequired, int maxLength) {
        YukonValidationUtils.checkIsBlank(errors, path, fieldValue, fieldName, isRequired);
        if (!errors.hasFieldErrors(path)) {
            YukonValidationUtils.checkExceedsMaxLength(errors, path, fieldValue, maxLength);
        }
    }

    /**
     * Validate Carrier Detect Wait
     */
    public static void validateCarrierDetectWait(Errors errors, Integer carrierDetectWaitInMilliseconds, String fieldName) {
        if (carrierDetectWaitInMilliseconds != null) {
            Range<Integer> range = Range.inclusive(0, 999);
            YukonValidationUtils.checkRange(errors, "carrierDetectWaitInMilliseconds", fieldName,
                    carrierDetectWaitInMilliseconds, range, false);
        }
    }

    /**
     * Validate Physical Port
     */
    public static void validatePhysicalPort(Errors errors, String fieldValue, String fieldName) {
        YukonValidationUtils.checkIsBlank(errors, "physicalPort", fieldValue, fieldName, false);
        if (!errors.hasFieldErrors("physicalPort")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "physicalPort", fieldValue, 8);
        }
    }

    /**
     * Validate Encryption Key
     */
    public static void validateEncryptionKey(Errors errors, String keyInHex) {
        if (StringUtils.isNotEmpty(keyInHex)) {
            if (!Validator.isHex(keyInHex)) {
                errors.rejectValue("keyInHex", key + "udpPort.invalidHexFormat");
            }
            if (!errors.hasFieldErrors("keyInHex")) {
                if (keyInHex.length() != 32) {
                    errors.rejectValue("keyInHex", key + "udpPort.invalidHexLength");
                }
            }
        }
    }
}