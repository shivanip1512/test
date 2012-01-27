package com.cannontech.amr.archivedValueExporter.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.cannontech.common.i18n.DisplayableEnum;


public enum YukonRoundingMode implements DisplayableEnum {

    UP(RoundingMode.valueOf(BigDecimal.ROUND_UP)),
    DOWN(RoundingMode.valueOf(BigDecimal.ROUND_DOWN)),
    CEILING(RoundingMode.valueOf(BigDecimal.ROUND_CEILING)),
    FLOOR(RoundingMode.valueOf(BigDecimal.ROUND_FLOOR)),
    HALF_UP(RoundingMode.valueOf(BigDecimal.ROUND_HALF_UP)),
    HALF_DOWN(RoundingMode.valueOf(BigDecimal.ROUND_HALF_DOWN)),
    HALF_EVEN(RoundingMode.valueOf(BigDecimal.ROUND_HALF_EVEN))
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
