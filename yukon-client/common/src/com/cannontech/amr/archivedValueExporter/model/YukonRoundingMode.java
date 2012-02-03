package com.cannontech.amr.archivedValueExporter.model;

import java.math.RoundingMode;

import com.cannontech.common.i18n.DisplayableEnum;


public enum YukonRoundingMode implements DisplayableEnum {

    UP(RoundingMode.UP),
    DOWN(RoundingMode.DOWN),
    CEILING(RoundingMode.CEILING),
    FLOOR(RoundingMode.FLOOR),
    HALF_UP(RoundingMode.HALF_UP),
    HALF_DOWN(RoundingMode.HALF_DOWN),
    HALF_EVEN(RoundingMode.HALF_EVEN)
    ;

    final RoundingMode roundingMode;

    private YukonRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }
    
    public RoundingMode getRoundingMode(){
        return roundingMode;
    }
    
    private final static String keyPrefix = "yukon.web.modules.amr.archivedValueExporter.";

    @Override
    public String getFormatKey() {
        return keyPrefix + "roundingMode." + name();
    }

}
