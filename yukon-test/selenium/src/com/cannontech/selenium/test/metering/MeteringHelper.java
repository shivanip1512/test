/**
 * 
 */
package com.cannontech.selenium.test.metering;

import org.junit.Assert;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.OperationsPageSolvent;
import com.cannontech.selenium.solvents.metering.MeterScheduleSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;
import com.cannontech.selenium.solvents.metering.ScheduleScriptsTableSolvent;

/**
 * This class will have helper methods to access functionalities in Metering section 
 * of the yukon application.
 * 
 * @author anuradha.uduwage
 *
 */
public class MeteringHelper extends SolventSeleniumTestCase {
	

	/**
	 * Method navigates to the meter detail page by searching for the meter from the 
	 * meter home page and verifies that the meter is present and enabled
	 */
	public void navigateToMeterDetail(String meter){
		CommonSolvent common = new CommonSolvent();
		MeteringSolvent metering = new MeteringSolvent();
		Assert.assertEquals("Metering", common.getPageTitle());
		
        common.enterInputTextByFormId("filterForm", "Quick Search", meter);
		common.clickButtonBySpanText("Search");

		//Verify meter was found
		Assert.assertEquals(true, common.isTextPresent(meter));
		//Verify meter's status is enabled
		Assert.assertEquals(true, common.isTextPresent("Enabled"));
	}
	
	  /**
	   * Method parses a string to verify an integer >= 0 exists.
	   * @param str
	   */
	  protected boolean valueExists(String str){
		  String[] split = str.split(" ");
		  boolean value = false;
		  for(int i = 0; i < split.length; i++){
			  try{
				  if(Integer.parseInt(split[i]) >= 0)
					  value = true;
			  } catch(Exception e) { ;}
		  }
		  return true;
	  }
	  
		
	/**
	 * Method pauses test run until the current time is within a second of the next minute
	 */
	protected void waitUntilNextMinute(){
        long t1;
        do{
            t1 = System.currentTimeMillis();
        }
        while (t1 % 60000 > 1000);
    }
	
	/**
	 * Method sets the schedule script start time from 1 minute from current time and stop time 2 minutes
	 * from the current time.
	 * @param meterName name of the meter.
	 */
	protected void setOneMinuteTestSchedule(String meterName){
		waitUntilNextMinute();
		CommonSolvent common = new CommonSolvent();
		ScheduleScriptsTableSolvent table = new ScheduleScriptsTableSolvent();
		MeterScheduleSolvent schedule = new MeterScheduleSolvent();
		
		table.clickLinkInARow(meterName);
		common.clickRadioButtonByName("Time");
		String time = schedule.getScheduleStartTime();
		String[] timeSplit = time.split("[ ]");
		time = timeSplit[0];
		String[] timeSplit2 = time.split("[':']");
		int minutes = Integer.parseInt(timeSplit2[1]);
		int hours = Integer.parseInt(timeSplit2[0]);
		boolean changeStopTime = false;
		if(minutes == 59){
			hours++;
			minutes = -1;
		}else if(minutes == 58)
			changeStopTime = true;
		minutes++;
		timeSplit2[1] = Integer.toString(minutes);
		String startTime = timeSplit2[0]+":"+timeSplit2[1]+" "+timeSplit[1];
		String stopTime;
		if(changeStopTime)
			stopTime = Integer.toString(hours++)+":00 "+timeSplit[1];
		else{
			minutes++;
			stopTime = timeSplit2[0]+":"+Integer.toString(minutes)+" "+timeSplit[1];
		}
		schedule.setScheduleStartTime(startTime);
		schedule.setScheduleStopTime(stopTime);
		common.clickButtonByName("Apply");
	}
	
	/**
	 * Method navigates to the Scripts page from the home page
	 */
	protected void navigateToScriptsPage(){
		CommonSolvent common = new CommonSolvent();
		new OperationsPageSolvent().clickLinkItem("Metering");
		waitTenSeconds();
		Assert.assertEquals("Metering", common.getPageTitle());
		common.clickLinkByName("Scripts");	
	}
	
	/**
	 * Method navigates to the Scripts page from the home page
	 */
	protected void navigateToMeteringReportsPage(){
		CommonSolvent common = new CommonSolvent();
		new OperationsPageSolvent().clickLinkItem("Reporting");
		waitTenSeconds();
		Assert.assertEquals("Reports", common.getPageTitle());
		common.clickLinkByName("Metering");	
	}
	
	/**
	 * Method disables a selected schedule.
	 * TODO: Remove if regression runs without any issue.
	 * @param schedule the schedule to disable
	 */
	protected void disableSchedule(String schedule){
		ScheduleScriptsTableSolvent table = new ScheduleScriptsTableSolvent();		
		table.clickDisableByScheduleName(schedule);
		for(int i= 0; i < 3; i++)
			waitTenSeconds();	//Waits for current state to finish updating
	}
	
	/**
	 * Method enables a selected schedule
	 * TODO: Remove if regression runs without any issue.
	 * @param schedule the schedule to enable
	 */
	protected void enableSchedule(String schedule){
		ScheduleScriptsTableSolvent table = new ScheduleScriptsTableSolvent();
		table.clickEnableByScheduleName(schedule);
	}
	
	/**
	 * Method runs a selected schedule
	 * @param schedule the schedule to run
	 */
	protected void runSchedule(String schedule){
		CommonSolvent common = new CommonSolvent();
		ScheduleScriptsTableSolvent table = new ScheduleScriptsTableSolvent();
		table.clickLinkInARow(schedule);
		common.clickButtonByName("Apply");
	}
	
	/**
	 * Method stops a selected schedule.
	 * @param schedule the schedule to stop
	 */
	protected void stopSchedule(String scheduleName){
		CommonSolvent common = new CommonSolvent();
		ScheduleScriptsTableSolvent scriptsTable = new ScheduleScriptsTableSolvent();
		
		scriptsTable.clickLinkInARow(scheduleName);
		common.clickButtonByName("Apply");
		if(!scriptsTable.getCurrentStateBySchedule(scheduleName).equals("Waiting")) {
			scriptsTable.clickLinkInARow(scheduleName);
			common.clickButtonByName("Apply");			
		}
		String schedule;
		do {
			waitTenSeconds();
			schedule = scriptsTable.getCurrentStateBySchedule(scheduleName);
		}while(!schedule.equals("Waiting"));
	}
	
	/**
	 * Return the KWH Value or Voltage value after splitting the meter reading value from "Usage Reading" or 
	 * Voltage reading.
	 * 
	 * @param label name of the label. 
	 * @return
	 */
	protected String getKWHOrVoltageValue(String label) {
		CommonSolvent common = new CommonSolvent();
		String[] kwhOrVoltage = common.getLabelValuesAfterSplit(label);
		return kwhOrVoltage[0];
	}


}
