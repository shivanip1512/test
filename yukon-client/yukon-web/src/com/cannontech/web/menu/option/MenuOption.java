package com.cannontech.web.menu.option;

import com.cannontech.i18n.YukonMessageSourceResolvable;


/**
 * Interface which represents a web menu option
 */
public interface MenuOption {

    public String getId();

    /**
     * Method to get the MessageSourceResovable that will generate the menu text
     * @return MessageSourceResovable
     */
    public YukonMessageSourceResolvable getMenuText();

}
