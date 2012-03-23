/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Handles Cap Control results table with schedules.
 * @author anuradha.uduwage
 *
 */
public class CapControlScheduleTableSolvent extends YukonTableSolvent {

	/**
	 * Default constructor sets the schedules results tableId
	 * @param params
	 */
	public CapControlScheduleTableSolvent(String... params) {
		super("tableId=scheduleTable");
	}
	
	/**
	 * Delete schedule from Cap Control Schedule results table.
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public CapControlScheduleTableSolvent deleteSchedule(String scheduleName) {
		String locator = getXpathRoot() + "//tr[contains(., '" + scheduleName + "')][1]//td//a[2]";
		waitForJSObject(2000);
		if(!selenium.isElementPresent(locator)) {
			throw new SeleniumException("Unable to find device name " + scheduleName + "in table with ID " + super.getTableId() + 
					" at " + locator);
		}
		selenium.click(locator);
		selenium.waitForConfirmation();
		selenium.chooseOkOnNextConfirmation();
		return this;
	}
	
	/**
	 * Click edit button based on the name of the schedule.
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public CapControlScheduleTableSolvent editSchedule(String scheduleName) {
		String locator = getXpathRoot() + "//tr[contains(., '" + scheduleName + "')][1]//td//a[1]";
		waitForJSObject(2000);
		if(!selenium.isElementPresent(locator)) {
			throw new SeleniumException("Unable to find device name " + scheduleName + "in table with ID " + super.getTableId() + 
					" at " + locator);
		}
		selenium.click(locator);
		return this;
	}

}
