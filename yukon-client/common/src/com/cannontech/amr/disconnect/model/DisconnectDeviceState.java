package com.cannontech.amr.disconnect.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This enum represents disconnect collection action "buckets". These are not
 * actual states of the meter/hardware.
 */
public enum DisconnectDeviceState implements DisplayableEnum {
    CONNECTED,
    DISCONNECTED,
    ARMED,
    UNSUPPORTED,
    NOT_CONFIGURED,
    CANCELED,
    FAILED;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.disconnect." + name();
    }
}
