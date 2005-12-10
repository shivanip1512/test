package com.cannontech.web.menu;



/**
 * ABC for menu options that do not have child options.
 */
public abstract class SimpleMenuOption extends BaseMenuOption {
    public SimpleMenuOption(String subOptionName) {
        this.linkName = subOptionName;
    }
    
    public abstract String getUrl();

}
