package com.cannontech.selenium.test.metering;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;

/**
 * @author jon.narr
 * @author anjana.manandhar
 */
public class TestStatusPointMonitorSelenium extends SolventSeleniumTestCase {
	public void init() {
		//Starts the session for the test.
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	
	/**
	 * This test logs into yukon and creates a Status Point Monitor.
	 */
	@Test
	public void createStatusPointMonitor() {
		init();
		MeteringSolvent meteringSolvent = new MeteringSolvent();
		CommonSolvent common= new CommonSolvent();
		common.clickLinkByName("Metering");
		
		//click create outage monitor button in widget
		meteringSolvent.clickCreateByWidget("Status Point Monitors");
		common.enterInputText("statusPointMonitoring/create", "statusPointMonitorName", "autoTest");
		common.selectDropDownMenu("statusPointMonitoring/create", "stateGroup", "TrueFalse");
		common.clickButtonBySpanText("Create");	
		Assert.assertEquals("Metering: Status Point Monitoring (autoTest)", common.getPageTitle());		
		common.end();
	}
	
	/**
	 * This test logs into yukon and edit the Status Point Monitor.
	 */
	@Test
	public void editStatusPointMonitor() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Metering");
		WidgetSolvent widget = new WidgetSolvent();
		
		widget.clickLinkByWidget("Status Point Monitors", "autoTest");
		Assert.assertEquals("Metering: Status Point Monitoring (autoTest)", common.getPageTitle());
		common.clickButtonBySpanText("Edit");
		common.clickButtonByTitle("Add a processing action.");
		common.selectDropDownMenu("statusPointMonitoring/update", "processors[0].prevState", "False");
		common.clickButtonBySpanText("Update");
		Assert.assertEquals("Metering", common.getPageTitle());
		common.end();
	}	
	
	/**
	 * This test logs into yukon and Disables and Enables the Status Point Monitor.
	 */
	@Test
	public void disableEnableStatusPointMonitor() {
		init();
		WidgetSolvent widget = new WidgetSolvent();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Metering");
		
		widget.disableByWidgetAndMonitorName("Status Point Monitors", "autoTest");
		widget.enableByWidgetAndMonitorName("Status Point Monitors", "autoTest");
		common.end();
	}
	
	/**
	 * This test logs into yukon and deletes the Status Point Monitor.
	 */
	@Test
	public void deleteStatusPointMonitor() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Metering");
		WidgetSolvent widget = new WidgetSolvent();
		
		widget.clickLinkByWidget("Status Point Monitors", "autoTest");
		Assert.assertEquals("Metering: Status Point Monitoring (autoTest)", common.getPageTitle());
		common.clickButtonBySpanText("Edit");
		common.clickButtonBySpanTextWithAjaxWait("Delete");
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Metering", common.getPageTitle());
		common.end();
	}

}
