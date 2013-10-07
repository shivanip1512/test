package com.cannontech.common.tdc.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public enum AltScanRate implements Displayable{            
    ONCE(0),
    MIN1(60),
    MIN2(120),
    MIN3(180),
    MIN4(240),
    MIN5(300),
    MIN10(600),
    MIN15(900),
    MIN30(1800),
    HOUR1(3600),
    HOURS2(7200),
    HOURS4(14400),
    HOURS16(28800),
    DAY1(86400),
    DAYS2(172800),
    DAYS5(432000);
    
    AltScanRate(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }
    public int getDuration() {
        return durationInSeconds;
    }

    private int durationInSeconds;

    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable( "yukon.web.modules.tools.tdc.altScanRate."  + name());
    }
}
