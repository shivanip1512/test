package com.cannontech.amr.rfn.message.dataRequest;

import java.io.Serializable;

/**
 * Sent from SM to WS to notify of completion of DynamicRfnDeviceData update.
 */
public class DynamicRfnDeviceDataResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;

    public DynamicRfnDeviceDataResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
