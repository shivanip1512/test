package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * ABC for menu options that do not have child options.
 */
public abstract class SimpleMenuOption extends BaseMenuOption {

    public SimpleMenuOption(YukonMessageSourceResolvable menuText) {
        super(menuText);
    }

    public abstract String getUrl();

}
