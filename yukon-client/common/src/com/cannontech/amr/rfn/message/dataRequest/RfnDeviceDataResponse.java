package com.cannontech.amr.rfn.message.dataRequest;

import java.io.Serializable;

/**
 * Sent from SM to WS to notify of completion of DynamicRfnDeviceData update.
 */
public class RfnDeviceDataResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * False - something happened to prevent table update
     * True - table was updated
     * NULL - no update required, table is up to date
     */
    private Boolean success;

    public RfnDeviceDataResponse() {
    }
    public RfnDeviceDataResponse(boolean success) {
        this.success = success;
    }

    public Boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return String.format("RfnDeviceDataResponse [success=%s]", success);
    }
}
