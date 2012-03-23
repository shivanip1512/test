package com.cannontech.selenium.test.stars;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.EnrollmentTableSolvent;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;
import com.cannontech.selenium.solvents.stars.HardwarePageSolvent;
import com.cannontech.selenium.solvents.stars.OptOutTableSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
/**
 * The purpose is to verify that Active Opt Outs are stopped in special situations
 *   - Active, then unenrol
 *   - Active, then enroll
 *   - Active, then delete device
 *  follows verification for test case  4.2.1.13, 
 *  but also covers test cases  4.2.1.1, 4.2.1.2, 
 *  Add tests for yuk-8672  Opt Out Admin > Cancel current Active Opt Outs by Program
 *      and then All current Active Opt Outs  
 * @author steve.junod
 */
public class TestXtendedOptOutsA2Selenium extends SolventSeleniumTestCase {
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
	}
	/**
	 * Method logins as Stars Oper
	 * starts Opt Out today, then unenrolls - Opt Out should stop
	 *  acct  71000005
	 */
	@Test
	public void optOutUnenroll() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		EnrollmentTableSolvent enroll = new EnrollmentTableSolvent();
	
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
	
		//Check no OptOut exists
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000005)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		
		//start OptOut 
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000005)", common.getPageTitle());
		common.selectCheckBox("711100051");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000005)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100051"));
		
		//  unenroll device while Opt Out is Active  ( yuk-8879 / 8818 / 8937)
		common.clickLinkByName("Enrollment");
		enroll.deleteProgramByName("LdPgm02-2Gear");
		Assert.assertEquals("Are you sure you want to unenroll the consumer from LdPgm02-2Gear?", common.getYukonText("Are you sure you want to unenroll the consumer from LdPgm02-2Gear?"));
		common.clickButtonByExactName("OK");
		Assert.assertEquals("Successfully unenrolled from LdPgm02-2Gear.", common.getYukonText("Successfully unenrolled from LdPgm02-2Gear."));

		//  verify no Opt Out is Active after unenroll
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));

		// verify event listed in Control History
		Assert.assertEquals(true, common.isTextPresent("Cancel by starsop"));
		common.end();
	}
	/**  *******************************************
	 * Method logins as Stars Oper
	 * starts Opt Out today, then enrolls - Opt Out should stop
	 *  acct  71000005
	 */
	@Test
	public void optOutEnroll() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
	
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
				
		//start OptOut, verify Active
		common.clickLinkByName("Opt Out");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000005)", common.getPageTitle());
		common.selectCheckBox("711100051");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000005)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100051"));
		
		//Enroll a device in a Program while device is Opted Out / Active
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Operator: Enrollment (71000005)", common.getPageTitle());

	    PopupMenuSolvent programPicker = PickerFactory.createPopupMenuSolvent("program", PickerType.SingleSelect);
	    common.clickButtonByTitle("Add");
	    programPicker.clickMenuItem("LdPgm01-2Gear");
		
	    common.selectCheckBox("711100051");         
		common.selectDropDownMenu("enrollment/confirmSave", "inventoryEnrollments[0].relay", "1");
		common.clickButtonBySpanTextWithElementWait("OK");
		Assert.assertEquals("Add Enrollment", common.getYukonText("Add Enrollment"));
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Successfully enrolled in LdPgm01-2Gear.", common.getYukonText("Successfully enrolled in LdPgm01-2Gear."));

		//  verify no Opt Out is Active after enroll
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
		
		// verify event listed in Control History
		Assert.assertEquals(true, common.isTextPresent("Cancel by starsop"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * starts Opt Out today, then removes the device from the account (into Inventory) 
	 * verify Opt Out has stopped;  
	 *  acct  71000005
	 */
	@Test
	public void optOutDelete() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		WidgetSolvent widget = new WidgetSolvent();
	
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
						
		//Start OptOut
		common.clickLinkByName("Opt Out");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000005)", common.getPageTitle());
		common.selectCheckBox("711100051");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000005)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100051"));
		
		//  delete device while Opt Out Active
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000005)", common.getPageTitle());
		hardware.clickLinkByName("711100051");
		Assert.assertEquals("Device Info", common.getYukonText("Device Info"));
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…");
		Assert.assertEquals("Delete Device UtilityPRO 711100051", common.getYukonText("Delete Device UtilityPRO 711100051"));
		common.clickRadioButtonByName("Delete it from inventory permanently.");
		common.clickFormButton("delete", "delete");
		Assert.assertEquals("Hardware Deleted", common.getYukonText("Hardware Deleted"));
		
		//  verify no Opt Out is Active after device delete
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));

		// verify event listed in Control History
		Assert.assertEquals(true, common.isTextPresent("Cancel by starsop"));
					
		//  yuk-9441		
		//  go to Opt Out Admin
		common.clickLinkByName("Home");
		common.clickLinkByName("Opt Out Administration");
		Assert.assertEquals("Demand Response: Opt Out Status", common.getPageTitle());
		
		//  cancel Active Opt Outs for only  LdPgm01-2Gear
		common.clickButtonBySpanText("Reactivate Opted Out Devices");
		Assert.assertEquals("Canceled Current Opt Outs For All Programs", common.getYukonText("Canceled Current Opt Outs For All Programs"));
		common.end();
	}
	/**
	 * This method adds Hardware from Inventory, removed above, 
	 * and then re-enrolls that device
	 */
	@Test
	public void addHardwareEnroll() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
		
		//Add inventory Hardware to Customer 
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000005)", common.getPageTitle());
		common.selectDropDownMenuByIdName("tstatTypeToAdd", "UtilityPRO");
		hardware.clickAddByTableHeader("Thermostats"); 
		hardware.enterSerialNumber("Serial Number:", "711100051");
		hardware.clickInventoryButton();
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
		common.selectDropDownMenu("create", "serviceCompanyId", "QA_Service");
		common.selectDropDownMenu("create", "routeId", "Default - a_CCU-711"); 	
		common.selectDropDownMenu("create", "deviceStatusEntryId", "Installed");
		common.clickFormButton("create", "save");
		Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));
		Assert.assertEquals("Operator: Hardware (UtilityPRO 711100051)", common.getPageTitle());
			
		//Enroll device in a Program 
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Operator: Enrollment (71000005)", common.getPageTitle());
	    
		PopupMenuSolvent programPicker = PickerFactory.createPopupMenuSolvent("program", PickerType.SingleSelect);
	    common.clickButtonByTitle("Add");
	    programPicker.clickMenuItem("LdPgm02-2Gear");

	    common.selectCheckBox("711100051");
		common.selectDropDownMenu("enrollment/confirmSave", "inventoryEnrollments[0].relay", "1");
		common.clickButtonBySpanTextWithElementWait("OK");
		Assert.assertEquals("Add Enrollment", common.getYukonText("Add Enrollment"));
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Successfully enrolled in LdPgm02-2Gear.", common.getYukonText("Successfully enrolled in LdPgm02-2Gear."));
		common.end();
	}
	/**
	 * This method will setup accts that will test Opt Out Admin > Cancel All Opt Outs
	 * acct 71000000 = LdPgm01-2Gear = Scheduled
	 * acct 71000001 = LdPgm01-2Gear = Active 
	 * acct 71000005 = LdPgm02-2Gear = Active
	 */
	@Test
	public void optOutSetupForCancelAll() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		EventsByTypeSolvent events = new EventsByTypeSolvent();
		
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000000)", common.getPageTitle());
		
		// Start Scheduled Opt Out  
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000000)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		
		//  Schedule OptOut, start tomorrow, for 2 days
		//  need to be able to select 'tomorrow'
		common.enterInputText("/optOut/deviceSelection", "startDate", events.returnDate(1));

		common.selectDropDownMenu("optOut/deviceSelection", "durationInDays", "2 Day");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000000)", common.getPageTitle());
		common.selectCheckBox("711100001");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000000)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100001"));
				
		common.clickLinkByName("Home");

		//  setup acct 71000001 as Active Opt Out
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000001");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000001)", common.getPageTitle());
						
		//Start Active Opt Out
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000001)", common.getPageTitle());
		common.selectCheckBox("711100011");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000001)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100011"));

		common.clickLinkByName("Home");

		//  setup acct 71000005 as Active Opt Out
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
						
		//Start Active OptOut
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000005)", common.getPageTitle());
		common.selectCheckBox("711100051");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000005)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100051"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper, goes to Opt Out Admin
	 * Cancels customer  Opt Outs for Program  LdPgm01-2Gear 
	 * 
	 */
	@Test
	public void optOutAdminCancelProgram() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
	
		//  go to Opt Out Admin
		common.clickLinkByName("Opt Out Administration");
		Assert.assertEquals("Demand Response: Opt Out Status", common.getPageTitle());
		
		//  cancel Active Opt Outs for only  LdPgm01-2Gear
		widget.clickLinkByWidget("Cancel Current Opt Outs","Choose Program");
		PopupMenuSolvent programPicker = PickerFactory.createPopupMenuSolvent("cancelOptOutsProgram", PickerType.SingleSelect);
		programPicker.clickMenuItem("LdPgm01-2Gear");
		common.clickButtonBySpanText("Reactivate Opted Out Devices");
		Assert.assertEquals("Canceled Current Opt Outs for the program LdPgm01-2Gear", common.getYukonText("Canceled Current Opt Outs for the program LdPgm01-2Gear"));
		common.end();
	}	
	/**
	 * This method will verify affect of Opt Out Admin  cancel  event
	 * for Program  LdPgm01-2Gear 
	 * acct 71000000 = LdPgm01-2Gear = Scheduled Opt Out not changed
	 * acct 71000001 = LdPgm01-2Gear = Active Opt Out should be canceled 
	 * acct 71000005 = LdPgm02-2Gear = Active Opt Out not changed
	 */
	@Test
	public void optOutAdminCancelVerify() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		
		//  verify acct 71000000
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000000)", common.getPageTitle());
		
		// verify 71000000 Opt Out Status is still Scheduled - not affected by Opt Out Admin cancel event  
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000000)", common.getPageTitle());
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100001"));
				
		common.clickLinkByName("Home");

		//  verify acct 71000001
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000001");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000001)", common.getPageTitle());
						
		//verify Active OptOut was canceled by  Opt Out Admin cancel event
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000001)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		
		common.clickLinkByName("Home");

		//  verify  acct 71000005 
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
						
		//verify Active OptOut not affected by  Opt Out Admin cancel event
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000005)", common.getPageTitle());
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100051"));
		common.end();
	}
	/**
	 * Method logins as Stars Oper, goes to Opt Out Admin
	 * Cancels customer  Opt Outs for All  Programs 
	 * 
	 */
	@Test
	public void optOutAdminCancelAll() {
		init();
		CommonSolvent common = new CommonSolvent();
	
		//  go to Opt Out Admin
		common.clickLinkByName("Opt Out Administration");
		Assert.assertEquals("Demand Response: Opt Out Status", common.getPageTitle());
		
		//  cancel Active Opt Outs for all Programs
		common.clickButtonBySpanText("Reactivate Opted Out Devices");
		Assert.assertEquals("Canceled Current Opt Outs For All Programs", common.getYukonText("Canceled Current Opt Outs For All Programs"));
		common.end();
	}
	/**
	 * This method will verify affect of Opt Out Admin  cancel  event
	 * for All Programs  
	 * acct 71000000 = LdPgm01-2Gear = Scheduled Opt Out not changed
	 * acct 71000005 = LdPgm02-2Gear = Active Opt Out should be canceled
	 */
	@Test
	public void optOutAdminCancelVerifyAll() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		
		//  verify acct 71000000
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000000)", common.getPageTitle());
		
		// verify 71000000 Opt Out Status is still Scheduled - not affected by Opt Out Admin cancel event  
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000000)", common.getPageTitle());
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100001"));
						
		common.clickLinkByName("Home");

		//  verify  acct 71000005 
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000005");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000005)", common.getPageTitle());
						
		//verify Active OptOut not affected by  Opt Out Admin cancel event
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000005)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		common.end();
	}
}