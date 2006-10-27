package com.cannontech.web.menu;



/**
 * ABC for menu options that do not have child options.
 */
public abstract class SimpleMenuOption extends BaseMenuOption {
    public SimpleMenuOption(String subOptionName) {
        setLinkName(subOptionName);
    }
    
    public SimpleMenuOption(OptionNameFactory factory) {
        super(factory);
    }
    
    public abstract String getUrl();

}
