package com.cannontech.web.menu.option;

import org.springframework.context.MessageSourceResolvable;


/**
 * Interface which represents a web menu option
 */
public interface MenuOption {

    /**
     * This is used to identify the menu for selection purposes. Each page
     * (in the standardMenu tag) indicates what option the menu should open 
     * to by default using a syntax that refers to the menu ids.
     * @return
     */
    public String getId();

    /**
     * Method to get the MessageSourceResovable that will generate the menu text
     * @return MessageSourceResovable
     */
    public MessageSourceResolvable getMenuText();

    /**
     * Method to get the MessageSourceResovable that will generate the menu tooltip
     * @return MessageSourceResovable
     */
    public MessageSourceResolvable getMenuTooltip();
    
}
