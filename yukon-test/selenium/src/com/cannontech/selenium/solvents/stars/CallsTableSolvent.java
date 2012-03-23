/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handles call information table under call tracking option.
 * @author anuradha.uduwage
 *
 */
public class CallsTableSolvent extends CommonTableSolvent {

	/**
	 * Default constructor setting the tableClass value.
	 * @param params
	 */
	public CallsTableSolvent(String... params) {
		super("tableClass=compactResultsTable callListTable rowHighlighting");
	}
	
	/**
	 * Method remove the Call number based on the given call number.
	 * @param number value of the call number.
	 * @return
	 */
	public CallsTableSolvent deleteByCallNumber(String number) {
		String numberRemoveLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + number + "']//following::input[@type='submit'][1]";
		String numberLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + number + "']";
		selenium.waitForElement(numberLocator);
		selenium.waitForElement(numberRemoveLocator);
		selenium.click(numberRemoveLocator);
		return this;
	}
	
	/**
	 * Method to click the call number link.
	 * @param number value of the call number.
	 * @return
	 */
	public CallsTableSolvent clickCallNumberLink(String number) {
		String numberLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + number + "']";

		selenium.waitForElement(numberLocator);
		selenium.click(numberLocator);
		return this;
	}

}
