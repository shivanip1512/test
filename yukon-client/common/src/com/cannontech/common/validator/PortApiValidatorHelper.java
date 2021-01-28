package com.cannontech.common.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.device.port.PortSharing;
import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.device.port.SharedPortType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.util.Validator;

public class PortApiValidatorHelper {

    public static void validatePortTimingFields(Errors errors, PortTiming timing) {
        if (!errors.hasFieldErrors("timing.preTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonApiValidationUtils.checkRange(errors, "timing.preTxWait", "Pre Tx Wait", timing.getPreTxWait(), range, false);
        }
        if (!errors.hasFieldErrors("timing.rtsToTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonApiValidationUtils.checkRange(errors, "timing.rtsToTxWait", "RTS To Tx Wait", timing.getRtsToTxWait(), range,
                    false);
        }
        if (!errors.hasFieldErrors("timing.postTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonApiValidationUtils.checkRange(errors, "timing.postTxWait", "Post Tx Wait", timing.getPostTxWait(), range, false);
        }
        if (!errors.hasFieldErrors("timing.receiveDataWait")) {
            Range<Integer> range = Range.inclusive(0, 1000);
            YukonApiValidationUtils.checkRange(errors, "timing.receiveDataWait", "Receive Data Wait", timing.getReceiveDataWait(),
                    range, false);
        }
        if (!errors.hasFieldErrors("timing.extraTimeOut")) {
            Range<Integer> range = Range.inclusive(0, 999);
            YukonApiValidationUtils.checkRange(errors, "timing.extraTimeOut", "Additional Time Out", timing.getExtraTimeOut(),
                    range,
                    false);
        }
    }

    public static void validatePortSharingFields(Errors errors, PortSharing sharing, String fieldName) {
        if (sharing.getSharedPortType() != null && sharing.getSharedPortType() != SharedPortType.NONE) {
            YukonApiValidationUtils.validatePort(errors, "sharing.sharedSocketNumber", fieldName,
                    String.valueOf(sharing.getSharedSocketNumber()));
        }

        if (sharing.getSharedSocketNumber() != null) {
            if ((sharing.getSharedPortType() == null || sharing.getSharedPortType() == SharedPortType.NONE)
                    && sharing.getSharedSocketNumber() != CommPort.DEFAULT_SHARED_SOCKET_NUMBER) {
                errors.rejectValue("sharing.sharedSocketNumber", ApiErrorDetails.INVALID_SOCKET_NUMBER.getCodeString());
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
                errors.rejectValue("portNumber", ApiErrorDetails.COMBINATION_NOT_UNIQUE.getCodeString(),
                        new Object[] { "IP Address", ipAddress, "Port Number", portNumber.toString() }, "");
                errors.rejectValue("ipAddress", ApiErrorDetails.COMBINATION_NOT_UNIQUE.getCodeString(),
                        new Object[] { "IP Address", ipAddress, "Port Number", portNumber.toString() }, "");
            } else {
                errors.rejectValue("portNumber", ApiErrorDetails.NOT_UNIQUE.getCodeString(),
                        new Object[] { "Port Number", portNumber.toString() }, "");
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
            PortApiValidatorHelper.validateDuplicateSocket(errors, ipAddress, portNumber, existingPortId, portIdString, portType);
        }
    }

    /**
     * Validate IP Address
     */
    public static void validateIPAddress(Errors errors, String ipAddress, String fieldName, boolean isIpAddressRequired) {
        if (isIpAddressRequired) {
            YukonApiValidationUtils.checkIfFieldRequired("ipAddress", errors, ipAddress, fieldName);
        } else {
            YukonApiValidationUtils.checkIsBlank(errors, "ipAddress", ipAddress, fieldName, false);
        }
        if (!errors.hasFieldErrors("ipAddress")) {
            YukonApiValidationUtils.ipHostNameValidator(errors, "ipAddress", ipAddress);
        }
    }

    /**
     * Validate Carrier Detect Wait
     */
    public static void validateCarrierDetectWait(Errors errors, Integer carrierDetectWaitInMilliseconds, String fieldName) {
        if (carrierDetectWaitInMilliseconds != null) {
            Range<Integer> range = Range.inclusive(0, 999);
            YukonApiValidationUtils.checkRange(errors, "carrierDetectWaitInMilliseconds", fieldName,
                    carrierDetectWaitInMilliseconds, range, false);
        }
    }

    /**
     * Validate Physical Port
     */
    public static void validatePhysicalPort(Errors errors, String fieldValue, String fieldName) {
        YukonApiValidationUtils.checkIsBlank(errors, "physicalPort", fieldValue, fieldName, false);
        if (!errors.hasFieldErrors("physicalPort")) {
            YukonApiValidationUtils.checkExceedsMaxLength(errors, "physicalPort", fieldValue, 8);
        }
    }

    /**
     * Validate Encryption Key
     */
    public static void validateEncryptionKey(Errors errors, String keyInHex) {
        if (StringUtils.isNotEmpty(keyInHex)) {
            if (!Validator.isHex(keyInHex)) {
                errors.rejectValue("keyInHex", ApiErrorDetails.INVALID_HEX_FORMAT.getCodeString());
            }
            if (!errors.hasFieldErrors("keyInHex")) {
                if (keyInHex.length() != 32) {
                    errors.rejectValue("keyInHex", ApiErrorDetails.INVALID_HEX_LENGTH.getCodeString());
                }
            }
        }
    }
}