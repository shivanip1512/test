package com.cannontech.selenium.test.metering;
import org.junit.Test;
import org.testng.Assert;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;
/**
 * This class tests the verified the creating, editing and deleting of Validation Monitor.
 * @author anjana.manandhar
 */
public class TestCRUDValidationMonitorSelenium extends SolventSeleniumTestCase {
	/**
	 * Test method logs in as syukon, syukon.
	 */
	public void init() {
		//start the session for the test. 
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	/**
	 * This test creates a Validation Monitor and verifies the Validation monitor was created.
	 */
	@Test
	public void createValidationMonitor() {
		init();
		WidgetSolvent widget = new WidgetSolvent();
		CommonSolvent common = new CommonSolvent();
		DeviceGroupPopupSolvent deviceGroupPopupSolvent = new DeviceGroupPopupSolvent();
		MeteringSolvent meteringSolvent = new MeteringSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		Assert.assertEquals("Validation Monitors", widget.getWidgetTitle("Validation Monitors"));
		
		meteringSolvent.clickCreateByWidget("Validation Monitor");
		common.enterInputText("monitor/update", "name", "Auto Validation 1");
		
		meteringSolvent.clickDeviceGroupIcon();
		deviceGroupPopupSolvent.selectTreeNode("Meters");
		deviceGroupPopupSolvent.clickSelectGroupButton();
		common.clickButtonByNameWithPageLoadWait("Save");
		Assert.assertEquals("Metering", common.getPageTitle());
		Assert.assertEquals("Auto Validation 1", common.getYukonText("Auto Validation 1"));
		common.end();
	}
	/**
	 * This test verifies creating, editing, and deleting of an Validation Monitor.
	 */
	@Test
	public void crudValidationMonitor() {
		init();
		WidgetSolvent widget = new WidgetSolvent();
		CommonSolvent common = new CommonSolvent();
		DeviceGroupPopupSolvent deviceGroupPopupSolvent = new DeviceGroupPopupSolvent();
		MeteringSolvent meteringSolvent = new MeteringSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		Assert.assertEquals("Validation Monitors", widget.getWidgetTitle("Validation Monitors"));
		
		meteringSolvent.clickCreateByWidget("Validation Monitor");
		common.enterInputText("monitor/update", "name", "Auto Validation 2");
		
		meteringSolvent.clickDeviceGroupIcon();
		deviceGroupPopupSolvent.selectTreeNode("Meters");
		deviceGroupPopupSolvent.clickSelectGroupButton();
		common.clickButtonByNameWithPageLoadWait("Save");
		
		Assert.assertEquals("Metering", common.getPageTitle());
		Assert.assertEquals("Auto Validation 2", common.getYukonText("Auto Validation 2"));
		
		//Disable the Validation Monitor
		widget.disableByWidgetAndMonitorName("Validation Monitors", "Auto Validation 2" );
		common.clickLinkByName("Auto Validation 2");
		Assert.assertEquals("Disabled", "Disabled");
		common.clickButtonByNameWithPageLoadWait("Cancel");
		
		//Enable the Validation Monitor
		widget.enableByWidgetAndMonitorName("Validation Monitors", "Auto Validation 2" );
		common.clickLinkByName("Auto Validation 2");
		common.enterInputText("monitor/update", "name", "Auto Validation 2Update");
		common.clickButtonByNameWithPageLoadWait("Update");
		Assert.assertEquals("Auto Validation 2Update", common.getYukonText("Auto Validation 2Update"));
		
		common.clickLinkByName("Auto Validation 2Update");
		common.clickButtonByNameWithPageLoadWait("Delete");
		Assert.assertFalse(false, "Auto Validation 2Update");
		common.end();
	}
}