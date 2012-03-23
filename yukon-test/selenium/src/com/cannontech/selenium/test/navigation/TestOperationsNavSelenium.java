package com.cannontech.selenium.test.navigation;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.OperationsPageSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
/**
 * Test to check the navigation options in Operations page.<br>
 * Multiple test cases are available to test two step deep process from Operations page.<br>
 * @author anuradha.uduwage
 */

public class TestOperationsNavSelenium extends SolventSeleniumTestCase {
	/**
	 * Test method logs in as syukon, syukon and check all the links in navigation
	 * page.
	 */
    public void init(){
		start();
		//use the LoginSolvent to login
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
    }
	@Test
	public void testNavigateToAllTrends() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("All Trends");
		Assert.assertEquals("Trends", common.getYukonText("Trends"));
		common.end();
	}
	@Test
	public void testNavigateToBulkImporter(){
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		common.clickLinkByName("Bulk Operations");
		Assert.assertEquals("Bulk Operations", common.getPageTitle());
		Assert.assertEquals("Choose Type Of Bulk Operation:", widget.getWidgetTitle("Choose Type Of Bulk Operation:"));
		
		common.clickLinkByName("Home");
		common.clickLinkByName("Bulk Importer");
		Assert.assertEquals(": Bulk Importer", common.getPageTitle());
		Assert.assertEquals("Actions", widget.getWidgetTitle("Actions"));
		Assert.assertEquals("Results Of Last Import", widget.getWidgetTitle("Results Of Last Import"));
		common.end();
	}
	/**
	 * Test method logs in as syukon, syukon, asserts the widgets on the Metering page, performs search on 
	 * the Metering Page and navigates to the Tamper Flag Monitoring page
	 */
	@Test
	public void testNavigateToMetering() {
		init();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		CommonSolvent common = new CommonSolvent();
			
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		Assert.assertEquals("Outage Monitors", widget.getWidgetTitle("Outage Monitors"));
		//Assert.assertEquals("Tamper Flag Monitors", new WidgetSolvent().getWidgetTitle("Tamper Flag Monitors"));
		Assert.assertEquals("Scheduled Requests", widget.getWidgetTitle("Scheduled Requests"));
		Assert.assertEquals("Validation Monitors", widget.getWidgetTitle("Validation Monitors"));
		Assert.assertEquals("Meter Search", widget.getWidgetTitle("Meter Search"));	
		
		widget.expandCollapseWidgetByTitle("Meter Search")
		.expandCollapseWidgetByTitle("Meter Search")
		.expandCollapseWidgetByTitle("Outage Monitors")
		.expandCollapseWidgetByTitle("Outage Monitors")/*
		.expandCollapseWidgetByTitle("Tamper Flag Monitors")
		.expandCollapseWidgetByTitle("Tamper Flag Monitors")*/;
		
		
		common.enterInputText("meter/search", "Quick Search", "Typing is working");
		common.enterInputText("meter/search", "Meter Number", "Typing in meter number");
		common.enterInputText("meter/search", "Device Type", "Typing device type");
		common.clickButtonBySpanText("Search");
		//Assert.assertEquals("Edit Filters", new CommonSolvent().getYukonText("Edit Filters"));
		topMenu.clickBreadcrumb("Metering");
		
		//MeteringSolvent meter = new MeteringSolvent();
		//meter.clickCreateByWidget("Tamper Flag Monitors");
		//Assert.assertEquals("Tamper Flag Monitoring", new CommonSolvent().getPageTitle());
		common.end();
	}
	@Test
	public void testNavigateToCapControl() {
		init();
		//Click multiple functions in operations page.	Showing the versatility
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Volt/Var Management");
		Assert.assertEquals("Volt Var Management", common.getPageTitle());
		Assert.assertEquals("Substation Areas", new WidgetSolvent().getWidgetTitle("Substation Areas"));
		common.end();
	}
	/**
	 * Test method logs in as syukon, syukon, clicks on Reporting link and navigates through 
	 * the Reporting pages.
	 */
	@Test
	public void testNavigateToReports() {
		init();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Reporting");
		Assert.assertEquals("Reports", common.getPageTitle());
		topMenu.clickTopSubMenuItem("Administrator");
		topMenu.clickTopSubMenuItem("Metering");
		topMenu.clickTopSubMenuItem("CapControl");
		topMenu.clickTopSubMenuItem("Statistical");
		topMenu.clickTopSubMenuItem("Database");
		topMenu.clickTopSubMenuItem("Load Management");
		topMenu.clickTopSubMenuItem("STARS");
		topMenu.clickTopSubMenuItem("C&I");
		common.end();
	}
	@Test
	public void testNavigateToCommander() {
		init();
		CommonSolvent common = new CommonSolvent();
		new OperationsPageSolvent().clickLinkItemByCategory("Analysis", "Commander");
		Assert.assertTrue(common.isTextPresent("Device Selection"));
		common.end();
	}
	@Test
	public void testNavigateToViewLogs() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("View Logs");
		Assert.assertEquals("Support: Log Explorer (/)", common.getPageTitle());
		Assert.assertEquals(true, common.isTextPresent("Directories"));
		common.end();
	}
	@Test
	public void testNavigateToManageIndexs() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Manage Indexes");
		Assert.assertEquals("Lucene Index Manager", common.getPageTitle());
		common.end();
	}
	@Test
	public void testNavigateToDeviceConfiguration() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Device Configuration");
		Assert.assertEquals("Device Configuration Page", common.getPageTitle());
		Assert.assertEquals("Create New Configuration", common.getYukonText("Create New Configuration"));
		common.end();
	}
}