package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * ABC for menu options that do not have child options.
 */
public abstract class SimpleMenuOption extends BaseMenuOption {

    public SimpleMenuOption(String id, YukonMessageSourceResolvable menuText) {
        super(id, menuText);
    }
    
    public SimpleMenuOption(String id, String menuTextKey) {
        super(id, menuTextKey);
    }

    public abstract String getUrl();
    
    public boolean isNewWindow() {
        return false;
    }

}
