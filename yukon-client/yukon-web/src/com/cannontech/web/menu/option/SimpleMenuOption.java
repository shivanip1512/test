package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResovable;

/**
 * ABC for menu options that do not have child options.
 */
public abstract class SimpleMenuOption extends BaseMenuOption {

    public SimpleMenuOption(YukonMessageSourceResovable menuText) {
        super(menuText);
    }

    public abstract String getUrl();

}
