package com.cannontech.selenium.test.stars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;
import com.cannontech.selenium.solvents.stars.OptOutTableSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
import com.cannontech.selenium.solvents.stars.ThermostatSchedulePageSolvent;
import com.cannontech.selenium.solvents.stars.EnrollmentTableSolvent;
/**
 * This test logs into Stars using a Residential Customer Account and Edits the Thermostat schedule,enrolls 
 * in a Program for OptOut,tests the Forgot Password functionality, and changes the login information
 * such as Username and Password.
 * @author anjana.manandhar
 */
public class TestResidentialFunctionsA1Selenium extends SolventSeleniumTestCase {
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("51000000", "51000000");
	}
	/**
	 * Method logs in with a Residential Customer account and edits the Thermostat schedule.
	 */
	@Test	 
	public void verifyGeneral() {
		init();
		CommonSolvent common = new CommonSolvent();
		Assert.assertEquals("Your Enrolled Programs",common.getYukonText("Your Enrolled Programs"));
		Assert.assertEquals("You are not enrolled in any viewable programs.",common.getYukonText("You are not enrolled in any viewable programs."));
		common.end();
	}
	/**
	 * Method logs in as a residential customer and edits a contact
	 */
	@Test	 
	public void editContacts() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Contacts");
		Assert.assertEquals("Account - Contacts",common.getYukonText("Account - Contacts"));
		//Adds a new notification
		common.clickFormButton("contacts/updateContact", "addNotification");
		common.selectDropDownMenu("contacts/updateContact", "notificationType_2", "Home Phone");
		common.enterInputText("contacts/updateContact", "notificationText_2", "555-555-1239");
		//Update Contact
		common.clickFormButton("contacts/updateContact", "update");
		//TODO add new contact
		common.end();
	}
	@Test	 
	public void editThermostatSchedule() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent thermostat = new ThermostatSchedulePageSolvent();
		
		common.clickLinkByNameWithoutPageLoadWait("511200001");
		stars.clickLinkByThermostatName("511200001", "Schedules");
		
		common.clickButtonBySpanTextWithElementWait("Create");
		thermostat.clickPopupButtonBySpanText("Create Schedule", "Next");
		thermostat.inputScheduleName("Create Schedule", "511200001"); 
		thermostat.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertEquals("The schedule 511200001 has been saved.",common.getYukonText("The schedule 511200001 has been saved."));
		
		thermostat.editTstatSchedule("511200001");
		thermostat.inputWakeStartTime("Edit Schedule - 511200001", "All", "05:00 AM");
		thermostat.inputLeaveStartTime("Edit Schedule - 511200001", "All", "08:00 AM");
		thermostat.inputReturnStartTime("Edit Schedule - 511200001", "All", "06:00 PM");
		thermostat.inputSleepStartTime("Edit Schedule - 511200001", "All", "10:00 PM");
		
		thermostat.inputWakeCoolingTemp("Edit Schedule - 511200001", "All", "74");
		thermostat.inputLeaveCoolingTemp("Edit Schedule - 511200001", "All", "74");
		thermostat.inputReturnCoolingTemp("Edit Schedule - 511200001", "All", "74");
		thermostat.inputSleepCoolingTemp("Edit Schedule - 511200001", "All", "74");
		
		thermostat.inputWakeHeatingTemp("Edit Schedule - 511200001", "All", "68");
		thermostat.inputLeaveHeatingTemp("Edit Schedule - 511200001", "All", "68");
		thermostat.inputReturnHeatingTemp("Edit Schedule - 511200001", "All", "68");
		thermostat.inputSleepHeatingTemp("Edit Schedule - 511200001", "All", "68");
		
		thermostat.clickPopupButtonBySpanText("Edit Schedule - 511200001", "Recommended Settings");
		
		thermostat.inputWakeStartTime("Edit Schedule - 511200001", "All", "07:00 AM");
		thermostat.inputLeaveStartTime("Edit Schedule - 511200001", "All", "10:00 AM");
		thermostat.inputReturnStartTime("Edit Schedule - 511200001", "All", "07:00 PM");
		thermostat.inputSleepStartTime("Edit Schedule - 511200001", "All", "11:00 PM");
		
		thermostat.inputWakeCoolingTemp("Edit Schedule - 511200001", "All", "65");
		thermostat.inputLeaveCoolingTemp("Edit Schedule - 511200001", "All", "60");
		thermostat.inputReturnCoolingTemp("Edit Schedule - 511200001", "All", "65");
		thermostat.inputSleepCoolingTemp("Edit Schedule - 511200001", "All", "60");
		
		thermostat.inputWakeHeatingTemp("Edit Schedule - 511200001", "All", "65");
		thermostat.inputLeaveHeatingTemp("Edit Schedule - 511200001", "All", "60");
		thermostat.inputReturnHeatingTemp("Edit Schedule - 511200001", "All", "65");
		thermostat.inputSleepHeatingTemp("Edit Schedule - 511200001", "All", "60");
		
		thermostat.inputScheduleName("Edit Schedule - 511200001", "Schedule-01");
		thermostat.clickPopupButtonBySpanText("Edit Schedule - 511200001", "Save");
		Assert.assertEquals("The schedule Schedule-01 has been saved.",common.getYukonText("The schedule Schedule-01 has been saved."));
		common.end();
	}
	/**
	 * Method logs in with a Residential Customer account and enrolls Programs
	 * 51000000 to LdPgm01-2Gear, and  511100001 to LdPgm02-2Gear
	 * Opt Out without specifying device, verify cannot do this,
	 * Opt Out 511100001 now, verify Active
	 */
	@Test	 
	public void resCustEnrollOptOut() {
		init();
		CommonSolvent common = new CommonSolvent();
		EnrollmentTableSolvent enroll = new EnrollmentTableSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		
		// enroll 1st device
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Programs - Enrollment",common.getYukonText("Programs - Enrollment"));
		enroll.enrollByProgramName("LdPgm01-2Gear");    //  yuk-9092   ****
		common.selectCheckBox("511200001");
		common.clickButtonByNameWithPageLoadWait("OK");
		common.getYukonText("** Program enrollment updated successfully.");
		common.clickButtonByNameWithPageLoadWait("OK");

		// enroll 2nd device
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Programs - Enrollment",common.getYukonText("Programs - Enrollment"));
		enroll.enrollByProgramName("LdPgm02-2Gear");    //  yuk-9092   ****
		common.selectCheckBox("511100001");
		common.clickButtonByNameWithPageLoadWait("OK");
		common.getYukonText("** Program enrollment updated successfully.");
		common.clickButtonByNameWithPageLoadWait("OK");
		
		//  Opt Out device - 1st try without selecting device, then select device
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Programs - Opt Out",common.getYukonText("Programs - Opt Out"));
		common.clickButtonByNameWithPageLoadWait("Apply");
		
		Assert.assertEquals("Programs - Opt Out", common.getYukonText("Programs - Opt Out"));
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("At least one device must be chosen.", common.getYukonText("At least one device must be chosen."));

		common.selectCheckBox("511100001");	
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Opt Out Result", common.getYukonText("Opt Out Result"));
		Assert.assertEquals("Your programs have been opted out.", common.getYukonText("Your programs have been opted out."));
		common.clickButtonByNameWithPageLoadWait("Ok");

		//  verify Opt Out went Active
		Assert.assertEquals("Programs - Opt Out", common.getYukonText("Programs - Opt Out"));
		Assert.assertEquals("Active", optout.getCustomerOptOutStatusValue("511100001", "Status"));
		common.end();
	}
	/**
	 * Method logs in as Residential Customer, 
	 * Schedules Opt Out for 511200001, verify goes Scheduled, then cancel Scheduled Opt Out.
	 * Schedules Opt Out for 511200001, verify goes Scheduled
	 * (cannot yet verify 2nd Status on same device) 
	 */
	@Test	 
	public void resCustOptOutSched() {
		init();
		CommonSolvent common = new CommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		EventsByTypeSolvent event = new EventsByTypeSolvent();
		
		//  schedule 51000000 Opt Out . then cancel 
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Programs - Opt Out",common.getYukonText("Programs - Opt Out"));

		common.enterInputText("optout/deviceSelection", "startDate", event.returnDate(1));
	
		common.clickButtonByName("Apply");
		common.selectCheckBox("511200001");
		common.clickButtonByName("Opt Out");
		Assert.assertEquals("Opt Out Result", common.getYukonText("Opt Out Result"));
		Assert.assertEquals("Your programs have been opted out.", common.getYukonText("Your programs have been opted out."));
		common.clickButtonByName("Ok");

		//  verify Opt Out went Scheduled
		Assert.assertEquals("Programs - Opt Out", common.getYukonText("Programs - Opt Out"));
		Assert.assertEquals("Scheduled", optout.getCustomerOptOutStatusValue("511200001", "Status"));

		//  cancel scheduled Opt Out
		common.clickButtonByNameWithPageLoadWait("Cancel");
		common.clickButtonByNameWithPageLoadWait("Cancel Opt Out");
		Assert.assertEquals("Programs - Opt Out", common.getYukonText("Programs - Opt Out"));

		common.enterInputText("optout/deviceSelection", "startDate", event.returnDate(1));
	
		common.clickButtonByName("Apply");
		common.selectCheckBox("511100001");	
		common.clickButtonByName("Opt Out");
		Assert.assertEquals("Opt Out Result", common.getYukonText("Opt Out Result"));
		Assert.assertEquals("Your programs have been opted out.", common.getYukonText("Your programs have been opted out."));
		common.clickButtonByName("Ok");

		//  verify Opt Out is Scheduled & Active
		//Assert.assertEquals("Programs - Opt Out", common.getYukonText("Programs - Opt Out"));
		//Assert.assertEquals("Scheduled", optout.getCustomerOptOutStatusValue("511100001", "Status"));
		//  cannot yet verify 2nd Status on same device   *********************************
		//Assert.assertEquals("Active", optout.getCustomerOptOutStatusValue("511100001", "Status"));
		common.end();
	}
	/**
	 * Method navigates to and verifies the Control History page.
	 */
	@Test	 
	public void verifyControlHistory() {
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Control History");
		Assert.assertEquals("Programs - Control History",common.getYukonText("Programs - Control History"));
		Assert.assertEquals("LdPgm01-2Gear",common.getYukonText("LdPgm01-2Gear"));
		Assert.assertEquals("You have not been controlled",common.getYukonText("You have not been controlled"));
		Assert.assertEquals("LdPgm02-2Gear",common.getYukonText("LdPgm02-2Gear"));
		Assert.assertEquals("Opted Out",common.getYukonText("Opted Out"));

		common.clickLinkByName("details");
		Assert.assertEquals("Program - Complete Control History",common.getYukonText("Program - Complete Control History"));
		Assert.assertEquals("LdPgm01-2Gear",common.getYukonText("LdPgm01-2Gear"));
		Assert.assertEquals("There has been no control during this period.",common.getYukonText("There has been no control during this period."));
		common.end();	
	}
	/**
	 * Method logs in with a Residential Customer account and unenrolls a Program.
	 */
	@Test	 
	public void resCustUnenroll() {
		init();
		CommonSolvent common = new CommonSolvent();
		EnrollmentTableSolvent enroll = new EnrollmentTableSolvent();
		
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Programs - Enrollment",common.getYukonText("Programs - Enrollment"));

		//  Unenroll
		enroll.unenrollByProgramName("LdPgm01-2Gear");    //  yuk-9092   ****		
		Assert.assertEquals("** Program enrollment updated successfully.",common.getYukonText("** Program enrollment updated successfully."));
		common.clickButtonByName("OK");
		
		enroll.unenrollByProgramName("LdPgm02-2Gear");    //  yuk-9092   ****		
		Assert.assertEquals("** Program enrollment updated successfully.",common.getYukonText("** Program enrollment updated successfully."));
		common.clickButtonByName("OK");
		common.end();
	}
	/**
	 * Method logins as Stars Oper
	 * verifies Opt Out was canceled when Cust unenrolled program
	 *  acct  51000000
	 */
	@Test
	public void operVerifyOptOutCanceled() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		login.cannonLogin("starsop", "starsop");
	
		//  search to cust acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
	
		//Check no OptOut exists
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (51000000)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		common.end();
	}
	/**
	 * Method logs in with a Residential Customer account with an incorrect password and 
	 * tests the Forgot Password functionality.
	 */
	@Test
	public void resCustForgotPassword() {
		start();
		CommonSolvent common = new CommonSolvent();
		new LoginLogoutSolvent().cannonLogin("51000000", "51000009");

		//Enter Invalid Password
		Assert.assertEquals("* Invalid username/password", common.getYukonText("* Invalid username/password"));
		common.clickLinkByName("Forgot your password?");
		Assert.assertEquals("Energy Services Operations Center", common.getPageTitle());

		//Enter Account Information for Forgot Password
		common.enterInputText("servlet/StarsPWordRequest", "USERNAME", "51000000");
		common.enterInputText("servlet/StarsPWordRequest", "EMAIL", "psplqa010@gmail.com");
		common.enterInputText("servlet/StarsPWordRequest", "FIRST_NAME", "No name");
		common.enterInputText("servlet/StarsPWordRequest", "LAST_NAME", "No name");
		common.enterInputText("servlet/StarsPWordRequest", "ACCOUNT_NUM", "51000000");  	 
		common.enterInputText("servlet/StarsPWordRequest", "ENERGY_COMPANY", "QATest");
		common.clickFormButton("servlet/StarsPWordRequest", "Submit2");
		Assert.assertEquals("Password request has been sent successfully", common.getYukonText("Password request has been sent successfully"));
		common.end();
	}
	/**
	 * Method logs in with a Residential Customer account and changes password.
	 * Method logs in with Updated Username,Password and logs out from the application.
	 */
	@Test
	public void resCustLogin() {
		init();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		//Change Username
		common.clickLinkByName("Change Login");
		Assert.assertEquals("Administration - Change Login", common.getYukonText("Administration - Change Login"));
		stars.editCustomerUserNameOrPassword("New Username:", "51000000a");
		stars.editCustomerUserNameOrPassword("Current Password:", "51000000");
		widget.clickButtonByWidget("Change Username", "Save");
		Assert.assertEquals("User login name updated successfully", common.getYukonText("User login name updated successfully"));
		
		//Change Password
		stars.editCustomerPasswordFields("Current Password:", "51000000");
		stars.editCustomerPasswordFields("New Password:", "51000000a");
		stars.editCustomerPasswordFields("(confirm password)", "51000000a");
		widget.clickButtonByWidget("Change Password", "Save");
		Assert.assertEquals("User login password updated successfully", common.getYukonText("User login password updated successfully"));
		common.clickLinkByName("Logout");
		
		//Login using the updated Username and Password
		login.cannonLogin("51000000a", "51000000a");
		Assert.assertEquals("Hi Exceptional QA People", common.getYukonText("Hi Exceptional QA People"));
		
		//  change login / password back to original
		common.clickLinkByName("Change Login");
		Assert.assertEquals("Administration - Change Login", common.getYukonText("Administration - Change Login"));
		stars.editCustomerUserNameOrPassword("New Username:", "51000000");
		stars.editCustomerUserNameOrPassword("Current Password:", "51000000a");
		widget.clickButtonByWidget("Change Username", "Save");
		Assert.assertEquals("User login name updated successfully", common.getYukonText("User login name updated successfully"));
		
		//Change Password
		stars.editCustomerPasswordFields("Current Password:", "51000000a");
		stars.editCustomerPasswordFields("New Password:", "51000000");
		stars.editCustomerPasswordFields("(confirm password)", "51000000");
		widget.clickButtonByWidget("Change Password", "Save");
		Assert.assertEquals("User login password updated successfully", common.getYukonText("User login password updated successfully"));
		common.end();
	}
}
