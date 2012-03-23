/**
 * 
 */
package com.cannontech.selenium.solvents.metering;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;

/**
 * Handle the all schedules table under schedules section in metering.
 * @author anuradha.uduwage
 *
 */
public class AllSchedulesTableSolvent extends YukonTableSolvent {

	/**
	 * Default constructor setting up root level table id.
	 * @param params
	 */
	public AllSchedulesTableSolvent(String... params) {
		super("tableId=jobsTable");
	}
	
	/**
	 * Method return the Request Type based on the given schedule name from all schedules table.
	 * @param scheduleName name of the schedule.
	 * @return requestType request type value for schedule name.
	 */
	public String getRequestTypeByScheduleName(String scheduleName) {
		String scheduleLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']";
		String requestTypeLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']/following::td[1]";
		selenium.waitForElement(scheduleLocator);
		if(!selenium.isElementPresent(requestTypeLocator))
			throw new SolventSeleniumException("Request type is not available for " + scheduleName + " at " + requestTypeLocator);
		String requestTypeValue = selenium.getText(requestTypeLocator);
		return  requestTypeValue;
	}
	
	/**
	 * Method return the Attribute/Command based on the given schedule name from all schedules table.
	 * @param scheduleName name of the schedule.
	 * @return commandValue Attribute/Command value for the schedule.
	 */
	public String getCommandByScheduleName(String scheduleName) {
		String scheduleLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']";
		String commandLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']/following::td[2]";
		selenium.waitForElement(scheduleLocator);
		if(!selenium.isElementPresent(commandLocator))
			throw new SolventSeleniumException("Attribute/Command is not available for " + scheduleName + " at " + commandLocator);
		String commandValue = selenium.getText(commandLocator);
		return commandValue;
	}
	
	/**
	 * Method return the Run Schedule based on the given schedule name from all schedules table. 
	 * @param scheduleName name of the schedule.
	 * @return runSchedule Run Schedule value for the schedule.
	 */
	public String getRunScheduleByScheduleName(String scheduleName) {
		String scheduleLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']";
		String runScheduleLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']/following::td[3]";
		selenium.waitForElement(scheduleLocator);
		if(!selenium.isElementPresent(runScheduleLocator))
			throw new SolventSeleniumException("Run Schedule is not abvailable for " + scheduleName + " at " + runScheduleLocator);
		String runSchedule = selenium.getText(runScheduleLocator);
		return runSchedule;
	}
	
	/**
	 * Method return last run date and time value for the given schedule name from all schedules table.
	 * @param scheduleName name of the schedule.
	 * @return lastRunValue date and time string of the last run for the schedule.
	 */
	public String getLastRunByScheduleName(String scheduleName) {
		String scheduleLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']";
		String lastRunScheduleLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']/following::td[4]";
		selenium.waitForElement(scheduleLocator);
		if(!selenium.isElementPresent(lastRunScheduleLocator))
			throw new SolventSeleniumException("Date & time of last run is not abvailable for " + scheduleName + " at " + lastRunScheduleLocator);
		String lastRunValue = selenium.getText(lastRunScheduleLocator);
		return lastRunValue;		
	}
	
	/**
	 * Method return next run date and time value for the given schedule name from all schedules table.
	 * @param scheduleName
	 * @return
	 */
	public String getNexRunByScheduleName(String scheduleName) {
		String scheduleLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']";
		String nextRunLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']/following::td[5]";
		selenium.waitForElement(scheduleLocator);
		if(!selenium.isElementPresent(nextRunLocator))
			throw new SolventSeleniumException("Date & time of last run is not abvailable for " + scheduleName + " at " + nextRunLocator);
		String nextRunValue = selenium.getText(nextRunLocator);
		return nextRunValue;			
	}

	/**
	 * Method return the status of the schedule based on the given schedule name from all schedules table.
	 * @param scheduleName name of the schedule.
	 * @return statusValue Current status of the schedule.
	 */
	public String getStatusByScheduleName(String scheduleName) {
		String scheduleLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']";
		String statusLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + scheduleName + "']/following::td[6]";
		selenium.waitForElement(scheduleLocator);
		if(!selenium.isElementPresent(statusLocator))
			throw new SolventSeleniumException("Status is not abvailable for " + scheduleName + " at " + statusLocator);
		String statusValue = selenium.getText(statusLocator);
		return statusValue;				
	}
}
