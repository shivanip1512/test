/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.locators.CssLocator;
import com.cannontech.selenium.core.locators.Locator;

/**
 * Class handles functions that are available and unique to operations page in Yukon Application.<br>
 * 
 * @author anuradha.uduwage
 *
 */
public class OperationsPageSolvent extends AbstractSolvent {

	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}
	
	/**
	 * Click any link on the page by its name. 
	 * @param linkName name of the link
	 * @return
	 */
	public void clickLinkItem(String linkName) {
		Locator linkLocator = new CssLocator("a:contains('"+linkName+"')");
		
		selenium.waitForElement(linkLocator);
		selenium.click(linkLocator);
		selenium.waitForPageToLoad();
	}
	
	/**
	 * Click links on the operations page based on the category name. <br>
	 * Ex: to click a link under Analysis, this method can be used as clickLinkItemByCategory("Analysis", Commander");
	 * 
	 * @param category name of the category.
	 * @param linkName name of the link.
	 * @return
	 */
	public void clickLinkItemByCategory(String category, String linkName) {
		String linkLocator = "//div[text()='" + category + "']/following::a[text()='" + linkName + "']";
		selenium.waitForElement(linkLocator);
		selenium.click(linkLocator);		
	}

}
