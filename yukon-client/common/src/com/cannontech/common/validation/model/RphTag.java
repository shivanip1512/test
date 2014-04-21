/**
 * 
 */
package com.cannontech.common.validation.model;

import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.LogoableEnum;
import com.google.common.collect.ImmutableSet;

public enum RphTag implements DisplayableEnum, LogoableEnum {
    // leave these in an order that makes sense for "display precedence" purposes. i.e PU > UU, PD > UD > UDC
    PU("icon-arrow-up-red"),
    PD("icon-arrow-down-red"), 
    UU("icon-trend-up"), 
    UD("icon-trend-down"), 
    UDC("icon-arrow-swap"),
    OK("");

    private final String iconClass;

    RphTag(String iconClass) {
        this.iconClass = iconClass;
    }

    public boolean isPeak() {
        return name().startsWith("P"); // good enough for now
    }
    public boolean isUnreasonable() {
        return name().startsWith("U"); // good enough for now
    }
    public static Set<RphTag> getAllUnreasonable() {
        return ImmutableSet.of(UU, UD, UDC); // good enough for now
    }
    public static Set<RphTag> getAllValidation() {
        return ImmutableSet.of(PU, PD, UU, UD, UDC); // good enough for now
    }
    
    @Override
    public String getFormatKey() {
    	return "yukon.web.modules.common.vee.rphTag." + this.name();
    }
    
    @Override
    public String getLogoKey() {
    	return getFormatKey() + ".img";
    }
    
    public String getIconClass() {
        return iconClass;
    }
}