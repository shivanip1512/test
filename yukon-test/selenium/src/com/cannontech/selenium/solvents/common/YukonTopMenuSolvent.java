/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import com.cannontech.selenium.core.AbstractSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Yukon top menu bar is common for multiple areas of the application. This class 
 * is a place holder for all the methods represent top menu bar.
 * @author anuradha.uduwage
 *
 */
public class YukonTopMenuSolvent extends AbstractSolvent {

	public YukonTopMenuSolvent(String...params) {
		// TODO: Nothing to do at this point.
	}

	@Override
	public void prepare() {
		//nothing to do at this point.
	}	
	
	/**
	 * Given a string locator this method will select the option from menu.
	 * @param option string value of the option in dropdown menu.
	 * @return
	 */
	public void selectALocation(String option) {
		String selectLocator = "//div[@id='Menu']//select[@onchange='javascript:window.location=(this[this.selectedIndex].value);']";

		selenium.waitForElement(selectLocator, CommonSolvent.WAIT_FOR_ELEMENT_TIMEOUT);

		selenium.select(selectLocator, option);
		selenium.waitForPageToLoad();
	}
	
	/**
	 * Method to take the user to home location
	 * @return
	 */
	public void clickHome() {
		String home = "//a[@class='stdhdr_menuLink border_menuLink' and normalize-space(text())='Home']";

		selenium.waitForElement(home, CommonSolvent.WAIT_FOR_ELEMENT_TIMEOUT);
		
		selenium.click(home);
		selenium.waitForPageToLoad();
	}
	/**
	 * Function to click home link when you are in All Trend Page
	 * TODO: at somepoint we will combine above method and this.
	 * @return
	 */
	public YukonTopMenuSolvent clickAllTrendsHome() {
		String allTrendHome = "//a[normalize-space(text())='Home']";
		selenium.waitForElement(allTrendHome, 2000);
		if(!selenium.isElementPresent(allTrendHome))
			throw new SeleniumException("Could not find 'Home' link on All Trends Page");
		selenium.click(allTrendHome);
		return this;
	}
	
	/**
	 * This method should provide access to all the bread crumbs links at the top menu.
	 * @param bcrumbName name of the bread-crumbs link.
	 * @return
	 */
	public YukonTopMenuSolvent clickBreadcrumb(String bcrumbName) {
		String linklocator = "//div[@class='breadCrumbs']//a[normalize-space(text())='" + bcrumbName + "']";
		selenium.waitForElement(linklocator, CommonSolvent.WAIT_FOR_ELEMENT_TIMEOUT);
		selenium.click(linklocator);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Click any menu item from top menu, example Metering, Billing, Scripts etc.
	 * @param menuItem Menu name.
	 * @return
	 */
	public YukonTopMenuSolvent clickTopMenuItem(String menuItem) {
		String locator = "//div[@id='topMenu']//a[(text() = '" + menuItem + "')]";
		selenium.waitForElement(locator, CommonSolvent.WAIT_FOR_ELEMENT_TIMEOUT);
		selenium.click(locator);
		selenium.waitForPageToLoad();

		return this;
	}

	   /**
     * Click any menu item from top menu, example Metering, Billing, Scripts etc.
     * @param menuItem Menu name.
     * @return
     */
    public YukonTopMenuSolvent clickTopMenuItem(String topLevelMenuItem, String secondLevelMenuItem) {
        String topLevelLocator = "//div[@id='topMenu']//a[(text() = '" + topLevelMenuItem + "')]";
        selenium.waitForElement(topLevelLocator, CommonSolvent.WAIT_FOR_ELEMENT_TIMEOUT);
        selenium.click(topLevelLocator);

        String secondLevelLocator = "//div[@id = 'subMenu']//a[text() = '" + secondLevelMenuItem + "']";
        selenium.waitForElement(secondLevelLocator, CommonSolvent.WAIT_FOR_ELEMENT_TIMEOUT);
        selenium.click(secondLevelLocator);
        selenium.waitForPageToLoad();

        return this;
    }
	
	/**
	 * Method clicks any sub menu item , example Area, Special Area or Billing Setup etc.
	 * @param menuName string value of the sub menu item.
	 * @return
	 */
	public void clickTopSubMenuItem(String menuName) {
		String menuLocator = "//div[@id='subMenu']//a[@title='Goto " + menuName + "' and text()='"+ menuName +"']";

		selenium.waitForElement(menuLocator);
		selenium.click(menuLocator);
		selenium.waitForPageToLoad();
	}
}
