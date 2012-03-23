/**
 * 
 */
package com.cannontech.selenium.solvents.metering;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * Class handle Schedule Scripts table in metering.
 * @author anuradha.uduwage
 *
 */
public class ScheduleScriptsTableSolvent extends CommonTableSolvent {

	/**
	 * Default constructor sets the root level element for the table.
	 * @param params
	 */
	public ScheduleScriptsTableSolvent(String... params) {
		super("tableClass=resultsTable");
	}
	
	/**
	 * Method click disable button based on a schedule name to diable a meter schedule.
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public ScheduleScriptsTableSolvent clickDisableByScheduleName(String scheduleName) {
		String disableButtonLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + scheduleName + "']/following::button[1]";
		String scheduleLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + scheduleName + "']";
		if(!selenium.isElementPresent(scheduleLocator))
			throw new SolventSeleniumException("Schedule name " + scheduleName + " is not available at" + scheduleLocator);
		selenium.waitForElement(disableButtonLocator);
		if(!selenium.isElementPresent(disableButtonLocator))
			throw new SolventSeleniumException("Disable button is not available at " + disableButtonLocator);
		selenium.click(disableButtonLocator);
		selenium.waitForAjax();
		return this;
	}
	
	/**
	 * Method click the enable button based on a schedule name to enable a meter schedule..
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public ScheduleScriptsTableSolvent clickEnableByScheduleName(String scheduleName) {
		String enableButtonLocator = getXpathRoot() + "//td[normalize-space(text())='" + scheduleName + "']/following::button[1]";
		String scheduleLocator = getXpathRoot() + "//td[normalize-space(text())='" + scheduleName + "']";
		if(!selenium.isElementPresent(scheduleLocator))
			throw new SolventSeleniumException("Schedule name " + scheduleName + " is not available at" + scheduleLocator);
		selenium.waitForElement(enableButtonLocator);
		selenium.click(enableButtonLocator);
		selenium.waitForAjax();
		return this;
	}
	
	/**
	 * Returns the value of the current state based on a schedule name.
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public String getCurrentStateBySchedule(String scheduleName) {
		String stateLocator = getXpathRoot() + "//td//following::*[contains(text(), '" + scheduleName + "')]/following::td[2]";
		String scheduleLocator = getXpathRoot() + "//td//following::*[contains(text(), '" + scheduleName + "')]";
		if(!selenium.isElementPresent(scheduleLocator))
			throw new SolventSeleniumException("Schedule with the name " + scheduleName + " is not available at " + scheduleLocator);
		if(!selenium.isElementPresent(stateLocator))
			throw new SolventSeleniumException("Current State Value for schedule " + scheduleName + " is not available at " + stateLocator);
		return selenium.getText(stateLocator);
	}
	
	/**
	 * Returns the value of the start date/time field for schedule.
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public String getStartDateTimeBySchedule(String scheduleName) {
		String startTimeLocator = getXpathRoot() + "//td//following::*[contains(text(), '" + scheduleName + "')]/following::td[3]";
		String scheduleLocator = getXpathRoot() + "//td//following::*[contains(text(), '" + scheduleName + "')]";
		if(!selenium.isElementPresent(scheduleLocator))
			throw new SolventSeleniumException("Schedule with the name " + scheduleName + " is not available at " + scheduleLocator);
		if(!selenium.isElementPresent(startTimeLocator))
			throw new SolventSeleniumException("Start Date/Time Value for schedule " + scheduleName + " is not available at " + startTimeLocator);
		return selenium.getText(startTimeLocator);		
	}
	
	/**
	 * Returns the value of the stop date/time field for schedule.
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public String getStopDateTimeBySchedule(String scheduleName) {
		String stopTimeLocator = getXpathRoot() + "//td//following::*[contains(text(), '" + scheduleName + "')]/following::td[4]";
		String scheduleLocator = getXpathRoot() + "//td//following::*[contains(text(), '" + scheduleName + "')]";
		if(!selenium.isElementPresent(scheduleLocator))
			throw new SolventSeleniumException("Schedule with the name " + scheduleName + " is not available at " + scheduleLocator);
		if(!selenium.isElementPresent(stopTimeLocator))
			throw new SolventSeleniumException("Stop Date/Time Value for schedule " + scheduleName + " is not available at " + stopTimeLocator);
		return selenium.getText(stopTimeLocator);		
	}
	
	/**
	 * Returns boolean value true if schedule is enable or returns false if schedule is disable.
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public boolean isScheduleEnable(String scheduleName) {
		boolean enableOrDisable = false;
		String buttonTextLocator = getXpathRoot() + "//td//following::*[contains(text(), '" + scheduleName + "')]/following::button[1]/text()";
		String scheduleLocator = getXpathRoot() + "//td//following::*[contains(text(), '" + scheduleName + "')]";

		selenium.waitForElement(scheduleLocator);
		if(!selenium.isElementPresent(buttonTextLocator)) {
			throw new SolventSeleniumException("Disable/Enable button is not available for schedule '" + scheduleName +"' at " + buttonTextLocator);
		}
		String buttonValue = null;
		buttonValue = selenium.getText(buttonTextLocator);
		if(buttonValue.trim().equals("Disable")) {
			enableOrDisable = true;
		}
		return enableOrDisable;
	}
}
