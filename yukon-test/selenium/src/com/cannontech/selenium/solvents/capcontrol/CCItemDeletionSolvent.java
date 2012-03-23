/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;

/**
 * This solvent handle the functions in Item Deletion page in Cap Control.
 * @author anuradha.uduwage
 *
 */
public class CCItemDeletionSolvent extends YukonTableSolvent {

	/**
	 * Default constructor sets the table id.
	 * @param params
	 */
	public CCItemDeletionSolvent(String... params) {
		super("tableId=deleteForm:body");
	}
	
	/**
	 * Method click the Submit button at the bottom of the item deletion page.
	 * @return
	 */
	public CCItemDeletionSolvent clickSubmitButton() {
		String submitButtonLocator = getXpathRoot() + "//tr//input[@value='Submit']";
		if(!selenium.isElementPresent(submitButtonLocator, 9000))
			throw new SolventSeleniumException("Unable to find Submit button at " + submitButtonLocator);
		selenium.click(submitButtonLocator);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Method click the Return button at the bottom of the item deletion page.
	 * @return
	 */
	public CCItemDeletionSolvent clickReturnButton() {
		String returnButtonLocator = getXpathRoot() + "//tr//input[@value='Return']";
		selenium.waitForElement(returnButtonLocator);
		if(!selenium.isElementPresent(returnButtonLocator, 9000))
			throw new SolventSeleniumException("Unable to find Submit button at " + returnButtonLocator);
		selenium.click(returnButtonLocator);
	        selenium.waitForPageToLoad();
		return this;		
	}

}
