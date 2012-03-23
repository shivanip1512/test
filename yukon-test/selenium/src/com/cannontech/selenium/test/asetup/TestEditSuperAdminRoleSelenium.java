package com.cannontech.selenium.test.asetup;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.ItemPickerSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;

/**
 * This class edits the Role Properties for the super Stars Admin Grp
 * @author kevin.krile
 */
public class TestEditSuperAdminRoleSelenium extends SolventSeleniumTestCase{

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
	 * Edits or adds the role properties for the login group super Stars Admin Group
	 */
	@Test
	public void editsuperStarsAdminGroupRoleProperties(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		Assert.assertEquals("System Administration: User/Group Editor", common.getPageTitle());
		common.clickLinkByName("super Stars Oper Grp");
		Assert.assertEquals("System Administration: Group Info (super Stars Oper Grp)", common.getPageTitle());
		
		//Adding the Administrator property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Administrator");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals(true, common.isTextPresent("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Administrator)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_SUPER_USER]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_DATABASE_MIGRATION]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_LM_USER_ASSIGN]", "TRUE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
		
		//Adding the Energy Company property
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", "Energy Company");
        common.clickFormButtonByButtonId(addRoleFormAction, "addButton");
		Assert.assertEquals(true, common.isTextPresent("Group Updated Successfully"));
		Assert.assertEquals("System Administration: Role Editor (Energy Company)", common.getPageTitle());
		common.selectDropDownMenu(updateRoleFormAction, "values[ADMIN_ALLOW_DESIGNATION_CODES]", "TRUE");
		common.selectDropDownMenu(updateRoleFormAction, "values[SINGLE_ENERGY_COMPANY]", "FALSE");
        common.clickFormButton(updateRoleFormAction, "save");
		Assert.assertEquals(true, common.isTextPresent("Login Group Role Updated Successfully"));
		common.end();
	}
	
	/**
	 * Edits or adds the role properties for the login group super Stars Admin Group
	 */
	@Test
	public void addSuperstarsopUserGroup(){
		init();
		CommonSolvent common = new CommonSolvent();
		ItemPickerSolvent itemPickerSolvent = new ItemPickerSolvent();
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		
		PopupMenuSolvent selectGroupPicker = PickerFactory.createPopupMenuSolvent("group", PickerType.Embedded);
		selectGroupPicker.enterFilterText("super");
		common.clickLinkByName("superstarsop");
		common.clickLinkByName("Login Groups");

		PopupMenuSolvent loginGroupPopup = PickerFactory.createPopupMenuSolvent("loginGroup", PickerType.MultiSelect);
		itemPickerSolvent.clickPickerButtonAjax("loginGroup");
        loginGroupPopup.clickMenuItem("STARS Operators Grp");
        loginGroupPopup.clickButton("OK");
        
		Assert.assertEquals(true, common.isTextPresent("User Updated Successfully"));
		common.end();
	}
}