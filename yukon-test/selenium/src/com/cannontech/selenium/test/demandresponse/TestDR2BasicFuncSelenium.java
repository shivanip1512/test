package com.cannontech.selenium.test.demandresponse;

import junit.framework.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRControlAreasTableSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRLoadGroupsTableSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRProgramsTableSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRScenariosTableSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRPopupWindowSolvent;
import com.cannontech.selenium.solvents.demandresponse.DRWidgetWithTableSolvent;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;

/**
 * This test class performs basic Demand Response Object verification
 * (titles, text, widgets, etc), and basic link/button functionality
 * @author steve.junod
 */

public class TestDR2BasicFuncSelenium extends SolventSeleniumTestCase {
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
	}
	/**
	 * verifies initial Demand Response main page, 
	 * Start Control from Recently Viewed widget,
	 * navigate using Recently Viewed widget links
	 */
	@Test
	public void RecentlyViewedFuncs() {
		init();
		//  login as Stars operator, navigate to Demand Response page
		//  and verify objects on page
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRWidgetWithTableSolvent drWidget = new DRWidgetWithTableSolvent ();
		common.clickLinkByName("Demand Response");
		
		// verify DR page widgets & text
		Assert.assertEquals("Demand Response", common.getPageTitle());
		Assert.assertEquals("Quick Searches", widget.getWidgetTitle("Quick Searches"));  
		Assert.assertEquals("No Favorites", common.getYukonText("No Favorites"));
		Assert.assertEquals("Recently Viewed Items", widget.getWidgetTitle("Recently Viewed Items"));  

		//  verify items in Recently Viewed Items widget
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","CntrlArea01-Manual");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","LdGrp02-Versacom");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","LdPgm01-2Gear");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","Scenario-01");

		//  verify Recently Viewed Items widget info (State)
		Assert.assertEquals("Inactive", drWidget.getStateByWidgetAndLinkName("Recently Viewed Items","CntrlArea01-Manual"));
		Assert.assertEquals("Inactive", drWidget.getStateByWidgetAndLinkName("Recently Viewed Items","LdGrp02-Versacom"));
		Assert.assertEquals("Inactive", drWidget.getStateByWidgetAndLinkName("Recently Viewed Items","LdPgm01-2Gear"));

		// start CA control from Recently Viewed (qa-307) widget to verify Quick Search widget 
		drWidget.clickStartByWidgetAndLinkName("Recently Viewed Items", "CntrlArea01-Manual");
		common.clickButtonByNameWithElementWait("Next", "//table[@id='startMultipleProgramsOverridePrograms']");	
		common.clickButtonByName("OK");
		Assert.assertEquals("Control area start requested.", common.getYukonText("Control area start requested."));

		//  verify State=Active in Recently Viewed widget
		drWidget.assertEqualsStateByWidgetAndLinkName("Active", "Recently Viewed Items", "CntrlArea01-Manual");
		drWidget.assertEqualsStateByWidgetAndLinkName("Active", "Recently Viewed Items","LdGrp02-Versacom");
		drWidget.assertEqualsStateByWidgetAndLinkName("Manual Active", "Recently Viewed Items","LdPgm01-2Gear");

		//  verify link navigation from Recently Viewed widget
		widget.clickLinkByWidgetWithPageLoadWait("Recently Viewed Items", "LdGrp02-Versacom");
		Assert.assertEquals("Load Group LdGrp02-Versacom", common.getPageTitle());
		topMenu.clickTopMenuItem("Demand Response");

		widget.clickLinkByWidgetWithPageLoadWait("Recently Viewed Items", "CntrlArea01-Manual");
		Assert.assertEquals("Control Area CntrlArea01-Manual", common.getPageTitle());
		topMenu.clickTopMenuItem("Demand Response");

		widget.clickLinkByWidgetWithPageLoadWait("Recently Viewed Items", "LdPgm01-2Gear");
		Assert.assertEquals("Program LdPgm01-2Gear", common.getPageTitle());
		topMenu.clickBreadcrumb("Demand Response");

		widget.clickLinkByWidgetWithPageLoadWait("Recently Viewed Items", "Scenario-01");
		Assert.assertEquals("Scenario Scenario-01", common.getPageTitle());
		topMenu.clickTopMenuItem("Demand Response");
		common.end();
	}	
		
	/**
	 * verifies DR main page Quick Search widget  Active Load Group  option
	 * sets up some Favorites to verify,
	 * performs basic Filter searches 
	 */
	@Test
	public void quickSearchLoadGrp() {
		init();
		//  login as Stars operator, navigate to Demand Response page
		//  and verify objects on page		
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRLoadGroupsTableSolvent drLdGrps = new DRLoadGroupsTableSolvent ();
		common.clickLinkByName("Demand Response");

		// verify  Quick Search  links
		//  Active Load Groups
		widget.clickLinkByWidgetWithPageLoadWait("Quick Searches","Active Load Groups");
		Assert.assertEquals("Load Groups", common.getPageTitle());

		//  verify list of Active  Load Groups
		Assert.assertEquals("LdGrp01-Expresscom", common.getLinkText("LdGrp01-Expresscom"));
		Assert.assertEquals("LdGrp01-Versacom", common.getLinkText("LdGrp01-Versacom"));
		Assert.assertEquals("LdGrp02-Expresscom", common.getLinkText("LdGrp02-Expresscom"));
		Assert.assertEquals("LdGrp02-Versacom", common.getLinkText("LdGrp02-Versacom"));

		//  verify  Filter  link, fields, & search
		common.clickButtonBySpanTextWithElementWait("Filter", "//div[@id='filterPopup']");     
		Assert.assertEquals("Name:", common.getYukonText("Name:"));
		Assert.assertEquals("State:", common.getYukonText("State:"));
		Assert.assertEquals("Last Action:", common.getYukonText("Last Action:"));

		common.enterInputText("loadGroup/list", "lastAction.min", "aaa");
	
		//  yuk-8777
		common.clickButtonByName("Filter");				
		Assert.assertEquals("Error found, check fields.", common.getYukonText("Error found, check fields."));

		common.enterInputText("loadGroup/list", "lastAction.min", "");	
		common.enterInputText("loadGroup/list", "name", "-Exp");
		common.clickButtonByNameWithPageLoadWait("Filter");

		//  verify Filter result list of  Ld Grps 
		Assert.assertEquals("LdGrp01-Expresscom", common.getLinkText("LdGrp01-Expresscom"));
		Assert.assertEquals("LdGrp02-Expresscom", common.getLinkText("LdGrp02-Expresscom"));

		//  click Favorites icon
		drLdGrps.addToFavoritesByName("LdGrp01-Expresscom");

		//drLdGrps.sortByColumnHeader("Name");
		// how can sort order be verified?     qa-299

		//  get back to DR page
		topMenu.clickBreadcrumb("Demand Response");		
		common.end();
	}

	/**
	 * verifies DR main page Quick Search widget Active Programs option
	 * sets up some Favorites to verify,
	 * verifies Change Gear from Programs  Active  widget
	 * performs basic Filter searches 
	 */
	@Test
	public void quickSearchPrograms() {
		init();
		//  login as Stars operator, navigate to Demand Response page
		//  and verify objects on page		
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRProgramsTableSolvent drPrograms = new DRProgramsTableSolvent ();
		DRPopupWindowSolvent drPopup = new DRPopupWindowSolvent ();
		common.clickLinkByName("Demand Response");

		// verify  Quick Search  links
		//  Active Programs
		widget.clickLinkByWidgetWithPageLoadWait("Quick Searches","Active Programs");
		Assert.assertEquals("Programs", common.getPageTitle());

		//  verify list of Active Programs
		Assert.assertEquals("LdPgm01-2Gear", common.getLinkText("LdPgm01-2Gear"));
		Assert.assertEquals("LdPgm02-2Gear", common.getLinkText("LdPgm02-2Gear"));
		Assert.assertEquals("LdPgm03-2Gear", common.getLinkText("LdPgm03-2Gear"));
		Assert.assertEquals("LdPgm04-2Gear", common.getLinkText("LdPgm04-2Gear"));

		//  verify  Filter  link, fields, & search
        common.clickButtonBySpanTextWithElementWait("Filter", "//div[@id='filterPopup']");     
		Assert.assertEquals("Name:", common.getYukonText("Name:"));
		Assert.assertEquals("State:", common.getYukonText("State:"));
		Assert.assertEquals("Priority:", common.getYukonText("Priority:"));
		Assert.assertEquals("Start:", common.getYukonText("Start:"));
		Assert.assertEquals("Stop:", common.getYukonText("Stop:"));
		
		//  yuk-8777
		common.enterInputText("program/list", "priority.min", "aaa");
		common.clickButtonByName("Filter");     
		Assert.assertEquals("Error found, check fields.", common.getYukonText("Error found, check fields."));
			
		common.enterInputText("program/list", "priority.min", "");
		common.selectDropDownMenu("program/list", "state", "Scheduled");
		common.clickButtonByName("Filter");
		Assert.assertEquals("No programs matched.", common.getYukonText("No programs matched."));
		
		topMenu.clickTopSubMenuItem("Programs");

        common.clickButtonBySpanTextWithElementWait("Filter", "//div[@id='filterPopup']");     
    	common.enterInputText("program/list", "name", "04-2Gear");
		common.clickButtonByNameWithPageLoadWait("Filter");
		//  verify Filter result list of  Programs 
		Assert.assertEquals("LdPgm04-2Gear", common.getLinkText("LdPgm04-2Gear"));
		
		//  click Favorites icon
		drPrograms.addToFavoritesByName("LdPgm04-2Gear");

		//drPrograms.sortByColumnHeader("Name");
		// how can sort order be verified?     qa-299

		//  verify  Change Gear  in Actions widget
		drPrograms.clickProgramByName("LdPgm04-2Gear");
		Assert.assertEquals("TrueCycle", widget.getTextFromWidgetByLabel("Info", "Current Gear:"));
		widget.clickLinkByWidget("Actions","Change Gears");
		Assert.assertEquals("Send Change Gear", common.getYukonText("Send Change Gear"));
		drPopup.selectGear("TimeRefresh");
		common.clickButtonByName("OK");	
		waitFiveSeconds();
		Assert.assertEquals("Gear changed.", common.getYukonText("Gear changed."));		
		Assert.assertEquals("TimeRefresh", widget.getTextFromWidgetByLabel("Info", "Current Gear:"));
		common.end();
	}
	
	/**
	 * verifies DR main page Quick Search widget Active Control Area  option
	 * sets up some Favorites to verify,
	 * performs basic Filter searches 
	 */
	@Test
	public void quickSearchCntlArea() {
		init();
		//  login as Stars operator, navigate to Demand Response page
		//  and verify objects on page		
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		DRControlAreasTableSolvent drCntlArea = new DRControlAreasTableSolvent ();
		common.clickLinkByName("Demand Response");
		
		//  Active Control Areas
		widget.clickLinkByWidgetWithPageLoadWait("Quick Searches","Active Control Areas");
		Assert.assertEquals("Control Areas", common.getPageTitle());
		//  verify list of Active  Control Areas
		Assert.assertEquals("CntrlArea01-Manual", common.getLinkText("CntrlArea01-Manual"));

		// verify  Filter  link, fields, & search 
	    common.clickButtonBySpanTextWithElementWait("Filter", "//div[@id='filterPopup']");     
	    Assert.assertEquals("Name:", common.getYukonText("Name:"));
		Assert.assertEquals("State:", common.getYukonText("State:"));
		Assert.assertEquals("Priority:", common.getYukonText("Priority:"));
		
		//  yuk-8777
		common.enterInputText("controlArea/list", "priority.min", "aaa");
		common.clickButtonByName("Filter");     
		Assert.assertEquals("Error found, check fields.", common.getYukonText("Error found, check fields."));
			
		common.enterInputText("controlArea/list", "priority.min", "");
		common.enterInputText("controlArea/list", "name", "Area");
		common.selectDropDownMenu("controlArea/list", "state", "All");
		common.clickButtonByNameWithPageLoadWait("Filter");     

		//  verify Filter result list of  Control Areas 
		Assert.assertEquals("CntrlArea01-Manual", common.getLinkText("CntrlArea01-Manual"));
		Assert.assertEquals("CntrlArea02-Manual", common.getLinkText("CntrlArea02-Manual"));

		//  click Favorites icon
		drCntlArea.addToFavoritesByName("CntrlArea01-Manual");

		/*    QA-309   Favorites icon outside of widget / table
		drCntlArea.clickControlAreaByName("CntrlArea02-Manual");
		Assert.assertEquals("Control Area CntrlArea02-Manual", common.getPageTitle());
		drCntlArea.addToFavoritesByName("CntrlArea02-Manual");
		*/
		common.end();
	}
		
	/**
	 * verifies DR main page  Favorites  widget options,
	 * verify  Favorites  list, verify items  State,
	 * click Control Area Stop Control icon, verify State = Inactive
	 * remove items from Favorites list, and verify gone
	 */
	@Test
	public void verifyFavoritesWidget() {
		init();
		//  login as Stars operator, navigate to Demand Response page
		//  and verify objects on page		
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRWidgetWithTableSolvent drWidget = new DRWidgetWithTableSolvent ();
		common.clickLinkByName("Demand Response");
		
		// verify items now in Favorites widget
		widget.getLinkFromWidgetByLinkName("Favorites", "CntrlArea01-Manual");
		widget.getLinkFromWidgetByLinkName("Favorites", "LdGrp01-Expresscom");
		widget.getLinkFromWidgetByLinkName("Favorites", "LdPgm04-2Gear");

		//  verify  Active  state in Favorites widget
		drWidget.assertEqualsStateByWidgetAndLinkName("Active", "Favorites","CntrlArea01-Manual");
		drWidget.assertEqualsStateByWidgetAndLinkName("Active", "Favorites","LdGrp01-Expresscom");
		drWidget.assertEqualsStateByWidgetAndLinkName("Manual Active", "Favorites","LdPgm04-2Gear");
		
		//  stop Control Area control using icon in Favorites widget (qa-307)
		drWidget.clickStopByWidgetAndLinkName("Favorites", "CntrlArea01-Manual");
		Assert.assertEquals("Stop Programs", common.getYukonText("Stop Programs"));
		Assert.assertEquals("Programs to Stop", common.getYukonText("Programs to Stop"));
		//common.clickButtonByName("OK");		

		//  setup for  yuk-8986 
		common.selectCheckBox("Stop Now");
		//  set  Stop Now  date - yesterday
		common.enterInputText("stop/stopMultiple", "stopDateDatePart", new EventsByTypeSolvent().returnDate(-1));
	
		common.clickButtonByName("OK");				
		Assert.assertEquals("Stop time must be in the future.", common.getYukonText("Stop time must be in the future."));
		
		common.selectCheckBox("Stop Now");
		common.clickButtonByName("OK");				
		Assert.assertEquals("Control area stop requested.", common.getYukonText("Control area stop requested."));

		//  verify Inactive state in Favorites widget
		drWidget.assertEqualsStateByWidgetAndLinkName("Inactive", "Favorites","CntrlArea01-Manual");
		drWidget.assertEqualsStateByWidgetAndLinkName("Inactive", "Favorites","LdGrp01-Expresscom");
		drWidget.assertEqualsStateByWidgetAndLinkName("Inactive", "Favorites","LdPgm04-2Gear");

		//  remove items in Favorites list  (click on icon)
		drWidget.addOrRemoveFromFavoritesByName("Favorites", "CntrlArea01-Manual");
		drWidget.addOrRemoveFromFavoritesByName("Favorites", "LdGrp01-Expresscom");
		drWidget.addOrRemoveFromFavoritesByName("Favorites", "LdPgm04-2Gear");
		
		topMenu.clickTopMenuItem("Demand Response");
		Assert.assertEquals("No Favorites", common.getYukonText("No Favorites"));
		common.end();
	}
	
	/**
	 * verifies DR main page recently Viewed widget options,
	 *  
	 */
	@Test
	public void verifyRecentlyViewedWidget() {
		init();
		//  login as Stars operator, navigate to Demand Response page
		//  and verify objects on page		
		WidgetSolvent widget = new WidgetSolvent();
		DRWidgetWithTableSolvent drWidget = new DRWidgetWithTableSolvent ();
		new CommonSolvent().clickLinkByName("Demand Response");
						
		// verify Inactive state in Recently Viewed widget
		drWidget.assertEqualsStateByWidgetAndLinkName("Inactive", "Recently Viewed Items","CntrlArea01-Manual");
		drWidget.assertEqualsStateByWidgetAndLinkName("Inactive", "Recently Viewed Items","LdGrp02-Versacom");
		drWidget.assertEqualsStateByWidgetAndLinkName("Inactive", "Recently Viewed Items","LdPgm01-2Gear");
				
		// verify items now in Recently Viewed widget
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","Scenario-01");
		widget.end();
	}
	 
	/**
	 * nav Program & Load Group detail page, verify widgets links at bottom of page
	 */
	@Test
	public void verifyOtherWidgetLinks() {
		init();
		// click on/navigate to Details default page (Control Areas) 
		// and verify objects on page
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		common.clickLinkByName("Demand Response");
		
		widget.clickLinkByWidgetWithPageLoadWait("Recently Viewed Items", "LdGrp02-Versacom");
		Assert.assertEquals("Load Group LdGrp02-Versacom", common.getPageTitle());

		widget.clickLinkByWidgetWithPageLoadWait("Programs", "LdPgm04-2Gear");
		Assert.assertEquals("Program LdPgm04-2Gear", common.getPageTitle());		

		widget.clickLinkByWidgetWithPageLoadWait("Control Area", "CntrlArea01-Manual");
		Assert.assertEquals("Control Area CntrlArea01-Manual", common.getPageTitle());		
		topMenu.clickBreadcrumb("Demand Response");		

		widget.clickLinkByWidgetWithPageLoadWait("Recently Viewed Items", "LdPgm01-2Gear");
		Assert.assertEquals("Program LdPgm01-2Gear", common.getPageTitle());

		widget.clickLinkByWidgetWithPageLoadWait("Scenarios", "Scenario-01");
		Assert.assertEquals("Scenario Scenario-01", common.getPageTitle());
		common.end();
	}	

	/**
	 * verify  Find & Go  search function on various pages
	 * 
	 */
	@Test
	public void verifyFindAndGo() {
		init();
		// click on/navigate to Details default page (Control Areas) 
		// and verify objects on page
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		common.clickLinkByName("Demand Response");
		
		common.clickFindAndGo("nario-01");
		Assert.assertEquals("Scenario Scenario-01", common.getPageTitle());
		
		common.clickFindAndGo("Area");
		Assert.assertEquals("Demand Response Quick Search", common.getPageTitle());
		//  verify table 'link' names  
		Assert.assertEquals("CntrlArea01-Manual", common.getLinkText("CntrlArea01-Manual"));
		Assert.assertEquals("CntrlArea02-Manual", common.getLinkText("CntrlArea02-Manual"));
		
		topMenu.clickTopSubMenuItem("Control Areas");
		Assert.assertEquals("Control Areas", common.getPageTitle());
		common.clickFindAndGo("Pgm");
		Assert.assertEquals("Demand Response Quick Search", common.getPageTitle());
		//  verify table 'link' names   
		Assert.assertEquals("LdPgm01-2Gear", common.getLinkText("LdPgm01-2Gear"));
		Assert.assertEquals("LdPgm02-2Gear", common.getLinkText("LdPgm02-2Gear"));
		Assert.assertEquals("LdPgm03-2Gear", common.getLinkText("LdPgm03-2Gear"));
		Assert.assertEquals("LdPgm04-2Gear", common.getLinkText("LdPgm04-2Gear"));
		Assert.assertEquals("LdPgm05-2Gear", common.getLinkText("LdPgm05-2Gear"));
		Assert.assertEquals("LdPgm06-2Gear", common.getLinkText("LdPgm06-2Gear"));
		Assert.assertEquals("LdPgm07-2Gear", common.getLinkText("LdPgm07-2Gear"));
		Assert.assertEquals("LdPgm08-2Gear", common.getLinkText("LdPgm08-2Gear"));
		
		topMenu.clickTopSubMenuItem("Programs");
		Assert.assertEquals("Programs", common.getPageTitle());
		common.clickFindAndGo("-Versa");
		Assert.assertEquals("Demand Response Quick Search", common.getPageTitle());
		//  verify table 'link' names  
		Assert.assertEquals("LdGrp01-Versacom", common.getLinkText("LdGrp01-Versacom"));
		Assert.assertEquals("LdGrp02-Versacom", common.getLinkText("LdGrp02-Versacom"));
		Assert.assertEquals("LdGrp03-Versacom", common.getLinkText("LdGrp03-Versacom"));
		Assert.assertEquals("LdGrp04-Versacom", common.getLinkText("LdGrp04-Versacom"));
		common.end();
	}	
		
	/**
	 * verify Scenario  Start Control  Cancel  button 
	 * 
	 */	
	@Test
	public void verifyScenarioStartControlCancel() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRScenariosTableSolvent drScenarios = new DRScenariosTableSolvent ();
		common.clickLinkByName("Demand Response");
		
		topMenu.clickTopSubMenuItem("Scenarios");
		drScenarios.clickStartButton("Scenario-01");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		common.clickButtonByName("Cancel");	

		drScenarios.clickScenarioByName("Scenario-01");
		widget.clickLinkByWidget("Actions", "Start Scenario");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		common.clickButtonByName("Next");	
		common.clickButtonByName("Cancel");
		common.end();
	}
	
	/**
	 * verify Control Areas  Start Control  Cancel  button 
	 * & Disable/Enable toggle for each 'device' type
	 */	
	@Test
	public void verifyCntlAreaStartControlCancel() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRControlAreasTableSolvent drCntlArea = new DRControlAreasTableSolvent ();
		common.clickLinkByName("Demand Response");
		
		topMenu.clickTopSubMenuItem("Control Areas");
		drCntlArea.clickStartButton("CntrlArea01-Manual");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		common.clickButtonByName("Cancel");	

		drCntlArea.clickControlAreaByName("CntrlArea01-Manual");
		widget.clickLinkByWidget("Actions", "Start Control Area");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		common.clickButtonByName("Next");	
		common.clickButtonByName("Cancel");	
		
		//  verify  Disable/Enable toggle option in Control Areas Actions widget
		widget.clickLinkByWidget("Actions", "Disable Control Area");
		Assert.assertEquals("Send Disable", common.getYukonText("Send Disable"));
		common.clickButtonByName("OK");	
		Assert.assertEquals("Control area disabled.", common.getYukonText("Control area disabled."));
		
		// nav back to CA main page and verify  Disabled  state
		topMenu.clickBreadcrumb("Control Areas");		
		Assert.assertEquals("Disabled: Inactive", drCntlArea.getDemandResponseState("CntrlArea01-Manual"));
		
		// nav back to CA detail page and verify  Disabled  state, then Enable CA
		drCntlArea.clickControlAreaByName("CntrlArea01-Manual");
		Assert.assertEquals("Disabled: Inactive", widget.getTextFromWidgetByLabel("Info", "State:"));
		widget.clickLinkByWidget("Actions", "Enable Control Area");
		Assert.assertEquals("Send Enable", common.getYukonText("Send Enable"));
		common.clickButtonByName("OK");	
		Assert.assertEquals("Control area enabled.", common.getYukonText("Control area enabled."));
		
		// nav back to CA main page and verify  Enabled  state
		topMenu.clickTopSubMenuItem("Control Areas");
		Assert.assertEquals("Inactive", drCntlArea.getDemandResponseState("CntrlArea01-Manual"));
		
		// nav back to CA detail page and verify  Enabled  state
		drCntlArea.clickControlAreaByName("CntrlArea01-Manual");
		Assert.assertEquals("Inactive", widget.getTextFromWidgetByLabel("Info", "State:"));
		common.end();
	}
	
	
	/**
	 * verify Control Areas  Change Time Window  option 
	 * on the CA detail page, Actions widget
	 */	
	@Test
	public void verifyCntlAreaChangeTimeWindow() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRControlAreasTableSolvent drCntlArea = new DRControlAreasTableSolvent ();
		common.clickLinkByName("Demand Response");
		
		//  navigate to Control Area detail page
		topMenu.clickTopSubMenuItem("Control Areas");
		drCntlArea.clickControlAreaByName("CntrlArea01-Manual");

		//  verify  Time Window values in Info widget
//		widget.assertEqualsTextFromWidgetByLabel("N/A - N/A", "Info", "Time Window:");

		//  change  Time Window  value
		widget.clickLinkByWidget("Actions", "Change Control Area Time Window");
		Assert.assertEquals(true, common.isTextPresent("Send Temporary Time Window Change"));
		common.enterInputText("controlArea/sendChangeTimeWindowConfirm", "startTime", "10:00");
		common.enterInputText("controlArea/sendChangeTimeWindowConfirm", "stopTime", "11:00");
		common.clickButtonByName("OK");	
		
		//  verify Confirmation & success
		Assert.assertEquals("Send Temporary Time Window Change", common.getYukonText("Send Temporary Time Window Change"));
		common.clickButtonByName("OK");	
		Assert.assertEquals("Time window changed.", common.getYukonText("Time window changed."));
		widget.assertEqualsTextFromWidgetByLabel("10:00 CST - 11:00 CST", "Info", "Time Window:");
		
		//  verify Time Window by starting Control Area, Constraints?
		widget.clickLinkByWidget("Actions", "Start Control Area");
		Assert.assertEquals("Start Programs", common.getYukonText("Start Programs"));
		common.clickButtonByName("Next");	
		Assert.assertEquals("Override", common.getYukonText("Override"));
		common.clickButtonByName("Cancel");	
		
		/*   yuk-9636  can't remove  Time Window
		//  remove  Time Window  change form above
		widget.clickLinkByWidget("Actions", "Change Control Area Time Window");
		Assert.assertEquals("Send Temporary Time Window Change", common.getYukonText("Send Temporary Time Window Change"));
		common.enterText("New Start Time:", "00:00");
		common.enterText("New Stop Time:", "00:00");
		common.clickButtonByName("OK");	
		
		//  verify Confirmation & success
		Assert.assertEquals("Send Temporary Time Window Change", common.getYukonText("Send Temporary Time Window Change"));
		common.clickButtonByName("OK");	
		Assert.assertEquals("Time window changed.", common.getYukonText("Time window changed."));
		waitFiveSeconds();
		Assert.assertEquals("N/A - N/A", widget.getTextFromWidgetByLabel("Info", "Time Window:"));
		*/
		common.end();
	}
	
	/**
	 * verify Programs  Start Control  Cancel  button 
	 * & Disable/Enable toggle for each 'device' type
	 */	
	@Test
	public void verifyProgramStartControlCancel() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRProgramsTableSolvent drPrograms = new DRProgramsTableSolvent ();
		common.clickLinkByName("Demand Response");
				
		topMenu.clickTopSubMenuItem("Programs");
		drPrograms.clickStartButton("LdPgm08-2Gear");
		Assert.assertEquals("Start Program", common.getYukonText("Start Program"));
		common.clickButtonByName("Cancel");	

		drPrograms.clickProgramByName("LdPgm08-2Gear");
		widget.clickLinkByWidget("Actions", "Start Program");
		Assert.assertEquals("Start Program", common.getYukonText("Start Program"));
		common.clickButtonByName("Next");	
		common.clickButtonByName("Cancel");	
		
		//  verify  Disable/Enable toggle option in Programs Actions widget
		widget.clickLinkByWidget("Actions", "Disable Program");
		Assert.assertEquals("Send Disable", common.getYukonText("Send Disable"));
		common.clickButtonByName("OK");	
		Assert.assertEquals("Program disabled.", common.getYukonText("Program disabled."));
		
		// nav back to Pgms main page and verify  Disabled  state
		topMenu.clickBreadcrumb("Programs");		
		Assert.assertEquals("Disabled: Inactive", drPrograms.getDemandResponseState("LdPgm08-2Gear"));
		
		// nav back to Pgms detail page and verify  Disabled  state
		drPrograms.clickProgramByName("LdPgm08-2Gear");
		Assert.assertEquals("Disabled: Inactive", widget.getTextFromWidgetByLabel("Info", "State:"));
		widget.clickLinkByWidget("Actions", "Enable Program");
		Assert.assertEquals("Send Enable", common.getYukonText("Send Enable"));
		common.clickButtonByName("OK");	
		Assert.assertEquals("Program enabled.", common.getYukonText("Program enabled."));
		
		// nav back to Pgms main page and verify  Enabled  state
		topMenu.clickTopSubMenuItem("Programs");
		Assert.assertEquals("Inactive", drPrograms.getDemandResponseState("LdPgm08-2Gear"));
		
		// nav back to Pgms detail page and verify  Enabled  state
		drPrograms.clickProgramByName("LdPgm08-2Gear");
		Assert.assertEquals("Inactive", widget.getTextFromWidgetByLabel("Info", "State:"));
		common.end();
	}
	
	/**
	 * verify Load Group  Start Control  Cancel  button 
	 * & Disable/Enable toggle for each 'device' type
	 */	
	@Test
	public void verifyLoadGrpStartControlCancel() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRLoadGroupsTableSolvent drLdGrps = new DRLoadGroupsTableSolvent ();
		common.clickLinkByName("Demand Response");
						
		topMenu.clickTopSubMenuItem("Load Groups");
		drLdGrps.clickStartButton("LdGrp04-Versacom");
		Assert.assertEquals("Send Shed", common.getYukonText("Send Shed"));
		common.clickButtonByName("Cancel");	
		
		//  verify  Disable/Enable toggle option in Load Groups Actions widget
		drLdGrps.clickLoadGroupByName("LdGrp04-Versacom");
		
		widget.clickLinkByWidget("Actions", "Disable Load Group");
		Assert.assertEquals("Send Disable", common.getYukonText("Send Disable"));
		common.clickButtonByName("OK");	
		Assert.assertEquals("Load group disabled.", common.getYukonText("Load group disabled."));
		
		// nav back to Ld Grps main page and verify  Disabled  state
		topMenu.clickBreadcrumb("Load Groups");		
		Assert.assertEquals("Disabled: Inactive", drLdGrps.getDemandResponseState("LdGrp04-Versacom"));
		
		// nav back to Ld Grps detail page and verify  Disabled  state
		drLdGrps.clickLoadGroupByName("LdGrp04-Versacom");
		Assert.assertEquals("Disabled: Inactive", widget.getTextFromWidgetByLabel("Info", "State:"));
		widget.clickLinkByWidget("Actions", "Enable Load Group");
		Assert.assertEquals("Send Enable", common.getYukonText("Send Enable"));
		common.clickButtonByName("OK");	
		Assert.assertEquals("Load group enabled.", common.getYukonText("Load group enabled."));

		// nav back to Ld Grps main page and verify  Enabled  state
		topMenu.clickTopSubMenuItem("Load Groups");
		Assert.assertEquals("Inactive", drLdGrps.getDemandResponseState("LdGrp04-Versacom"));
		
		// nav back to Ld Grps detail page and verify  Enabled  state
		drLdGrps.clickLoadGroupByName("LdGrp04-Versacom");
		Assert.assertEquals("Inactive", widget.getTextFromWidgetByLabel("Info", "State:"));
		common.end();
	}
	
	/**
	 * verify items in DR Recently Viewed widget 
	 * 
	 */	
	@Test
	public void verifyRecentlyViewedList() {
		init();
		WidgetSolvent widget = new WidgetSolvent();
		new CommonSolvent().clickLinkByName("Demand Response");
				
		// verify items now in Recently Viewed widget
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","CntrlArea01-Manual");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","LdGrp02-Versacom");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","LdGrp04-Versacom");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","LdPgm01-2Gear");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","LdPgm04-2Gear");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","LdPgm08-2Gear");
		widget.getLinkFromWidgetByLinkName("Recently Viewed Items","Scenario-01");
		widget.end();
	}
					
	/**
	 * verify items in DR Recently Viewed widget
	 */	
	@Test
	public void verifyCntlAreaChangeTriggerValues() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DRControlAreasTableSolvent drCntlArea = new DRControlAreasTableSolvent ();
		common.clickLinkByName("Demand Response");
		
		//  navigate to Control Area detail page
		topMenu.clickTopSubMenuItem("Control Areas");
		drCntlArea.clickControlAreaByName("Threshold01");

		//  verify  Time Window values in Info widget
		widget.assertEqualsTextFromWidgetByLabel("1.00 / 99,999.00", "Info", "Value/Threshold:");

		//  change  Time Window  value
		widget.clickLinkByWidget("Actions", "Change Control Area Trigger Values");
		Assert.assertEquals("Send Trigger Change", common.getYukonText("Send Trigger Change"));
		common.enterInputText("controlArea/triggerChange", "trigger1.threshold", "1000");
		common.clickButtonByNameWithPageLoadWait("OK");	
		
		//  verify Confirmation & success
		//Assert.assertEquals("Send Trigger Change", common.getYukonText("Send Trigger Change"));
		//common.clickButtonByName("OK");	
		Assert.assertEquals("Trigger value changed.", common.getYukonText("Trigger value changed."));
		widget.assertEqualsTextFromWidgetByLabel("1.00 / 1,000.00", "Info", "Value/Threshold:");

        //  change  Time Window  value back to original
        widget.clickLinkByWidget("Actions", "Change Control Area Trigger Values");
        Assert.assertEquals("Send Trigger Change", common.getYukonText("Send Trigger Change"));
        common.enterInputText("controlArea/triggerChange", "trigger1.threshold", "99999.00");
        common.clickButtonByNameWithPageLoadWait("OK"); 
        
        //  verify  Time Window values in Info widget
        widget.assertEqualsTextFromWidgetByLabel("1.00 / 99,999.00", "Info", "Value/Threshold:");
        common.end();
	}
				
	/**
	 * verify Opt Out Admin page widgets 
	 */
	@Test
	public void verifyDRoptOutAdminLink() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		common.clickLinkByName("Demand Response");
						
		// click on/navigate to Opt Out > Admin page  
		// verify widget names, text on page
		topMenu.clickTopMenuItem("Opt Out");
		Assert.assertEquals("Demand Response: Opt Out Status", common.getPageTitle());		
   		Assert.assertEquals("System Information: Opt Out", widget.getWidgetTitle("System Information: Opt Out"));
		Assert.assertEquals("Disable Consumer Opt Out Ability for Today", widget.getWidgetTitle("Disable Consumer Opt Out Ability for Today"));
		Assert.assertEquals("Cancel Current Opt Outs", widget.getWidgetTitle("Cancel Current Opt Outs"));
		Assert.assertEquals("Count Today Against Opt Out Limits", widget.getWidgetTitle("Count Today Against Opt Out Limits"));
		Assert.assertEquals("Account Search", widget.getWidgetTitle("Account Search"));
		common.end();
	}		
				 	
}

