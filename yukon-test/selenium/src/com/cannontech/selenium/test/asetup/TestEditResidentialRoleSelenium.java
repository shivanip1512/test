package com.cannontech.selenium.test.asetup;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;

/**
 * This class edits the Role Properties for the STARS Residential Customers Grp
 * @author kevin.krile
 * edit 9/7/2011 - sjunod - add Web Client > Theme file > LocalizedXML
 */
public class TestEditResidentialRoleSelenium extends SolventSeleniumTestCase{
	
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
	 * Edits or adds the stars role properties for the login group STARS Residential Customers Group
	 */
	@Test
	public void editStarsResidentialCustomerGroupRoleProperties(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		Assert.assertEquals("System Administration: User/Group Editor", common.getPageTitle());
		common.clickLinkByName("STARS Residential Customers Grp");
		Assert.assertEquals("System Administration: Group Info (STARS Residential Customers Grp)", common.getPageTitle());
		
		//Adding the Trending property
	    common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Trending");
	    common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Trending)", common.getPageTitle());
		common.enterInputText(updateRoleFormAction, "values[SCAN_NOW_ENABLED]", "true");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Editing the Web Client property
		common.clickLinkByName("Web Client");
		Assert.assertEquals("System Administration: Role Editor (Web Client)", common.getPageTitle());
		common.enterInputText(updateRoleFormAction, "values[HEADER_LOGO]", "yukon/DemoHeaderCES.gif");
		common.enterInputText(updateRoleFormAction, "values[HOME_URL]", "/spring/stars/consumer/general");
		common.enterInputText(updateRoleFormAction, "values[THEME_NAME]", "LocalizedXML");
		common.selectDropDownMenu(updateRoleFormAction, "values[VIEW_ALARMS_AS_ALERTS]", "FALSE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Editing the Residential Customer property
		common.clickLinkByName("Residential Customer");
		Assert.assertEquals("System Administration: Role Editor (Residential Customer)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[RESIDENTIAL_CONTACTS_ACCESS]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[RESIDENTIAL_CREATE_LOGIN_FOR_ACCOUNT]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[RESIDENTIAL_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[RESIDENTIAL_ENROLLMENT_PER_DEVICE]", "TRUE");
		common.enterInputText(updateRoleFormAction, "values[RESIDENTIAL_OPT_OUT_LIMITS]", "[{start:1;stop:12;limit:5}]");
		common.enterInputText(updateRoleFormAction, "values[RESIDENTIAL_OPT_OUT_PERIOD]", "1,2,3,4,5,6,7");
		common.selectDropDownMenu(updateRoleFormAction, "values[RESIDENTIAL_OPT_OUT_TODAY_ONLY]", "FALSE");
		common.selectDropDownMenu(updateRoleFormAction, "values[RESIDENTIAL_CONSUMER_INFO_THERMOSTATS_ALL]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding Configuration property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Configuration");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Configuration)", common.getPageTitle());
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		common.end();
	}
}
