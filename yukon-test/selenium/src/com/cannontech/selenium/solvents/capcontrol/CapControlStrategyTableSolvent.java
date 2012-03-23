/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Handles Cap Control results table with strategies.
 * @author anuradha.uduwage
 *
 */
public class CapControlStrategyTableSolvent extends YukonTableSolvent {

	/**
	 * Default constructor sets the strategies results tableId
	 * @param params
	 */
	public CapControlStrategyTableSolvent(String... params) {
		super("tableId=strategyTable");
	}
	
	/**
	 * Delete schedule from Cap Control Schedule results table.
	 * @param strategyName name of the schedule.
	 * @return
	 */
	public CapControlStrategyTableSolvent deleteStrategy(String strategyName) {
		String locator = getXpathRoot() + "//tr[contains(., '" + strategyName + "')][1]//td//a[2]";
		waitForJSObject(2000);
		if(!selenium.isElementPresent(locator)) {
			throw new SeleniumException("Unable to find device name " + strategyName + " in table with ID " + super.getTableId() + 
					" at " + locator);
		}
		selenium.click(locator);
		selenium.waitForConfirmation();
		selenium.chooseOkOnNextConfirmation();
		return this;
	}
	
	/**
	 * Click edit button based on the name of the schedule.
	 * @param strategyName name of the schedule.
	 * @return
	 */
	public CapControlStrategyTableSolvent editStrategy(String strategyName) {
		String locator = getXpathRoot() + "//tr[contains(., '" + strategyName + "')][1]//td//a[1]";
		waitForJSObject(2000);
		if(!selenium.isElementPresent(locator)) {
			throw new SeleniumException("Unable to find device name " + strategyName + "in table with ID " + super.getTableId() + 
					" at " + locator);
		}
		selenium.click(locator);
		return this;
	}	

}
