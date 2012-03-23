package com.cannontech.selenium.test.capcontrol;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.capcontrol.CCSubBusTableSolvent;
import com.cannontech.selenium.solvents.capcontrol.CapControlTableSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;


/**
 * This test case contains the test procedures for Recent Controls
 * @author ricky.jones
 * @auther anjana.manandhar
 */
public class TestVerifyRecentControlsSelenium extends SolventSeleniumTestCase {
	
	/**
	 * Test method logs in as syukon, syukon and check all the links in navigation
	 * page.
	 */
	public void init() {
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		loginLogoutSolvent.cannonLogin("syukon", "syukon");		
	}
	
    
	/**
	 * This test case will verify the recent commands for subareas, substations, feeders,
	 * CBCs, and Cap Banks
	 */
	@Test
	public void ViewRecentControls() {
			init();
			CapControlTableSolvent capTable = new CapControlTableSolvent();
			CommonSolvent common = new CommonSolvent();
			YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
			CCSubBusTableSolvent subBusTable = new CCSubBusTableSolvent();

			common.clickLinkByName("Volt/Var Management");
			Assert.assertEquals("SubArea initially enabled", true, common.isTextPresent("ENABLED"));
			capTable.clickEnableByAreaName("SubArea-101");
			common.clickLinkByNameWithoutPageLoadWait("Disable Area");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("SubArea has been disabled", true, common.isTextPresent("DISABLED"));
			capTable.clickStateLinkByDeviceName("SubArea-101", "DISABLED");
			common.clickLinkByNameWithoutPageLoadWait("Enable Area");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("SubArea has been enabled", true, common.isTextPresent("ENABLED"));
			
			common.clickLinkByName("SubArea-101");
			common.clickLinkByName("Substation-101");
			
			//Verify Enable/Disable Substation
			Assert.assertEquals("Substation initially enabled", true, common.isTextPresent("ENABLED"));
			capTable.clickStateLinkByDeviceName("Substation-101", "ENABLED");
			common.clickLinkByNameWithoutPageLoadWait("Disable Substation");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("Substation has been disabled", true, common.isTextPresent("DISABLED"));
			capTable.clickStateLinkByDeviceName("Substation-101", "DISABLED");
			common.clickLinkByNameWithoutPageLoadWait("Enable Substation");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("Substation has been enabled", true, common.isTextPresent("ENABLED"));
			
			//Verify Recent Commands
			capTable.clickStateLinkByDeviceName("Substation-101", "ENABLED");
			common.clickLinkByName("View Recent Cmds");
			Assert.assertEquals("Results", common.getPageTitle());
			
			topmenu.clickBreadcrumb("Home");
			common.clickLinkByName("SubArea-101");
			common.clickLinkByName("Substation-101");
			
			//Verify Enable/Disable Subbus
			Assert.assertEquals("SubBus initially enabled", true, common.isTextPresent("ENABLED"));
			capTable.clickStateLinkByDeviceName("SubBus-101", "ENABLED");
			common.clickLinkByNameWithoutPageLoadWait("Disable Subbus");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("Subbus has been disabled", true, common.isTextPresent("DISABLED"));
			capTable.clickStateLinkByDeviceName("SubBus-101", "DISABLED");
			common.clickLinkByNameWithoutPageLoadWait("Enable Subbus");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("Subbus has been enabled", true, common.isTextPresent("ENABLED"));

			//Verify Recent Commands
			capTable.clickStateLinkByDeviceName("SubBus-101", "ENABLED");
			common.clickLinkByName("View Recent Cmds");
			Assert.assertEquals("Results", common.getPageTitle());
			
			topmenu.clickBreadcrumb("Home");
			common.clickLinkByName("SubArea-101");
			common.clickLinkByName("Substation-101");
					
			//Verify Enable/Disable Feeder
			Assert.assertEquals("Feeder initially enabled", true, common.isTextPresent("ENABLED"));
			capTable.clickStateLinkByDeviceName("Feeder-101", "ENABLED");
			common.clickLinkByNameWithoutPageLoadWait("Disable Feeder");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("Feeder has been disabled", true, common.isTextPresent("DISABLED"));
			capTable.clickStateLinkByDeviceName("Feeder-101", "DISABLED");
			common.clickLinkByNameWithoutPageLoadWait("Enable Feeder");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("Feeder has been enabled", true, common.isTextPresent("ENABLED"));

			//Verify Recent Commands
			capTable.clickStateLinkByDeviceName("Feeder-101", "ENABLED");
			common.clickLinkByName("View Recent Cmds");
			Assert.assertEquals("Results", common.getPageTitle());
			
			topmenu.clickBreadcrumb("Home");
			common.clickLinkByName("SubArea-101");
			common.clickLinkByName("Substation-101");
			
			//Verify Enable/Disable Cap Bank
			Assert.assertEquals("Cap Bank initially enabled", true, common.isTextPresent("Open"));
			capTable.clickStateLinkByDeviceName("CBC7010-101", "Open");
			common.clickLinkByNameWithoutPageLoadWait("Disable CapBank");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("Cap Bank has been disabled", true, common.isTextPresent("DISABLED : Open"));
			capTable.clickStateLinkByDeviceName("CBC7010-101", "DISABLED : Open");
			common.clickLinkByNameWithoutPageLoadWait("Enable CapBank");
			common.clickButtonByExactName("Submit");
			Assert.assertEquals("Cap Bank has been enabled", true, common.isTextPresent("Open"));

			//Verify Recent Commands
			capTable.clickStateLinkByDeviceName("CBC7010-101", "Open");
			common.clickLinkByName("View Recent Cmds");
			Assert.assertEquals("Results", common.getPageTitle());
			
			//Verify CBC name Recent Commands
			topmenu.clickBreadcrumb("Home");
			common.clickLinkByName("SubArea-101");
			common.clickLinkByName("Substation-101");
			common.clickLinkBySpanText("CapBank-101 (1)");
			common.clickLinkByName("View Recent Cmds");
			Assert.assertEquals("Results", common.getPageTitle());
			
			topmenu.clickBreadcrumb("Home");
			common.clickLinkByName("SubArea-101");
			common.clickLinkByName("Substation-101");
			
			//Verify Recent controls by View
			subBusTable.clickSubBusCheckBox("SubBus-101");
			topmenu.clickTopMenuItem("View", "Recent Controls");
			common.selectDropDownMenuByIdName("rcDateFilter", "7 Day (s)");
			
			//Verify SubArea Recent Commands
			topmenu.clickBreadcrumb("Home");
			capTable.clickStateLinkByDeviceName("SubArea-101", "ENABLED");
			common.clickLinkByName("View Recent Cmds");
			Assert.assertEquals("Results", common.getPageTitle());
			common.selectDropDownMenuByIdName("rcDateFilter", "7 Day (s)");
			common.end();
	}
}