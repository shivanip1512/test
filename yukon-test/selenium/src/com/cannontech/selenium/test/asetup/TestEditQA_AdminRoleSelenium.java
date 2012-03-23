package com.cannontech.selenium.test.asetup;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;

/**
 * This class edits the Role Properties for the QA Admin Grp
 * @author kevin.krile
 */
public class TestEditQA_AdminRoleSelenium extends SolventSeleniumTestCase{
	
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
	 * Edits or adds the role properties for the login group QA Admin Grp
	 */
	@Test
	public void editQAAdminGroupRoleProperties(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		Assert.assertEquals("System Administration: User/Group Editor", common.getPageTitle());
		common.clickLinkByName("QA Admin Grp");
		Assert.assertEquals("System Administration: Group Info (QA Admin Grp)", common.getPageTitle());
		
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
		
		//Adding the Database Editor property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Database Editor");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Database Editor)", common.getPageTitle());
		common.enterInputText(updateRoleFormAction, "values[ALLOW_MEMBER_PROGRAMS]", "true");
        common.enterInputText(updateRoleFormAction, "values[TRANS_EXCLUSION]", "true");
        common.enterInputText(updateRoleFormAction, "values[DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV]", "FFFFFFFF");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Esubstation Editor property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Esubstation Editor");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Esubstation Editor)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[ESUB_EDITOR_ROLE_EXITS]", "TRUE");
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
		
		//Adding Tabular Data Console property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Tabular Data Console");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Tabular Data Console)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Trending property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Trending");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Trending)", common.getPageTitle());
        common.enterInputText(updateRoleFormAction, "values[SCAN_NOW_ENABLED]", "true");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Web Client properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Web Client");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Web Client)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[VIEW_ALARMS_AS_ALERTS]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding CapControl Settings property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Cap Control Settings");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Cap Control Settings)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[CAP_CONTROL_ACCESS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[ADD_COMMENTS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[CBC_ALLOW_OVUV]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[CBC_DATABASE_EDIT]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[FORCE_COMMENTS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[MODIFY_COMMENTS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[SHOW_CB_ADDINFO]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[SHOW_FLIP_COMMAND]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[SYSTEM_WIDE_CONTROLS]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Cap Bank Display property 
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Cap Bank Display");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Cap Bank Display)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Feeder Display property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Feeder Display");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Feeder Display)", common.getPageTitle());
		common.enterInputText(updateRoleFormAction, "values[FDR_THREE_PHASE]", "true");
		common.enterInputText(updateRoleFormAction, "values[FDR_VOLT]", "true");
		common.enterInputText(updateRoleFormAction, "values[FDR_WATT]", "true");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));	
		
		//Adding the Substation Display
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Substation Display");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Substation Display)", common.getPageTitle());
        common.enterInputText(updateRoleFormAction, "values[SUB_THREE_PHASE]", "true");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));	
		
		//Adding the Direct Loadcontrol properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Direct Loadcontrol");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Direct Loadcontrol)", common.getPageTitle());
		//common.selectDropDownMenuByName("3 Tier Direct Control:", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ALLOW_STOP_GEAR_ACCESS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[CONTROL_AREA_ATKU]", "FALSE");
        common.selectDropDownMenu(updateRoleFormAction, "values[CONTROL_AREA_PEAK_PROJECTION]", "FALSE");
        common.selectDropDownMenu(updateRoleFormAction, "values[LOAD_GROUP_LAST_ACTION]", "FALSE");
        common.selectDropDownMenu(updateRoleFormAction, "values[LOAD_GROUP_REDUCTION]", "FALSE");
        common.selectDropDownMenu(updateRoleFormAction, "values[PROGRAM_REDUCTION]", "FALSE");
        common.selectDropDownMenu(updateRoleFormAction, "values[DEMAND_RESPONSE]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding Configuration property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Configuration");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Configuration)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Administrator properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Administrator");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Administrator)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_SUPER_USER]", "TRUE");
		common.enterInputText(updateRoleFormAction, "values[ADMIN_AUTO_PROCESS_BATCH_COMMANDS]", "true");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_DATABASE_MIGRATION]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_EDIT_CONFIG]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_EVENT_LOGS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_LM_USER_ASSIGN]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_MULTISPEAK_SETUP]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_VIEW_BATCH_COMMANDS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_VIEW_OPT_OUT_EVENTS]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Device Actions properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Device Actions");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Device Actions)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[ADD_REMOVE_POINTS]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[ASSIGN_CONFIG]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[MASS_DELETE]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[SEND_READ_CONFIG]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Esubstation Drawings properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Esubstation Drawings");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Esubstation Drawings)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_ESUBSTATION_DRAWINGS_CONTROL]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[OPERATOR_ESUBSTATION_DRAWINGS_EDIT]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding or editing the Metering properties
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Metering");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Metering)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[CIS_DETAIL_TYPE]", "MULTISPEAK");
        common.selectDropDownMenu(updateRoleFormAction, "values[PHASE_DETECT]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[PORTER_RESPONSE_MONITORING]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[STATUS_POINT_MONITORING]", "TRUE");
        common.selectDropDownMenu(updateRoleFormAction, "values[VALIDATION_ENGINE]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding Scheduler property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Scheduler");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Scheduler)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		common.end();
	}
}
