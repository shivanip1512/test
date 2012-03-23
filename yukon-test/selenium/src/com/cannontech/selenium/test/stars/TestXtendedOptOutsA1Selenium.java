 package com.cannontech.selenium.test.stars;

import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;
import com.cannontech.selenium.solvents.stars.OptOutTableSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;

/**
 * This class uses xml file to import accounts and views the Account Information that was imported.
 * Main idea of this class is to verify Opt Out functionality in more depth 
 * this also includes Opt Out Administration functionality
 * Add  yuk-8672  Opt Out Admin > Disable Consumer Opt Out by Program
 *      and  Consumer Opt Out Limits by Program
 *  follows verification for test case  4.2.1.10, 3.2.1.7
 * @author steve.junod
 */
public class TestXtendedOptOutsA1Selenium extends SolventSeleniumTestCase {
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
	}
	/**
	 * Method logins as Stars Operator, Imports Account needed for Opt Out tests.
	 */
	@Test
	public void setupAccounts() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Import Account");
		Assert.assertEquals("Operator: Account Import", common.getYukonText("Operator: Account Import"));
		Assert.assertEquals("Operator: Account Import", common.getPageTitle());

		common.enterInputText("account/uploadImportFile", "accountImportFile", getParamString("ImportInsertAccount"));
		common.clickButtonBySpanText("Prescan");
		common.assertEqualsTextIsPresent("Finished - Passed");
		common.assertEqualsTextIsPresent("Customer File Lines Processed:");    // yuk-9145 
		common.assertEqualsTextIsPresent("6");     //  yuk-9145
		common.clickFormButtonByButtonId("account/doAccountImport", "importButton");

		//  check for  100%  value   yuk-9144,9147
		common.assertEqualsTextIsPresent("Accounts:");     
		common.assertEqualsTextIsPresent("100%");     
		common.assertEqualsTextIsPresent("6 Added, 0 Updated, 0 Removed", Duration.standardSeconds(6));
		common.clickLinkByName("Back to Account Import");
		
		common.enterInputText("account/uploadImportFile", "accountImportFile", getParamString("ImportUpdateAccount"));
		common.clickButtonBySpanText("Prescan");
		
		common.assertEqualsTextIsPresent("Finished - Passed");
		common.assertEqualsTextIsPresent("Customer File Lines Processed:");    // yuk-9145 
		common.assertEqualsTextIsPresent("6");     //  yuk-9145
		common.clickFormButtonByButtonId("account/doAccountImport", "importButton");
		
		//  check for  100%  value   yuk-9144,9147
		common.assertEqualsTextIsPresent("Accounts:");     
		common.assertEqualsTextIsPresent("100%");     
		common.assertEqualsTextIsPresent("0 Added, 6 Updated, 0 Removed", Duration.standardSeconds(6));
		common.clickLinkByName("Back to Account Import");

		common.enterInputText("account/uploadImportFile", "hardwareImportFile", getParamString("ImportUpdEnrollAccount"));
		common.clickButtonBySpanText("Prescan");

		common.assertEqualsTextIsPresent("Finished - Passed");
		common.assertEqualsTextIsPresent("Hardware File Lines Processed:");    // yuk-9145 
		common.assertEqualsTextIsPresent("7");     //  yuk-9145
		common.clickFormButtonByButtonId("account/doAccountImport", "importButton");

		//  check for  100%  value   yuk-9144,9147
		common.assertEqualsTextIsPresent("Hardware:");     
		common.assertEqualsTextIsPresent("100%");     
		common.assertEqualsTextIsPresent("7 Added, 0 Updated, 0 Removed", Duration.standardSeconds(8));
		common.clickLinkByName("Back to Account Import");

		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * acct search = 71000001, then verify Import Account update info
	 *  yuk-9148
	 */
	@Test
	public void verifyAcctUpdInfo() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
	
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000001");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000001)", common.getPageTitle());
	
		// verify Imp Account update info
		Assert.assertEquals(true, common.isTextPresent("First01"));
		Assert.assertEquals(true, common.isTextPresent("Last01"));
		Assert.assertEquals(true, common.isTextPresent("7255 First Road"));     
		Assert.assertEquals(true, common.isTextPresent("St Addr 2"));     
		Assert.assertEquals(true, common.isTextPresent("Fort Worth"));
		Assert.assertEquals(true, common.isTextPresent("NH"));  
		Assert.assertEquals(true, common.isTextPresent("59327"));
			
		common.clickLinkByName("Home");
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
	
		// verify Imp Account update info
		Assert.assertEquals(true, common.isTextPresent("First05"));
		Assert.assertEquals(true, common.isTextPresent("Last05"));
		Assert.assertEquals(true, common.isTextPresent("9999 Second Drive"));     
		Assert.assertEquals(true, common.isTextPresent("St Addr 2"));     
		Assert.assertEquals(true, common.isTextPresent("Omacron5"));
		Assert.assertEquals(true, common.isTextPresent("MN"));  
		Assert.assertEquals(true, common.isTextPresent("12345"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * starts / stops Opt Out today
	 * starts Opt Out today, for 1 day, manual verify complete tomorrow
	 *  acct  71000004
	 */
	@Test
	public void optOutNow1Day() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
	
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000004");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000004)", common.getPageTitle());
	
		//Check no OptOut exists
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000004)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		Assert.assertEquals("There are no previous Opt Outs.",common.getYukonText("There are no previous Opt Outs."));
		
		//OptOut, then cancel
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000004)", common.getPageTitle());
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("No device selected", common.getYukonText("No device selected"));
		common.selectCheckBox("711100041");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000004)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100041"));
		
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100041");
		optout.confirmOKOrCancel("cancel the opt out for 711100041", "submit");
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));

		//  check event in Control history
		Assert.assertEquals("Cancel by starsop", optout.getOptOutHistoryActionsByDevice("711100041"));
		Assert.assertEquals("0d 0h", optout.getOptOutHistoryDurationByDevice("711100041"));
		
		//OptOut, then verify successful complete after 1 day
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000004)", common.getPageTitle());
		common.selectCheckBox("711100041");
		
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000004)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100041"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * starts Opt Out today, for 2 days, manual verify complete in 2 days
	 *  acct  71000003
	 */
	@Test
	public void optOutNow2Day() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
	
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000003");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000003)", common.getPageTitle());
	
		//Check no OptOut exists
		common.clickLinkByName("Opt Out");
		
		Assert.assertEquals("Operator: Opt Out (71000003)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		Assert.assertEquals("There are no previous Opt Outs.",common.getYukonText("There are no previous Opt Outs."));
		
		//OptOut, verify goes Active, then verify complete successful after 2 days
		common.selectDropDownMenu("optOut/deviceSelection", "durationInDays", "2 Day");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		
		Assert.assertEquals("Operator: Device Selection (71000003)", common.getPageTitle());
		common.selectCheckBox("711100031");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000003)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100031"));
		
		//  Opt Out  Resend  action
		optout.resendOptOut("Current Opt Outs", "711100031");
		optout.confirmOKOrCancel("resend an opt out command for 711100031", "submit");
		Assert.assertEquals("Opt Out Command Resent", common.getYukonText("Opt Out Command Resent"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * starts Opt Out tomorrow, for 2 days, 
	 * manual verify complete in 2 days
	 *  acct  71000002
	 */
	@Test
	public void optOutNext2Day() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		EventsByTypeSolvent event = new EventsByTypeSolvent();
		
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000002");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000002)", common.getPageTitle());
	
		//Check no OptOut exists
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000002)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		Assert.assertEquals("There are no previous Opt Outs.",common.getYukonText("There are no previous Opt Outs."));
		
		//  Schedule OptOut, start tomorrow, for 2 days, than cancel
		//  need to be able to select 'tomorrow'
		common.enterInputText("optOut/deviceSelection", "startDate", event.returnDate(1));
	
		common.selectDropDownMenu("optOut/deviceSelection", "durationInDays", "2 Day");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000002)", common.getPageTitle());
		common.selectCheckBox("711100021");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000002)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100021"));
				
		//  cancel Scheduled Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100021");
		optout.confirmOKOrCancel("cancel the opt out for 711100021", "submit");
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
		
		//  verify event in Opt Out history
		Assert.assertEquals("Cancel Schedule by starsop", optout.getOptOutHistoryActionsByDevice("711100021"));
		Assert.assertEquals("Canceled", optout.getOptOutHistoryDurationByDevice("711100021"));

		common.assertEqualsTextIsPresent("Cancel Schedule by starsop");
		
		//  Schedule OptOut, start tomorrow, for 2 days
		//  verify complete successful after 2 days
		//  need to be able to select 'tomorrow'
		common.enterInputText("/optOut/deviceSelection", "startDate", event.returnDate(1));
	
		common.selectDropDownMenu("optOut/deviceSelection", "durationInDays", "2 Day");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000002)", common.getPageTitle());
		common.selectCheckBox("711100021");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000002)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100021"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * goes to Opt Out Admin, verify Scheduled Opt Outs
	 * disable Opt Out for Cust, LdPgm01-2Gear, verify Opt Out option not in Cust page (71000000)
	 * then verify Opt Out option visible for Cust 71000005 
	 * causes  Opt Outs from above (71000002 thru 71000004) to be cancelled
	 * covers test case  3.2.1.7 also
	 */
	@Test
	public void optOutAdminDisable() {
		init();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
	
		//  go to Opt Out Admin
		common.clickLinkByName("Opt Out Administration");
		Assert.assertEquals("Demand Response: Opt Out Status", common.getPageTitle());
		
		//  verify current Active / Scheduled Opt Outs  ***** 5.2.3 yuk-9273 (widget titles changed)
		//Assert.assertEquals("2", widget.getTextFromWidgetByLabel("System Information: Opt Out", "s Opt Outs:"));
		Assert.assertEquals("17", widget.getTextFromWidgetByLabel("System Information: Opt Out", "Total Consumer Accounts:"));
		Assert.assertEquals("1", widget.getTextFromWidgetByLabel("System Information: Opt Out", "Future Opt Outs:"));

		//  go to Sched Events and verify Scheduled Opt Outs    yuk-9212
		//  should match above  'Future Opt Outs'
		topMenu.clickTopSubMenuItem("Scheduled Events");
		Assert.assertEquals("Demand Response: Scheduled Opt Outs", common.getPageTitle());
		
		// disable cust Opt Out option    **** 5.2.3 error  yuk-9272
		topMenu.clickTopSubMenuItem("Admin");
		//  new feature   yuk-8672   Choose Program
		// click on Choose Program & select  LdPgm01-2Gear, then 'prohibit Opt Outs'
		widget.clickLinkByWidget("Disable Consumer Opt Out Ability for Today","Choose Program");
		PopupMenuSolvent programPicker = PickerFactory.createPopupMenuSolvent("disabledProgram", PickerType.SingleSelect);
		programPicker.clickMenuItem("LdPgm01-2Gear");
		//common.clickLinkByNameWithoutPageLoadWait("LdPgm01-2Gear");
		common.clickFormButton("admin/setDisabled", "disableOptOuts");
		Assert.assertEquals("Disabled the Consumer's Ability to Opt Out for the program LdPgm01-2Gear. The Consumer can communicate with devices.", common.getYukonText("Disabled the Consumer"));
			
		topMenu.clickTopMenuItem("Logout");

		//  login as Cust and verify Opt Out option not visible
		login.cannonLogin("71000000", "71000000");
	
		//  check for  Opt Out  link - should not be available   
		Assert.assertFalse(common.isLinkPresent("Opt Out"));

		common.clickLinkByName("Logout");

		//  login as Cust and verify Opt Out option is visible
		login.cannonLogin("71000005", "71000005");
	
		//  check for  Opt Out  link - should be available   
		Assert.assertEquals(true, common.isLinkPresent("Opt Out"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper, goes to Opt Out Admin
	 * disable Opt Out for Cust, all Programs, verify Opt Out option not in Cust page (71000005)
	 *   3.2.1.7
	 */
	@Test
	public void optOutAdminDisable2() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
	
		//  go to Opt Out Admin
		common.clickLinkByName("Opt Out Administration");
		Assert.assertEquals("Demand Response: Opt Out Status", common.getPageTitle());
		
		// disable cust Opt Out option for all programs    **** 5.2.3 error  yuk-9272
		common.clickFormButton("admin/setDisabled", "disableOptOuts");
		//  can't verify  text  with apostrophe's 
		Assert.assertEquals("Disabled the Consumer's Ability to Opt Out for all programs. The Consumer can communicate with devices.", common.getYukonText("Disabled the Consumer"));
		common.end();
	}
	/**     
	 * Method logins as Stars Cust to verify that Opt Out option is disabled
	 *    3.2.1.7
	 */
	@Test
	public void optOutCustDisabled2() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
				
		login.cannonLogin("71000005", "71000005");
	
		//  check for  Opt Out  link - should not be available   **** 5.2.3 error  yuk-9272
		Assert.assertEquals(common.isLinkPresent("Opt Out"), false);     //  ***********  qa-417
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * goes to Opt Out Admin, re-enable cust Opt Outs
	 * enable  Don't Count Opt Out Towards Limit  for any cust enrolled in Program LdPgm01-2Gear
	 *    3.2.1.6
	 */
	@Test
	public void optOutAdminDontCount() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
	
		//  go to Opt Out Admin
		common.clickLinkByName("Opt Out Administration");

		// enable cust Opt Out option
		common.clickFormButton("admin/setDisabled", "enableOptOuts");
		Assert.assertEquals("Enabled the Consumer's Ability to Opt Out for all programs and communicate with devices.", common.getYukonText("Enabled the Consumer"));
			
		// don't count Opt Outs towards limit for Program  LdPgm01-2Gear
		widget.clickLinkByWidget("Count Today Against Opt Out Limits","Choose Program");
		PopupMenuSolvent programPicker = PickerFactory.createPopupMenuSolvent("disabledCountProgram", PickerType.SingleSelect);
		programPicker.clickMenuItem("LdPgm01-2Gear");
		common.clickFormButton("admin/setCounts", "dontCount");
		common.end();
	}
	/**
	 * Basic test is to Not Count Opt Outs for a specific Program (LdPgm01-2Gear)
	 * then verify Cust Opt Out using Program doesn't count (71000001),
	 * and verify Cust Opt Out not using Program does count (71000005)  
	 * login as starsop, cancel Active Opt Out for acct 71000005 only
	 *    3.2.1.6
	 */
	@Test
	public void optOutCustLimits() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
				
		login.cannonLogin("71000001", "71000001");
	
		//  get to Opt Out page, verify current Opt Out Limits values  qa-386 (cust side)  *********
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("0", optout.getCustomerOptOutUsedValue("711100011", "Used"));
		Assert.assertEquals("5", optout.getCustomerOptOutRemainingValue("711100011", "Remaining"));
		
		//  start the Opt Out
		common.clickButtonByNameWithPageLoadWait("Apply");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Opt Out Result", common.getYukonText("Opt Out Result"));
		Assert.assertEquals("Your programs have been opted out.", common.getYukonText("Your programs have been opted out."));
 		common.clickButtonByNameWithPageLoadWait("Ok");
		
		//  verify  Active  status
		Assert.assertEquals("Active", optout.getCustomerOptOutStatusValue("711100011", "Status"));
		
		//  verify that Opt Out event did not count towards Limit    
		Assert.assertEquals("0", optout.getCustomerOptOutUsedValue("711100011", "Used"));
		Assert.assertEquals("5", optout.getCustomerOptOutRemainingValue("711100011", "Remaining"));
				
		common.clickLinkByName("Logout");

		// verify Cust Opt Out not using Program (LdPgm01-2Gear) does count (71000005)
		login.cannonLogin("71000005", "71000005");
		
		//  get to Opt Out page, verify current Opt Out Limits values  qa-386 (cust side)  *********
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("0", optout.getCustomerOptOutUsedValue("711100051", "Used"));
		Assert.assertEquals("5", optout.getCustomerOptOutRemainingValue("711100051", "Remaining"));
		
		//  start the Opt Out
		common.clickButtonByNameWithPageLoadWait("Apply");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Opt Out Result", common.getYukonText("Opt Out Result"));
		Assert.assertEquals("Your programs have been opted out.", common.getYukonText("Your programs have been opted out."));
 		common.clickButtonByNameWithPageLoadWait("Ok");
		
		//  verify  Active  status
		Assert.assertEquals("Active", optout.getCustomerOptOutStatusValue("711100051", "Status"));
		
		//  verify that Opt Out event did count towards Limit    
		Assert.assertEquals("1", optout.getCustomerOptOutUsedValue("711100051", "Used"));
		Assert.assertEquals("4", optout.getCustomerOptOutRemainingValue("711100051", "Remaining"));
				
		common.clickLinkByName("Logout");

		//  login as startop and cancel Active Opt Out for acct  71000005
		login.cannonLogin("starsop", "starsop");
		
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
	
		//Cancel the Active Opt Out for acct 71000005
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000005)", common.getPageTitle());
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100051");
		optout.confirmOKOrCancel("cancel the opt out for 711100051", "submit");
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
		
		Assert.assertEquals("Cancel by starsop", optout.getOptOutHistoryActionsByDevice("711100051"));
		Assert.assertEquals("0d 0h", optout.getOptOutHistoryDurationByDevice("711100051"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * verifies Opt Out by customer is Active, and Limit values did not change
	 * Resets  count Opt Outs = enabled
	 *  acct  71000001
	 *      3.2.1.7
	 */
	@Test
	public void optOutOperLimits() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		//search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000001");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000001)", common.getPageTitle());
	
		//verify that Opt Out = Active
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000001)", common.getPageTitle());
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100011"));
		
		// verify Opt Out limits values - oper side
		Assert.assertEquals("0", optout.getOperatorOptOutUsedValue("711100011"));
		Assert.assertEquals("5", optout.getOperatorOptOutRemainingValue("711100011"));
		
		// back to Opt Out Admin, re-enable Count Opt Outs
		topMenu.clickHome();
		common.clickLinkByName("Opt Out Administration");
		widget.clickButtonByWidget("Count Today Against Opt Out Limits","Count Today");
		common.end();
	}	
	/**
	 * Method logins as Stars Cust, starts a Scheduled Opt Out
	 * verify Scheduled not counted towards Limit
	 * 3.2.1.7
	 */
	@Test
	public void optOutCustSched() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
				
		login.cannonLogin("71000000", "71000000");
	
		//  get to Opt Out page, verify Opt Out Limits values 
		common.clickLinkByName("Opt Out");
		
		//  Schedule the Opt Out
		common.enterInputText("optout/deviceSelection", "startDate", new EventsByTypeSolvent().returnDate(1));

		common.clickButtonByNameWithPageLoadWait("Apply");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		common.clickButtonByNameWithPageLoadWait("Ok");
		
		Assert.assertEquals("Scheduled", optout.getCustomerOptOutStatusValue("711100001", "Status"));

		//  verify that Scheduled Opt Out event did not count towards Limit    
		Assert.assertEquals("0", optout.getCustomerOptOutUsedValue("711100001", "Used"));
		Assert.assertEquals("5", optout.getCustomerOptOutRemainingValue("711100001", "Remaining"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * verifies Opt Out by customer is Scheduled
	 * Cancels a Scheduled Opt Out started by the Cust
	 *  acct  71000000
	 */
	@Test
	public void optOutOperCancelSched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000000)", common.getPageTitle());
		
		//verify  Scheduled  OptOut
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100001"));
		
		//  verify that Scheduled Opt Out event did not count towards Limit   
		Assert.assertEquals("0", optout.getOperatorOptOutUsedValue("711100001"));
		Assert.assertEquals("5", optout.getOperatorOptOutRemainingValue("711100001"));
		
		//  cancel  Scheduled  Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100001");
		optout.confirmOKOrCancel("cancel the opt out for 711100001", "submit");
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
		
		//  verify event in  Opt Out History
		Assert.assertEquals("Cancel Schedule by starsop", optout.getOptOutHistoryActionsByDevice("711100001"));
		Assert.assertEquals("Canceled", optout.getOptOutHistoryDurationByDevice("711100001"));
		common.end();
	}
	/**
	 * Method logins as Stars Cust, starts an  Active  Opt Out
	 *   and verifies State & Limits
	 * then logs in as Stars Oper, verifies State & Limits
	 *   then cancels Opt Out
	 * and loops 5 times
	 *  **** yuk-9382  causes error, then domino's into following scripts
	 */
	@Test
	public void optOutCustNowVerfiyLimitsLoop() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		int usedVal = 0;
		int remVal = 0;
				
		for(int i=0; i < 5; i++) {
				
			//  login as Cust, start Active Opt Out, verify State & Limits
			login.cannonLogin("71000000", "71000000");
	
			//  get to Opt Out page 
			common.clickLinkByName("Opt Out");
		
			//  Start the Opt Out, verify Active    ******  can't start last Opt Out, yuk-9382
			common.clickButtonByName("Apply");
			common.clickButtonByName("Opt Out");
			common.clickButtonByName("Ok");
			Assert.assertEquals("Active", optout.getCustomerOptOutStatusValue("711100001", "Status"));

			// verify Used / Remaining values for Cust Opt Out page; shld start as  0 / 5
			//  verify Limits  Used = int + 1       Remaining = 5 - (i + 1)
			usedVal = i + 1;
		    String usedValString = Integer.toString(usedVal);
			remVal = 5 - usedVal;  
		    String remValString = Integer.toString(remVal);
			Assert.assertEquals(usedValString, optout.getCustomerOptOutUsedValue("711100001", "Used"));
			Assert.assertEquals(remValString, optout.getCustomerOptOutRemainingValue("711100001", "Remaining"));
		
			new LoginLogoutSolvent().yukonLogout();

			//  login as Stars operator, and navigate to account pages
			login.cannonLogin("starsop", "starsop");
			stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000000");
			common.clickMagnifyingIcon("Search for existing customer:");
			Assert.assertEquals("Operator: Account (71000000)", common.getPageTitle());
			
			//  get to Opt Out page, verify Active OptOut in Oper page
			common.clickLinkByName("Opt Out");
			Assert.assertEquals("Operator: Opt Out (71000000)", common.getPageTitle());
			Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100001"));
			
			// verify Used / Remaining values for Oper Opt Out page
			Assert.assertEquals(usedValString, optout.getOperatorOptOutUsedValue("711100001"));
			Assert.assertEquals(remValString, optout.getOperatorOptOutRemainingValue("711100001"));

			//  Cancel Active Opt Out
			optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100001");
			optout.confirmOKOrCancel("cancel the opt out for 711100001", "submit");
			Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
			Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
			
			//  verify event in  Opt Out History
			Assert.assertEquals("Cancel by starsop", optout.getOptOutHistoryActionsByDevice("711100001"));
			Assert.assertEquals("0d 0h", optout.getOptOutHistoryDurationByDevice("711100001"));
			
			new LoginLogoutSolvent().yukonLogout();
		
		}
		common.end();
	}
	/**
	 * Method logins as Stars Oper, adds extra Opt out,
	 * verifies Used / Remaining values
	 *  acct  71000000
	 */
	@Test
	public void optOutOperExtra() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
				
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000000)", common.getPageTitle());
		
		//  verify current Opt Out values
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000000)", common.getPageTitle());
		Assert.assertEquals("5", optout.getOperatorOptOutUsedValue("711100001"));
		Assert.assertEquals("0", optout.getOperatorOptOutRemainingValue("711100001"));

		//  add extra Opt Out
		optout.addExtraOptOut("711100001");
		//   yuk-9341   fixed in  5.2.5
		Assert.assertEquals("Are you sure you want to add an extra opt out for 711100001?", common.getYukonText("Are you sure you want to add an extra opt out for 711100001?"));
		optout.confirmOKOrCancel("add an extra opt out for 711100001", "submit");
		Assert.assertEquals("Opt Out Added", common.getYukonText("Opt Out Added"));

		//  verify extra Opt Out added
		Assert.assertEquals("5", optout.getOperatorOptOutUsedValue("711100001"));
		Assert.assertEquals("1", optout.getOperatorOptOutRemainingValue("711100001"));
		common.end();
	}
	/**
	 * Method logins as Stars Cust, starts an  Scheduled  Opt Out
	 *   and verifies State & Limits
	 * then tries to start an Active  Opt Out, 
	 * verfiy 'not available' msg on 'device selection' page (yuk-8909)
	 *   then cancel  Scheduled  Opt Out
	 */
	@Test
	public void optOutCustNotAvailable() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
				
		//  login as Cust, start Active Opt Out, verify State & Limits
		login.cannonLogin("71000000", "71000000");
	
		//  get to Opt Out page 
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("5", optout.getCustomerOptOutUsedValue("711100001", "Used"));
		Assert.assertEquals("1", optout.getCustomerOptOutRemainingValue("711100001", "Remaining"));
					
		//  Schedule the Opt Out
		common.enterInputText("optout/deviceSelection", "startDate", new EventsByTypeSolvent().returnDate(1));

		common.clickButtonByNameWithPageLoadWait("Apply");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		common.clickButtonByNameWithPageLoadWait("Ok");
		
		//  verify  Status & Limit
		Assert.assertEquals("Scheduled", optout.getCustomerOptOutStatusValue("711100001", "Status"));
		Assert.assertEquals("5", optout.getCustomerOptOutUsedValue("711100001", "Used"));
		Assert.assertEquals("1", optout.getCustomerOptOutRemainingValue("711100001", "Remaining"));
		
		Assert.assertEquals(true, common.isTextPresent("All opt outs for all devices have been used."));
		
		//  cancel  Scheduled  Opt Out
		common.clickButtonByNameWithPageLoadWait("Cancel");
		common.clickButtonByNameWithPageLoadWait("Cancel Opt Out");
		
		//  verify  Status & Limit
		Assert.assertEquals("5", optout.getCustomerOptOutUsedValue("711100001", "Used"));
		Assert.assertEquals("1", optout.getCustomerOptOutRemainingValue("711100001", "Remaining"));
		common.end();
	}
	/**
	 * Method logins as Stars Cust, verifies Limits
	 *   then starts an  Active  Opt Out
	 *   and reverifies State & Limit
	 */
	@Test
	public void optOutCustNow2() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
				
		//  login as Cust
		login.cannonLogin("71000000", "71000000");

		//  get to Opt Out page, verify Opt Out Limits values 
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("5", optout.getCustomerOptOutUsedValue("711100001", "Used"));
		Assert.assertEquals("1", optout.getCustomerOptOutRemainingValue("711100001", "Remaining"));
		
		//  Start the Opt Out      *****  cannot start Opt Out,  yuk-9382
		common.clickButtonByNameWithPageLoadWait("Apply");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		common.clickButtonByNameWithPageLoadWait("Ok");
		
		Assert.assertEquals("Active", optout.getCustomerOptOutStatusValue("711100001", "Status"));
		Assert.assertEquals("6", optout.getCustomerOptOutUsedValue("711100001", "Used"));
		Assert.assertEquals("0", optout.getCustomerOptOutRemainingValue("711100001", "Remaining"));
				
		Assert.assertEquals(true, common.isTextPresent("All opt outs for all devices have been used."));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * verifies Opt Out by customer is Active, verifies Used / Remaining values
	 * Cancels Opt Out, Resets Remaining to max, verifies Limits values
	 *  acct  71000000
	 */
	@Test
	public void optOutOperReset() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
				
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000000)", common.getPageTitle());
		
		//  verify Active Opt Out by Cust; verify current Limits values
		common.clickLinkByName("Opt Out");
		Assert.assertEquals(true, common.isTextPresent("All opt outs for all devices have been used."));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100001"));
		Assert.assertEquals("6", optout.getOperatorOptOutUsedValue("711100001"));
		Assert.assertEquals("0", optout.getOperatorOptOutRemainingValue("711100001"));
					
		//  cancel Scheduled Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100001");
		optout.confirmOKOrCancel("cancel the opt out for 711100001", "submit");
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
		
		//  verify event in  Opt Out History
		Assert.assertEquals("Cancel by starsop", optout.getOptOutHistoryActionsByDevice("711100001"));
		Assert.assertEquals("0d 0h", optout.getOptOutHistoryDurationByDevice("711100001"));
		
		
		//  Reset Remaining Opt Out value, verify Limits values
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
		optout.resetOptOut("Opt Out Limits", "711100001");
		optout.confirmOKOrCancel("reset the opt out limit for 711100001", "submit");
		
		Assert.assertEquals("6", optout.getOperatorOptOutUsedValue("711100001"));
		Assert.assertEquals("5", optout.getOperatorOptOutRemainingValue("711100001"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * decrements Remaining Opt Out, verify new Remaining value
	 *  acct  71000000
	 */
	@Test
	public void optOutOperDecrement() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
			
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000000)", common.getPageTitle());
		
		//  verify current  Used / Remaining  values
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("6", optout.getOperatorOptOutUsedValue("711100001"));
		Assert.assertEquals("5", optout.getOperatorOptOutRemainingValue("711100001"));

		//  decrement a  Opt Out Remaining  value
		optout.decrementOptOut("711100001");
		optout.confirmOKOrCancel("remove an opt out for 711100001", "submit");
		Assert.assertEquals("Opt Out Allowance Reduced", common.getYukonText("Opt Out Allowance Reduced"));
		
		//  verify Remaining Opt Out value is decremented
		Assert.assertEquals("6", optout.getOperatorOptOutUsedValue("711100001"));
		Assert.assertEquals("4", optout.getOperatorOptOutRemainingValue("711100001"));
		common.end();
	}
	/**
	 * Method logins as Stars Cust, verifies Limits after decrement
	 */
	@Test
	public void optOutCustDecrement() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
				
		//  login as Cust
		login.cannonLogin("71000000", "71000000");

		//  get to Opt Out page, verify Opt Out Limits values 
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("6", optout.getCustomerOptOutUsedValue("711100001", "Used"));
		Assert.assertEquals("4", optout.getCustomerOptOutRemainingValue("711100001", "Remaining"));
		common.end();
	}
}