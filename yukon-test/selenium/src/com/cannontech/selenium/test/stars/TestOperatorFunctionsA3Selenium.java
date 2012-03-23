package com.cannontech.selenium.test.stars;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.EnrollmentTableSolvent;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;
import com.cannontech.selenium.solvents.stars.OptOutTableSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
/**
 * Main idea of this class is to verify additional Operator functionality in more depth 
 *   especially Tstat Scheduled
 *   Specific Opt Out cases
 *     
 *  follows verification for test cases  3.2.1.5
 * @author steve.junod
 */
public class TestOperatorFunctionsA3Selenium extends SolventSeleniumTestCase {
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
	}
    // TODO we should look and see if this is generic enough to use in the common servlet.
    private static final String OPT_OUT_CONFIRMATION_XPATH = "//div[contains(@class, 'popUpDiv')]";
    /**
	 * This method enrolls 2 existing devices (711100151 & 711200151) on account 71000015
	 * then Opt Out 1st device
	 */
	@Test
	public void setupTstatDevices() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		
		//  get to acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
		
		// First, enroll devices (711100151 & 711200151) in a Program
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Operator: Enrollment (71000015)", common.getPageTitle());

	    PopupMenuSolvent programPicker = PickerFactory.createPopupMenuSolvent("program", PickerType.SingleSelect);
	    common.clickButtonByTitle("Add");
	    programPicker.clickMenuItem("LdPgm01-2Gear");

		common.selectDropDownMenu("operator/enrollment", "loadGroupId", "LdGrp01-Expresscom");
		common.selectCheckBox("711100151");          
		common.selectCheckBox("711200151");
		common.selectCheckBox("711100152");          

		//   QA-554
		common.selectDropDownMenu("enrollment/confirmSave", "inventoryEnrollments[0].relay", "1");
		common.selectDropDownMenu("enrollment/confirmSave", "inventoryEnrollments[1].relay", "2");
		common.selectDropDownMenu("enrollment/confirmSave", "inventoryEnrollments[2].relay", "3");
		
		common.clickButtonBySpanTextWithElementWait("OK");
		Assert.assertEquals("Add Enrollment", common.getYukonText("Add Enrollment"));
		common.clickButtonBySpanText("OK");
		Assert.assertEquals("Successfully enrolled in LdPgm01-2Gear.", common.getYukonText("Successfully enrolled in LdPgm01-2Gear."));
		
		// get to Opt Out page, Opt Out 1st device
		common.clickLinkByName("Opt Out");
		
		//  but 1st verify bad date throws error
		common.enterInputText("optOut/deviceSelection","startDate", "");
		common.clickButtonByName("Opt Out");
		Assert.assertEquals("Error found, check fields.", common.getYukonText("Error found, check fields."));
		Assert.assertEquals("Invalid start date.", common.getYukonText("Invalid start date."));
		
		common.clickLinkByName("Opt Out");
		common.clickButtonByName("Opt Out");
		common.selectCheckBox("711100151");
		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000015)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100151"));
		common.end();
	}
	/**
	 * This method navigates to the Opt Out page in the Operator side <br>
	 * verify 711100151 device is currently Active, Opt Out 711200151 & 711100152
	 * verify all Active & not available for another Today Opt Out
	 */
	@Test
	public void verifyTstatsNotAvail() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();

		//  get to acct page 
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		//   get to Opt Out page 
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000015)", common.getPageTitle());
		
		// verify 1st device shows 'currently opted out' on Device selection page
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000015)", common.getPageTitle());
		Assert.assertEquals("This device is currently opted out.", common.getYukonText("This device is currently opted out."));

		//  select 2nd and 3rd devices & Opt Out
		common.selectCheckBox("711200151");
		common.selectCheckBox("711100152");
		common.clickButtonBySpanText("Opt Out");

		//  verify all devices  Active
		Assert.assertEquals("Operator: Opt Out (71000015)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100151"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711200151"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100152"));
		
		//  try to Opt Out again while all devices  Active - verify err msg
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Error found, check fields.", common.getYukonText("Error found, check fields."));
		Assert.assertEquals("All of the devices are already opted out for today.", common.getYukonText("All of the devices are already opted out for today."));
		
		//  yuk-9428  verify data visible after above test
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100151"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711200151"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100152"));

		Assert.assertEquals("1", optout.getOperatorOptOutUsedValue("711100151"));
		Assert.assertEquals("4", optout.getOperatorOptOutRemainingValue("711100151"));
		Assert.assertEquals("1", optout.getOperatorOptOutUsedValue("711200151"));
		Assert.assertEquals("4", optout.getOperatorOptOutRemainingValue("711200151"));
		Assert.assertEquals("1", optout.getOperatorOptOutUsedValue("711100152"));
		Assert.assertEquals("4", optout.getOperatorOptOutRemainingValue("711100152"));
		
		//  yuk-9428  also test some buttons, verifying correct result
		//  add extra Opt Out, then verify value
		optout.addExtraOptOut("711100152");
		Assert.assertEquals("Are you sure you want to add an extra opt out for 711100152?", common.getYukonText("Are you sure you want to add an extra opt out for 711100152?"));

		optout.confirmOKOrCancel("add an extra opt out for 711100152", "submit");
		Assert.assertEquals("Opt Out Added", common.getYukonText("Opt Out Added"));
		Assert.assertEquals("1", optout.getOperatorOptOutUsedValue("711100152"));
		Assert.assertEquals("5", optout.getOperatorOptOutRemainingValue("711100152"));
		
		//  yuk-9428  also test some buttons, verifying correct result
		//  decrement  Opt Out Remaining  value, then verify value
		optout.decrementOptOut("711100152");
		optout.confirmOKOrCancel("remove an opt out for 711100152", "submit");
		Assert.assertEquals("Opt Out Allowance Reduced", common.getYukonText("Opt Out Allowance Reduced"));
		Assert.assertEquals("1", optout.getOperatorOptOutUsedValue("711100152"));
		Assert.assertEquals("4", optout.getOperatorOptOutRemainingValue("711100152"));
		
		//  yuk-9428  also test some buttons, verifying correct result
		//  Opt Out  Resend  action
		optout.resendOptOut("Current Opt Outs", "711200151");
		optout.confirmOKOrCancel("resend an opt out command for 711200151", "submit");
		Assert.assertEquals("Opt Out Command Resent", common.getYukonText("Opt Out Command Resent"));
		common.end();
	}
	/**
	 * This method navigates to the Opt Out page in the Operator side <br>
	 * schedule Opt Out for all devices while they all are Active Opt Outs
	 * verify Active and Scheduled  status
	 */
	@Test
	public void schedActiveTstats() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();
		
		//  get to acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// get to Opt Out page  
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000015)", common.getPageTitle());

		//  Schedule OptOut, start tomorrow, for 2 days, on all devices		
		common.enterInputText("optOut/deviceSelection","startDate", new EventsByTypeSolvent().returnDate(1));
	
		common.selectDropDownMenu("optOut/deviceSelection","durationInDays", "2 Day");
		common.clickButtonByNameWithPageLoadWait("Opt Out");
		Assert.assertEquals("Operator: Device Selection (71000015)", common.getPageTitle());
		common.selectCheckBox("711100151");
		common.selectCheckBox("711200151");
		common.selectCheckBox("711100152");

		common.clickButtonBySpanText("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000015)", common.getPageTitle());
		Assert.assertEquals("Opt Out Successful", common.getYukonText("Opt Out Successful"));
						
		//  verify Active & Scheduled status on all devices
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100151"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711200151"));
		Assert.assertEquals("Active", optout.getCurrntOptOutStatusByDevice("711100152"));
		//   QA-557  can't verify status of device listed twice
		//Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100151"));
		//Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711200151"));
		//Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100152"));
		common.end();
	}
	/**
	 * This method navigates to the Opt Out page in the Operator side <br>
	 * account  71000015
	 * cancels all Active & Scheduled Opt Outs
	 */
	@Test
	public void cancelAllTstatOptOut() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		OptOutTableSolvent optout = new OptOutTableSolvent();

		//  get to acct page
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "71000015");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (71000015)", common.getPageTitle());
			
		// get to Opt Out page
		common.clickLinkByName("Opt Out");
		Assert.assertEquals("Operator: Opt Out (71000015)", common.getPageTitle());

		//  cancel Scheduled Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100151");
        common.clickOkOnConfirmation(OPT_OUT_CONFIRMATION_XPATH);
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		
		//  cancel Scheduled Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711200151");
        common.clickOkOnConfirmation(OPT_OUT_CONFIRMATION_XPATH);
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		
		//  cancel Scheduled Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100152");
        common.clickOkOnConfirmation(OPT_OUT_CONFIRMATION_XPATH);
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		
		//  verify only  Scheduled  Opt Outs are left
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100151"));
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711200151"));
		Assert.assertEquals("Scheduled", optout.getCurrntOptOutStatusByDevice("711100152"));

		//  cancel Scheduled Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100151");
		common.clickOkOnConfirmation(OPT_OUT_CONFIRMATION_XPATH);
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		
		//  cancel Scheduled Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711200151");
		common.clickOkOnConfirmation(OPT_OUT_CONFIRMATION_XPATH);
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
				
		//  cancel Scheduled Opt Out
		optout.removeDeviceByTableAndDeviceName("Current Opt Outs", "711100152");
        common.clickOkOnConfirmation(OPT_OUT_CONFIRMATION_XPATH);
		Assert.assertEquals("Opt Out Canceled", common.getYukonText("Opt Out Canceled"));
		Assert.assertEquals("There are no active or scheduled opt outs.", common.getYukonText("There are no active or scheduled opt outs."));
		common.end();
	}
}
