/**
 * 
 */
package com.cannontech.selenium.solvents.metering;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This solvent handle item picker popup windows which are under metering section. 
 * This class was created due to the inconsistency of item pickers between cap control and metering item pickers.
 * 
 * @author anuradha.uduwage
 *
 */
public class MeterItemPickerSolvent extends CommonTableSolvent {

	/**
	 * Default constructor sets the table class for picker results table.
	 * @param params
	 */
	public MeterItemPickerSolvent(String... params) {
		super("tableClass=compactResultsTable pickerResultTable");
	}

	@Override
	public void prepare() {
		//nothing to do at this time.
	}
	
	/**
	 * Method enter query string in item picker popup window.
	 * @param searchString text to be search.
	 * @return
	 */
	public MeterItemPickerSolvent enterQueryTerm(String searchString) {
		String queryLocator = "//label[normalize-space(text())='Query:']/input[1]";
		selenium.waitForElement(queryLocator);
		if(!selenium.isElementPresent(queryLocator))
			throw new SolventSeleniumException("Unable to enter values for 'Query: input at " + queryLocator);
		selenium.typeKeys(queryLocator, searchString);
		selenium.waitForAjax();
		return this;
	}
	
	/**
	 * Get previous arrow to get items in picker.
	 * @return
	 */
	public MeterItemPickerSolvent clickPrevious() {
		String previous = "//td[@class='previousLink']//div[@class='enabledAction']//img[1]";
		selenium.waitForElement(previous);
		if(!selenium.isElementPresent(previous))
			throw new SolventSeleniumException("Previous Arrow link is not available at " + previous);
		selenium.click(previous);
		return this;
	}
	
	/**
	 * Click next arrow to get items in picker.
	 * @return
	 */
	public MeterItemPickerSolvent clickNext() {
		String next = "//td[@class='nextLink']//div[@class='enabledAction']//img[1]";
		selenium.waitForElement(next);
		if(!selenium.isElementPresent(next))
			throw new SolventSeleniumException("Next Arrow link is not available at " + next);
		selenium.click(next);
		return this;
	}	

}
