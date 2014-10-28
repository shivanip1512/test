package com.cannontech.common.userpage.model;

import java.util.Locale;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * An enum representing categories on the site map.
 * 
 * Ordering here is important.  The order of these values determines the order they are displayed in the UI.
 */
public enum SiteMapCategory implements DisplayableEnum {
    AMI,
    DR,
    VV,
    ASSETS,
    TOOLS,
    ADMIN,
    SUPPORT,
    DEVELOPMENT,
    CCURT,
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.menu." + name().toLowerCase(Locale.US);
    }
}
