package com.cannontech.selenium.test.sysadminstars;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.EnergyCompanySolvent;
import com.cannontech.selenium.solvents.stars.HardwarePageSolvent;
import com.cannontech.selenium.test.common.CommonHelper;
import com.google.common.collect.Lists;
/**
 * This Class creates and edits Parent ECs QA_Test10 and QA_Test20, creates 2 member ECs for each, and 
 * edits all the role properties for the admin group associated with each respective EC
 * @author Kevin Krile	
 */
public class TestECCreateMemberECSelenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as @param username/password
	 */
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("superstarsop", "superstarsop");
	}
	/**
	 * Creates new Parent Member Energy Companies QA_Test10 and QA_Test20
	 * This test depend on data in input file {@link TestECCreateMemberECSelenium.xml}
	 */
	@Test
	public void createEnergyCompany1020(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		PopupMenuSolvent primaryPopup = PickerFactory.createPopupMenuSolvent("primaryOperatorGroup", PickerType.SingleSelect);
        PopupMenuSolvent adtlPopup = PickerFactory.createPopupMenuSolvent("additionalOperatorGroup", PickerType.MultiSelect);
        PopupMenuSolvent resPopup = PickerFactory.createPopupMenuSolvent("residentialGroup", PickerType.MultiSelect);
		List<String> parentCompanyNames = Lists.newArrayList(getParamStrings("Parent Company Name"));
		List<String> parentPrimaryLogins = Lists.newArrayList(getParamStrings("Parent Primary Login"));
		
		for(int i=0;i< parentCompanyNames.size(); i++){
		    String parentPrimaryLogin = parentPrimaryLogins.get(i);
		    String parentCompanyName = parentCompanyNames.get(i);
		    
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
			common.clickFormButton("energyCompany/new", "create");
			Assert.assertEquals("System Administration: Create Energy Company", common.getPageTitle());
			common.enterInputText("energyCompany/create", "name", parentCompanyName);
			common.enterInputText("energyCompany/create", "email", "QA-MainAdmin@gmail.com");
			common.selectDropDownMenu("energyCompany/create", "defaultRouteId", "a_CCU-711");
			common.enterInputText("energyCompany/create", "adminUsername", parentPrimaryLogin);
			common.enterInputText("energyCompany/create", "adminPassword1", parentPrimaryLogin);
			common.enterInputText("energyCompany/create", "adminPassword2", parentPrimaryLogin);

		    primaryPopup.openPickerPopup();
	        primaryPopup.clickMenuItem("STARS Operators Grp");
		        
		    adtlPopup.openPickerPopup();
		    adtlPopup.clickMenuItem("Operators Grp");
		    adtlPopup.clickButton("OK");
		        
		    resPopup.openPickerPopup();
		    resPopup.clickMenuItem("STARS Residential Customers Grp");
		    resPopup.clickMenuItem("Residential Customers Grp");
		    resPopup.clickButton("OK");

			common.clickFormButton("energyCompany/create", "save");
			Assert.assertEquals("System Administration: General Info (" + parentCompanyName + ")", 
			                    common.getPageTitle());
			Assert.assertEquals("Energy Company " + parentCompanyName + " Created Successfully", 
			                    common.getYukonText("Energy Company " + parentCompanyName + " Created Successfully"));
			common.clickLinkByName("Home");
		}
		buildIndexes();	//have had trouble with oracle databases, so using this
		common.end();
	}
	/**
	 * Edits or adds the role properties for the login groups QA_Test10 Admin Group and QA_Test20 Admin Grp
	 * This test depend on data in input file {@link TestECCreateMemberECSelenium.xml}
	 */
	@Test
	public void editQA_Test1020AdminGroupRoleProperties(){
		init();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		String[] group = getParamStrings("Parent Admin Group");
		
		for(int i=0;i<group.length;i++){
			common.clickLinkByName("System Administration");
			topMenu.clickTopMenuItem("User/Group");
			Assert.assertEquals("System Administration: User/Group Editor", common.getPageTitle());
			common.clickLinkByName(group[i]);
			Assert.assertEquals("System Administration: Group Info (" + group[i] + ")", common.getPageTitle());
			
			//Adding Configuration property
			
			common.selectDropDownMenu("groupEditor/addRole", "newRoleId", "Configuration");
			common.clickButtonByTitle("Add");
			Assert.assertEquals(true, common.isTextPresent("Group Updated Successfully"));
			Assert.assertEquals("System Administration: Role Editor (Configuration)", common.getPageTitle());
			common.clickFormButton("roleEditor/update", "save");
			Assert.assertEquals(true, common.isTextPresent("Login Group Role Updated Successfully"));
			
			//Editing the Administrator property
			
			common.clickLinkByName("Administrator");
			Assert.assertEquals("System Administration: Role Editor (Administrator)", common.getPageTitle());
			common.selectDropDownMenuByIdName("ADMIN_CREATE_DELETE_ENERGY_COMPANY", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_DATABASE_MIGRATION", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_EDIT_CONFIG", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_EDIT_ENERGY_COMPANY", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_EVENT_LOGS", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_LM_USER_ASSIGN", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_MANAGE_MEMBERS", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_MEMBER_LOGIN_CNTRL", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_MEMBER_ROUTE_SELECT", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_MULTI_WAREHOUSE", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_VIEW_OPT_OUT_EVENTS", "TRUE");
			common.clickFormButton("roleEditor/update", "save");
			Assert.assertEquals(true, common.isTextPresent("Login Group Role Updated Successfully"));
			
			//Editing the Energy Company property
			
			widget.clickLinkByWidgetWithPageLoadWait("Roles", "Energy Company");
			Assert.assertEquals("System Administration: Role Editor (Energy Company)", common.getPageTitle());
			common.selectDropDownMenuByIdName("SINGLE_ENERGY_COMPANY", "FALSE");
			common.clickFormButton("roleEditor/update", "save");
			Assert.assertEquals(true, common.isTextPresent("Login Group Role Updated Successfully"));
			common.clickLinkByName("Home");
		}
		common.end();
	}
	/**
	 * This test logs in as superstarsop and starsop11 and creates member Energy Companies for QA_Test10 and QA_Test20
	 * This test is designed to test the functionality of creating a member EC from either a super admin or a regular admin
	 * This test depend on data in input file {@link TestECCreateMemberECSelenium.xml}
	 */
	@Test
	public void addQA_Test1020MemberEnergyCompanies(){
		start();
		CommonSolvent common = new CommonSolvent();
		EnergyCompanySolvent energy = new EnergyCompanySolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		
		PopupMenuSolvent primaryPopup = PickerFactory.createPopupMenuSolvent("primaryOperatorGroup", PickerType.SingleSelect);
		PopupMenuSolvent adtlPopup = PickerFactory.createPopupMenuSolvent("additionalOperatorGroup", PickerType.MultiSelect);
		PopupMenuSolvent resPopup = PickerFactory.createPopupMenuSolvent("residentialGroup", PickerType.MultiSelect);
		
		String[] name = getParamStrings("Member Company Name");
		String[] primary = getParamStrings("Member Primary Login");
		String[] parent = getParamStrings("Parent Company Name");
		String[] login = getParamStrings("EC Login");
		
		//using the division and modulus functions here to deal with the length of each string array.  When an array has i/2, it means that intead of putting 
		//a,a,b,b I simply put a,b and have i/2 in the calling, so as i goes from 0,1,2,3 /2 it is 0,0,1,1.
		for(int i=0;i<name.length;i++){
			loginlogoutsolvent.cannonLogin(login[i/2], login[i/2]);
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			Assert.assertEquals(true, common.isLinkPresent(parent[i/2]));
			common.clickLinkByName(parent[i/2]);
			common.clickButtonBySpanText("Create");
			
			// General Information
			common.enterInputText("energyCompany/create", "name", name[i]);
			common.enterInputText("energyCompany/create", "email", "QA-MainAdmin@gmail.com");
			common.selectDropDownMenu("energyCompany/create", "defaultRouteId", "a_CCU-711");
			
			// Admin Operator Login Information
			common.enterInputText("energyCompany/create", "adminUsername", primary[i]);
			common.enterInputText("energyCompany/create", "adminPassword1", primary[i]);
			common.enterInputText("energyCompany/create", "adminPassword2", primary[i]);
			
			// Set the primary operator group to "Operators Grp"
			primaryPopup.openPickerPopup();
			//note picker.clickMenuItem uses contains, and since Esub Operators Grp is before Operators Grp, the Esub item is picked
			primaryPopup.clickMenuItem("Operators Grp");

			// Change the primary operator group from "Operators Grp" to "STARS Operators Grp"
			primaryPopup.openPickerPopup();
			primaryPopup.clickMenuItem("STARS Operators Grp");
			
			//we need to select an additional operator group so the residential group is the only one with 'none selected' in the link, because that is how
			//we bring up the pop-up menu - will delete the additional operator group after the res group is picked
			adtlPopup.openPickerPopup();
			adtlPopup.clickMenuItem("Esub Operators Grp");
			adtlPopup.clickButton("OK");
			
			resPopup.openPickerPopup();
			resPopup.clickMenuItem("STARS Residential Customers Grp");
			resPopup.clickButton("OK");

			common.clickFormButton("energyCompany/create", "save");
			Assert.assertEquals("Energy Company " + name[i] + " Created Successfully", common.getYukonText("Energy Company " + name[i] + " Created Successfully"));
			Assert.assertEquals("No Member Energy Companies", common.getYukonText("No Member Energy Companies"));
			Assert.assertEquals(name[i],common.getYukonExactText(name[i]));
			Assert.assertEquals("QA-MainAdmin@gmail.com", common.getYukonText("QA-MainAdmin@gmail.com"));
			energy.removeLoginGroup("Esub Operators Grp");
			loginlogoutsolvent.yukonLogout();
		}
		//building the index again
		loginlogoutsolvent.cannonLogin("DefaultCTI","$cti_default");
		buildIndexes();
		common.end();
	}
	/**
	 * This temporary method is created for YUK-9919, an error when adding a mem EC changes what shows up as the default route on the gen info page
	 * Adds the default route to the Parent EC
	 */
	@Test
	public void addDefaultRouteToParent(){
		start();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		String[] login= getParamStrings("Parent Primary Login");
		String[] parent = getParamStrings("Parent Company Name");
		
		for(int i=0;i<login.length;i++){
			loginlogoutsolvent.cannonLogin(login[i], login[i]);
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			common.clickLinkByName(parent[i]);
			common.clickLinkByName("Substations And Routes");
			common.selectDropDownMenuByAttributeName("routeId", "a_CCU-711");
			hardware.clickAddByTableHeader("Assigned Routes");
			Assert.assertEquals("Route Added Successfully",common.getYukonText("Route Added Successfully"));
			loginlogoutsolvent.yukonLogout();
		}
		common.end();
	}
	/**
	 * This method logs in as superstarsop and starsop11 and verifies the Member EC properties
	 * This test is designed to test the functionality of verifying a member EC from either a super admin or a regular admin
	 * This test depend on data in input file {@link TestECCreateMemberECSelenium.xml}
	 */
	@Test
	public void verifyQA_Test1020MemberEnergyCompany(){
		start();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		String[] name = getParamStrings("Member Company Name");
		String[] primary = getParamStrings("Member Primary Login");
		String[] parent = getParamStrings("Parent Company Name");
		String[] login = getParamStrings("EC Login");
		
		//using the division and modulus functions here to deal with the length of each string array.  When an array has i/2, it means that instead of putting 
		//a,a,b,b I simply put a,b and have i/2 in the calling, so as i goes from 0,1,2,3 /2 it is 0,0,1,1.
		for(int i=0;i<name.length;i++){
			loginlogoutsolvent.cannonLogin(login[i/2], login[i/2]);
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			Assert.assertEquals(true, common.isLinkPresent(parent[i/2]));
			common.clickLinkByName(name[i]);
			Assert.assertEquals("No Member Energy Companies", common.getYukonText("No Member Energy Companies"));
			Assert.assertEquals(true, common.isTextPresent(name[i]));	//this text is also in the page title, so getYukonText returns General Info with it
			Assert.assertEquals("QA-MainAdmin@gmail.com", common.getYukonText("QA-MainAdmin@gmail.com"));
			Assert.assertEquals("a_CCU-711", common.getYukonText("a_CCU-711"));
			Assert.assertEquals(primary[i], common.getYukonText(primary[i]));
			common.clickLinkByName("Operator Logins");
			Assert.assertEquals(true,common.isLinkPresent(primary[i]));
			common.clickLinkByName("Substations And Routes");
			Assert.assertEquals("a_CCU-711", common.getYukonText("a_CCU-711"));
			loginlogoutsolvent.yukonLogout();
		}
		common.end();
	}
	/**
	 * Re-building the indices
	 */
	public void buildIndexes(){
		new CommonSolvent().clickLinkByName("Manage Indexes");
		new CommonHelper().buildAllIndexes();
	}
	/**
	 * Logs in as superstarsop and adds or edits some Member Role Properties
	 * This test depend on data in input file {@link TestECCreateMemberECSelenium.xml}
	 */
	@Test
	public void editMemberRoleProperties(){
		init();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		String[] adminGroups = getParamStrings("Member Admin Group");
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
		for(int i=0;i<adminGroups.length;i++){
			topMenu.clickTopMenuItem("User/Group");
			Assert.assertEquals("System Administration: User/Group Editor", common.getPageTitle());
			common.clickLinkByName(adminGroups[i]);
			
			//Edits the administrator role property
			
			common.clickLinkByName("Administrator");
			common.selectDropDownMenuByIdName("ADMIN_DATABASE_MIGRATION", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_EDIT_CONFIG", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_EDIT_ENERGY_COMPANY", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_EVENT_LOGS", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_LM_USER_ASSIGN", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_MANAGE_MEMBERS", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_MEMBER_LOGIN_CNTRL", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_MEMBER_ROUTE_SELECT", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_MULTI_WAREHOUSE", "TRUE");
			common.selectDropDownMenuByIdName("ADMIN_VIEW_OPT_OUT_EVENTS", "TRUE");
			common.clickFormButton("roleEditor/update", "save");
			Assert.assertEquals(true, common.isTextPresent("Login Group Role Updated Successfully"));
			
			//Edits the energy company role property
			
			widget.clickLinkByWidget("Roles", "Energy Company");
			common.selectDropDownMenuByIdName("SINGLE_ENERGY_COMPANY", "FALSE");
			common.clickFormButton("roleEditor/update", "save");
			Assert.assertEquals(true, common.isTextPresent("Login Group Role Updated Successfully"));
		}
	common.end();
	}
}
 