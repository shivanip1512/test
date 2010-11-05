package com.cannontech.stars.dr.optout.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OptOutEnabled implements DisplayableEnum {

	ENABLED{
		@Override
		public boolean isEnabled() {
			return true;
		}
	}, 
	DISABLED {
		@Override
		public boolean isEnabled() {
			return false;
		}
	};

	public static OptOutEnabled valueOf(boolean enabled) {
		if (enabled) {
			return ENABLED;
		} else {
			return DISABLED;
		}
	}
	
	/**
	 * This method takes the optOutValue of an optOutTemporaryOverride object and creates a 
     * OptOutEnabled enum value.
	 */
	public static OptOutEnabled valueOf(int enabled) {
        if (enabled == 1) {
            return ENABLED;
        } else if (enabled == 0) {
            return DISABLED;
        } else {
            throw new IllegalArgumentException("Invalid opt out enabled value: "+enabled);
        }
    }
	
	public abstract boolean isEnabled();
	
	private final static String keyPrefix = "yukon.web.modules.dr.optOut.optOutEnabledEnum.";

	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
