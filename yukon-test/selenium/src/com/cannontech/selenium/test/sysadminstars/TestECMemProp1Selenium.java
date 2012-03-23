package com.cannontech.selenium.test.sysadminstars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.EnergyCompanySolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
/**
 * This Class further tests the properties of Member Energy Companies
 * @author Kevin Krile	
 */
public class TestECMemProp1Selenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as @param username/password
	 */
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("superstarsop", "superstarsop");
	}
	/**
	 * Logs into the member EC from the parent's login and verifies the member EC's info
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void loginToMemECFromParentEC(){
		start();
		CommonSolvent common = new CommonSolvent();
		EnergyCompanySolvent energy= new EnergyCompanySolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		String[] name = getParamStrings("Member Company Name");
		String[] primary = getParamStrings("Member Primary Login");
		String[] parent = getParamStrings("Parent Company Name");
		String[] login = getParamStrings("EC Login");
		
		//using the division and modulus functions here to deal with the length of each string array.  When an array has i/2, it means that instead of putting 
		//a,a,b,b I simply put a,b and have i/2 in the calling, so as i goes from 0,1,2,3 i/2 is 0,0,1,1.
		for(int i=0;i<name.length;i++){
			loginlogoutsolvent.cannonLogin(login[i/2], login[i/2]);
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
			energy.loginMemberEnergyCompany(name[i]);
			Assert.assertEquals(true, common.isLinkPresent("Back to (" + parent[i/2] + ")"));
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			Assert.assertEquals(true, common.isLinkPresent(name[i]));
			common.clickLinkByName(name[i]);
			Assert.assertEquals("No Member Energy Companies", common.getYukonText("No Member Energy Companies"));
			Assert.assertEquals(true, common.isTextPresent(name[i]));
			Assert.assertEquals("STARS Operators Grp", common.getYukonText("STARS Operators Grp"));
			Assert.assertEquals("STARS Residential Customers Grp", common.getYukonText("STARS Residential Customers Grp"));
			Assert.assertEquals(primary[i],common.getYukonText(primary[i]));
			Assert.assertEquals("a_CCU-711", common.getYukonText("a_CCU-711"));
			common.clickLinkByName("Operator Logins");
			Assert.assertEquals(true,common.isLinkPresent(primary[i]));
			common.clickLinkByName("Substations And Routes");
			Assert.assertEquals("a_CCU-711", common.getYukonText("a_CCU-711"));
			common.clickLinkByName("Back to (" + parent[i/2] + ")");
			Assert.assertEquals(true, common.isLinkPresent(parent[i/2]));
			for(int k=0;k<2;k++)
				Assert.assertEquals(true, common.isLinkPresent(name[k]));
			loginlogoutsolvent.yukonLogout();
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11/21 and creates an operator login for QA_Test10/20
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void addLogin(){
		start();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		String[] name = getParamStrings("Create Operator Login");
		String[] company = getParamStrings("EC");
		String[] login = getParamStrings("EC Login 2");

		//loops for both QA_Test10/20
		for(int k=0;k<company.length;k++){
			loginlogoutsolvent.cannonLogin(login[k],login[k]);
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			Assert.assertEquals(true, common.isLinkPresent(company[k]));
			common.clickLinkByName(company[k]);
			common.clickLinkByName("Operator Logins");
			common.clickButtonBySpanText("Create");
			common.enterInputText("operatorLogin/update", "username", name[k]);
			common.enterInputText("operatorLogin/update", "password1", name[k]);
			common.enterInputText("operatorLogin/update", "password2", name[k]);
			common.clickFormButton("operatorLogin/update", "create");
			Assert.assertEquals("Successfully created the login.", common.getYukonText("Successfully created the login."));	
			Assert.assertFalse(common.isLinkPresent("DISABLED"));
			Assert.assertEquals(true, common.isLinkPresent(name[k]));
			loginlogoutsolvent.yukonLogout();
		}
		common.end();
	}
	/**
	 * verifies that there is no System Administration button for secondary logins starsop12/22, Memberop10/20-01a/02a (6 total)
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void verifyNoSystemAdmin(){
		start();
		LoginLogoutSolvent login= new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Create Operator Login");
		
		for(int i=0;i<name.length;i++){
			login.cannonLogin(name[i], name[i]);
			Assert.assertFalse(common.isLinkPresent("System Administration"));
			login.yukonLogout();
		}
		common.end();
	}
	/**
	 * logs in with the primary login and edits the info for the corresponding member ECs
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void editMemInfo(){
		start();
		LoginLogoutSolvent login= new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Member Company Name");
		String[] primary = getParamStrings("Member Primary Login");
		String[] address1 = getParamStrings("Address 1");
		String[] address2 = getParamStrings("Address 2");
		String[] city = getParamStrings("City");
		String[] county = getParamStrings("County");
		
		for(int i=0;i<primary.length;i++){
			login.cannonLogin(primary[i], primary[i]);
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			common.clickLinkByName(name[i]);
			Assert.assertEquals("No Member Energy Companies", common.getYukonText("No Member Energy Companies"));
			Assert.assertEquals(true, common.isTextPresent(name[i]));
			Assert.assertEquals("QA-MainAdmin@gmail.com", common.getYukonText("QA-MainAdmin@gmail.com"));
			common.clickFormButton("update", "edit");
			common.enterInputText("update", "address.locationAddress1", address1[i]);
			common.enterInputText("update", "address.locationAddress2", address2[i]);
			common.enterInputText("update", "address.cityName", city[i]);
			common.enterInputText("update", "address.stateCode", "MN");
			common.enterInputText("update", "address.zipCode", "99124");
			common.enterInputText("update", "address.county", county[i]);
			common.selectDropDownMenu("update", "defaultRouteId", "a_CCU-711");
			common.clickFormButton("update", "save");
			Assert.assertEquals("General Info Updated Successfully", common.getYukonText("General Info Updated Successfully"));
			login.yukonLogout();
		}
		common.end();
	}
	/**
	 * method searches by user, then edits member user memop10/20-02a password
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void editMemberUserPassword(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] old = getParamStrings("Login Old");
		String[] edit = getParamStrings("Login Edit");
		
		for(int i=0;i<old.length;i++){
			common.clickLinkByName("System Administration");
			common.clickLinkByName("User/Group");
			common.clickLinkByName("STARS Operators Grp");
			common.clickLinkByName("Users");
			common.clickLinkByName(old[i]);
			common.clickButtonBySpanTextWithElementWait("Change Password");
			//enter a password that doesn't match YUK 10013
			common.enterInputText("changePassword", "password", "test1");
			common.enterInputText("changePassword", "confirmPassword", "test2");
			common.clickButtonBySpanText("Save");
			Assert.assertEquals(true, common.isTextPresent("Password fields do not match"));
			common.enterInputText("changePassword", "password", edit[i]);
			common.enterInputText("changePassword", "confirmPassword", edit[i]);
			common.clickButtonBySpanText("Save");
			Assert.assertEquals("Password Updated Successfully", common.getYukonText("Password Updated Successfully"));
			common.clickFormButton("edit", "edit");
			common.enterInputText("edit", "username", edit[i]);
			common.selectDropDownMenu("edit", "authType", "Active Directory");
			common.selectDropDownMenu("edit", "loginStatus", "Disabled");
			common.clickFormButton("edit","update");
			Assert.assertEquals("User Updated Successfully", common.getYukonText("User Updated Successfully"));
			Assert.assertEquals("Disabled", common.getYukonText("Disabled"));
			common.clickLinkByName("Odds For Control");
			common.enterInputText("roleEditor/update", "values[ODDS_FOR_CONTROL_LABEL]", "Odds for Control Edit");
			common.clickFormButton("roleEditor/update", "save");
			Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
			common.clickLinkByName("Home");
		}
		common.end();
	}
	/**
	 * verifies the previously changed password is not enabled
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void verifyDisabledLogin(){
		start();
		LoginLogoutSolvent login= new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] edit = getParamStrings("Login Edit");
		
		for(int i=0;i<edit.length;i++){
			login.cannonLogin(edit[i], edit[i]);
			Assert.assertEquals(true, common.isTextPresent("* Invalid username/password"));
		}
		common.end();
	}
	/**
	 * adds then deletes a Login group for each user
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void addDeleteLoginGroupToMemberUser(){
		init();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] edit = getParamStrings("Login Edit");
		String[] group = getParamStrings("Member Admin Group2");
		
		for(int i=0;i<edit.length;i++){
			common.clickLinkByName("System Administration");
			common.clickLinkByName("User/Group");
			common.inputTextById("picker_userPicker_ss","mem");
			common.clickLinkByName(edit[i]);
			common.clickLinkByName("Login Groups");
			
		    PopupMenuSolvent loginGroupPicker = PickerFactory.createPopupMenuSolvent("loginGroup", PickerType.MultiSelect);
			common.clickButtonBySpanTextWithElementWait("Add", "//div[@id='loginGroupPicker']");
	        loginGroupPicker.clickMenuItem(group[i]);
	        loginGroupPicker.clickButton("OK");

			Assert.assertEquals("User Updated Successfully", common.getYukonText("User Updated Successfully"));
			stars.removeUserOrGroup(group[i]);
			Assert.assertEquals("User Updated Successfully", common.getYukonText("User Updated Successfully"));
			Assert.assertFalse(common.isLinkPresent(group[i]));
			common.clickLinkByName("Home");
		}
		common.end();
	}
	/**
	 * method edits the name for the group QA_Test10/20 Member02 Admin Grp, then adds the CapControl Settings role property
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void editGroupRolePropertyName(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] group = getParamStrings("Member Admin Group2");
		
		common.clickLinkByName("System Administration");
		for(int i=0;i<group.length;i++){
			common.clickLinkByName("User/Group");
			common.clickLinkByName(group[i]);
			common.clickFormButton("groupEditor/edit", "edit");
			common.enterInputText("groupEditor/edit", "groupName", group[i] + " Edit");
			common.enterTextInTextArea("Description:","Privilege group for the energy company's default operator login Edit");
			common.clickButtonByAttribute("name","update");
			Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
			Assert.assertEquals(true, common.isTextPresent(group[i] + " Edit"));
			Assert.assertEquals(true, common.isTextPresent("Privilege group for the energy"));
			common.selectDropDownMenu("groupEditor/addRole", "newRoleId", "Cap Control Settings");
			common.clickButtonBySpanText("Add");
			Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
			common.clickFormButton("roleEditor/update", "save");
			Assert.assertEquals("Login Group Role Updated Successfully", common.getYukonText("Login Group Role Updated Successfully"));
			Assert.assertEquals("Cap Control Settings", common.getYukonText("Cap Control Settings"));
		}
		common.end();
	}
	/**
	 * method adds a user for each Admin Group, then removes that added user
	 * This test depend on data in input file {@link TestECMemProp1Selenium.xml}
	 */
	@Test
	public void editGroupRolePropertyUser(){
		init();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] group = getParamStrings("Member Admin Group Edit");
		String[] edit = getParamStrings("Login Edit");
		
		common.clickLinkByName("System Administration");
		for(int i=0;i<group.length;i++){
			common.clickLinkByName("User/Group");
			common.clickLinkByName(group[i]);
			common.clickLinkByName("Users");
			
	        PopupMenuSolvent loginGroupPicker = PickerFactory.createPopupMenuSolvent("user", PickerType.MultiSelect);
	        common.clickButtonBySpanTextWithElementWait("Add", "//div[@id='userPicker']");
	        loginGroupPicker.clickMenuItem(edit[i]);
	        loginGroupPicker.clickButton("OK");

			Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
			stars.removeUserOrGroup(edit[i]);
			Assert.assertEquals("Group Updated Successfully", common.getYukonText("Group Updated Successfully"));
		}
		common.end();
	}
}