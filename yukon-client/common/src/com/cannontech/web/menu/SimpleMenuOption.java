package com.cannontech.web.menu;



/**
 * ABC for menu options that do not have child options.
 */
public abstract class SimpleMenuOption extends BaseMenuOption {
    public SimpleMenuOption(String linkKey) {
        setLinkKey(linkKey);
    }
    
    public abstract String getUrl();

}
