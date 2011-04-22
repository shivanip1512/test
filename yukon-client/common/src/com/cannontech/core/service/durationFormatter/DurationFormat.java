package com.cannontech.core.service.durationFormatter;

import java.math.RoundingMode;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DurationFormat implements DisplayableEnum {
	
	YMODHMS(RoundingMode.HALF_UP, true),
    DHMS(RoundingMode.HALF_UP, true),
    DHMS_REDUCED(RoundingMode.HALF_UP, false),
    DH(RoundingMode.HALF_UP, true),
    DH_ABBR(RoundingMode.HALF_UP, true),
    HMS(RoundingMode.HALF_UP, true),
    HM(RoundingMode.HALF_UP, true),
    H(RoundingMode.HALF_UP, true),
    M(RoundingMode.HALF_UP, true),
    S(RoundingMode.HALF_UP, true),
    HM_ABBR(RoundingMode.HALF_UP, true),
    HM_SHORT(RoundingMode.DOWN, true),
	MS_ABBR(RoundingMode.HALF_UP, true);
    
    private RoundingMode roundingMode;
    private static final String keyPrefix = "yukon.common.durationFormatting.pattern.";
    private boolean printZeros;
    
    DurationFormat(RoundingMode roundingMode, boolean printZeros) {
    	this.roundingMode = roundingMode;
        this.printZeros = printZeros;
    }
    
    public RoundingMode getRoundingMode() {
		return roundingMode;
	}
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    public boolean isPrintZeros() {
        return printZeros;
    }
}
