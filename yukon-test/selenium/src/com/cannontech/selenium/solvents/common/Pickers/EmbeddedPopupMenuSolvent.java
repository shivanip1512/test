package com.cannontech.selenium.solvents.common.Pickers;

import com.cannontech.selenium.solvents.common.PopupMenuSolvent;


/**
 * This solvent should handle most of the popup menus such as popup menu
 * we get buy clicking "enable" link in cap control.
 * @author anuradha.uduwage
 *
 */
public class EmbeddedPopupMenuSolvent extends PopupMenuSolvent {

    public EmbeddedPopupMenuSolvent(String...params) {
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
        selenium.waitForElementToBecomeVisible(getMenuXpath(), WAIT_TIME_TO_OPEN);
        selenium.click(getMenuItemXpath(menuItemText));
        // This should have a some wait syntax
    }
    
    @Override
    public void clickButton(String buttonValue) {
        throw new IllegalStateException("This method is not supported for single select popups.");
    }

    @Override
    public void openPickerPopup() {
        throw new IllegalStateException("The embedded picker cannot be openeded since it is embedded in the page.");
    }
}
