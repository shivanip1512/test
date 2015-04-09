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
    PEAKUP("icon-arrow-up-red"),
    PEAKDOWN("icon-arrow-down-red"),
    UNREASONABLEUP("icon-trend-up"),
    UNREASONABLEDOWN("icon-trend-down"),
    CHANGEOUT("icon-arrow-swap"),
    ACCEPTED("");
    private static final ImmutableSet<RphTag> peakTags = ImmutableSet.of(PEAKUP, PEAKDOWN);
    private static final ImmutableSet<RphTag> unreasonableTags = ImmutableSet.of(UNREASONABLEUP, UNREASONABLEDOWN,
        CHANGEOUT);
    private static final ImmutableSet<RphTag> validationTags = ImmutableSet.of(PEAKUP, PEAKDOWN, UNREASONABLEUP,
        UNREASONABLEDOWN, CHANGEOUT);

    public boolean isPeak() {
        return peakTags.contains(this);
    }

    public boolean isUnreasonable() {
        return unreasonableTags.contains(this);
    }

    public static Set<RphTag> getAllUnreasonable() {
        return unreasonableTags;
    }

    public static Set<RphTag> getAllValidation() {
        return validationTags;
    }

    private final String iconClass;

    RphTag(String iconClass) {
        this.iconClass = iconClass;
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