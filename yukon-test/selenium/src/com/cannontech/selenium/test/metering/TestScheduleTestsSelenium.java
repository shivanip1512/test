package com.cannontech.selenium.test.metering;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.metering.AllSchedulesTableSolvent;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;
import com.cannontech.selenium.solvents.metering.MeterScheduleSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;

/**
 * Test verifies test schedule functionality
 * @author ricky.jones
 */
public class TestScheduleTestsSelenium extends SolventSeleniumTestCase {
	/**
	 * Test method logs in as yukon, yukon and check all the links in navigation
	 * page.
	 */
	public void init() {
		//use the LoginSolvent to login
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	/**
	 * This method returns the nearest full 5 minutes interval to the current time
	 * @param minutes
	 * @return
	 */
	private int getNextFiveMinuteInterval(int minutes){
		for(int i = 0; i <= 60; i+=5){
			if(i > minutes+5){
				minutes = i;
				i = 100;
			}
		}
		if(minutes == 60)
			return 0;
		else
			return minutes;
	}
	/**
	 * This method sets the test schedule to the next full five minute interval
	 */
	public void setSchedule(){
		MeterScheduleSolvent schedule = new MeterScheduleSolvent();
		CommonSolvent common = new CommonSolvent();
		//Set schedule to nearest time
		int hours = Integer.parseInt(common.getCurrentTime("h"));
		int minutes = getNextFiveMinuteInterval(Integer.parseInt(common.getCurrentTime("mm")));
		String minuteString;
		String hourString;
		if(minutes == 0){
			hours++;
			minuteString = "00";
		} else{
			minuteString = Integer.toString(minutes);
		}
		hourString = Integer.toString(hours);
		
		schedule.selectAttributeTimeFrequency("One-Time");
		schedule.selectHoursForAttribute(hourString);
		schedule.selectMinutesForAttribute(minuteString);
		schedule.selectAM_PMForAttribute(common.getCurrentTime("a"));
	}

	/**
	 * Test creates a demand test schedule and verifies that it can be enabled/disabled
	 * and then deletes it
	 */
	@Test
	public void createScheduledRequest1() {		
		init();
		CommonSolvent common = new CommonSolvent();
		MeterScheduleSolvent schedule = new MeterScheduleSolvent();
		AllSchedulesTableSolvent table = new AllSchedulesTableSolvent();
		DeviceGroupPopupSolvent device = new DeviceGroupPopupSolvent();
		MeteringSolvent metering = new MeteringSolvent();
		WidgetSolvent widget = new WidgetSolvent();

		//Create Auto Scheduled Request 1
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		widget.clickButtonInWidgetByType("Scheduled Requests","submit");
		schedule.enterTxtToAttributeFormField("Schedule Name:", "autoTestSchedule");
		common.selectFromMultiListBox("Select Attribute:", "Demand");
		schedule.clickDeviceGroupIconAttributeForm();
		setSchedule();
		//device.expandByName("Meters");
		device.selectTreeNode("Meters");
		device.clickSelectGroupButton();
		
		schedule.clickButtonInAttributeTab("Schedule");
		waitFiveSeconds();
		Assert.assertEquals("Metering: Schedule Detail (autoTestSchedule)", common.getPageTitle());
		//Verify name
		Assert.assertEquals(true, common.isTextPresent("autoTestSchedule"));
		//Verify Request type
		Assert.assertEquals(true, common.isTextPresent("Scheduled Group Attribute Read"));
		//Verify Device group
		Assert.assertEquals(true, common.isLinkPresent("/Meters"));
		
		//Verify user
		Assert.assertEquals(true, common.isTextPresent("syukon"));
		//Verify status
		Assert.assertEquals(true, common.isTextPresent("Scheduled"));
		//Verify last run
		Assert.assertEquals(true, common.isTextPresent("N/A"));
		//Verify executions
		Assert.assertEquals(true, common.isLinkPresent("View All Executions"));

		common.clickLinkByName("All");
		common.isTextPresent("autoTestSchedule");

		//Verify request type
		//table.clickRowByColumnValue("autoTestSchedule");
		common.clickLinkByName("autoTestSchedule");
		waitFiveSeconds();
		Assert.assertEquals("Metering: Schedule Detail (autoTestSchedule)", common.getPageTitle());
		
		//Verify name
		Assert.assertEquals(true, common.isTextPresent("autoTestSchedule"));
		common.clickButtonByNameWithPageLoadWait("Edit");
		schedule.clickButtonInAttributeTab("Disable");
		waitFiveSeconds();
		
		//verify schedule is disabled
		Assert.assertEquals(true, common.isTextPresent("Disabled"));
		common.clickLinkByName("All");
		common.clickLinkByName("autoTestSchedule");
		waitFiveSeconds();
		Assert.assertEquals("Metering: Schedule Detail (autoTestSchedule)", common.getPageTitle());
		common.clickButtonByNameWithPageLoadWait("Edit");
		schedule.clickButtonInAttributeTab("Enable");
		
		//verify schedule is enabled
		Assert.assertEquals(true, common.isTextPresent("Scheduled"));
		common.clickLinkByName("All");
		Assert.assertEquals(true, common.isTextPresent("autoTestSchedule"));
		common.clickLinkByName("autoTestSchedule");
		waitFiveSeconds();
		Assert.assertEquals("Metering: Schedule Detail (autoTestSchedule)", common.getPageTitle());

		common.clickButtonByNameWithPageLoadWait("Edit");
		//delete schedule
		schedule.clickButtonInAttributeTab("Delete");
		waitFiveSeconds();
		common.end();
	}


	/**
	 * Test verifies a scheduled command can be created and deleted
	 */
	@Test
	public void createScheduledCommand() {
		init();
		CommonSolvent common = new CommonSolvent();
		MeterScheduleSolvent schedule = new MeterScheduleSolvent();
		DeviceGroupPopupSolvent meters = new DeviceGroupPopupSolvent();
		AllSchedulesTableSolvent table = new AllSchedulesTableSolvent();
		MeteringSolvent metering = new MeteringSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		widget.clickButtonInWidgetByType("Scheduled Requests","submit");
		schedule.clickCommandTab();
		schedule.enterTxtToCommandFormField("Schedule Name:", "autoTestScheduleCmd");
		common.selectDropDownMenu("scheduledGroupRequestExecution/schedule", "commandSelectValue", "Read Energy");
		
		schedule.clickDeviceGroupIconCmdForm();
		meters.selectTreeNode("Meters");
		meters.clickSelectGroupButton();
		
		schedule.clickButtonInCommandTab("Schedule");
		waitFiveSeconds();
		common.clickLinkByName("All");
		
		Assert.assertEquals(true, common.isTextPresent("autoTestScheduleCmd"));
		
		//delete schedule
		common.clickLinkByName("autoTestScheduleCmd");
		Assert.assertEquals("Metering: Schedule Detail (autoTestScheduleCmd)", common.getPageTitle());
		common.clickButtonByNameWithPageLoadWait("Edit");
		schedule.clickButtonInCommandTab("Delete");
		waitFiveSeconds();
		Assert.assertFalse(common.isTextPresent("autoTestScheduleCmd"));
		common.end();
	}
}