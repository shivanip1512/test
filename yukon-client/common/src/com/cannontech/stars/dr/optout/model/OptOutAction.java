package com.cannontech.stars.dr.optout.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Enum which represents an opt out action
 */
public enum OptOutAction implements DisplayableEnum {

	START_OPT_OUT, CANCEL, SCHEDULE, CANCEL_SCHEDULE, REPEAT_START_OPT_OUT, RESET;

    private final static String keyPrefix = "yukon.dr.program.optOutAction.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
