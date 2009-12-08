package com.cannontech.stars.dr.optout.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Enum to represent whether an opt out counts or not
 */
public enum OptOutCounts implements DisplayableEnum {

	COUNT{
		@Override
		public boolean isCounts() {
			return true;
		}
	}, 
	DONT_COUNT {
		@Override
		public boolean isCounts() {
			return false;
		}
	};

	public static OptOutCounts valueOf(boolean counts) {
		if (counts) {
			return COUNT;
		} else {
			return DONT_COUNT;
		}
	}
	
	public abstract boolean isCounts();
	
	private final static String keyPrefix = "yukon.web.modules.dr.optOut.optOutCountsEnum.";

	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
