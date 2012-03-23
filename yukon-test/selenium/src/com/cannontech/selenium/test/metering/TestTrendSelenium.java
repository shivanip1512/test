package com.cannontech.selenium.test.metering;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;

/**
 * This class verifies the Graph Types, Time Periods available in the Trend Widget for MCT-410IL.
 * @author anjana.manandhar
 *
 */
public class TestTrendSelenium extends SolventSeleniumTestCase {
	
	public void init() {
		//Starts the new session
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");
	}

	/**
	 * Method verifies the graph types available in the Trend widget by clicking on the links. 
	 */
	@Test
	public void verifyGraphType() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
        common.enterInputTextByFormId("filterForm", "Quick Search", "MCT-410iL-02 (1471788)");
		common.clickButtonBySpanText("Search");

		Assert.assertEquals("MCT-410iL-02 (1471788)", common.getPageTitle());
		widget.getTextFromWidgetByLabel("Meter Information", "Device Name:");
		Assert.assertEquals("MCT-410iL-02 (1471788)",common.getYukonText("MCT-410iL-02 (1471788)"));		
		Assert.assertEquals("Enabled", common.getYukonText("Enabled"));
		
		widget.clickLinkByWidget("Trend", "Load Profile");
		widget.clickLinkByWidget("Trend", "Voltage Profile");
		widget.clickLinkByWidget("Trend", "Demand");
    	widget.clickLinkByWidget("Trend", "Usage (Normalized)");
    	common.end();
	}
	
	/**
	 * Method verifies the Time Periods available in the Trend widget by clicking on the links. 
	 */
	@Test
	public void verifyTimePeriod() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
        common.enterInputTextByFormId("filterForm", "Quick Search", "MCT-410iL-02 (1471788)");
		common.clickButtonBySpanText("Search");
		
		Assert.assertEquals("MCT-410iL-02 (1471788)", common.getPageTitle());
		widget.getTextFromWidgetByLabel("Meter Information", "Device Name:");
		Assert.assertEquals("MCT-410iL-02 (1471788)",common.getYukonText("MCT-410iL-02 (1471788)"));		
		Assert.assertEquals("Enabled", common.getYukonText("Enabled"));
		
		widget.clickLinkByWidget("Trend", "1D");
		widget.clickLinkByWidget("Trend", "1W");
		widget.clickLinkByWidget("Trend", "1M");
		widget.clickLinkByWidget("Trend", "3M");
		widget.clickLinkByWidget("Trend", "1Y");
		widget.clickLinkByWidget("Trend", "Custom");
		common.end();
	}
}