package com.cannontech.selenium.test.asetup;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;

/**
 * This class edits the Role Properties for the STARS Operator Grp
 * @author kevin.krile
 * edit 9/7/2011 - sjunod - add Web Client > Theme file > LocalizedXML
 */
public class TestEditOperatorsRoleSelenium extends SolventSeleniumTestCase{
	
    private final String addRoleFormAction = "groupEditor/addRole";
    private final String updateRoleFormAction = "roleEditor/update";
    
	/**
	 * This method logs in as DefaultCTI/$cti_default
	 */
	public void init(){
		start();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		loginlogoutsolvent.cannonLogin("DefaultCTI", "$cti_default");
	}
	
	/**
	 * Edits or adds the stars role properties for the login group STARS Operators Group
	 */
	@Test
	public void editStarsOperatorsGroupRoleProperties(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		Assert.assertEquals("System Administration: User/Group Editor", common.getPageTitle());
		common.clickLinkByName("STARS Operators Grp");
		Assert.assertEquals("System Administration: Group Info (STARS Operators Grp)", common.getPageTitle());
		
		//Adding Billing property
		common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Billing");
		common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Billing)", common.getPageTitle());
		common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding Commander property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Commander");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Commander)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Reporting properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Reporting");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Reporting)", common.getPageTitle());
        common.selectDropDownMenu(updateRoleFormAction, "values[CI_CURTAILMENT_REPORTS_GROUP]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[CAP_CONTROL_REPORTS_GROUP]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[LOAD_MANAGEMENT_REPORTS_GROUP]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding Trending property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Trending");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Trending)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Editing the Web Client properties
		common.clickLinkByName("Web Client");
		Assert.assertEquals("System Administration: Role Editor (Web Client)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[SUPPRESS_ERROR_PAGE_DETAILS]", "FALSE");
        common.selectDropDownMenu(updateRoleFormAction, "values[VIEW_ALARMS_AS_ALERTS]", "FALSE");
		common.enterInputText(updateRoleFormAction, "values[THEME_NAME]", "LocalizedXML");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Direct Loadcontrol properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Direct Loadcontrol");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Direct Loadcontrol)", common.getPageTitle());
		//common.selectDropDownMenuByName("3 Tier Direct Control:", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ALLOW_STOP_GEAR_ACCESS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[DEMAND_RESPONSE]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[IGNORE_PER_PAO_PERMISSIONS]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding CI Curtailment properties if it doesn't exist yet
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "CI Curtailment");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (CI Curtailment)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Editing the Consumer Info properties
		common.clickLinkByName("Consumer Info");
		Assert.assertEquals("System Administration: Role Editor (Consumer Info)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_OPT_OUT_TODAY_ONLY]", "FALSE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CONSUMER_INFO_ADMIN_FAQ]", "TRUE");
		common.enterInputText(updateRoleFormAction, "values[OPERATOR_CALL_NUMBER_AUTO_GEN]", "true");
		common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CREATE_LOGIN_FOR_ACCOUNT]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CONSUMER_INFO_METERING_CREATE]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY]", "TRUE");
		common.enterInputText(updateRoleFormAction, "values[OPERATOR_IMPORT_CUSTOMER_ACCOUNT]", "true");
		common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CONSUMER_INFO_METERING_INTERVAL_DATA]", "TRUE");
		common.enterInputText(updateRoleFormAction, "values[OPERATOR_OPT_OUT_PERIOD]", "1,2,3,4,5,6,7");
		common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_OPT_OUT_SURVEY_EDIT]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_ORDER_NUMBER_AUTO_GEN]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_SURVEY_EDIT]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_CONSUMER_INFO_WORK_ORDERS]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Device Actions properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Device Actions");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Device Actions)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[BULK_IMPORT_OPERATION]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[BULK_UPDATE_OPERATION]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Editing the Inventory property
		common.clickLinkByName("Inventory");
		Assert.assertEquals("System Administration: Role Editor (Inventory)", common.getPageTitle());
//		common.enterText("Allow Designation Codes:", "true");
		common.selectDropDownMenu(updateRoleFormAction, "values[ALLOW_MULTIPLE_WAREHOUSES]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[DEVICE_RECONFIG]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[PURCHASING_ACCESS]", "FALSE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding Odds for Control property
		common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Odds For Control");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Odds For Control)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		common.end();
	}
}