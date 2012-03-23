package com.cannontech.selenium.test.stars;

import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.stars.HardwarePageSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
import com.cannontech.selenium.solvents.stars.ThermostatSchedulePageSolvent;


/**
 * This class uses xml file to import accounts and views the Account Information that was imported.
 * Main idea of this class is to verify additional Operator functionality in more depth 
 * especially Tstat Scheduled
 * @author steve.junod
 * revised @author kevin.krile
 */

public class TestOperatorFunctionsA2Selenium  extends SolventSeleniumTestCase {
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
		common.assertEqualsTextIsPresent("8");     //  yuk-9145
		common.clickFormButtonByButtonId("account/doAccountImport", "importButton");

		//  check for  100%  value   yuk-9144,9147
		common.assertEqualsTextIsPresent("Hardware:");     
		common.assertEqualsTextIsPresent("100%");     
		common.assertEqualsTextIsPresent("8 Added, 0 Updated, 0 Removed", Duration.standardSeconds(8));
		common.clickLinkByName("Back to Account Import");

		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and adds new Hardware Thermostats (UPro and ExpressStat) to the Account 71000015.
	 * ( 365 secs )
	 */
	@Test
	public void addTstatDevices() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		//Add Hardware - Cancel before Adding is complete 
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickAddByTableHeader("Thermostats"); 		
		hardware.enterSerialNumber("Serial Number:", "711100152");
		hardware.clickInventoryButton();
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
		common.clickButtonByAttributeWithPageLoad("name","cancel");
		
		//Add Hardware - first Tstat to Customer ( UPro )
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		common.selectDropDownMenuByIdName("tstatTypeToAdd","UtilityPRO");
		hardware.clickAddByTableHeader("Thermostats"); 		
		hardware.enterSerialNumber("Serial Number:", "711100151");
		hardware.clickInventoryButton();
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
		common.selectDropDownMenu("create", "serviceCompanyId", "QA_Service");
		common.selectDropDownMenu("create", "routeId", "Default - a_CCU-711"); 	
		common.selectDropDownMenu("create", "deviceStatusEntryId", "Installed");
		common.clickFormButton("create", "save");
		Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));
		Assert.assertEquals("Operator: Hardware (UtilityPRO 711100151)", common.getPageTitle());
		
		//Add Hardware - second Tstat to Customer ( ExpressStat )
		common.clickLinkByName("Hardware");
		common.selectDropDownMenuByIdName("tstatTypeToAdd","ExpressStat");
		hardware.clickAddByTableHeader("Thermostats"); 
		hardware.enterSerialNumber("Serial Number:", "711200151");
		hardware.clickInventoryButton();
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
		common.selectDropDownMenu("create", "serviceCompanyId", "QA_Service");
		common.selectDropDownMenu("create", "routeId", "Default - a_CCU-711");
		common.selectDropDownMenu("create", "deviceStatusEntryId", "Installed");
		common.clickFormButton("create", "save");
		Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));
		Assert.assertEquals("Operator: Hardware (ExpressStat 711200151)", common.getPageTitle());
		
		//Add Hardware - third Tstat to Customer ( UPRO )
		common.clickLinkByName("Hardware");
		common.selectDropDownMenuByIdName("tstatTypeToAdd","UtilityPRO");
		hardware.clickAddByTableHeader("Thermostats"); 
		hardware.enterSerialNumber("Serial Number:", "711100152");
		hardware.clickInventoryButton();
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
		common.selectDropDownMenu("create", "serviceCompanyId", "QA_Service");
		common.selectDropDownMenu("create", "routeId", "Default - a_CCU-711");
		common.selectDropDownMenu("create", "deviceStatusEntryId", "Installed");
		common.clickFormButton("create", "save");
		Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));
		Assert.assertEquals("Operator: Hardware (UtilityPRO 711100152)", common.getPageTitle());
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of UPro device 711100151, 
	 * and creates a weekday, Saturday, Sunday schedule called Sched01-15
	 */
	@Test
	public void createDefaultTstat15Sched() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		String[] tstats = {"711100151","711100152"};
		for(String tstat : tstats){
			hardware.clickEditSchedule(tstat); 	
			Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
			
			common.clickButtonBySpanTextWithElementWait("Create");
			schedPage.clickRadioButtonScheduleType("WEEKDAY_WEEKEND");
			schedPage.clickPopupButtonBySpanText("Create Schedule", "Next");
			// enter Sched name, then Save
			schedPage.inputScheduleName("Create Schedule", tstat); 
			schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
			Assert.assertEquals("The schedule " + tstat + " has been saved.",common.getYukonText("The schedule " + tstat + " has been saved."));
			common.clickLinkByName("Hardware");
		}
		hardware.clickEditSchedule("711200151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		
		common.clickButtonBySpanTextWithElementWait("Create");
		schedPage.clickRadioButtonScheduleType("ALL");
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Next");
		// enter Sched name, then Save
		schedPage.inputScheduleName("Create Schedule", "711200151"); 
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertEquals("The schedule 711200151 has been saved.",common.getYukonText("The schedule 711200151 has been saved."));
		common.clickLinkByName("Hardware");
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of UPro device 711100151, 
	 * and verifies the default values of the schedule 711100151 and 711100152.
	 * (105 s)
	 */
	@Test
	public void verifyDefaultSchedValues() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 711100151
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		
		//  verify default values of Sched page on first display
		//	verifying weekday schedule value for 711100151
		Assert.assertEquals(true, common.isTextPresent("M-F"));
		Assert.assertEquals(true, common.isTextPresent("Sa/Su"));
		Assert.assertEquals("6:00 AM", schedPage.getWakeStartTime("711100151", "M-F"));
		Assert.assertEquals("72", schedPage.getWakeCoolingTemp("711100151", "M-F"));
		Assert.assertEquals("72", schedPage.getWakeHeatingTemp("711100151", "M-F"));
		Assert.assertEquals("8:30 AM", schedPage.getLeaveStartTime("711100151", "M-F"));
		Assert.assertEquals("72", schedPage.getLeaveCoolingTemp("711100151", "M-F"));
		Assert.assertEquals("72", schedPage.getLeaveHeatingTemp("711100151", "M-F"));
		Assert.assertEquals("5:00 PM", schedPage.getReturnStartTime("711100151", "M-F"));
		Assert.assertEquals("72", schedPage.getReturnCoolingTemp("711100151", "M-F"));
		Assert.assertEquals("72", schedPage.getReturnHeatingTemp("711100151", "M-F"));
		Assert.assertEquals("9:00 PM", schedPage.getSleepStartTime("711100151", "M-F"));
		Assert.assertEquals("72", schedPage.getSleepCoolingTemp("711100151", "M-F"));
		Assert.assertEquals("72", schedPage.getSleepHeatingTemp("711100151", "M-F"));

		//	verifying weekend schedule value for 711100151
		Assert.assertEquals("6:00 AM", schedPage.getWakeStartTime("711100151", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getWakeCoolingTemp("711100151", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getWakeHeatingTemp("711100151", "Sa/Su"));
		Assert.assertEquals("8:30 AM", schedPage.getLeaveStartTime("711100151", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getLeaveCoolingTemp("711100151", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getLeaveHeatingTemp("711100151", "Sa/Su"));
		Assert.assertEquals("5:00 PM", schedPage.getReturnStartTime("711100151", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getReturnCoolingTemp("711100151", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getReturnHeatingTemp("711100151", "Sa/Su"));
		Assert.assertEquals("9:00 PM", schedPage.getSleepStartTime("711100151", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getSleepCoolingTemp("711100151", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getSleepHeatingTemp("711100151", "Sa/Su"));

		//	verifying weekday schedule value for 711100152
		Assert.assertEquals("6:00 AM", schedPage.getWakeStartTime("711100152", "M-F"));
		Assert.assertEquals("72", schedPage.getWakeCoolingTemp("711100152", "M-F"));
		Assert.assertEquals("72", schedPage.getWakeHeatingTemp("711100152", "M-F"));
		Assert.assertEquals("8:30 AM", schedPage.getLeaveStartTime("711100152", "M-F"));
		Assert.assertEquals("72", schedPage.getLeaveCoolingTemp("711100152", "M-F"));
		Assert.assertEquals("72", schedPage.getLeaveHeatingTemp("711100152", "M-F"));
		Assert.assertEquals("5:00 PM", schedPage.getReturnStartTime("711100152", "M-F"));
		Assert.assertEquals("72", schedPage.getReturnCoolingTemp("711100152", "M-F"));
		Assert.assertEquals("72", schedPage.getReturnHeatingTemp("711100152", "M-F"));
		Assert.assertEquals("9:00 PM", schedPage.getSleepStartTime("711100152", "M-F"));
		Assert.assertEquals("72", schedPage.getSleepCoolingTemp("711100152", "M-F"));
		Assert.assertEquals("72", schedPage.getSleepHeatingTemp("711100152", "M-F"));

		//	verifying weekend schedule value for 711100152
		Assert.assertEquals("6:00 AM", schedPage.getWakeStartTime("711100152", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getWakeCoolingTemp("711100152", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getWakeHeatingTemp("711100152", "Sa/Su"));
		Assert.assertEquals("8:30 AM", schedPage.getLeaveStartTime("711100152", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getLeaveCoolingTemp("711100152", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getLeaveHeatingTemp("711100152", "Sa/Su"));
		Assert.assertEquals("5:00 PM", schedPage.getReturnStartTime("711100152", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getReturnCoolingTemp("711100152", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getReturnHeatingTemp("711100152", "Sa/Su"));
		Assert.assertEquals("9:00 PM", schedPage.getSleepStartTime("711100152", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getSleepCoolingTemp("711100152", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getSleepHeatingTemp("711100152", "Sa/Su"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of UPro device 711100151, 
	 * and creates a weekday, Saturday, Sunday schedule called Sched01-15
	 */
	@Test
	public void createSaveTstat15Sched() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		
		common.clickButtonBySpanTextWithElementWait("Create");
		schedPage.clickRadioButtonScheduleType("WEEKDAY_SAT_SUN");
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Next");
		
		//editing the weekday schedule values

		schedPage.inputWakeStartTime("Create Schedule", "M-F", "05:00 AM");	//should change to 5:00 AM
		schedPage.inputLeaveStartTime("Create Schedule", "M-F", "8:00 AM");
		schedPage.inputReturnStartTime("Create Schedule", "M-F", "6:00 PM");
		schedPage.inputSleepStartTime("Create Schedule", "M-F", "10:00 PM");
		schedPage.inputWakeCoolingTemp("Create Schedule", "M-F", "74");
		schedPage.inputLeaveCoolingTemp("Create Schedule", "M-F", "74");
		schedPage.inputReturnCoolingTemp("Create Schedule", "M-F", "74");
		schedPage.inputSleepCoolingTemp("Create Schedule", "M-F", "74");
		schedPage.inputWakeHeatingTemp("Create Schedule", "M-F", "68");
		schedPage.inputLeaveHeatingTemp("Create Schedule", "M-F", "68");
		schedPage.inputReturnHeatingTemp("Create Schedule", "M-F", "68");
		schedPage.inputSleepHeatingTemp("Create Schedule", "M-F", "68");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.inputWakeStartTime("Create Schedule", "M-F", "05:00 AM");	//should change to 5:00 AM
		schedPage.inputLeaveStartTime("Create Schedule", "M-F", "8:00 AM");
		schedPage.inputReturnStartTime("Create Schedule", "M-F", "6:00 PM");
		schedPage.inputSleepStartTime("Create Schedule", "M-F", "10:00 PM");
		schedPage.inputWakeCoolingTemp("Create Schedule", "M-F", "74");
		schedPage.inputLeaveCoolingTemp("Create Schedule", "M-F", "74");
		schedPage.inputReturnCoolingTemp("Create Schedule", "M-F", "74");
		schedPage.inputSleepCoolingTemp("Create Schedule", "M-F", "74");
		schedPage.inputWakeHeatingTemp("Create Schedule", "M-F", "68");
		schedPage.inputLeaveHeatingTemp("Create Schedule", "M-F", "68");
		schedPage.inputReturnHeatingTemp("Create Schedule", "M-F", "68");
		schedPage.inputSleepHeatingTemp("Create Schedule", "M-F", "68");
		
		//editing the saturday schedule values

		schedPage.inputWakeStartTime("Create Schedule", "Sa", "5:15 AM");
		schedPage.inputLeaveStartTime("Create Schedule", "Sa", "8:15 AM");
		schedPage.inputReturnStartTime("Create Schedule", "Sa", "6:15 PM");
		schedPage.inputSleepStartTime("Create Schedule", "Sa", "10:15 PM");
		schedPage.inputWakeCoolingTemp("Create Schedule", "Sa", "75");
		schedPage.inputLeaveCoolingTemp("Create Schedule", "Sa", "75");
		schedPage.inputReturnCoolingTemp("Create Schedule", "Sa", "75");
		schedPage.inputSleepCoolingTemp("Create Schedule", "Sa", "75");
		schedPage.inputWakeHeatingTemp("Create Schedule", "Sa", "69");
		schedPage.inputLeaveHeatingTemp("Create Schedule", "Sa", "69");
		schedPage.inputReturnHeatingTemp("Create Schedule", "Sa", "69");
		schedPage.inputSleepHeatingTemp("Create Schedule", "Sa", "69");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.inputWakeStartTime("Create Schedule", "Sa", "5:15 AM");
		schedPage.inputLeaveStartTime("Create Schedule", "Sa", "8:15 AM");
		schedPage.inputReturnStartTime("Create Schedule", "Sa", "6:15 PM");
		schedPage.inputSleepStartTime("Create Schedule", "Sa", "10:15 PM");
		schedPage.inputWakeCoolingTemp("Create Schedule", "Sa", "75");
		schedPage.inputLeaveCoolingTemp("Create Schedule", "Sa", "75");
		schedPage.inputReturnCoolingTemp("Create Schedule", "Sa", "75");
		schedPage.inputSleepCoolingTemp("Create Schedule", "Sa", "75");
		schedPage.inputWakeHeatingTemp("Create Schedule", "Sa", "69");
		schedPage.inputLeaveHeatingTemp("Create Schedule", "Sa", "69");
		schedPage.inputReturnHeatingTemp("Create Schedule", "Sa", "69");
		schedPage.inputSleepHeatingTemp("Create Schedule", "Sa", "69");

		//editing the sunday schedule values

		schedPage.inputWakeStartTime("Create Schedule", "Su", "5:05 AM");
		schedPage.inputLeaveStartTime("Create Schedule", "Su", "08:05 AM");	//should change to 8:05 AM
		schedPage.inputReturnStartTime("Create Schedule", "Su", "06:05 PM");//should change to 6:05 PM
		schedPage.inputSleepStartTime("Create Schedule", "Su", "10:05 PM");	//should change to 10:05 PM
		schedPage.inputWakeCoolingTemp("Create Schedule", "Su", "76");
		schedPage.inputLeaveCoolingTemp("Create Schedule", "Su", "76");
		schedPage.inputReturnCoolingTemp("Create Schedule", "Su", "76");
		schedPage.inputSleepCoolingTemp("Create Schedule", "Su", "76");
		schedPage.inputWakeHeatingTemp("Create Schedule", "Su", "66");
		schedPage.inputLeaveHeatingTemp("Create Schedule", "Su", "66");
		schedPage.inputReturnHeatingTemp("Create Schedule", "Su", "66");
		schedPage.inputSleepHeatingTemp("Create Schedule", "Su", "66");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.inputWakeStartTime("Create Schedule", "Su", "5:05 AM");
		schedPage.inputLeaveStartTime("Create Schedule", "Su", "08:05 AM");	//should change to 8:05 AM
		schedPage.inputReturnStartTime("Create Schedule", "Su", "06:05 PM");//should change to 6:05 PM
		schedPage.inputSleepStartTime("Create Schedule", "Su", "10:05 PM");	//should change to 10:05 PM
		schedPage.inputWakeCoolingTemp("Create Schedule", "Su", "76");
		schedPage.inputLeaveCoolingTemp("Create Schedule", "Su", "76");
		schedPage.inputReturnCoolingTemp("Create Schedule", "Su", "76");
		schedPage.inputSleepCoolingTemp("Create Schedule", "Su", "76");
		schedPage.inputWakeHeatingTemp("Create Schedule", "Su", "66");
		schedPage.inputLeaveHeatingTemp("Create Schedule", "Su", "66");
		schedPage.inputReturnHeatingTemp("Create Schedule", "Su", "66");
		schedPage.inputSleepHeatingTemp("Create Schedule", "Su", "66");
		
		//  yuk-8666, verify invalid data replaced with previous data 
		schedPage.inputWakeCoolingTemp("Create Schedule", "M-F", "ad");
		schedPage.inputWakeStartTime("Create Schedule", "M-F", "5:00 AM"); //wake cool temp should change back to 74
		schedPage.inputWakeStartTime("Create Schedule", "M-F", "33:99 AM");
		schedPage.inputWakeHeatingTemp("Create Schedule", "M-F", "68"); 	//wake start time should change back to 5:00 AM
		
		// enter Sched name, then Save
		schedPage.inputScheduleName("Create Schedule", "711100151"); 
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertTrue("Schedule saved 2 of the same named schedules ",
				common.isTextPresent("Name already in use, please provide a different schedule name."));
		schedPage.inputScheduleName("Create Schedule", "Sched01-15");
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertEquals("The schedule Sched01-15 has been saved.",common.getYukonText("The schedule Sched01-15 has been saved."));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of device UPro 711100151, 
	 * and verifies the edited schedule values.
	 */
	@Test	 
	public void verifyTstat15SchedAfterSave() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 711100151
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
	
		hardware.clickEditSchedule("711100151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		Assert.assertEquals(true, common.isTextPresent("Sched01-15"));
		
		//verifying weekday values
		
		Assert.assertEquals("5:00 AM", schedPage.getWakeStartTime("Sched01-15", "M-F"));
		Assert.assertEquals("74", schedPage.getWakeCoolingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("68", schedPage.getWakeHeatingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("8:00 AM", schedPage.getLeaveStartTime("Sched01-15", "M-F"));
		Assert.assertEquals("74", schedPage.getLeaveCoolingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("68", schedPage.getLeaveHeatingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("6:00 PM", schedPage.getReturnStartTime("Sched01-15", "M-F"));
		Assert.assertEquals("74", schedPage.getReturnCoolingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("68", schedPage.getReturnHeatingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("10:00 PM", schedPage.getSleepStartTime("Sched01-15", "M-F"));
		Assert.assertEquals("74", schedPage.getSleepCoolingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("68", schedPage.getSleepHeatingTemp("Sched01-15", "M-F"));

		//verifying saturday values
		
		Assert.assertEquals("5:15 AM", schedPage.getWakeStartTime("Sched01-15", "Sa"));
		Assert.assertEquals("75", schedPage.getWakeCoolingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("69", schedPage.getWakeHeatingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("8:15 AM", schedPage.getLeaveStartTime("Sched01-15", "Sa"));
		Assert.assertEquals("75", schedPage.getLeaveCoolingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("69", schedPage.getLeaveHeatingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("6:15 PM", schedPage.getReturnStartTime("Sched01-15", "Sa"));
		Assert.assertEquals("75", schedPage.getReturnCoolingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("69", schedPage.getReturnHeatingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("10:15 PM", schedPage.getSleepStartTime("Sched01-15", "Sa"));
		Assert.assertEquals("75", schedPage.getSleepCoolingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("69", schedPage.getSleepHeatingTemp("Sched01-15", "Sa"));

		//verifying sunday values
		
		Assert.assertEquals("5:05 AM", schedPage.getWakeStartTime("Sched01-15", "Su"));
		Assert.assertEquals("76", schedPage.getWakeCoolingTemp("Sched01-15", "Su"));
		Assert.assertEquals("66", schedPage.getWakeHeatingTemp("Sched01-15", "Su"));
		Assert.assertEquals("8:05 AM", schedPage.getLeaveStartTime("Sched01-15", "Su"));
		Assert.assertEquals("76", schedPage.getLeaveCoolingTemp("Sched01-15", "Su"));
		Assert.assertEquals("66", schedPage.getLeaveHeatingTemp("Sched01-15", "Su"));
		Assert.assertEquals("6:05 PM", schedPage.getReturnStartTime("Sched01-15", "Su"));
		Assert.assertEquals("76", schedPage.getReturnCoolingTemp("Sched01-15", "Su"));
		Assert.assertEquals("66", schedPage.getReturnHeatingTemp("Sched01-15", "Su"));
		Assert.assertEquals("10:05 PM", schedPage.getSleepStartTime("Sched01-15", "Su"));
		Assert.assertEquals("76", schedPage.getSleepCoolingTemp("Sched01-15", "Su"));
		Assert.assertEquals("66", schedPage.getSleepHeatingTemp("Sched01-15", "Su"));
		common.end();
	}	
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of UPro device 711100151,
	 * and verifies the °C and °F radio buttons, as well as the changing of the temperature
	 */
	@Test
	public void verifyDegreeChangeAlgorithm(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		
		common.clickRadioButtonByLabelValue("C");
		
		//verifying weekday values applying the celcius to fahrenteit (F=C*1.8+32)
		
		Assert.assertEquals("23.5", schedPage.getWakeCoolingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("20.0", schedPage.getWakeHeatingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("23.5", schedPage.getLeaveCoolingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("20.0", schedPage.getLeaveHeatingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("23.5", schedPage.getReturnCoolingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("20.0", schedPage.getReturnHeatingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("23.5", schedPage.getSleepCoolingTemp("Sched01-15", "M-F"));
		Assert.assertEquals("20.0", schedPage.getSleepHeatingTemp("Sched01-15", "M-F"));

		//verifying saturday values applying the celcius to fahrenteit (F=C*1.8+32)
		
		Assert.assertEquals("24.0", schedPage.getWakeCoolingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("20.5", schedPage.getWakeHeatingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("24.0", schedPage.getLeaveCoolingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("20.5", schedPage.getLeaveHeatingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("24.0", schedPage.getReturnCoolingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("20.5", schedPage.getReturnHeatingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("24.0", schedPage.getSleepCoolingTemp("Sched01-15", "Sa"));
		Assert.assertEquals("20.5", schedPage.getSleepHeatingTemp("Sched01-15", "Sa"));

		//verifying sunday values applying the celcius to fahrenteit (F=C*1.8+32)
		
		Assert.assertEquals("24.5", schedPage.getWakeCoolingTemp("Sched01-15", "Su"));
		Assert.assertEquals("19.0", schedPage.getWakeHeatingTemp("Sched01-15", "Su"));
		Assert.assertEquals("24.5", schedPage.getLeaveCoolingTemp("Sched01-15", "Su"));
		Assert.assertEquals("19.0", schedPage.getLeaveHeatingTemp("Sched01-15", "Su"));
		Assert.assertEquals("24.5", schedPage.getReturnCoolingTemp("Sched01-15", "Su"));
		Assert.assertEquals("19.0", schedPage.getReturnHeatingTemp("Sched01-15", "Su"));
		Assert.assertEquals("24.5", schedPage.getSleepCoolingTemp("Sched01-15", "Su"));
		Assert.assertEquals("19.0", schedPage.getSleepHeatingTemp("Sched01-15", "Su"));
		
		//changing back to fahrenheit
		
		common.clickRadioButtonByLabelValue("F");
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of ExpressStat device 711200151, 
	 * and verifies the default schedule values.
	 */
	@Test	 
	public void verifyDefaultTstat115Sched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 711200151
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
	
		hardware.clickEditSchedule("711200151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
			
		//  verify default values of Sched page on first display
		Assert.assertEquals("6:00 AM", schedPage.getWakeStartTime("711200151", "All"));
		Assert.assertEquals("72", schedPage.getWakeCoolingTemp("711200151", "All"));
		Assert.assertEquals("72", schedPage.getWakeHeatingTemp("711200151", "All"));	
		Assert.assertEquals("8:30 AM", schedPage.getLeaveStartTime("711200151", "All"));
		Assert.assertEquals("72", schedPage.getLeaveCoolingTemp("711200151", "All"));
		Assert.assertEquals("72", schedPage.getLeaveHeatingTemp("711200151", "All"));	
		Assert.assertEquals("5:00 PM", schedPage.getReturnStartTime("711200151", "All"));
		Assert.assertEquals("72", schedPage.getReturnCoolingTemp("711200151", "All"));
		Assert.assertEquals("72", schedPage.getReturnHeatingTemp("711200151", "All"));
		Assert.assertEquals("9:00 PM", schedPage.getSleepStartTime("711200151", "All"));
		Assert.assertEquals("72", schedPage.getSleepCoolingTemp("711200151", "All"));
		Assert.assertEquals("72", schedPage.getSleepHeatingTemp("711200151", "All"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of device ExpressStat 711200151, 
	 * and Edits & Saves the schedule values.
	 * 
	 */
	@Test	 
	public void editSaveTstat115Sched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
	
		hardware.clickEditSchedule("711200151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());

		schedPage.editTstatSchedule("711200151");

		schedPage.inputWakeStartTime("Edit Schedule - 711200151", "All", "5:05 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711200151", "All", "8:05 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711200151", "All", "6:05 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711200151", "All", "10:05 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711200151", "All", "100");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711200151", "All", "39");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711200151", "All", "100");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711200151", "All", "39");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711200151", "All", "100");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711200151", "All", "39");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711200151", "All", "100");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711200151", "All", "39");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.inputWakeStartTime("Edit Schedule - 711200151", "All", "5:05 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711200151", "All", "8:05 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711200151", "All", "6:05 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711200151", "All", "10:05 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711200151", "All", "100");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711200151", "All", "39");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711200151", "All", "100");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711200151", "All", "39");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711200151", "All", "100");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711200151", "All", "39");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711200151", "All", "100");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711200151", "All", "39");
				
		// enter Sched name, then Save
		schedPage.inputScheduleName("Edit Schedule - 711200151", "711200151-01");
		schedPage.clickPopupButtonBySpanText("Edit Schedule - 711200151", "Save");
		Assert.assertEquals("The schedule 711200151-01 has been saved.",common.getYukonText("The schedule 711200151-01 has been saved."));
		common.end();
	}	
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of ExpressStat device 711200151, 
	 * and verifies the edited schedule values.
	 * 
	 */
	@Test	 
	public void verifyTstat115SchedAfterSave() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
	
		hardware.clickEditSchedule("711200151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		
		Assert.assertEquals("5:05 AM", schedPage.getWakeStartTime("711200151-01", "All"));
		Assert.assertEquals("88", schedPage.getWakeCoolingTemp("711200151-01", "All"));
		Assert.assertEquals("88", schedPage.getWakeHeatingTemp("711200151-01", "All"));
		Assert.assertEquals("8:05 AM", schedPage.getLeaveStartTime("711200151-01", "All"));
		Assert.assertEquals("45", schedPage.getLeaveCoolingTemp("711200151-01", "All"));
		Assert.assertEquals("45", schedPage.getLeaveHeatingTemp("711200151-01", "All"));
		Assert.assertEquals("6:05 PM", schedPage.getReturnStartTime("711200151-01", "All"));
		Assert.assertEquals("88", schedPage.getReturnCoolingTemp("711200151-01", "All"));
		Assert.assertEquals("88", schedPage.getReturnHeatingTemp("711200151-01", "All"));
		Assert.assertEquals("10:05 PM", schedPage.getSleepStartTime("711200151-01", "All"));
		Assert.assertEquals("45", schedPage.getSleepCoolingTemp("711200151-01", "All"));
		Assert.assertEquals("45", schedPage.getSleepHeatingTemp("711200151-01", "All"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of UPro device 711100152, 
	 * and Edits & Saves the schedule values and name
	 * 
	 */
	@Test	 
	public void editSaveTstat215Sched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Saved Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100152");
		schedPage.editTstatSchedule("711100152");
		
		//editing the weekday values
		
		schedPage.inputWakeStartTime("Edit Schedule - 711100152", "M-F", "05:00 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711100152", "M-F", "8:00 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711100152", "M-F", "6:00 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711100152", "M-F", "10:00 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711100152", "M-F", "84");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711100152", "M-F", "84");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711100152", "M-F", "84");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711100152", "M-F", "84");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711100152", "M-F", "78");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711100152", "M-F", "78");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711100152", "M-F", "78");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711100152", "M-F", "78");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.editTstatSchedule("711100152");
		schedPage.inputWakeStartTime("Edit Schedule - 711100152", "M-F", "05:00 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711100152", "M-F", "8:00 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711100152", "M-F", "6:00 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711100152", "M-F", "10:00 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711100152", "M-F", "84");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711100152", "M-F", "84");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711100152", "M-F", "84");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711100152", "M-F", "84");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711100152", "M-F", "78");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711100152", "M-F", "78");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711100152", "M-F", "78");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711100152", "M-F", "78");
		
		//editing the weekend values

		schedPage.inputWakeStartTime("Edit Schedule - 711100152", "Sa/Su", "5:15 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711100152", "Sa/Su", "8:15 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711100152", "Sa/Su", "6:15 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711100152", "Sa/Su", "10:15 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711100152", "Sa/Su", "85");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711100152", "Sa/Su", "85");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711100152", "Sa/Su", "85");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711100152", "Sa/Su", "85");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711100152", "Sa/Su", "79");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711100152", "Sa/Su", "79");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711100152", "Sa/Su", "79");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711100152", "Sa/Su", "79");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.inputWakeStartTime("Edit Schedule - 711100152", "Sa/Su", "5:15 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711100152", "Sa/Su", "8:15 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711100152", "Sa/Su", "6:15 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711100152", "Sa/Su", "10:15 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711100152", "Sa/Su", "85");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711100152", "Sa/Su", "85");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711100152", "Sa/Su", "85");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711100152", "Sa/Su", "85");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711100152", "Sa/Su", "79");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711100152", "Sa/Su", "79");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711100152", "Sa/Su", "79");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711100152", "Sa/Su", "79");
		//editing the schedule name and saving
		schedPage.inputScheduleName("Edit Schedule - 711100152", "Sched01-215");		
		schedPage.clickPopupButtonBySpanText("Edit Schedule - 711100152", "Save");
		Assert.assertEquals("The schedule Sched01-215 has been saved.",common.getYukonText("The schedule Sched01-215 has been saved."));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Schedule page of UPro device 71000015, 
	 * and verifies the edited schedule values
	 */
	@Test	 
	public void verifyTstat215AfterSave() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100151"); 	
		
		//verifying weekday values
		
		Assert.assertEquals("5:00 AM", schedPage.getWakeStartTime("Sched01-215", "M-F"));
		Assert.assertEquals("84", schedPage.getWakeCoolingTemp("Sched01-215", "M-F"));
		Assert.assertEquals("78", schedPage.getWakeHeatingTemp("Sched01-215", "M-F"));
		Assert.assertEquals("8:00 AM", schedPage.getLeaveStartTime("Sched01-215", "M-F"));
		Assert.assertEquals("84", schedPage.getLeaveCoolingTemp("Sched01-215", "M-F"));
		Assert.assertEquals("78", schedPage.getLeaveHeatingTemp("Sched01-215", "M-F"));
		Assert.assertEquals("6:00 PM", schedPage.getReturnStartTime("Sched01-215", "M-F"));
		Assert.assertEquals("84", schedPage.getReturnCoolingTemp("Sched01-215", "M-F"));
		Assert.assertEquals("78", schedPage.getReturnHeatingTemp("Sched01-215", "M-F"));
		Assert.assertEquals("10:00 PM", schedPage.getSleepStartTime("Sched01-215", "M-F"));
		Assert.assertEquals("84", schedPage.getSleepCoolingTemp("Sched01-215", "M-F"));
		Assert.assertEquals("78", schedPage.getSleepHeatingTemp("Sched01-215", "M-F"));

		//verifying weekend values
		
		Assert.assertEquals("5:15 AM", schedPage.getWakeStartTime("Sched01-215", "Sa/Su"));
		Assert.assertEquals("85", schedPage.getWakeCoolingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals("79", schedPage.getWakeHeatingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals("8:15 AM", schedPage.getLeaveStartTime("Sched01-215", "Sa/Su"));
		Assert.assertEquals("85", schedPage.getLeaveCoolingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals("79", schedPage.getLeaveHeatingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals("6:15 PM", schedPage.getReturnStartTime("Sched01-215", "Sa/Su"));
		Assert.assertEquals("85", schedPage.getReturnCoolingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals("79", schedPage.getReturnHeatingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals("10:15 PM", schedPage.getSleepStartTime("Sched01-215", "Sa/Su"));
		Assert.assertEquals("85", schedPage.getSleepCoolingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals("79", schedPage.getSleepHeatingTemp("Sched01-215", "Sa/Su"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Saved Schedule page of device 711100152, 
	 * and sends the schedule (Sched01-215) to the thermostat 711100152
	 */
	@Test	 
	public void sendSchedule() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 711100152
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100152"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		
		schedPage.clickSendNowTstatSchedule("Sched01-215");
		schedPage.clickPopupButtonBySpanText("Send Schedule", "OK");
		Assert.assertEquals("The schedule Sched01-215 has been sent. It may take a few minutes before the thermostat receives it.",
				common.getYukonText("The schedule Sched01-215 has been sent."));	//has trouble with multi-sentence lines, but this works
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Saved Schedule page of device 711100151, 
	 * and copies the schedule
	 */
	@Test	 
	public void copySchedule() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		//copying the schedule
		schedPage.clickCopyTstatSchedule("Sched01-215");
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertEquals("The schedule Copy of Sched01-215 has been saved.", common.getYukonText("The schedule Copy of Sched01-215 has been saved."));
		Assert.assertEquals(true,common.isTextPresent("Copy of Sched01-215"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Saved Schedule page of device 711100151, 
	 * and copies the schedule
	 */
	@Test	 
	public void verifyCopiedSchedule() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		
		//verifying copy values
		Assert.assertEquals(schedPage.getWakeStartTime("Copy of Sched01-215", "M-F"), schedPage.getWakeStartTime("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getWakeCoolingTemp("Copy of Sched01-215", "M-F"), schedPage.getWakeCoolingTemp("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getWakeHeatingTemp("Copy of Sched01-215", "M-F"), schedPage.getWakeHeatingTemp("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getLeaveStartTime("Copy of Sched01-215", "M-F"), schedPage.getLeaveStartTime("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getLeaveCoolingTemp("Copy of Sched01-215", "M-F"), schedPage.getLeaveCoolingTemp("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getLeaveHeatingTemp("Copy of Sched01-215", "M-F"), schedPage.getLeaveHeatingTemp("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getReturnStartTime("Copy of Sched01-215", "M-F"), schedPage.getReturnStartTime("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getReturnCoolingTemp("Copy of Sched01-215", "M-F"), schedPage.getReturnCoolingTemp("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getReturnHeatingTemp("Copy of Sched01-215", "M-F"), schedPage.getReturnHeatingTemp("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getSleepStartTime("Copy of Sched01-215", "M-F"), schedPage.getSleepStartTime("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getSleepCoolingTemp("Copy of Sched01-215", "M-F"), schedPage.getSleepCoolingTemp("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getSleepHeatingTemp("Copy of Sched01-215", "M-F"), schedPage.getSleepHeatingTemp("Sched01-215", "M-F"));
		Assert.assertEquals(schedPage.getWakeStartTime("Copy of Sched01-215", "Sa/Su"), schedPage.getWakeStartTime("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getWakeCoolingTemp("Copy of Sched01-215", "Sa/Su"), schedPage.getWakeCoolingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getWakeHeatingTemp("Copy of Sched01-215", "Sa/Su"), schedPage.getWakeHeatingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getLeaveStartTime("Copy of Sched01-215", "Sa/Su"), schedPage.getLeaveStartTime("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getLeaveCoolingTemp("Copy of Sched01-215", "Sa/Su"), schedPage.getLeaveCoolingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getLeaveHeatingTemp("Copy of Sched01-215", "Sa/Su"), schedPage.getLeaveHeatingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getReturnStartTime("Copy of Sched01-215", "Sa/Su"), schedPage.getReturnStartTime("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getReturnCoolingTemp("Copy of Sched01-215", "Sa/Su"), schedPage.getReturnCoolingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getReturnHeatingTemp("Copy of Sched01-215", "Sa/Su"), schedPage.getReturnHeatingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getSleepStartTime("Copy of Sched01-215", "Sa/Su"), schedPage.getSleepStartTime("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getSleepCoolingTemp("Copy of Sched01-215", "Sa/Su"), schedPage.getSleepCoolingTemp("Sched01-215", "Sa/Su"));
		Assert.assertEquals(schedPage.getSleepHeatingTemp("Copy of Sched01-215", "Sa/Su"), schedPage.getSleepHeatingTemp("Sched01-215", "Sa/Su"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Saved Schedule page of device 711100151, and deletes the Sched01-15
	 */
	@Test	 
	public void deleteTstat15Sched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100151"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());

		//deleting the schedule
		schedPage.editTstatSchedule("Sched01-15");
		schedPage.clickPopupButtonBySpanText("Edit Schedule - Sched01-15", "Delete");
		schedPage.clickPopupButtonBySpanText("Confirm Delete", "OK");
		Assert.assertEquals("Schedule \"Sched01-15\" Deleted.", common.getYukonText("Schedule \"Sched01-15\" Deleted."));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * then to the Saved Schedule page of device 711100151, and deletes the Sched01-15
	 */
	@Test	 
	public void confirmHistoryPage() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// Nav to Hardware, then Tstat Schedule page for device 71000015
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (71000015)", common.getPageTitle());
		hardware.clickEditSchedule("711100152"); 	
		Assert.assertEquals("Operator: Thermostat Schedules", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("View the history for the selected thermostat(s).");

		Assert.assertEquals("Schedule not found in history ", true, common.isTextPresent("Schedule: Sched01-215"));
		common.end();
	}
	
}