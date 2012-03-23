package com.cannontech.selenium.test.stars;

import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.ApplianceTableSolvent;
import com.cannontech.selenium.solvents.stars.CallsTableSolvent;
import com.cannontech.selenium.solvents.stars.ContactInfoTableSolvent;
import com.cannontech.selenium.solvents.stars.EnrollmentTableSolvent;
import com.cannontech.selenium.solvents.stars.HardwarePageSolvent;
import com.cannontech.selenium.solvents.stars.OptOutTableSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
import com.cannontech.selenium.solvents.stars.WorkOrderTableSolvent;
/**
 * This class uses xml file to import accounts and views the Account Information that was imported.
 * Main idea of this class is to navigate through all the Operator side pages and 
 * perform functionality tests.
 * @author anjana.manandhar
 * @author kevin.krile
 */
public class TestOperatorFunctionsA1Selenium extends SolventSeleniumTestCase {
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
	}
	/**
	 * Method logins as Stars Operator, Imports Account and views the Account Information.
	 *  s junod - 9/22/2010 - changes for new Import Accounts pages (yuk-8700)
	 */
	@Test
	public void importAccounts() {
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Import Account");
		Assert.assertEquals("Operator: Account Import", common.getPageTitle());
 		
	    common.enterInputText("uploadImportFiles", "accountImportFile", getParamString("ImportInsertAccount"));
		common.clickButtonBySpanText("Prescan");
		
		common.assertEqualsTextIsPresent("Finished - Passed");
		common.assertEqualsTextIsPresent("Customer File Lines Processed:");    // yuk-9145 
		common.assertEqualsTextIsPresent("5");     //  yuk-9145
		common.clickFormButtonByButtonId("account/doAccountImport", "importButton");

		//  check for  100%  value   yuk-9144,9147
		common.assertEqualsTextIsPresent("Accounts:");     
		common.assertEqualsTextIsPresent("100%");
		common.assertEqualsTextIsPresent("5 Added, 0 Updated, 0 Removed", Duration.standardSeconds(5));
		common.clickLinkByName("Back to Account Import");

        common.enterInputText("uploadImportFiles", "hardwareImportFile", getParamString("EnrollDeviceAccount"));
		common.clickButtonBySpanText("Prescan");
		
		common.assertEqualsTextIsPresent("Finished - Passed");
		common.assertEqualsTextIsPresent("Hardware File Lines Processed:");    // yuk-9145 
		common.assertEqualsTextIsPresent("4");     //  yuk-9145
		common.clickFormButtonByButtonId("account/doAccountImport", "importButton");
		
		//  check for  100%  value   yuk-9144,9147
		common.assertEqualsTextIsPresent("Hardware:");     
		common.assertEqualsTextIsPresent("100%");     
		common.assertEqualsTextIsPresent("4 Added, 0 Updated, 0 Removed", Duration.standardSeconds(4));
		common.clickLinkByName("Back to Account Import");
		
		common.end();
	}
	/**
	 * This method logs in as Stars Operator and manually creates an account
	 */
	@Test
	public void manualCreateEditAccount() {
		init();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();

		// manually create New account using  Select a location  drop down 
		topMenu.selectALocation("New Account");    // yuk-9153
		Assert.assertEquals("Operator: Create Account", common.getPageTitle());
				
		common.clickLinkByName("Home");
		
		// manually create New account using  New Account  link 
		common.clickLinkByName("New Account");	
		Assert.assertEquals("Operator: Create Account", common.getPageTitle());
		
		common.enterInputText("account/createAccount", "accountDto.accountNumber", "51000005");
		common.enterInputText("account/createAccount", "accountDto.customerNumber", "51000005");
		common.enterInputText("account/createAccount", "accountDto.streetAddress.locationAddress1", "123 First Ave");
		common.enterInputText("account/createAccount", "accountDto.streetAddress.cityName", "QA City");
		common.enterInputText("account/createAccount", "accountDto.streetAddress.stateCode", "MN");
		common.enterInputText("account/createAccount", "accountDto.streetAddress.zipCode", "99999");
		common.selectCheckBox("Same As Above");    // yuk-9189
		common.selectDropDownMenu("account/createAccount", "accountDto.siteInfo.substationName", "QA_Substation");    // yuk-9188
		common.enterInputText("account/createAccount", "loginBackingBean.username", "51000005");
		common.enterInputText("account/createAccount", "loginBackingBean.password1", "51000005");
		common.enterInputText("account/createAccount", "loginBackingBean.password2", "51000005");
		
		//  save the acct info 
		common.clickButtonBySpanText("Create");
		Assert.assertEquals("Account Created", common.getYukonText("Account Created"));
		Assert.assertEquals("Operator: Account (51000005)", common.getPageTitle());  // yuk-9134
		common.clickButtonByTitleWithPageLoadWait("Edit");
		Assert.assertEquals("Operator: Edit Account (51000005)", common.getPageTitle());  // yuk-9134

		//Edit new account Customer Information
	    common.enterInputText("account/updateAccount", "accountDto.lastName", "QATest");
	    common.enterInputText("account/updateAccount", "accountDto.firstName", "Sixth");
	    common.enterInputText("account/updateAccount", "accountDto.homePhone", "555-555-1234");
	    common.enterInputText("account/updateAccount", "accountDto.workPhone", "555-555-1235");
	    common.enterInputText("account/updateAccount", "accountDto.altTrackingNumber", "51000005a");
		stars.enterNotesDescription("Notes:", "awesome QA");
		common.selectDropDownMenu("account/updateAccount", "accountDto.siteInfo.substationName", "QA_Substation");
		common.selectDropDownMenu("account/updateAccount", "accountDto.rateScheduleEntryId", "PP");
		common.enterInputText("account/updateAccount", "accountDto.streetAddress.locationAddress2", "Address 2");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals("Account Updated", common.getYukonText("Account Updated"));
		common.end();
	}
	/**
	 * This method navigates to the Customer Contacts page, Edits the existing Contact Information. 
	 * Adds a Second Contact and Deletes the second contact that was added.
	 */
	@Test
	public void viewContacts() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ContactInfoTableSolvent contactTable = new ContactInfoTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Edit Customer Contact information
		common.clickLinkByName("Contacts");
		Assert.assertEquals("Operator: Contacts (51000000)", common.getPageTitle());

		common.clickLinkByName("QATest, First");
		Assert.assertEquals("Operator: Contact View (First QATest)", common.getPageTitle());

		common.clickButtonByTitle("Edit");
		common.enterInputText("contacts/contactUpdate", "workPhone", "555-555-1236");

		common.clickButtonBySpanText("Save");
		Assert.assertEquals("Operator: Contacts (51000000)",common.getPageTitle());
		Assert.assertEquals("Contact Updated", common.getYukonText("Contact Updated"));

		//Create secondary Contact 
		common.clickButtonBySpanText("Create");
		Assert.assertEquals("Operator: Contact Create", common.getPageTitle());
		common.enterInputText("contacts/contactUpdate", "firstName", "Second");
		common.enterInputText("contacts/contactUpdate", "lastName","QATest");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals(true, common.isTextPresent("Contact Created"));
		

		Assert.assertEquals("Operator: Contacts (51000000)",common.getPageTitle());
		contactTable.removeContactInfoByName("QATest, Second");
		Assert.assertEquals("Contact Deleted", common.getYukonText("Contact Deleted"));
		common.end();
	}
	/**
	 * This method navigates to the Residence Information page, and edits the Customer Residence information. 
	 */
	@Test
	public void residentInformation() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
			
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Create Customer Residence Information
		common.clickLinkByName("Residence");
		Assert.assertEquals("Operator: Residence (51000000)", common.getPageTitle());
		common.clickButtonByTitle("Edit");
		common.selectDropDownMenu("residence/residenceUpdate", "residenceTypeId", "Two Story");
		common.selectDropDownMenu("residence/residenceUpdate", "constructionMaterialId", "Frame"); 
		common.selectDropDownMenu("residence/residenceUpdate", "decadeBuiltId", "2000"); 
		common.selectDropDownMenu("residence/residenceUpdate", "squareFeetId", "2500-2999"); 
		common.selectDropDownMenu("residence/residenceUpdate", "insulationDepthId", "Unknown");
		common.selectDropDownMenu("residence/residenceUpdate", "generalConditionId", "Excellent");
		common.selectDropDownMenu("residence/residenceUpdate", "mainCoolingSystemId", "Central Air");
		common.selectDropDownMenu("residence/residenceUpdate", "mainHeatingSystemId", "Gas Forced Air");
		common.selectDropDownMenu("residence/residenceUpdate", "numberOfOccupantsId", "3 - 4");
		common.selectDropDownMenu("residence/residenceUpdate", "ownershipTypeId","Own");
		common.selectDropDownMenu("residence/residenceUpdate", "mainFuelTypeId", "Natural Gas");
		stars.enterNotesDescription("Notes:", "QA Test House");
		common.clickButtonBySpanText("Save");	
		Assert.assertEquals("Residence Created", common.getYukonText("Residence Created"));
		common.end();
	}
	/**
	 * This method navigates to the Call Tracking page in the Operator Side <br>
	 * Creates Call Tracking and then Deletes it.
	 */
	@Test
	public void callTracking() { 
		init();
		CommonSolvent common = new CommonSolvent();
		CallsTableSolvent callsTable = new CallsTableSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Create Call Tracking
		common.clickLinkByName("Call Tracking");
		common.clickButtonBySpanText("Create");
		
		Assert.assertEquals("Operator: Create Call", common.getPageTitle());
		common.enterInputText("callTracking/updateCall", "takenBy", "QA expert");
		stars.enterNotesDescription("Description:", "QA is the best");
		common.clickButtonBySpanText("Save");	
		Assert.assertEquals("Call Created", common.getYukonText("Call Created"));
		Assert.assertEquals("Operator: Call Tracking (51000000)", common.getPageTitle());
		
		//Delete Call Tracking
		// TODO We should find a better way of interpreting this row.
		callsTable.clickCallNumberLink("1");
		Assert.assertEquals("Operator: View Call (1)", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		Assert.assertEquals("Operator: View Call (1)", common.getPageTitle());
		common.clickButtonBySpanText("Cancel");
		common.clickLinkByName("Call Tracking");
		callsTable.deleteByCallNumber("1");
		
		Assert.assertEquals("Call Removed", common.getYukonText("Call Removed"));
		Assert.assertEquals("Operator: Call Tracking (51000000)", common.getPageTitle());
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and Adds a new Hardware Thermostat to the Customer.
	 */
	@Test
	public void addHardware() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Add Hardware to Customer 
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		common.selectDropDownMenuByIdName("tstatTypeToAdd", "ExpressStat");
		
		hardware.clickAddByTableHeader("Thermostats");
		hardware.enterSerialNumber("Serial Number:", "511200001");
		hardware.clickInventoryButton();
		common.clickButtonBySpanText("OK");

		Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
		common.selectDropDownMenu("create", "serviceCompanyId", "QA_Service");
		common.selectDropDownMenu("create", "routeId", "Default - a_CCU-711"); 	
		common.selectDropDownMenu("create", "deviceStatusEntryId", "Installed");
		common.clickFormButton("create", "save");
		Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));
		Assert.assertEquals("Operator: Hardware (ExpressStat 511200001)", common.getPageTitle());
		common.end();
	}
	/**
	 * This method navigates to the Enrollment page in the Operator side <br>
	 * and Enrolls a Thermostat to a Program.
	 */
	@Test
	public void programEnrollment() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		EnrollmentTableSolvent enroll = new EnrollmentTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Enroll a Thermostat in a Program
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Operator: Enrollment (51000000)", common.getPageTitle());
		
		PopupMenuSolvent programPicker = PickerFactory.createPopupMenuSolvent("program", PickerType.SingleSelect);
		common.clickButtonByTitle("Add");
		programPicker.clickMenuItem("LdPgm01-2Gear");

		common.selectDropDownMenu("enrollment/confirmSave", "loadGroupId", "LdGrp01-Expresscom");
		common.selectCheckBox("511200001");
		common.selectDropDownMenu("enrollment/confirmSave", "inventoryEnrollments[0].relay", "1");
		common.clickButtonBySpanTextWithElementWait("OK");
		Assert.assertEquals(true, common.isTextPresent("Add Enrollment"));
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Successfully enrolled in LdPgm01-2Gear.", common.getYukonText("Successfully enrolled in LdPgm01-2Gear."));
		Assert.assertEquals("Signup", common.getYukonText("Signup"));
		
		//Edit Enrollment based on Program Name
		enroll.editProgramByName("LdPgm01-2Gear");
		Assert.assertEquals("Select configuration for program LdPgm01-2Gear:", common.getYukonText("Select configuration for program LdPgm01-2Gear:"));
		common.selectDropDownMenu("enrollment/confirmSave", "inventoryEnrollments[0].relay", "2");
		common.clickButtonBySpanTextWithElementWait("OK");
		Assert.assertEquals("Are you sure you want to update enrollment information for LdPgm01-2Gear?", common.getYukonText("Are you sure you want to update enrollment information for LdPgm01-2Gear?"));
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Operator: Enrollment (51000000)", common.getPageTitle());
		Assert.assertEquals("Successfully updated LdPgm01-2Gear.", common.getYukonText("Successfully updated LdPgm01-2Gear."));
		common.end();
	}
	/**
	 * This method navigates to the Control History page and views the Details page.
	 */
	@Test
	public void viewControlHistory() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//View Control History
		common.clickLinkByName("Control History");
		Assert.assertEquals("Operator: Control History (51000000)", common.getPageTitle());
		Assert.assertEquals("LdPgm01-2Gear", common.getYukonText("LdPgm01-2Gear"));
		common.clickLinkByName("details");
		Assert.assertEquals("Operator: Complete Control History (LdPgm01-2Gear)", common.getPageTitle());
		Assert.assertEquals("There has been no control during this period.", common.getYukonText("There has been no control during this period."));
		common.end();
	}
	/**
	 * This method navigates to the OptOut page,checks there are no OptOut schedules. <br>
	 * Set up an OptOut and Deletes the OptOut successfully.
	 */
	@Test
	public void addEditViewOptOut() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Check no OptOut exists
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (51000000)", common.getPageTitle());
		Assert.assertEquals("There are no active or scheduled opt outs.",common.getYukonText("There are no active or scheduled opt outs."));
		Assert.assertEquals("There are no previous Opt Outs.",common.getYukonText("There are no previous Opt Outs."));
		
		// start Opt Out, verify Active
		common.clickButtonByNameWithPageLoadWait("Opt Out…");
		Assert.assertEquals("Operator: Device Selection (51000000)", common.getPageTitle());
		common.selectCheckBox("511200001");
		
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (51000000)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("511200001"));
		
		//YUK-8818
		//Delete an OptOut
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "511200001");
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
	
		common.clickLinkByName("View Complete History");
		Assert.assertEquals("Operator: Opt Out History (51000000)", common.getPageTitle());
		Assert.assertEquals(true, common.isTextPresent("Opt Out History"));
		common.clickLinkByName("Opt Out");
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side.<br>
	 * Adds a second Thermostat, enrolls Thermostat in the a Program, navigates to Thermostat Schedule and Manual page.
	 */
	@Test
	public void addEditHardware() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Add a second hardware of type UtilityPRO
		common.clickLinkByName("Hardware");
		common.selectDropDownMenuByIdName("tstatTypeToAdd", "UtilityPRO"); 
		hardware.clickAddByTableHeader("Thermostats");
		hardware.enterSerialNumber("Serial Number:", "511100001");
		hardware.clickInventoryButton();
		common.clickButtonBySpanText("OK");

		Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
		common.selectDropDownMenu("create", "serviceCompanyId", "QA_Service"); 
		common.selectDropDownMenu("create", "routeId", "Default - a_CCU-711");
		common.clickFormButton("create", "save");
		Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));

		//Navigate to Edit Hardware configuration page
		common.clickLinkByName("Hardware");
		hardware.clickEditConfiguration("511100001");
		Assert.assertEquals("Operator: Hardware Configuration (511100001)", common.getPageTitle());
		common.clickButtonByNameWithPageLoadWait("Cancel");
		
		//Click on Hardware link and view Schedule page
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		common.clickLinkByName("Select Multiple");
		Assert.assertEquals("Operator: Thermostat Select (51000000)", common.getPageTitle());
		common.selectCheckBox("511100001");
		common.clickButtonByNameWithPageLoadWait("Schedule");
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
				
		//Click on Hardware link and view Manual page
		common.clickLinkByName("Hardware");
		common.clickLinkByName("Select Multiple");
		common.selectCheckBox("511200001");
		common.clickButtonByNameWithPageLoadWait("Manual");
		Assert.assertEquals("Operator: Thermostat Manual (511200001)", common.getPageTitle());
		
		//Click on Hardware link then Edit Hardware Device Info
		common.clickLinkByName("Hardware");
		common.clickLinkByName("511100001");
		Assert.assertEquals("Operator: Hardware (UtilityPRO 511100001)", common.getPageTitle());
		Assert.assertEquals("Device Info", common.getYukonText("Device Info"));
		Assert.assertEquals("Device Status History", common.getYukonText("Device Status History"));
		Assert.assertEquals("Hardware History", common.getYukonText("Hardware History"));

		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.selectDropDownMenu("update", "deviceStatusEntryId", "Installed");
		common.clickFormButton("update", "save");
		Assert.assertEquals("Hardware Updated", common.getYukonText("Hardware Updated"));
		Assert.assertEquals("Operator: Hardware (UtilityPRO 511100001)", common.getPageTitle());
		common.end();
	}
	/**
	 * This method navigates to the Appliances page, creates and then edits the Appliances.
	 */
	@Test
	public void addEditAppliances() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ApplianceTableSolvent appliance = new ApplianceTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		common.clickLinkByName("Appliances");
		common.clickButtonBySpanTextWithElementWait("Create", "//div[@id='createAppliancePopup']");
		common.selectDropDownMenu("appliances/applianceNew", "applianceCategoryId", "AC");
		common.clickButtonBySpanText("OK");
		common.clickFormButton("appliances/applianceUpdate", "create");
		
		common.clickLinkByName("AC");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.enterInputText("applianceUpdate", "modelNumber", "1234567AB");
		common.clickFormButton("appliances/applianceUpdate", "update");
		Assert.assertEquals("Operator: Appliances (51000000)", common.getPageTitle());

		//Delete the Appliance created above
		appliance.deleteByProgramName("N/A");
		Assert.assertEquals("Appliance Deleted", common.getYukonText("Appliance Deleted"));		
		common.end();
	}
	/**
	 * This method navigates to the Enrollment page, and Unenrolls
	 * (moved from the programEnrollment method)
	 */
	@Test
	public void programUnenroll() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		EnrollmentTableSolvent enroll = new EnrollmentTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());

		common.clickLinkByName("Enrollment");

		//Delete Enrollment based on Program Name
		enroll.deleteProgramByName("LdPgm01-2Gear");
		Assert.assertEquals("Are you sure you want to unenroll the consumer from LdPgm01-2Gear?", common.getYukonText("Are you sure you want to unenroll the consumer from LdPgm01-2Gear?"));
		common.clickButtonByExactName("OK");
		Assert.assertEquals("Successfully unenrolled from LdPgm01-2Gear.", common.getYukonText("Successfully unenrolled from LdPgm01-2Gear."));
		common.end();
	}	
	/**
	 * This method navigates to the Control History page and views the Details page.
	 */
	@Test
	public void createWorkOrder() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//View Control History
		common.clickLinkByName("Work Order");
		Assert.assertEquals("Operator: Work Order (51000000)", common.getPageTitle());
		
		common.clickButtonBySpanText("Create");
		Assert.assertEquals("Operator: Create Work Order (51000000)", common.getPageTitle());
		common.selectDropDownMenu("workOrder/updateWorkOrder", "workOrderBase.workTypeId", "Install");
		common.enterInputText("workOrder/updateWorkOrder", "workOrderBase.orderedBy", "Awesome QA");
		common.enterInputText("workOrder/updateWorkOrder", "workOrderBase.additionalOrderNumber", "51000000abc");
		common.selectDropDownMenu("workOrder/updateWorkOrder", "workOrderBase.currentStateId", "Scheduled");
		common.selectDropDownMenu("workOrder/updateWorkOrder", "workOrderBase.serviceCompanyId", "QA_Service");
		stars.enterNotesDescription("Description:", "QA WO test - Description");
		common.clickButtonBySpanText("Save");
				
		Assert.assertEquals("Operator: Work Order (51000000)", common.getPageTitle());
		Assert.assertEquals("Work Order Created", common.getYukonText("Work Order Created"));
		common.end();
	}
	/**
	 * This method navigates to the Control History page and views the Details page.
	 */
	@Test
	public void editWorkOrder() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		CallsTableSolvent callsTable = new CallsTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		// Verify input values from above
		common.clickLinkByName("Work Order");
		Assert.assertEquals("Operator: Work Order (51000000)", common.getPageTitle());
	
		// TODO This should be changed to not be dependent on '1'.  If you run this a second time on a server this test will fail because of this.
		callsTable.clickCallNumberLink("1");
		Assert.assertEquals("Operator: View Work Order (1)", common.getPageTitle());   // yuk-9312
		Assert.assertEquals(true, common.isTextPresent("1"));
		Assert.assertEquals(true, common.isTextPresent("Install"));
		Assert.assertEquals(true, common.isTextPresent("Awesome QA"));
		Assert.assertEquals(true, common.isTextPresent("51000000abc"));
		Assert.assertEquals(true, common.isTextPresent("QA_Service"));
		Assert.assertEquals(true, common.isTextPresent("QA WO test - Description"));

		common.clickButtonByTitleWithPageLoadWait("Edit");
		Assert.assertEquals("Operator: Edit Work Order (1)", common.getPageTitle());
		common.selectDropDownMenu("workOrder/updateWorkOrder", "workOrderBase.workTypeId", "Other");
		common.enterInputText("workOrder/updateWorkOrder", "workOrderBase.orderedBy", "Awesome QA - edit");
		common.enterInputText("workOrder/updateWorkOrder", "workOrderBase.additionalOrderNumber", "51000000abcEdit");
		common.selectDropDownMenu("workOrder/updateWorkOrder", "workOrderBase.currentStateId", "Completed");
		stars.enterNotesDescription("Action Taken:", "QA Work Order test - Edit");
		stars.enterNotesDescription("Description:", "QA WO test - Description - Edit");
		common.clickButtonBySpanText("Save");
				
		Assert.assertEquals("Operator: Work Order (51000000)", common.getPageTitle());
		Assert.assertEquals("Work Order Updated", common.getYukonText("Work Order Updated"));
		common.end();
	}
	/**
	 * This method navigates to the Control History page and views the Details page.
	 */
	@Test
	public void deleteWorkOrder() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		WorkOrderTableSolvent work = new WorkOrderTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//View Control History
		common.clickLinkByName("Work Order");
		Assert.assertEquals("Operator: Work Order (51000000)", common.getPageTitle());
		
		//Delete Work Order
        // TODO This should be changed to not be dependent on '1'.  If you run this a second time on a server this test will fail because of this.
		work.deleteByWorkOrderNumber("1");
		Assert.assertEquals("Are you sure you want to delete work order 1?", common.getYukonText("Are you sure you want to delete work order 1?"));
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Work Order Removed", common.getYukonText("Work Order Removed"));

		// create another Work Order to verify WO # auto incremented		
		common.clickButtonBySpanText("Create");
		Assert.assertEquals("Operator: Create Work Order (51000000)", common.getPageTitle());
		common.selectDropDownMenu("workOrder/updateWorkOrder", "workOrderBase.workTypeId", "Install");
		common.enterInputText("workOrder/updateWorkOrder", "workOrderBase.orderedBy", "Awesome QA");
		common.enterInputText("workOrder/updateWorkOrder", "workOrderBase.additionalOrderNumber", "51000000def");
		common.selectDropDownMenu("workOrder/updateWorkOrder", "workOrderBase.currentStateId", "Scheduled");
		common.selectDropDownMenu("workOrder/updateWorkOrder", "workOrderBase.serviceCompanyId", "QA_Service");
		stars.enterNotesDescription("Description:", "QA WO test 2 - Description");
		common.clickButtonBySpanText("Save");
				
		Assert.assertEquals("Operator: Work Order (51000000)", common.getPageTitle());
		Assert.assertEquals("Work Order Created", common.getYukonText("Work Order Created"));
		//  verify new Work Order Number (should be  2 )
		Assert.assertTrue(common.isLinkPresent("2"));     
		common.end();
	}
	/**
	 * This method changes the login information Username & Password, Deletes the login,
	 * then Creates it again.  
	 * edit method for yuk-8894 - login stuff now on Accounts page
	 */
	@Test
	public void addEditLoginInfo() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Change Username & password
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.enterInputText("updateAccount", "loginBackingBean.username", "51000000a");
		common.clickLinkByNameWithoutPageLoadWait("Change Password");
		common.enterInputText("updatePassword", "password1", "51000000a");
		common.enterInputText("updatePassword", "password2", "51000000a");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals("Account Updated", common.getYukonText("Account Updated")); 
		
		//Delete Login   
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickStarsButtonByName("Delete Login");
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Login Deleted", common.getYukonText("Login Deleted"));		  

		//Change username & password back
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.enterInputText("updateAccount", "loginBackingBean.username", "51000000");
		common.clickLinkByNameWithoutPageLoadWait("Change Password");
		common.enterInputText("updateAccount", "password1", "51000000");
		common.enterInputText("updateAccount", "password2", "51000000");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals("Account Updated", common.getYukonText("Account Updated")); 
		common.end();
	}
	/**
	 * This method navigates to the Account Log page and clicks on Logout link to logout from the application.
	 */
	@Test
	public void viewAccountLog() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		common.clickLinkByName("Account Log");
		Assert.assertEquals("Operator: Account Log (51000000)", common.getPageTitle());
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side, <br>
	 * deletes a meter, then deletes the account.
	 */
	@Test
	public void deviceAcctDelete() {
		init();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		
		//  login as oper, get to acct pages
		login.cannonLogin("starsop", "starsop");
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000003");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000003)", common.getPageTitle());
				
		//Click on Hardware link, Delete device 
		common.clickLinkByName("Hardware");
		hardware.clickLinkByName("511100031");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…", "//div[@id='deleteHardwarePopup']");    //  not displaying delete device ## popup window   qa-415
		Assert.assertEquals("Delete Device UtilityPRO 511100031", common.getYukonText("Delete Device UtilityPRO 511100031"));
		common.clickRadioButtonByName("Delete it from inventory permanently.");
		common.clickFormButton("delete", "delete");
		Assert.assertEquals("Hardware Deleted", common.getYukonText("Hardware Deleted"));
				
		// delete account
		//just saying contains finds accounts on the top sub menu
		common.clickLinkByName("Account");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickStarsButtonByName("Delete");
		Assert.assertEquals("Confirm Delete", common.getYukonText("Confirm Delete"));
		common.clickButtonBySpanText("OK");
		
		Assert.assertEquals("Account Deleted", common.getYukonText("Account Deleted"));
		common.end();
	}

}
