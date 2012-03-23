package com.cannontech.selenium.test.stars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
import com.cannontech.selenium.solvents.stars.ThermostatManualPageSolvent;
import com.cannontech.selenium.solvents.stars.ThermostatSchedulePageSolvent;

/**
 * This test logs into Stars using a Residential Customer Account and Edits the Thermostat schedule,
 * 1 UPro and 1 ExpressStat device
 * includes verification done in test case  3.2.2.1
 * @author steve.junod
 * @author kevin.krile revision 8/11
 */

public class TestResidentialFunctionsA2Selenium extends SolventSeleniumTestCase {
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("71000014", "71000014");
	}
	@Test	 
	public void verifyTstat114DefaultSched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		common.clickLinkByNameWithoutPageLoadWait("711100141");
		stars.clickLinkByThermostatName("711100141", "Schedules");

		common.clickButtonBySpanTextWithElementWait("Create");
		schedPage.clickRadioButtonScheduleType("WEEKDAY_WEEKEND");
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Next");
		// enter Sched name, then Save
		schedPage.inputScheduleName("Create Schedule", "711100141"); 
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertEquals("The schedule 711100141 has been saved.",common.getYukonText("The schedule 711100141 has been saved."));
		//  verify default values of Sched page on first display
		//	verifying weekday schedule value for 711100141
		Assert.assertEquals(true, common.isTextPresent("M-F"));
		Assert.assertEquals(true, common.isTextPresent("Sa/Su"));
		Assert.assertEquals("6:00 AM", schedPage.getWakeStartTime("711100141", "M-F"));
		Assert.assertEquals("72", schedPage.getWakeCoolingTemp("711100141", "M-F"));
		Assert.assertEquals("72", schedPage.getWakeHeatingTemp("711100141", "M-F"));
		Assert.assertEquals("8:30 AM", schedPage.getLeaveStartTime("711100141", "M-F"));
		Assert.assertEquals("72", schedPage.getLeaveCoolingTemp("711100141", "M-F"));
		Assert.assertEquals("72", schedPage.getLeaveHeatingTemp("711100141", "M-F"));
		Assert.assertEquals("5:00 PM", schedPage.getReturnStartTime("711100141", "M-F"));
		Assert.assertEquals("72", schedPage.getReturnCoolingTemp("711100141", "M-F"));
		Assert.assertEquals("72", schedPage.getReturnHeatingTemp("711100141", "M-F"));
		Assert.assertEquals("9:00 PM", schedPage.getSleepStartTime("711100141", "M-F"));
		Assert.assertEquals("72", schedPage.getSleepCoolingTemp("711100141", "M-F"));
		Assert.assertEquals("72", schedPage.getSleepHeatingTemp("711100141", "M-F"));

		//	verifying weekend schedule value for 711100141
		Assert.assertEquals("6:00 AM", schedPage.getWakeStartTime("711100141", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getWakeCoolingTemp("711100141", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getWakeHeatingTemp("711100141", "Sa/Su"));
		Assert.assertEquals("8:30 AM", schedPage.getLeaveStartTime("711100141", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getLeaveCoolingTemp("711100141", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getLeaveHeatingTemp("711100141", "Sa/Su"));
		Assert.assertEquals("5:00 PM", schedPage.getReturnStartTime("711100141", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getReturnCoolingTemp("711100141", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getReturnHeatingTemp("711100141", "Sa/Su"));
		Assert.assertEquals("9:00 PM", schedPage.getSleepStartTime("711100141", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getSleepCoolingTemp("711100141", "Sa/Su"));
		Assert.assertEquals("72", schedPage.getSleepHeatingTemp("711100141", "Sa/Su"));
		common.end();
	}

	@Test	 
	public void createSaveTstat114Sched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711100141");
		stars.clickLinkByThermostatName("711100141", "Schedules");
	
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
		schedPage.inputScheduleName("Create Schedule", "Sched01-114"); 	
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertEquals("The schedule Sched01-114 has been saved.",common.getYukonText("The schedule Sched01-114 has been saved."));
		common.end();
	}	
	/**
	 * This method navigates to the Hardware page in the Customer side <br>
	 * then to the Schedule page of device UPro 711100141, 
	 * and verifies the edited schedule values.
	 */
	@Test	 
	public void verifyTstat114SchedAfterSave() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711100141");
		stars.clickLinkByThermostatName("711100141", "Schedules");
	
		//verifying weekday values
		
		Assert.assertEquals("5:00 AM", schedPage.getWakeStartTime("Sched01-114", "M-F"));
		Assert.assertEquals("74", schedPage.getWakeCoolingTemp("Sched01-114", "M-F"));
		Assert.assertEquals("68", schedPage.getWakeHeatingTemp("Sched01-114", "M-F"));
		Assert.assertEquals("8:00 AM", schedPage.getLeaveStartTime("Sched01-114", "M-F"));
		Assert.assertEquals("74", schedPage.getLeaveCoolingTemp("Sched01-114", "M-F"));
		Assert.assertEquals("68", schedPage.getLeaveHeatingTemp("Sched01-114", "M-F"));
		Assert.assertEquals("6:00 PM", schedPage.getReturnStartTime("Sched01-114", "M-F"));
		Assert.assertEquals("74", schedPage.getReturnCoolingTemp("Sched01-114", "M-F"));
		Assert.assertEquals("68", schedPage.getReturnHeatingTemp("Sched01-114", "M-F"));
		Assert.assertEquals("10:00 PM", schedPage.getSleepStartTime("Sched01-114", "M-F"));
		Assert.assertEquals("74", schedPage.getSleepCoolingTemp("Sched01-114", "M-F"));
		Assert.assertEquals("68", schedPage.getSleepHeatingTemp("Sched01-114", "M-F"));

		//verifying saturday values
		
		Assert.assertEquals("5:15 AM", schedPage.getWakeStartTime("Sched01-114", "Sa"));
		Assert.assertEquals("75", schedPage.getWakeCoolingTemp("Sched01-114", "Sa"));
		Assert.assertEquals("69", schedPage.getWakeHeatingTemp("Sched01-114", "Sa"));
		Assert.assertEquals("8:15 AM", schedPage.getLeaveStartTime("Sched01-114", "Sa"));
		Assert.assertEquals("75", schedPage.getLeaveCoolingTemp("Sched01-114", "Sa"));
		Assert.assertEquals("69", schedPage.getLeaveHeatingTemp("Sched01-114", "Sa"));
		Assert.assertEquals("6:15 PM", schedPage.getReturnStartTime("Sched01-114", "Sa"));
		Assert.assertEquals("75", schedPage.getReturnCoolingTemp("Sched01-114", "Sa"));
		Assert.assertEquals("69", schedPage.getReturnHeatingTemp("Sched01-114", "Sa"));
		Assert.assertEquals("10:15 PM", schedPage.getSleepStartTime("Sched01-114", "Sa"));
		Assert.assertEquals("75", schedPage.getSleepCoolingTemp("Sched01-114", "Sa"));
		Assert.assertEquals("69", schedPage.getSleepHeatingTemp("Sched01-114", "Sa"));

		//verifying sunday values
		
		Assert.assertEquals("5:05 AM", schedPage.getWakeStartTime("Sched01-114", "Su"));
		Assert.assertEquals("76", schedPage.getWakeCoolingTemp("Sched01-114", "Su"));
		Assert.assertEquals("66", schedPage.getWakeHeatingTemp("Sched01-114", "Su"));
		Assert.assertEquals("8:05 AM", schedPage.getLeaveStartTime("Sched01-114", "Su"));
		Assert.assertEquals("76", schedPage.getLeaveCoolingTemp("Sched01-114", "Su"));
		Assert.assertEquals("66", schedPage.getLeaveHeatingTemp("Sched01-114", "Su"));
		Assert.assertEquals("6:05 PM", schedPage.getReturnStartTime("Sched01-114", "Su"));
		Assert.assertEquals("76", schedPage.getReturnCoolingTemp("Sched01-114", "Su"));
		Assert.assertEquals("66", schedPage.getReturnHeatingTemp("Sched01-114", "Su"));
		Assert.assertEquals("10:05 PM", schedPage.getSleepStartTime("Sched01-114", "Su"));
		Assert.assertEquals("76", schedPage.getSleepCoolingTemp("Sched01-114", "Su"));
		Assert.assertEquals("66", schedPage.getSleepHeatingTemp("Sched01-114", "Su"));
		common.end();
	}	
	/** This method navigates to the Hardware page in the Customer side <br>
	 * then to the Schedule page of device UPro 711100141, 
	 * and Edits & Saves the schedule values.
	 * 
	 */
	@Test	 
	public void editSaveTstat114Sched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711100141");
		stars.clickLinkByThermostatName("711100141", "Schedules");
		schedPage.editTstatSchedule("711100141");

		//editing the weekday schedule values
		
		schedPage.inputWakeStartTime("Edit Schedule - 711100141", "M-F", "5:05 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711100141", "M-F", "08:05 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711100141", "M-F", "06:05 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711100141", "M-F", "10:05 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711100141", "M-F", "76");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711100141", "M-F", "76");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711100141", "M-F", "76");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711100141", "M-F", "76");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711100141", "M-F", "66");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711100141", "M-F", "66");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711100141", "M-F", "66");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711100141", "M-F", "66");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.inputWakeStartTime("Edit Schedule - 711100141", "M-F", "5:05 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711100141", "M-F", "08:05 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711100141", "M-F", "06:05 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711100141", "M-F", "10:05 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711100141", "M-F", "76");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711100141", "M-F", "76");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711100141", "M-F", "76");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711100141", "M-F", "76");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711100141", "M-F", "66");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711100141", "M-F", "66");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711100141", "M-F", "66");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711100141", "M-F", "66");

		//editing the weekend schedule values
		
		schedPage.inputWakeStartTime("Edit Schedule - 711100141", "Sa/Su", "5:15 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711100141", "Sa/Su", "08:15 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711100141", "Sa/Su", "06:15 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711100141", "Sa/Su", "10:15 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711100141", "Sa/Su", "100");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711100141", "Sa/Su", "39");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711100141", "Sa/Su", "100");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711100141", "Sa/Su", "39");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711100141", "Sa/Su", "100");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711100141", "Sa/Su", "39");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711100141", "Sa/Su", "100");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711100141", "Sa/Su", "39");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.inputWakeStartTime("Edit Schedule - 711100141", "Sa/Su", "5:15 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711100141", "Sa/Su", "08:15 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711100141", "Sa/Su", "06:15 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711100141", "Sa/Su", "10:15 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711100141", "Sa/Su", "100");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711100141", "Sa/Su", "39");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711100141", "Sa/Su", "100");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711100141", "Sa/Su", "39");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711100141", "Sa/Su", "100");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711100141", "Sa/Su", "39");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711100141", "Sa/Su", "100");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711100141", "Sa/Su", "39");
		
		//verify adding a number too high brings it back to the previous number
		
		
		// enter Sched name, then Save
		schedPage.inputScheduleName("Edit Schedule - 711100141", "711100141-01");
		schedPage.clickPopupButtonBySpanText("Edit Schedule - 711100141", "Save");
		Assert.assertEquals("The schedule 711100141-01 has been saved.",common.getYukonText("The schedule 711100141-01 has been saved."));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Customer side <br>
	 * then to the Schedule page of UPro device 711100141, 
	 * and verifies the edited schedule values.
	 * 
	 */
	@Test	 
	public void verifyTStat114SchedAfterSave() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711100141");
		stars.clickLinkByThermostatName("711100141", "Schedules");

		//verifying weekday values
		
		Assert.assertEquals("5:05 AM", schedPage.getWakeStartTime("711100141-01", "M-F"));
		Assert.assertEquals("76", schedPage.getWakeCoolingTemp("711100141-01", "M-F"));
		Assert.assertEquals("66", schedPage.getWakeHeatingTemp("711100141-01", "M-F"));
		Assert.assertEquals("8:05 AM", schedPage.getLeaveStartTime("711100141-01", "M-F"));
		Assert.assertEquals("76", schedPage.getLeaveCoolingTemp("711100141-01", "M-F"));
		Assert.assertEquals("66", schedPage.getLeaveHeatingTemp("711100141-01", "M-F"));
		Assert.assertEquals("6:05 PM", schedPage.getReturnStartTime("711100141-01", "M-F"));
		Assert.assertEquals("76", schedPage.getReturnCoolingTemp("711100141-01", "M-F"));
		Assert.assertEquals("66", schedPage.getReturnHeatingTemp("711100141-01", "M-F"));
		Assert.assertEquals("10:05 PM", schedPage.getSleepStartTime("711100141-01", "M-F"));
		Assert.assertEquals("76", schedPage.getSleepCoolingTemp("711100141-01", "M-F"));
		Assert.assertEquals("66", schedPage.getSleepHeatingTemp("711100141-01", "M-F"));
		
		//verifying weekend values
		
		Assert.assertEquals("5:15 AM", schedPage.getWakeStartTime("711100141-01", "Sa/Su"));
		Assert.assertEquals("99", schedPage.getWakeCoolingTemp("711100141-01", "Sa/Su"));
		Assert.assertEquals("90", schedPage.getWakeHeatingTemp("711100141-01", "Sa/Su"));
		Assert.assertEquals("8:15 AM", schedPage.getLeaveStartTime("711100141-01", "Sa/Su"));
		Assert.assertEquals("50", schedPage.getLeaveCoolingTemp("711100141-01", "Sa/Su"));
		Assert.assertEquals("40", schedPage.getLeaveHeatingTemp("711100141-01", "Sa/Su"));
		Assert.assertEquals("6:15 PM", schedPage.getReturnStartTime("711100141-01", "Sa/Su"));
		Assert.assertEquals("99", schedPage.getReturnCoolingTemp("711100141-01", "Sa/Su"));
		Assert.assertEquals("90", schedPage.getReturnHeatingTemp("711100141-01", "Sa/Su"));
		Assert.assertEquals("10:15 PM", schedPage.getSleepStartTime("711100141-01", "Sa/Su"));
		Assert.assertEquals("50", schedPage.getSleepCoolingTemp("711100141-01", "Sa/Su"));
		Assert.assertEquals("40", schedPage.getSleepHeatingTemp("711100141-01", "Sa/Su"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Customer side <br>
	 * then to the Schedule page of ExpressStat device 711200141, 
	 * and Edits & Saves the schedule values and name
	 * 
	 */
	@Test	 
	public void editSaveTstat214Sched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711200141");
		stars.clickLinkByThermostatName("711200141", "Schedules");
	
		common.clickButtonBySpanTextWithElementWait("Create");
		schedPage.clickRadioButtonScheduleType("ALL");
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Next");
		// enter Sched name, then Save
		schedPage.inputScheduleName("Create Schedule", "711200141"); 
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertEquals("The schedule 711200141 has been saved.",common.getYukonText("The schedule 711200141 has been saved."));
		
		schedPage.editTstatSchedule("711200141");
	
		//editing the schedule values
		
		schedPage.inputWakeStartTime("Edit Schedule - 711200141", "All", "05:00 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711200141", "All", "8:00 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711200141", "All", "6:00 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711200141", "All", "10:00 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711200141", "All", "84");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711200141", "All", "84");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711200141", "All", "84");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711200141", "All", "84");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711200141", "All", "78");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711200141", "All", "78");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711200141", "All", "78");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711200141", "All", "78");
		//when entering a string using a program, had a difficult time saving the edited values, however it saves the correct values when inputing the values twice
		//now entering the values twice
		schedPage.editTstatSchedule("711200141");
		schedPage.inputWakeStartTime("Edit Schedule - 711200141", "All", "05:00 AM");
		schedPage.inputLeaveStartTime("Edit Schedule - 711200141", "All", "8:00 AM");
		schedPage.inputReturnStartTime("Edit Schedule - 711200141", "All", "6:00 PM");
		schedPage.inputSleepStartTime("Edit Schedule - 711200141", "All", "10:00 PM");
		schedPage.inputWakeCoolingTemp("Edit Schedule - 711200141", "All", "84");
		schedPage.inputLeaveCoolingTemp("Edit Schedule - 711200141", "All", "84");
		schedPage.inputReturnCoolingTemp("Edit Schedule - 711200141", "All", "84");
		schedPage.inputSleepCoolingTemp("Edit Schedule - 711200141", "All", "84");
		schedPage.inputWakeHeatingTemp("Edit Schedule - 711200141", "All", "78");
		schedPage.inputLeaveHeatingTemp("Edit Schedule - 711200141", "All", "78");
		schedPage.inputReturnHeatingTemp("Edit Schedule - 711200141", "All", "78");
		schedPage.inputSleepHeatingTemp("Edit Schedule - 711200141", "All", "78");
		
		//editing the schedule name and saving
		schedPage.inputScheduleName("Edit Schedule - 711200141", "Sched01-214");		
		schedPage.clickPopupButtonBySpanText("Edit Schedule - 711200141", "Save");
		Assert.assertEquals("The schedule Sched01-214 has been saved.",common.getYukonText("The schedule Sched01-214 has been saved."));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Customer side <br>
	 * then to the Schedule page of ExpressStat device 711200141, 
	 * and verifies the edited schedule values
	 */
	@Test	 
	public void verifyTstat214AfterSave() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711200141");
		stars.clickLinkByThermostatName("711200141", "Schedules");

		//verifying schedule values
		
		Assert.assertEquals("5:00 AM", schedPage.getWakeStartTime("Sched01-214", "All"));
		Assert.assertEquals("84", schedPage.getWakeCoolingTemp("Sched01-214", "All"));
		Assert.assertEquals("78", schedPage.getWakeHeatingTemp("Sched01-214", "All"));
		Assert.assertEquals("8:00 AM", schedPage.getLeaveStartTime("Sched01-214", "All"));
		Assert.assertEquals("84", schedPage.getLeaveCoolingTemp("Sched01-214", "All"));
		Assert.assertEquals("78", schedPage.getLeaveHeatingTemp("Sched01-214", "All"));
		Assert.assertEquals("6:00 PM", schedPage.getReturnStartTime("Sched01-214", "All"));
		Assert.assertEquals("84", schedPage.getReturnCoolingTemp("Sched01-214", "All"));
		Assert.assertEquals("78", schedPage.getReturnHeatingTemp("Sched01-214", "All"));
		Assert.assertEquals("10:00 PM", schedPage.getSleepStartTime("Sched01-214", "All"));
		Assert.assertEquals("84", schedPage.getSleepCoolingTemp("Sched01-214", "All"));
		Assert.assertEquals("78", schedPage.getSleepHeatingTemp("Sched01-214", "All"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Customer side <br>
	 * then to the Saved Schedule page of device 711200141, 
	 * and sends the schedule (Sched01-214) to the thermostat 711200141
	 */
	@Test	 
	public void sendSchedule214() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711200141");
		stars.clickLinkByThermostatName("711200141", "Schedules");
		
		// Nav to Hardware, then Tstat Schedule page for device 711200141
		schedPage.clickSendNowTstatSchedule("Sched01-214");
		schedPage.clickPopupButtonBySpanText("Send Schedule", "OK");
		Assert.assertEquals("The schedule Sched01-214 has been sent. It may take a few minutes before the thermostat receives it.",
				common.getYukonText("The schedule Sched01-214 has been sent."));	//has trouble with multi-sentence lines, but this works
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Customer side <br>
	 * then to the Saved Schedule page of account 71000014, 
	 * and copies the schedule
	 */
	@Test	 
	public void copySchedule214() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711200141");
		stars.clickLinkByThermostatName("711200141", "Schedules");
		//copying the schedule
		schedPage.clickCopyTstatSchedule("Sched01-214");
		schedPage.clickPopupButtonBySpanText("Create Schedule", "Save");
		Assert.assertEquals("The schedule Copy of Sched01-214 has been saved.", common.getYukonText("The schedule Copy of Sched01-214 has been saved."));
		Assert.assertEquals(true,common.isTextPresent("Copy of Sched01-214"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Customer side <br>
	 * then to the Saved Schedule page of device 71000014, and deletes the Sched01-114
	 */
	@Test	 
	public void deleteTstat114Sched() {
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		ThermostatSchedulePageSolvent schedPage = new ThermostatSchedulePageSolvent();
		
		//  navigate to 1st device  Schedule  page
		common.clickLinkByNameWithoutPageLoadWait("711100141");
		stars.clickLinkByThermostatName("711100141", "Schedules");

		//deleting the schedule
		schedPage.editTstatSchedule("Sched01-114");
		schedPage.clickPopupButtonBySpanText("Edit Schedule - Sched01-114", "Delete");
		schedPage.clickPopupButtonBySpanText("Confirm Delete", "OK");
		Assert.assertEquals("Schedule \"Sched01-114\" Deleted.", common.getYukonText("Schedule \"Sched01-114\" Deleted."));
		common.end();
	}
	@Test	 
	public void verifyManualAdjust() {
		start();
		CommonSolvent common = new CommonSolvent();
		ThermostatManualPageSolvent manualPage = new ThermostatManualPageSolvent();
		new LoginLogoutSolvent().cannonLogin("71000013", "71000013");
		
		common.clickLinkByNameWithoutPageLoadWait("711100131");
		common.clickLinkByName("Manual");   //  yuk-9102  ****
		Assert.assertEquals("Thermostat - Manual\n 711100131", common.getYukonText("Thermostat - Manual"));

		common.clickLinkByNameWithoutPageLoadWait("edit");
		common.enterInputText("/spring/stars/consumer/thermostat/saveLabel","deviceLabel","711100131");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals("Thermostat - Manual\n 711100131", common.getYukonText("Thermostat - Manual"));

		common.clickLinkByNameWithoutPageLoadWait("edit");
		common.enterInputText("/spring/stars/consumer/thermostat/saveLabel","deviceLabel", "711100131");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals("Thermostat - Manual\n 711100131", common.getYukonText("Thermostat - Manual"));

		manualPage.selectModeOrFanType("MODE","Cool");
		manualPage.selectModeOrFanType("FAN","On");
		common.selectCheckBox("Hold");
		common.clickButtonByName("Send");   //  yuk-9102  ****
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("The manual option has been sent to thermostat 711100131. It may take a few minutes before the thermostat receives it.",
				common.getYukonText("The manual option has been sent to thermostat 711100131."));
		common.clickButtonByName("Run Program");   //  yuk-9102  ****
		Assert.assertEquals("The program command has been sent to thermostat 711100131. It may take a few minutes before the thermostat receives it.",
				common.getYukonText("The program command has been sent to thermostat 711100131."));
		Assert.assertEquals("Thermostat - Schedules\n  711100131", common.getYukonText("Thermostat - Schedules"));
		common.end();
	}
	/**
	 * TODO add more to this test
	 */
	@Test	 
	public void verifyHistory() {
		start();
		CommonSolvent common = new CommonSolvent();
		new LoginLogoutSolvent().cannonLogin("71000013", "71000013");
		
		common.clickLinkByNameWithoutPageLoadWait("711100131");
		common.clickLinkByName("History");   //  yuk-9102  ****
		Assert.assertEquals(false, common.isTextPresent("No command history available."));

		common.end();
	}
}
