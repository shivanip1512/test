package com.cannontech.dr.meterDisconnect;

import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DrMeterControlStatus implements DisplayableEnum {
    CONTROL_NOT_SENT,               // Event started, disconnect command not sent yet
    CONTROL_SENT,                   // Disconnect command sent, waiting for response
    CONTROL_CONFIRMED,              // Device confirmed successful disconnect
    CONTROL_FAILED_UNSUPPORTED,     // Disconnect failed - command unsupported
    CONTROL_FAILED_NOT_CONFIGURED,  // Disconnect failed - device not configured
    CONTROL_FAILED,                 // Disconnect failed - device sent failure response
    CONTROL_UNKNOWN,                // Disconnect status unknown - received an unexpected status
    CONTROL_TIMEOUT,                // Timeout, no response received for disconnect message
    NO_CONTROL_OPTED_OUT,           // Opt-out prevented disconnect command from being sent
    MANUAL_CONTROL_SENT,            // Disconnect command sent from report page, waiting for response
    
    FAILED_ARMED,                   // Connect failed - device misconfigured to arm    
    RESTORE_NOT_SENT,               // No re-connect sent yet
    RESTORE_SENT,                   // Device controlled, re-connect command sent, waiting for response.
    RESTORE_CONFIRMED,              // Device confirmed successful re-connect
    RESTORE_FAILED_UNSUPPORTED,     // Re-connect failed - command unsupported
    RESTORE_FAILED_NOT_CONFIGURED,  // Re-connect failed - device not configured
    RESTORE_FAILED,                 // Re-connect failed - device sent failure response
    RESTORE_TIMEOUT,                // Timeout, no response received for re-connect message
    RESTORE_UNKNOWN,                // Re-connect status unknown - received an unexpected status
    RESTORE_OPT_OUT_SENT,           // Opt-out interrupted control, re-connect sent, waiting for response
    RESTORE_OPT_OUT_CONFIRMED,      // Opt-out interrupted control
    MANUAL_RESTORE_SENT,            // Re-connect command sent from report page, waiting for response
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.controlStatus." + name();
    }
    
    public static final List<DrMeterControlStatus> getShedStatuses() {
        return List.of(
            CONTROL_NOT_SENT,
            CONTROL_SENT,
            CONTROL_CONFIRMED,
            CONTROL_FAILED_UNSUPPORTED,
            CONTROL_FAILED_NOT_CONFIGURED,
            CONTROL_FAILED,
            CONTROL_UNKNOWN,
            CONTROL_TIMEOUT,
            NO_CONTROL_OPTED_OUT,
            MANUAL_CONTROL_SENT
        );
    }
    
    public static final List<DrMeterControlStatus> getRestoreStatuses() {
        return List.of(
            FAILED_ARMED,           
            RESTORE_NOT_SENT,
            RESTORE_SENT,
            RESTORE_CONFIRMED,
            RESTORE_FAILED_UNSUPPORTED,
            RESTORE_FAILED_NOT_CONFIGURED,
            RESTORE_FAILED,
            RESTORE_TIMEOUT,
            RESTORE_UNKNOWN,
            RESTORE_OPT_OUT_SENT,
            RESTORE_OPT_OUT_CONFIRMED,
            MANUAL_RESTORE_SENT
        );
    }
}
