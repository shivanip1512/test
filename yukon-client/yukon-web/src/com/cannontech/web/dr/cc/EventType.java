package com.cannontech.web.dr.cc;

import com.cannontech.common.i18n.DisplayableEnum;

enum EventType implements DisplayableEnum {
    CURRENT,
    PENDING,
    RECENT,
    ;
    
    private String base = "yukon.web.modules.dr.cc.eventType.";
    
    @Override
    public String getFormatKey() {
        return base + name();
    }
}