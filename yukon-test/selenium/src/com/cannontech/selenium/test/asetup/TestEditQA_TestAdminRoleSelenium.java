package com.cannontech.selenium.test.asetup;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;

/**
 * This class edits the Role Properties for the QA_Test Admin Grp
 * @author kevin.krile
 */
public class TestEditQA_TestAdminRoleSelenium extends SolventSeleniumTestCase{
	
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
	 * Edits or adds the role properties for the login group QA_Test Admin Group
	 */
	@Test
	public void editQA_TestAdminGroupRoleProperties(){
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		Assert.assertEquals("System Administration: User/Group Editor", common.getPageTitle());
		common.clickLinkByName("QA_Test Admin Grp");
		Assert.assertEquals("System Administration: Group Info (QA_Test Admin Grp)", common.getPageTitle());
		
		//Adding Configuration property
	    common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Configuration");
	    common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Configuration)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Editing the Administrator property
		common.clickLinkByName("Administrator");
		Assert.assertEquals("System Administration: Role Editor (Administrator)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_SUPER_USER]", "FALSE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_CREATE_DELETE_ENERGY_COMPANY]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_DATABASE_MIGRATION]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_EDIT_CONFIG]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_EDIT_ENERGY_COMPANY]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_EVENT_LOGS]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_LM_USER_ASSIGN]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_MANAGE_MEMBERS]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_MEMBER_LOGIN_CNTRL]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_MEMBER_ROUTE_SELECT]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_MULTI_WAREHOUSE]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_VIEW_OPT_OUT_EVENTS]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//editing the Energy Company property
		
		widget.clickLinkByWidgetWithPageLoadWait("Roles", "Energy Company");
		Assert.assertEquals("System Administration: Role Editor (Energy Company)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[SINGLE_ENERGY_COMPANY]", "FALSE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_ALLOW_DESIGNATION_CODES]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[AUTOMATIC_CONFIGURATION]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_ALLOW_THERMOSTAT_SCHEDULE_7_DAY]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		common.end();
	}
}
