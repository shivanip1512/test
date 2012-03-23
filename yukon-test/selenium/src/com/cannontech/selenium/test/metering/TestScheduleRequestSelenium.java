package com.cannontech.selenium.test.metering;

import org.junit.Assert;

import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;
import com.cannontech.selenium.solvents.metering.MeterScheduleSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;

/**
 * This test verifies that a scheduled request can be created, edited, and deleted
 * @author ricky.jones
 *
 */
public class TestScheduleRequestSelenium extends SolventSeleniumTestCase {
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
	 * Test creates a new scheduled request called Auto Scheduled Request.
	 */
	@Test
	public void createScheduledRequest() {
		init();
		MeterScheduleSolvent schedule = new MeterScheduleSolvent();
		DeviceGroupPopupSolvent device = new DeviceGroupPopupSolvent();
		CommonSolvent common = new CommonSolvent();
		MeteringSolvent metering = new MeteringSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		//Create Auto Scheduled Request 1
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		
		widget.clickButtonInWidgetByType("Scheduled Requests","submit");
		schedule.enterTxtToAttributeFormField("Schedule Name:", "Auto Scheduled Request 1");
		common.selectFromMultiListBox("Select Attribute:", "Blink Count");
		schedule.clickDeviceGroupIconAttributeForm();
		device.selectTreeNode("Meters");
		device.clickSelectGroupButton();
		Assert.assertEquals(true, common.isTextPresent("/Meters"));
		schedule.clickButtonInAttributeTab("Schedule");
		waitFiveSeconds();
		Assert.assertEquals("Metering: Schedule Detail (Auto Scheduled Request 1)", common.getPageTitle());
		
		//Verify Test 1 widget was created
		Assert.assertEquals(true, common.isTextPresent("Auto Scheduled Request 1"));
		common.end();
	}
	/**
	 * Test creates another schedule and disables both schedules, changes the name of one, and deletes both. 
	 */
	@Test
	public void createEditDeleteScheduledRequest() {
		init();
		CommonSolvent common = new CommonSolvent();
		MeterScheduleSolvent schedule = new MeterScheduleSolvent();
		DeviceGroupPopupSolvent device = new DeviceGroupPopupSolvent();
		MeteringSolvent meters = new MeteringSolvent();
		MeteringSolvent metering = new MeteringSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		//Create Auto Scheduled Request 2
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		widget.clickButtonInWidgetByType("Scheduled Requests","submit");
		schedule.enterTxtToAttributeFormField("Schedule Name:", "Auto Scheduled Request 2");
		common.selectFromMultiListBox("Select Attribute:", "Blink Count");
		
		//Customized method to click device group icon in schedule page.
		schedule.clickDeviceGroupIconAttributeForm();
		device.selectTreeNode("Meters");
		device.clickSelectGroupButton();
		Assert.assertEquals(true, common.isTextPresent("/Meters"));
		schedule.clickButtonInAttributeTab("Schedule");
		waitFiveSeconds();
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		
		//Verify Test 2 widget was created
		Assert.assertEquals(true, common.isTextPresent("Auto Scheduled Request 2"));
		
		//Disable monitors and Verify Test 1 is disabled 
		meters.disableByWidgetAndMonitorName("Scheduled Requests", "Auto Scheduled Request 1");
		Assert.assertEquals(true, common.isTextPresent("Disabled"));
		meters.disableByWidgetAndMonitorName("Scheduled Requests", "Auto Scheduled Request 2");
		
		// Enable the Test 1
		widget.clickLinkByWidget("Scheduled Requests", "Auto Scheduled Request 1");
		common.clickButtonByNameWithPageLoadWait("Edit");
		schedule.clickButtonInAttributeTab("Enable");
		waitFiveSeconds();
		schedule.enterTxtToAttributeFormField("Schedule Name:", "Changed");
		schedule.clickButtonInAttributeTab("Update");
		waitFiveSeconds();
		Assert.assertEquals(true, common.isTextPresent("Scheduled"));
		common.clickLinkByName("Metering");
		//Verify Test 1 widget has been changed and delete widgets
		widget.clickLinkByWidget("Scheduled Requests", "Changed");
		common.clickButtonByNameWithPageLoadWait("Edit");
		schedule.clickButtonInAttributeTab("Delete");
		waitFiveSeconds();
		Assert.assertEquals("Metering: All Schedules", common.getPageTitle());
		common.clickLinkByName("Metering");
		widget.clickLinkByWidget("Scheduled Requests", "Auto Scheduled Request 2");
		common.clickButtonByNameWithPageLoadWait("Edit");
		schedule.clickButtonInAttributeTab("Delete");
		waitFiveSeconds();
		Assert.assertEquals("Metering: All Schedules", common.getPageTitle());
		common.end();
	}
}