package com.cannontech.selenium.test.demandresponse;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRControlAreasTableSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRLoadGroupsTableSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRPopupWindowSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRProgramsTableSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRScenariosTableSolvent;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;

/**
 * This test class performs basic Demand Response Load control
 * functional testing 
 * @author steve.junod
 */

public class TestDR1LoadControlSelenium extends SolventSeleniumTestCase {
	
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
	}
	/**
	 * This test verifies basic Control functionality from the
	 * Scenarios main page, Scenarios  table icons
	 */
		@Test
	public void verifyControlScenarioMain() {
		init();
		//  login as starsop to verify Start/Stop control functionality
		//    from the Scenario main page
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRScenariosTableSolvent drScenarios = new DRScenariosTableSolvent();
		common.clickLinkByName("Demand Response");
		
		Assert.assertEquals("Demand Response", common.getPageTitle());
		Assert.assertEquals("Quick Searches", widget.getWidgetTitle("Quick Searches"));  
		Assert.assertEquals("Active Control Areas", common.getYukonText("Active Control Areas"));
		Assert.assertEquals("Active Programs", common.getYukonText("Active Programs"));
		Assert.assertEquals("Active Load Groups", common.getYukonText("Active Load Groups"));

		// nav to the  Scenarios  main page
		topMenu.clickTopSubMenuItem("Scenarios");
		Assert.assertEquals("Scenarios", common.getPageTitle());

		// verify start/stop control via Scenarios table icons
		// start control using table Start icon
		drScenarios.clickStartButton("Scenario-01");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		common.clickButtonByName("Next");	
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to Scenario page
		Assert.assertEquals("Scenarios", common.getPageTitle());		

		//  verify that control goes Active
		Assert.assertEquals("Scenario start requested.", common.getYukonText("Scenario start requested."));
		
		//  stop control 
		drScenarios.clickStopButton("Scenario-01");
		Assert.assertEquals("Stop Programs", common.getYukonText("Stop Programs"));
		common.clickButtonByNameWithPageLoadWait("OK");		
		
		//  verify that control goes Inactive
		Assert.assertEquals("Scenario stop requested.", common.getYukonText("Scenario stop requested."));
		common.end();
	}
	/**
	 * This test verifies basic Control functionality from the
	 * Scenarios detail page  Action widget
	 */
	@Test
	public void verifyControlScenarioDetail() {
		init();
		//  login as starsop to verify Start/Stop control functionality
		//  from the Scenario detail page
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRScenariosTableSolvent drScenarios = new DRScenariosTableSolvent();
		DRProgramsTableSolvent drPrograms = new DRProgramsTableSolvent();
		common.clickLinkByName("Demand Response");

		//  get to Scenario main page
		topMenu.clickTopSubMenuItem("Scenarios");
		Assert.assertEquals("Scenarios", common.getPageTitle());
		
		// nav to the  Scenarios detail  main page
		drScenarios.clickScenarioByName("Scenario-01");
		Assert.assertEquals("Scenario Scenario-01", common.getPageTitle());		
		
		//  verify page widgets & Programs state
		Assert.assertEquals("Info", widget.getWidgetTitle("Info"));
		Assert.assertEquals("Actions", widget.getWidgetTitle("Actions"));
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm01-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm02-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm05-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm06-2Gear");

		//  verify Actions widget  Start Scenario   functionality 
		widget.clickLinkByWidget("Actions", "Start Scenario");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));

		// select subset of available Programs (de-select following pgms) 
		common.selectCheckBox("LdPgm01-2Gear");
		common.selectCheckBox("LdPgm06-2Gear");
		
		// select 2nd Gear  
		common.selectDropDownMenu("program/start/multipleDetails", "programStartInfo[1].gearNumber", "TimeRefresh");
		common.selectDropDownMenu("program/start/multipleDetails", "programStartInfo[2].gearNumber", "TimeRefresh");
		
		common.clickButtonByName("Next");
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to Scenario detail

		Assert.assertEquals("Scenario Scenario-01", common.getPageTitle());		

		//  verify that control goes Active
		Assert.assertEquals("Scenario start requested.", common.getYukonText("Scenario start requested."));

		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm01-2Gear");
		drPrograms.assertEqualsDemandResponseState("Manual Active", "LdPgm02-2Gear");
		drPrograms.assertEqualsDemandResponseState("Scheduled", "LdPgm05-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm06-2Gear");
		
		//  verify Current Gear value in Pgm table is 2nd Gear
		Assert.assertEquals("TimeRefresh", drPrograms.getCurrentGearbyProgram("LdPgm02-2Gear"));
		Assert.assertEquals("TimeRefresh", drPrograms.getCurrentGearbyProgram("LdPgm05-2Gear"));
			
		//  verify Actions widget  Stop Scenario   functionality 
		widget.clickLinkByWidget("Actions", "Stop Scenario");
		Assert.assertEquals("Stop Programs", common.getYukonText("Stop Programs"));
		common.clickButtonByNameWithPageLoadWait("OK");	
		
		//  verify that control goes Inactive
		Assert.assertEquals("Scenario stop requested.", common.getYukonText("Scenario stop requested."));

		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm01-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm02-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm05-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm06-2Gear");
		common.end();	
	}
	/**
	 * This test verifies basic Control functionality from the
	 * Control Area main page (CntrlArea01-Manual), CA table icons
	 */
	@Test
	public void verifyControlCAsMain() {
		init();
		//  login as starsop to verify Start/Stop control functionality
		//    from the Control Area main page
		CommonSolvent common = new CommonSolvent();
		EventsByTypeSolvent events = new EventsByTypeSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRControlAreasTableSolvent drCntlArea = new DRControlAreasTableSolvent();
		common.clickLinkByName("Demand Response");
		 
		// nav to  Control Areas  main page
		topMenu.clickTopSubMenuItem("Control Areas");
		Assert.assertEquals("Control Areas", common.getPageTitle());
		drCntlArea.assertEqualsDemandResponseState("Inactive", "CntrlArea01-Manual");

		// verify start/stop control via Control Area table icons
		// start control using table Start icon
		drCntlArea.clickStartButton("CntrlArea01-Manual");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		Assert.assertEquals("Programs to Start", common.getYukonText("Programs to Start"));
		common.clickButtonByName("Next");	
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to CA detail
		Assert.assertEquals("Control Areas", common.getPageTitle());		

		//  verify that control goes Active
		Assert.assertEquals("Control area start requested.", common.getYukonText("Control area start requested."));
		drCntlArea.assertEqualsDemandResponseState("Active", "CntrlArea01-Manual");
		
		//  stop control 
		drCntlArea.clickStopButton("CntrlArea01-Manual");
		Assert.assertEquals("Stop Programs", common.getYukonText("Stop Programs"));
		Assert.assertEquals("Programs to Stop", common.getYukonText("Programs to Stop"));
		common.clickButtonByNameWithPageLoadWait("OK");		
		
		//  verify that control goes Inactive
		Assert.assertEquals("Control area stop requested.", common.getYukonText("Control area stop requested."));
		drCntlArea.assertEqualsDemandResponseState("Inactive", "CntrlArea01-Manual");
		//Schedule the control area
		drCntlArea.clickStartButton("CntrlArea01-Manual");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		Assert.assertEquals("Programs to Start", common.getYukonText("Programs to Start"));
		common.selectCheckBox("Start Now");
		
		common.enterInputText("dr/program/start/multipleDetails", "startDateDatePart", events.returnDate(1));
		common.enterInputText("dr/program/start/multipleDetails", "stopDateDatePart", events.returnDate(2));
		common.clickButtonByName("Next");	
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to CA detail

		Assert.assertEquals("Control Areas", common.getPageTitle());		

		//  verify that control goes Scheduled
		Assert.assertEquals("Control area start requested.", common.getYukonText("Control area start requested."));
		drCntlArea.assertEqualsDemandResponseState("Scheduled", "CntrlArea01-Manual");
		
		//  stop control 
		drCntlArea.clickStopButton("CntrlArea01-Manual");
		Assert.assertEquals("Stop Programs", common.getYukonText("Stop Programs"));
		Assert.assertEquals("Programs to Stop", common.getYukonText("Programs to Stop"));
		common.clickButtonByNameWithPageLoadWait("OK");
		
		//  verify that control goes Inactive
		Assert.assertEquals("Control area stop requested.", common.getYukonText("Control area stop requested."));
		drCntlArea.assertEqualsDemandResponseState("Inactive", "CntrlArea01-Manual");
		
		//Partially Schedule the control area
		drCntlArea.clickStartButton("CntrlArea01-Manual");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		Assert.assertEquals("Programs to Start", common.getYukonText("Programs to Start"));
		common.selectCheckBox("Start Now");
		common.enterInputText("dr/program/start/multipleDetails", "startDateDatePart", events.returnDate(1));
		common.enterInputText("dr/program/start/multipleDetails", "stopDateDatePart", events.returnDate(2));
		common.selectCheckBox("LdPgm01-2Gear");
		common.clickButtonByName("Next");	
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to CA detail

		Assert.assertEquals("Control Areas", common.getPageTitle());		

		//  verify that control goes Scheduled
		Assert.assertEquals("Control area start requested.", common.getYukonText("Control area start requested."));
		drCntlArea.assertEqualsDemandResponseState("Partially Scheduled", "CntrlArea01-Manual");
		
		//  stop control 
		drCntlArea.clickStopButton("CntrlArea01-Manual");
		Assert.assertEquals("Stop Programs", common.getYukonText("Stop Programs"));
		Assert.assertEquals("Programs to Stop", common.getYukonText("Programs to Stop"));
		common.clickButtonByNameWithPageLoadWait("OK");
		
		//  verify that control goes Inactive
		Assert.assertEquals("Control area stop requested.", common.getYukonText("Control area stop requested."));
		drCntlArea.assertEqualsDemandResponseState("Inactive", "CntrlArea01-Manual");
		common.end();
	}
	/**
	 * This test verifies basic Control functionality from the
	 * Control Area detail page (CntrlArea01-Manual), Action widget
	 */
	@Test
	public void verifyControlCAsDetail() {
		init();
		//  login as starsop to verify Start/Stop control functionality
		//    from the Control Area detail page
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRControlAreasTableSolvent drCntlArea = new DRControlAreasTableSolvent();
		DRProgramsTableSolvent drPrograms = new DRProgramsTableSolvent();
		common.clickLinkByName("Demand Response");
			 
		// nav to  Control Areas  main page
		topMenu.clickTopSubMenuItem("Control Areas");
		Assert.assertEquals("Control Areas", common.getPageTitle());

		//  nav to Control Area detail page, verify some info
		drCntlArea.clickControlAreaByName("CntrlArea01-Manual");
		Assert.assertEquals("Control Area CntrlArea01-Manual", common.getPageTitle());		
		Assert.assertEquals("Info", widget.getWidgetTitle("Info"));
		widget.assertEqualsTextFromWidgetByLabel("Inactive", "Info", "State:");
		widget.assertEqualsTextFromWidgetByLabel("1", "Info", "Priority:");
		widget.assertEqualsTextFromWidgetByLabel("N/A - N/A", "Info", "Time Window:");

		Assert.assertEquals("Actions", widget.getWidgetTitle("Actions"));
		
		//  verify controls using  Actions widget  Control Area detail page 
		widget.clickLinkByWidget("Actions", "Start Control Area");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		Assert.assertEquals("Programs to Start", common.getYukonText("Programs to Start"));
		
		// select subset of available Programs
		common.selectCheckBox("LdPgm02-2Gear");
		common.selectCheckBox("LdPgm03-2Gear");
		
		//  select 2nd Gear  
		common.selectDropDownMenu("program/start/multipleDetails", "programStartInfo[0].gearNumber", "TimeRefresh");
		common.selectDropDownMenu("program/start/multipleDetails", "programStartInfo[3].gearNumber", "TimeRefresh");
		
		common.clickButtonByName("Next");	
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to CA detail

		Assert.assertEquals("Control Area CntrlArea01-Manual", common.getPageTitle());		
		Assert.assertEquals("Control area start requested.", common.getYukonText("Control area start requested."));

		//  verify that control goes Active
		widget.assertEqualsTextFromWidgetByLabel("Partially Active", "Info", "State:");
		drPrograms.assertEqualsDemandResponseState("Manual Active", "LdPgm01-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm02-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm03-2Gear");
		drPrograms.assertEqualsDemandResponseState("Manual Active", "LdPgm04-2Gear");
		
		//  verify Current Gear value in Pgm table is 2nd Gear
		Assert.assertEquals("TimeRefresh", drPrograms.getCurrentGearbyProgram("LdPgm01-2Gear"));
		Assert.assertEquals("TimeRefresh", drPrograms.getCurrentGearbyProgram("LdPgm04-2Gear"));
			
		//  verify Actions widget  Stop Control Area   functionality 
		widget.clickLinkByWidget("Actions", "Stop Control Area");
		Assert.assertEquals("Stop Programs", common.getYukonText("Stop Programs"));
		Assert.assertEquals("Programs to Stop", common.getYukonText("Programs to Stop"));
		common.clickButtonByNameWithPageLoadWait("OK");	
		
		//  verify that control goes Inactive
		Assert.assertEquals("Control area stop requested.", common.getYukonText("Control area stop requested."));
		
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm01-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm02-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm03-2Gear");
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm04-2Gear");
		widget.assertEqualsTextFromWidgetByLabel("Inactive", "Info", "State:");
		common.end();
	}
	/**
	 * This test verifies basic Control functionality from the
	 * Program main page, Program  table icons
	 */	
	@Test
	public void verifyControlLdPgmsMain() {
		init();
		//  login as starsop to verify Start/Stop control functionality
		//    from the Programs main page
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRProgramsTableSolvent drPrograms = new DRProgramsTableSolvent();
		common.clickLinkByName("Demand Response");
		
		// nav to the  Programs  main page
		topMenu.clickTopSubMenuItem("Programs");
		Assert.assertEquals("Programs", common.getPageTitle());

		// verify start/stop control via Program table icons
		// start control using table Start icon (for now, must use 'Stop' to Start pgm cntl)
		drPrograms.clickStartButton("LdPgm01-2Gear");
		Assert.assertEquals("Start Program", common.getYukonText("Start Program"));
		common.clickButtonByName("Next");	
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to Pgm detail
		Assert.assertEquals("Programs", common.getPageTitle());		

		//  verify that control goes Active
		Assert.assertEquals("Program start requested.", common.getYukonText("Program start requested."));
		drPrograms.assertEqualsDemandResponseState("Manual Active", "LdPgm01-2Gear");
		
		//  stop control (for now, must use 'Start' to Stop program control)
		drPrograms.clickStopButton("LdPgm01-2Gear");
		Assert.assertEquals("Stop Program", common.getYukonText("Stop Program"));
		common.clickButtonByNameWithPageLoadWait("OK");		
		
		//  verify that control goes Inactive
		Assert.assertEquals("Program stop requested.", common.getYukonText("Program stop requested."));
		drPrograms.assertEqualsDemandResponseState("Inactive", "LdPgm01-2Gear");
		common.end();
	}
	/**
	 * This test verifies basic Control functionality from the
	 * Program detail page, Program  table icons
	 */
	@Test
	public void verifyControlLdPgmsDetail() {
		init();
		//  login as starsop to verify Start/Stop control functionality
		//    from the Programs detail page
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRLoadGroupsTableSolvent drLdGrps = new DRLoadGroupsTableSolvent();
		DRProgramsTableSolvent drPrograms = new DRProgramsTableSolvent();
		DRPopupWindowSolvent drPopup = new DRPopupWindowSolvent();
		common.clickLinkByName("Demand Response");
		
		// nav to the  Programs  main page
		topMenu.clickTopSubMenuItem("Programs");
		Assert.assertEquals("Programs", common.getPageTitle());
		
		// nav to the  Program detail  main page
		drPrograms.clickProgramByName("LdPgm01-2Gear");
		Assert.assertEquals("Program LdPgm01-2Gear", common.getPageTitle());		
		Assert.assertEquals("Info", widget.getWidgetTitle("Info"));
		
		widget.assertEqualsTextFromWidgetByLabel("Inactive", "Info", "State:");
		widget.assertEqualsTextFromWidgetByLabel("TrueCycle", "Info", "Current Gear:");
		widget.assertEqualsTextFromWidgetByLabel("1", "Info", "Priority:");
		widget.assertEqualsTextFromWidgetByLabel("0.0", "Info", "Reduction:");
		Assert.assertEquals("Actions", widget.getWidgetTitle("Actions"));
		Assert.assertEquals("Control Area", widget.getWidgetTitle("Control Area"));
		Assert.assertEquals("Scenarios", widget.getWidgetTitle("Scenarios"));
		
		//  verify Actions widget  Start Program   functionality 
		widget.clickLinkByWidget("Actions", "Start Program");
		Assert.assertEquals("Start Program", common.getYukonText("Start Program"));
		
		//  select 2nd Gear  
		drPopup.selectGear("TimeRefresh");
		common.clickButtonByName("Next");	
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to Pgm detail
		Assert.assertEquals("Program LdPgm01-2Gear", common.getPageTitle());		
	
		//  verify that control goes Active
		Assert.assertEquals("Program start requested.", common.getYukonText("Program start requested."));
		drLdGrps.assertEqualsDemandResponseState("Active", "LdGrp01-Expresscom");
		widget.assertEqualsTextFromWidgetByLabel("Manual Active", "Info", "State:");
		widget.assertEqualsTextFromWidgetByLabel("TimeRefresh", "Info", "Current Gear:");
		
		//  verify Actions widget  Stop Program   functionality 
		widget.clickLinkByWidget("Actions", "Stop Program");
		Assert.assertEquals("Stop Program", common.getYukonText("Stop Program"));
		common.clickButtonByNameWithPageLoadWait("OK");	
		
		//  verify that control goes Inactive
		Assert.assertEquals("Program stop requested.", common.getYukonText("Program stop requested."));
		drLdGrps.assertEqualsDemandResponseState("Inactive", "LdGrp01-Expresscom");
		widget.assertEqualsTextFromWidgetByLabel("Inactive", "Info", "State:");
				
		//verifyControlLdGrps();
		common.end();
	}
	/**
	 * This test verifies basic Control functionality from the
	 * Load Groups main page, Load Groups  table icons
	 */	
	@Test
	public void verifyControlLdGrpsMain() {
		init();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRLoadGroupsTableSolvent drLdGrps = new DRLoadGroupsTableSolvent();
		DRPopupWindowSolvent drPopup = new DRPopupWindowSolvent();
		
		//  login as starsop to verify Start/Stop control functionality
		//    from the Programs detail page
		common.clickLinkByName("Demand Response");
		
		// nav to the  Load Groups  main page
		topMenu.clickTopSubMenuItem("Load Groups");
		Assert.assertEquals("Load Groups", common.getPageTitle());
		drLdGrps.assertEqualsDemandResponseState("Inactive", "LdGrp04-Expresscom");

		// verify send/restore control via Load Group table icons
		// start control using table Send Shed icon
		drLdGrps.clickStartButton("LdGrp04-Expresscom");
		Assert.assertEquals("Send Shed", common.getYukonText("Send Shed"));
		
		// select Shed time 
		drPopup.selectShedTime("30 minutes");
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to Ld Grp
		Assert.assertEquals("Load Groups", common.getPageTitle());		

		//  verify that control goes Active
		Assert.assertEquals("Shed sent.", common.getYukonText("Shed sent."));
		drLdGrps.assertEqualsDemandResponseState("Active", "LdGrp04-Expresscom");
		
		//  stop control 
		drLdGrps.clickStopButton("LdGrp04-Expresscom");
		Assert.assertEquals("Send Restore", common.getYukonText("Send Restore"));
		common.clickButtonByNameWithPageLoadWait("OK");		
		
		//  verify that control goes Inactive
		Assert.assertEquals("Restore sent.", common.getYukonText("Restore sent."));
		drLdGrps.assertEqualsDemandResponseState("Inactive", "LdGrp04-Expresscom");
		common.end();
	}
	/**
	 * This test verifies basic Control functionality from the
	 * Load Group detail page (LdGrp02-Versacom), Action widget
	 */		
	@Test
	public void verifyControlLdGrpsDetail() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRLoadGroupsTableSolvent drLdGrps = new DRLoadGroupsTableSolvent();
		DRPopupWindowSolvent drPopup = new DRPopupWindowSolvent();
		
		//  login as starsop to verify Start/Stop control functionality
		//    from the Programs detail page
		common.clickLinkByName("Demand Response");
		
		// nav to the  Load Groups  main page
		topMenu.clickTopSubMenuItem("Load Groups");
		Assert.assertEquals("Load Groups", common.getPageTitle());
		// nav to the  Load Group detail  main page
		drLdGrps.clickLoadGroupByName("LdGrp02-Versacom");
		Assert.assertEquals("Load Group LdGrp02-Versacom", common.getPageTitle());		
		
		Assert.assertEquals("Info", widget.getWidgetTitle("Info"));
		widget.assertEqualsTextFromWidgetByLabel("Inactive", "Info", "State:");
		widget.assertEqualsTextFromWidgetByLabel("0", "Info", "Reduction:");
		Assert.assertEquals("Actions", widget.getWidgetTitle("Actions"));
		Assert.assertEquals("Programs", widget.getWidgetTitle("Programs"));
		Assert.assertEquals("Macro Load Groups", widget.getWidgetTitle("Macro Load Groups"));
		
		//  verify Actions widget  Send Shed   functionality 
		widget.clickLinkByWidget("Actions", "Send Shed");
		Assert.assertEquals("Send Shed", common.getYukonText("Send Shed"));

		// select Shed time  
		drPopup.selectShedTime("30 minutes");
		common.clickButtonByNameWithPageLoadWait("OK");		//  starts control, nav back to Ld Grp detail
		Assert.assertEquals("Load Group LdGrp02-Versacom", common.getPageTitle());		

		//  verify that control goes Active
		Assert.assertEquals("Shed sent.", common.getYukonText("Shed sent."));
		widget.assertEqualsTextFromWidgetByLabel("Active", "Info", "State:");
		
		//  verify Actions widget  Send Restore   functionality 
		widget.clickLinkByWidget("Actions", "Send Restore");
		Assert.assertEquals("Send Restore", common.getYukonText("Send Restore"));
		common.clickButtonByNameWithPageLoadWait("OK");	
		
		//  verify that control goes Inactive
		Assert.assertEquals("Restore sent.", common.getYukonText("Restore sent."));
		widget.assertEqualsTextFromWidgetByLabel("Inactive", "Info", "State:");
		common.end();
	}
}

