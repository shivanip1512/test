package com.cannontech.selenium.solvents.common.Pickers;

import com.cannontech.selenium.solvents.common.PopupMenuSolvent;



/**
 * This solvent should handle most of the popup menus such as popup menu
 * we get buy clicking "enable" link in cap control.
 * @author anuradha.uduwage
 *
 */
public class MultiSelectPopupMenuSolvent extends PopupMenuSolvent {

    public MultiSelectPopupMenuSolvent(String...params) {
        super(params);
    }

    @Override
    public void prepare() {}
    
    /**
     * Method locate the a menu item in a menu and click the menu item.
     * This method should be used if the menu is already open.
     * @param menuItemText String value of the menu item.
     * @return
     */
    public void clickMenuItem(String menuItemText) {
        selenium.waitForElement(getMenuXpath());
        selenium.waitForElementToBecomeVisible(getMenuXpath(), WAIT_TIME_TO_OPEN);
        selenium.click(getMenuItemXpath(menuItemText));
    }

}
