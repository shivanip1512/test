package com.cannontech.core.service.durationFormatter;

import java.math.RoundingMode;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DurationFormat implements DisplayableEnum {
	
	YMODHMS(RoundingMode.HALF_UP),
    DHMS(RoundingMode.HALF_UP),
    DH(RoundingMode.HALF_UP),
    DH_ABBR(RoundingMode.HALF_UP),
    HMS(RoundingMode.HALF_UP),
    HM(RoundingMode.HALF_UP),
    H(RoundingMode.HALF_UP),
    M(RoundingMode.HALF_UP),
    S(RoundingMode.HALF_UP),
    HM_ABBR(RoundingMode.HALF_UP),
    HM_SHORT(RoundingMode.DOWN),
	MS_ABBR(RoundingMode.HALF_UP);
    
    private RoundingMode roundingMode;
    private static final String keyPrefix = "yukon.common.durationFormatting.pattern.";
    
    DurationFormat(RoundingMode roundingMode) {
    	this.roundingMode = roundingMode;
    }
    
    public RoundingMode getRoundingMode() {
		return roundingMode;
	}
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
