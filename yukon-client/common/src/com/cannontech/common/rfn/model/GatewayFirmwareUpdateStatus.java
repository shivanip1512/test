package com.cannontech.common.rfn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.google.common.collect.ImmutableMap;

/**
 * These are the possible status indicators of an RFN firmware upgrade operation.
 * <ul>
 * <li>STARTED indicates that the operation has been initiated.</li>
 * <li>ACCEPTED indicates that the operation is complete and successful.</li>
 * <li>TIMEOUT indicates that the operation was initated, but no response was received within the timeout period.</li>
 * <li>All other values indicate an error occurred. These values correspond to GatewayFirmwareUpdateRequestResult values.</li>
 * </ul>
 */
public enum GatewayFirmwareUpdateStatus implements DisplayableEnum {
    STARTED(false),
    TIMEOUT(true),
    //The following statuses correspond to GatewayFirmwareUpdateRequestResult values
    ACCEPTED(false),
    REJECTED_UPDATE_IN_PROGRESS(true),
    REJECTED_BATTERY_ISSUE(true),
    VERSION_MISMATCH(true),
    GENERAL_ERROR(true),
    CONFIGURATION_PARSE_ERROR(true),
    FILE_IO_ERROR(true),
    NETWORK_FAILURE(true),
    SSL_VERIFICATION_FAILURE(true),
    AUTHENTICATION_FAILURE(true),
    PROTOCOL_ERROR(true),
    SERVER_ERROR(true)
    ;
    
    private static final String keyBase = "yukon.common.rfn.gatewayFirmwareUpdateStatus.";
    private static List<GatewayFirmwareUpdateStatus> failedStates;
    private static Map<GatewayFirmwareUpdateRequestResult, GatewayFirmwareUpdateStatus> resultToStatus;
    
    static {
        ImmutableMap.Builder<GatewayFirmwareUpdateRequestResult, GatewayFirmwareUpdateStatus> builder = ImmutableMap.builder();
        
        builder.put(GatewayFirmwareUpdateRequestResult.ACCEPTED, ACCEPTED);
        builder.put(GatewayFirmwareUpdateRequestResult.REJECTED_UPDATE_IN_PROGRESS, REJECTED_UPDATE_IN_PROGRESS);
        builder.put(GatewayFirmwareUpdateRequestResult.REJECTED_BATTERY_ISSUE, REJECTED_BATTERY_ISSUE);
        builder.put(GatewayFirmwareUpdateRequestResult.VERSION_MISMATCH, VERSION_MISMATCH);
        builder.put(GatewayFirmwareUpdateRequestResult.GENERAL_ERROR, GENERAL_ERROR);
        builder.put(GatewayFirmwareUpdateRequestResult.CONFIGURATION_PARSE_ERROR, CONFIGURATION_PARSE_ERROR);
        builder.put(GatewayFirmwareUpdateRequestResult.FILE_IO_ERROR, FILE_IO_ERROR);
        builder.put(GatewayFirmwareUpdateRequestResult.NETWORK_FAILURE, NETWORK_FAILURE);
        builder.put(GatewayFirmwareUpdateRequestResult.SSL_VERIFICATION_FAILURE, AUTHENTICATION_FAILURE);
        builder.put(GatewayFirmwareUpdateRequestResult.AUTHENTICATION_FAILURE, AUTHENTICATION_FAILURE);
        builder.put(GatewayFirmwareUpdateRequestResult.PROTOCOL_ERROR, PROTOCOL_ERROR);
        builder.put(GatewayFirmwareUpdateRequestResult.SERVER_ERROR, SERVER_ERROR);
        resultToStatus = builder.build();
        
        failedStates = new ArrayList<>();
        
        for (GatewayFirmwareUpdateStatus status : values()) {
            if (status.isFailed) {
                failedStates.add(status);
            }
        }
    }
    
    /**
     * @return The GatewayFirmwareUpdateStatus corresponding to the specified GatewayFirmwareUpdateRequestResult.
     */
    public static GatewayFirmwareUpdateStatus of(GatewayFirmwareUpdateRequestResult result) {
        GatewayFirmwareUpdateStatus status = resultToStatus.get(result);
        if (status == null) {
            throw new IllegalArgumentException("Firmware update request result " + result + " does not have a "
                    + "corresponding firmware update status.");
        }
        return status;
    }
    
    /**
     * @return A List of all states that indicate the firmware upgrade failed.
     */
    public static List<GatewayFirmwareUpdateStatus> getFailedStates() {
        return failedStates;
    }
    
    @Override
    public String getFormatKey() {
        return keyBase + name();
    }
    
    private boolean isFailed;
    
    private GatewayFirmwareUpdateStatus(boolean isFailed) {
        this.isFailed = isFailed;
    }

}
