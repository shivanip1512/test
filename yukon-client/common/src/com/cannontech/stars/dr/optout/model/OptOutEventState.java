package com.cannontech.stars.dr.optout.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Enum which represents an opt out event's state
 */
public enum OptOutEventState implements DisplayableEnum {

	START_OPT_OUT_SENT, CANCEL_SENT, SCHEDULED, SCHEDULE_CANCELED, RESET_SENT;

	private static final String keyPrefix = "yukon.dr.consumer.optout.state.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
