package com.cannontech.amr.meter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PointSortField implements DisplayableEnum {
    
    ATTRIBUTE("attribute"),
    POINTNAME("pointName"),
    POINTTYPE("pointType"),
    POINTOFFSET("pointOffset"),
    TIMESTAMP("timestamp"),
    VALUE("value"),
    QUALITY("quality"),
    ;
    
    private final String keyPart;
    
    private PointSortField(String keyPart) {
        this.keyPart = keyPart;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.common." + keyPart;
    }
}