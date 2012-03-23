/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import org.joda.time.Duration;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.locators.CssLocator;
import com.cannontech.selenium.core.locators.Locator;

/**
 * This solvent should handle most of the popup menus such as popup menu
 * we get buy clicking "enable" link in cap control.
 * @author anuradha.uduwage
 *
 */
public abstract class PopupMenuSolvent extends AbstractSolvent {

	protected PopupMenuSolvent(String...params) {
		super(params);
	}

	protected Duration WAIT_TIME_TO_OPEN = Duration.standardSeconds(10);
	
	public void openMenu() {
		selenium.click(getParams("linklocator"));
		selenium.waitForElementToBecomeVisible(getMenuXpath(), WAIT_TIME_TO_OPEN);
	}
	
    /**
     * Method locate the a menu item in a menu and click the menu item.
     * This method should be used if the menu is already open.
     * @param menuItemText String value of the menu item.
     * @return
     */
    public abstract void clickMenuItem(String menuItemText);
    
    /** 
     * @param buttonValue
     */
    public void clickButton(String buttonValue) {
        Locator inputPathLocator = getInputPath(buttonValue);
        
        selenium.waitForElementToBecomeVisible(inputPathLocator, Duration.standardSeconds(5));
        selenium.click(inputPathLocator);
// TODO Find a good way to detect a closed popup window.
//        selenium.waitForElementRemoved(inputPathLocator, 5000);
    }

	/**
	 * Click at 'X' sign to close the popup menu.
	 * @return
	 */
	public void closePopupMenu() {
		Locator removeXButtonLocator = getMenuXpath().append("img");
		
		// Wait for the remove x to appear and click it.
		selenium.waitForElement(removeXButtonLocator);
		selenium.click(removeXButtonLocator);
	}
	
	/**
	 * Return the xpath of a menu-item.
	 * @param itemText String value for the menu item.
	 * @return
	 */
	protected Locator getMenuItemXpath(String itemText) {
		Locator menuItem = getMenuXpath().append("a:contains('" + itemText + "')");
//		Locator menuItem = new LinkLocator(itemText);

		selenium.waitForElement(menuItem);
		return menuItem;
	}
	
	
    public void openPickerPopup() {
        Locator pickerLinkPath = generatePickerLinkPath();
        Locator pickerPopupHtmlBasePath = generatePickerPopupHtmlBasePath();
        
        // Click the link and wait for the picker popup to appear
        selenium.waitForElement(pickerLinkPath);
        selenium.click(pickerLinkPath);
        selenium.waitForElement(pickerPopupHtmlBasePath, CommonSolvent.PICKER_POPUP_LOAD_TIMEOUT);
    }

    /**
     * Construct the Xpath expression for the menu.
     * if menuId param is set, then it get used, else look for div. 
     * @return manuXpath xpath of the menu.
     */
    protected Locator getMenuXpath() {
        if(getParams("menuId") != null) {
            return generatePickerPopupHtmlBasePath();
        } else {
            return new CssLocator("div.'popUpDiv simplePopup thinBorder'");
        }
    }

    /**
     * This method should be used when trying to filter inside of a picker.
     */
    public void enterFilterText(String filterText) {
        Locator popupBasePath = generatePickerPopupHtmlBasePath();
        Locator filterFieldPath = popupBasePath.append("input[name='ss']");
        
        selenium.type(filterFieldPath, filterText);
        selenium.keyUp(filterFieldPath.generateLocatorString(), "\\13");
        selenium.waitForAjax();
    }
    
    /**
     * This method generates the full path for a given picker span.  This then can be used to click the link of a picker popup.
     */
	protected Locator generatePickerLinkPath() {
        String pickerBase = getParams("menuId");
        return new CssLocator("span#picker_"+pickerBase+"Picker_label a");
    }
    
    protected Locator generatePickerPopupHtmlBasePath() {
        String pickerBase = getParams("menuId");
        return new CssLocator("div#"+pickerBase+"Picker");
    }
    
    private Locator getInputPath(String buttonValue) {
        return generatePickerPopupHtmlBasePath().append("input[value="+buttonValue+"]");
    }

}
