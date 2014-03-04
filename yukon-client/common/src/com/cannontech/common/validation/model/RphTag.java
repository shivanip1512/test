/**
 * 
 */
package com.cannontech.common.validation.model;

import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.LogoableEnum;
import com.google.common.collect.ImmutableSet;

public enum RphTag implements DisplayableEnum, LogoableEnum {
    PU, // leave these in an order that makes sense for "display precedence" purposes. i.e PU > UU, PD > UD > UDC
    PD, 
    UU, 
    UD, 
    UDC,
    OK;
    
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
        if(name().equalsIgnoreCase("PU")) {
            return "icon-arrow-up-red";
        } else if(name().equalsIgnoreCase("PD")) {
            return "icon-arrow-down-red";
        } else if(name().equalsIgnoreCase("UDC")) {
            return "icon-arrow-swap";
        } else if(name().equalsIgnoreCase("UU")) {
            return "icon-trend-up";
        } else if(name().equalsIgnoreCase("UD")) {
            return "icon-trend-down";
        }
        return "";
    }
}