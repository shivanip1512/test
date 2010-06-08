package com.cannontech.core.service.durationFormatter;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DurationFormat implements DisplayableEnum {
	
    DHMS(true),
    DH(true),
    DH_ABBR(true),
    HMS(true),
    HM(true),
    H(true),
    M(true),
    HM_ABBR(true),
    HM_SHORT(false);
    
    private boolean roundRightmostUpDefault;
    private static final String keyPrefix = "yukon.common.durationFormatting.pattern.";
    
    DurationFormat(boolean roundRightmostUpDefault) {
    	this.roundRightmostUpDefault = roundRightmostUpDefault;
    }
    
    public boolean getRoundRightmostUpDefault() {
    	return roundRightmostUpDefault;
    }
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
