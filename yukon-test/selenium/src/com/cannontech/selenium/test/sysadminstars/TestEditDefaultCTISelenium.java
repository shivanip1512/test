package com.cannontech.selenium.test.sysadminstars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.HardwarePageSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
/**
 * This Class edits the different properties of the DefaultCTI user
 * @author Kevin Krile	
 */
public class TestEditDefaultCTISelenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as starsop11/starsop11
	 */
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("superstarsop", "superstarsop");
	}
	/**
	 * This test logs in as superstarsop and changes the authentication for DefaultCTI
	 */
	@Test
	public void editAuth(){
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] auth = {"LDAP", "Active Directory", "No Login", "Hashed"};
		
		for(int i=0;i<auth.length;i++){
			login.cannonLogin("superstarsop", "superstarsop");
			common.clickLinkByName("System Administration");
			common.clickLinkByName("User/Group");
			common.clickLinkByName("DefaultCTI");
			common.clickButtonByTitleWithPageLoadWait("Edit");
			common.selectDropDownMenu("edit", "authType", auth[i]);
			common.clickFormButton("edit", "update");
			Assert.assertEquals("User Updated Successfully", common.getYukonText("User Updated Successfully"));
			Assert.assertEquals(true, common.isTextPresent(auth[i]));
			login.yukonLogout();
			login.cannonLogin("DefaultCTI", "$cti_default");
			Assert.assertEquals("* Invalid username/password", common.getYukonText("* Invalid username/password"));
			login.cannonLogin("superstarsop", "superstarsop");
			common.clickLinkByName("System Administration");
			common.clickLinkByName("User/Group");
			common.clickLinkByName("DefaultCTI");
			common.clickButtonByTitleWithPageLoadWait("Edit");
			common.selectDropDownMenu("edit", "authType", "Normal");
			common.clickFormButton("edit", "update");
			Assert.assertEquals("User Updated Successfully", common.getYukonText("User Updated Successfully"));
			login.yukonLogout();
			login.cannonLogin("DefaultCTI", "$cti_default");
			Assert.assertEquals("System Administration link not found", true, common.isLinkPresent("System Administration"));
			login.yukonLogout();
		}
		common.end();
	}
	/**
	 * This test logs in as superstarsop and changes the password for DefaultCTI
	 */
	@Test
	public void editPassword(){
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		
		login.cannonLogin("superstarsop", "superstarsop");
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		common.clickLinkByName("DefaultCTI");
		common.clickButtonByAttribute("id", "changePasswordButton");
		common.enterInputText("changePassword", "password", "$cti_defaultEdit");
		common.enterInputText("changePassword", "confirmPassword", "$cti_defaultEdit");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals("Password Updated Successfully", common.getYukonText("Password Updated Successfully"));
		login.yukonLogout();
		login.cannonLogin("DefaultCTI", "$cti_default");
		Assert.assertEquals("* Invalid username/password", common.getYukonText("* Invalid username/password"));
		login.cannonLogin("DefaultCTI", "$cti_defaultEdit");
		Assert.assertEquals("System Administration link not found", true, common.isLinkPresent("System Administration"));
		login.yukonLogout();
		login.cannonLogin("superstarsop", "superstarsop");
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		common.clickLinkByName("DefaultCTI");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.selectDropDownMenu("edit", "authType", "Hashed");
		common.clickFormButton("edit", "update");
		Assert.assertEquals("User Updated Successfully", common.getYukonText("User Updated Successfully"));
		Assert.assertEquals(true, common.isTextPresent("Hashed"));
		login.yukonLogout();
		login.cannonLogin("DefaultCTI", "$cti_defaultEdit");
		Assert.assertEquals("* Invalid username/password", common.getYukonText("* Invalid username/password"));
		login.cannonLogin("superstarsop", "superstarsop");
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		common.clickLinkByName("DefaultCTI");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.selectDropDownMenu("edit", "authType", "Normal");
		common.clickFormButton("edit", "update");
		Assert.assertEquals("User Updated Successfully", common.getYukonText("User Updated Successfully"));
		common.clickButtonByAttribute("id", "changePasswordButton");
		common.enterInputText("changePassword", "password", "$cti_default");
		common.enterInputText("changePassword", "confirmPassword", "$cti_default");
		common.clickButtonBySpanText("Save");
		login.yukonLogout();
		login.cannonLogin("DefaultCTI", "$cti_default");
		Assert.assertEquals("System Administration link not found", true, common.isLinkPresent("System Administration"));
		login.yukonLogout();
		common.end();
	}
	/**
	 * This test logs in as superstarsop and changes the password, 
	 * only to have it fail to an expected error
	 */
	@Test
	public void editPasswordError(){
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] users = {"DefaultCTI", "71000000"};
		String[] password = {"Pass1", "", "Pass1", "ThisQAgroupisthebestgroupIhaveeverbeenapartofandIamalsoproudofthem"};
		String[] confirm = {"", "Conf1", "Conf1", "ThisQAgroupisthebestgroupIhaveeverbeenapartofandIamalsoproudofthem"};
		String[] error = {"Password fields do not match", "Exceeds maximum length of 64."};
		for(int i=0;i<password.length;i++){
			login.cannonLogin("superstarsop", "superstarsop");
			common.clickLinkByName("System Administration");
			common.clickLinkByName("User/Group");
			common.clickLinkByName(users[i/3]);
			common.clickButtonByAttribute("id", "changePasswordButton");
			common.enterInputText("changePassword", "password", password[i]);
			common.enterInputText("changePassword", "confirmPassword", confirm[i]);
			common.clickButtonBySpanText("Save");
			Assert.assertEquals("Errors found text not found", true, common.isTextPresent("Errors found, check fields."));
			Assert.assertEquals("Password fields error text not found", true, common.isTextPresent(error[i/3]));
			common.clickButtonByAttribute("id", "cancelChangePassword");
			login.yukonLogout();
		}
		common.end();
	}
	/**
	 * This test logs in as superstarsop and adds permissions to the user
	 */
	@Test
	public void addPermissions(){
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		String[] tableHeader = {"User Permission Editor: Load Management Visibility", "User Permission Editor: Cap Control Object Visibility"};
		String[] permissions1 = {"LdGrp01-Expresscom", "SubArea-103"};
		String[] permissions2 = {"LdGrp02-Expresscom", "SubArea-101"};
		//TODO change this when possible - right now (1/3/12) each popup contains a randomly generated number in the div
		String[] onclick = {"lmDevicePicker.okPressed", "capControlAreaPicker.okPressed"};
		login.cannonLogin("superstarsop", "superstarsop");
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		common.clickLinkByName("DefaultCTI");
		common.clickLinkByName("Permissions");
		for(int i=0;i<tableHeader.length;i++){
			hardware.clickAddByTableHeader(tableHeader[i]);
			common.clickLinkByNameWithoutPageLoadWait(permissions1[i]);
			common.clickLinkByNameWithoutPageLoadWait(permissions2[i]);
			common.clickButtonByAttribute("onclick", onclick[i]);
			Assert.assertEquals("Couldn't find selected permission 1", true, common.isTextPresent(permissions1[i]));
			Assert.assertEquals("Couldn't find selected permission 2", true, common.isTextPresent(permissions2[i]));
			common.clickButtonBySpanTextWithElementWait("Save");
		}
		common.end();
	}
	/**
	 * This test logs in as superstarsop and removes permissions of the user
	 */
	@Test
	public void removePermissions(){
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] tableHeader = {"User Permission Editor: Load Management Visibility", "User Permission Editor: Cap Control Object Visibility"};
		String[] permissions1 = {"LdGrp01-Expresscom", "SubArea-103"};
		String[] permissions2 = {"LdGrp02-Expresscom", "SubArea-101"};
		login.cannonLogin("superstarsop", "superstarsop");
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		common.clickLinkByName("DefaultCTI");
		common.clickLinkByName("Permissions");
		for(int i=0;i<tableHeader.length;i++){
			common.clickButtonByTitle("Remove this PAO");
			common.clickButtonByTitle("Remove this PAO");
			common.assertEqualsTextIsNotPresent(permissions1[i]);
			common.assertEqualsTextIsNotPresent(permissions2[i]);
			common.clickButtonBySpanTextWithElementWait("Save");
			Assert.assertEquals("Success message not found", true, common.isTextPresent("Save Successful"));
		}
		common.end();
	}
	/**
	 * This test logs in as superstarsop and adds a login group to the user
	 */
	@Test
	public void addLoginGroups(){
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		PopupMenuSolvent loginGroupPicker = PickerFactory.createPopupMenuSolvent("loginGroup", PickerType.SingleSelect);
				
		login.cannonLogin("superstarsop", "superstarsop");
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		common.clickLinkByName("DefaultCTI");
		
		//add 1 group successfully
		common.clickLinkByName("Login Groups");
		common.clickButtonBySpanTextWithElementWait("Add");
		loginGroupPicker.clickMenuItem("STARS Operators Grp");
		common.clickButtonByNameWithPageLoadWait("OK");
		Assert.assertEquals("Link not found", true, common.isLinkPresent("STARS Operators Grp"));
		Assert.assertEquals("Success message not found", true, common.isTextPresent("User Updated Successfully"));
	
		//add 2 groups successfully
		common.clickLinkByName("Login Groups");
		common.clickButtonBySpanTextWithElementWait("Add");
		loginGroupPicker.clickMenuItem("Esub Users Grp");
		loginGroupPicker.clickMenuItem("QA_Test Admin Grp");
		common.clickButtonByNameWithPageLoadWait("OK");
		Assert.assertEquals("Link not found", true, common.isLinkPresent("Esub Users Grp"));
		Assert.assertEquals("Link not found", true, common.isLinkPresent("QA_Test Admin Grp"));
		Assert.assertEquals("Success message not found", true, common.isTextPresent("User Updated Successfully"));
		
		//add 1 group unsuccessfully
		common.clickLinkByName("Login Groups");
		common.clickButtonBySpanTextWithElementWait("Add");
		loginGroupPicker.clickMenuItem("Esub Operators Grp");
		common.clickButtonByNameWithPageLoadWait("OK");
		common.assertEqualsLinkTextNotPresent("Esub Operators Grp");
		Assert.assertEquals("Error message not found", true, common.isTextPresent("Could not add user to group(s). The groups this user is already a member of and the new group(s) " +
				"[Esub Operators Grp] have conflicting roles."));
		common.end();
	}
	/**
	 * This test logs in as superstarsop and removes 
	 * the login groups recently added to the user
	 */
	@Test
	public void removeLoginGroups(){
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		String[] loginGroups = {"STARS Operators Grp", "Esub Users Grp", "QA_Test Admin Grp"};	
		
		login.cannonLogin("superstarsop", "superstarsop");
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		common.clickLinkByName("DefaultCTI");
		for(String group : loginGroups){
			common.clickLinkByName("Login Groups");
			stars.removeUserOrGroup(group);
			Assert.assertEquals("Link incorrectly found", false, common.isLinkPresent(group));
			Assert.assertEquals("Success message not found", true, common.isTextPresent("User Updated Successfully"));
		}
		common.end();
	}
}
