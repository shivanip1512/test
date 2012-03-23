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
 * @author kevin.krile	
 */
public class TestECzMemProp2Selenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as superstarsop/superstarsop
	 */
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("superstarsop", "superstarsop");
	}
	private String action = "account/createAccount";
	/**
	 * Creates a new account for QA_Test10 and QA_Test20
	 * This test depend on data in input file {@link TestECzMemProp2Selenium.xml}
	 */
	@Test
	public void parentCreateNewAccount(){
		start();
		CommonSolvent common = new CommonSolvent();
		EnergyCompanySolvent energy= new EnergyCompanySolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		String[] member = getParamStrings("Member EC");
		String[] parent = getParamStrings("Parent EC");
		String[] login = getParamStrings("Parent Login");
		String[] account = getParamStrings("Parent Add Account Number");
		String[] first = getParamStrings("Parent Add First Name");
		String[] last = getParamStrings("Parent Add Last Name");
		String[] home = getParamStrings("Parent Add Home Phone");
		String[] addr1 = getParamStrings("Parent Add Address 1");
		String[] addr2 = getParamStrings("Parent Add Address 2");
		
		//using the division and modulus functions here to deal with the length of each string array.  When an array has i/2, it means that intead of putting 
		//a,a,b,b I simply put a,b and have i/2 in the calling, so as i goes from 0,1,2,3 /2 it is 0,0,1,1.  The modulus is a,b instead of a,b,a,b so 0,1,2,3 %2 is 0,1,0,1
		//this shortens the length of the xml file, and makes it easier to read.
		for(int i=0;i<account.length;i++){
			loginlogoutsolvent.cannonLogin(login[i/2],login[i/2]);
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			Assert.assertEquals(true, common.isLinkPresent(parent[i/2]));
			energy.loginMemberEnergyCompany(member[i/2]);
			Assert.assertEquals(true,common.isLinkPresent("Back to (" + parent[i/2] + ")"));
			common.clickLinkByName("New Account");
			common.enterInputText(action, "accountDto.accountNumber", account[i]);
			common.enterInputText(action, "accountDto.customerNumber", account[i]);
			common.enterInputText(action, "accountDto.lastName", last[i%2]);
			common.enterInputText(action, "accountDto.firstName", first[i%2]);
			common.enterInputText(action, "accountDto.homePhone", home[i%2]);
			common.enterInputText(action, "accountDto.streetAddress.locationAddress1", addr1[i%2]);
			common.enterInputText(action, "accountDto.streetAddress.locationAddress2", addr2[i%2]);
			common.enterInputText(action, "accountDto.streetAddress.cityName", "QA City");
			common.enterInputText(action, "accountDto.streetAddress.stateCode", "MN");
			common.enterInputText(action, "accountDto.streetAddress.zipCode", "99123");
			common.selectCheckBox("Same As Above");
			common.enterInputText(action, "loginBackingBean.username", account[i]);
			common.enterInputText(action, "loginBackingBean.password1", account[i]);
			common.enterInputText(action, "loginBackingBean.password2", account[i]);
			common.clickButtonBySpanText("Create");
			common.clickLinkByName("Back to (" + parent[i/2] + ")");
			loginlogoutsolvent.yukonLogout();
		}
	common.end();
	}
	/**
	 * Creates a new account for QA_Test10/20 Memeber02
	 * This test depend on data in input file {@link TestECzMemProp2Selenium.xml}
	 */
	@Test
	public void memberCreateNewAccount(){
		start();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		String[] login = getParamStrings("Member Login 2");
		String[] account = getParamStrings("Member Add Account Number");
		String[] last = getParamStrings("Member Add Last Name");
		String[] home = getParamStrings("Member Add Home Phone");
		String[] addr1 = getParamStrings("Member Add Address 1");
		String[] addr2 = getParamStrings("Member Add Address 2");
		
		//using the division and modulus functions here to deal with the length of each string array.  When an array has i/2, it means that intead of putting 
		//a,a,b,b I simply put a,b and have i/2 in the calling, so as i goes from 0,1,2,3 /2 it is 0,0,1,1.  The modulus is a,b instead of a,b,a,b so 0,1,2,3 %2 is 0,1,0,1
		//this shortens the length of the xml file, and makes it easier to read.
		for(int i=0;i<account.length;i++){
			loginlogoutsolvent.cannonLogin(login[i/2],login[i/2]);
			common.clickLinkByName("New Account");
			common.enterInputText(action, "accountDto.accountNumber", account[i]);
			common.enterInputText(action, "accountDto.customerNumber", account[i]);
			common.enterInputText(action, "accountDto.lastName", last[i%2]);
			common.enterInputText("account/createAccount", "accountDto.firstName", "Loyal");
			common.enterInputText(action, "accountDto.homePhone", home[i%2]);
			common.enterInputText(action, "accountDto.streetAddress.locationAddress1", addr1[i%2]);
			common.enterInputText(action, "accountDto.streetAddress.locationAddress2", addr2[i%2]);
			common.enterInputText(action, "accountDto.streetAddress.cityName", "QA City");
			common.enterInputText(action, "accountDto.streetAddress.stateCode", "MN");
			common.enterInputText(action, "accountDto.streetAddress.zipCode", "99123");
			common.selectCheckBox("Same As Above");
			common.enterInputText(action, "loginBackingBean.username", account[i]);
			common.enterInputText(action, "loginBackingBean.password1", account[i]);
			common.enterInputText(action, "loginBackingBean.password2", account[i]);
			common.clickButtonBySpanText("Create");
			Assert.assertEquals(true,common.isTextPresent("Account Created"));
			loginlogoutsolvent.yukonLogout();
		}
	common.end();
	}
	/**
	 * Checks to see if the created accounts are visible to the parent
	 * This test depend on data in input file {@link TestECzMemProp2Selenium.xml}
	 */
	@Test
	public void parentAccountSearch(){
		start();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		String[] login = getParamStrings("Parent Login");
		String[] search = getParamStrings("Parent Search Value");
		String[] company = getParamStrings("Company Search");
		
		//using the division and modulus functions here to deal with the length of each string array.  When an array has i/2, it means that intead of putting 
		//a,a,b,b I simply put a,b and have i/2 in the calling, so as i goes from 0,1,2,3 /2 it is 0,0,1,1.  The modulus is a,b instead of a,b,a,b so 0,1,2,3 %2 is 0,1,0,1
		//this shortens the length of the xml file, and makes it easier to read.
		for(int k=0;k<login.length;k++){
			loginlogoutsolvent.cannonLogin(login[k],login[k]);
			for(int i=0;i<search.length;i++){
				stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", search[i]);
				common.clickMagnifyingIcon("Search for existing customer:");
				Assert.assertEquals("company " + company[i%2+(k*2)] + " could not be found", true,common.isTextPresent(company[i%2+(k*2)]));
				common.clickLinkByName("Home");
			}
			loginlogoutsolvent.yukonLogout();
		}
	common.end();
	}
	/**
	 * Checks to see if the member accounts are visible and the parent accounts are not to the member EC
	 * This test depend on data in input file {@link TestECzMemProp2Selenium.xml}
	 */
	@Test
	public void memberAccountSearch(){
		start();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		String[] login = getParamStrings("Member Login 2");
		String[] parent = getParamStrings("Parent Add Account Number");
		String[] member = getParamStrings("Member Add Account Number");
		
		//using the division and modulus functions here to deal with the length of each string array.  When an array has i/2, it means that intead of putting 
		//a,a,b,b I simply put a,b and have i/2 in the calling, so as i goes from 0,1,2,3 /2 it is 0,0,1,1.  The modulus is a,b instead of a,b,a,b so 0,1,2,3 %2 is 0,1,0,1
		//this shortens the length of the xml file, and makes it easier to read.
		for(int i=0;i<member.length;i++){
			loginlogoutsolvent.cannonLogin(login[i/2],login[i/2]);
			stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "72");
			common.clickMagnifyingIcon("Search for existing customer:");
			common.assertEqualsTextIsPresent(member[i]);
			common.assertEqualsTextIsNotPresent(parent[i]);
			loginlogoutsolvent.yukonLogout();
		}
	common.end();
	}
	/**
	 * Verifies appliances get inherited from the parent EC, and creates a new appliance for a member EC
	 * This test depend on data in input file {@link TestECzMemProp2Selenium.xml}
	 */
	@Test
	public void memberApplianceInheritanceCheck(){
		start();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
        PopupMenuSolvent programPopup = PickerFactory.createPopupMenuSolvent("program", PickerType.MultiSelect);
		
		String[] login = getParamStrings("Member Login 1");
		String[] program = getParamStrings("Program");
		String[] member = getParamStrings("Member EC");
		String[] parentAppliances = getParamStrings("Parent Appliances");
		
		loginlogoutsolvent.cannonLogin(login[0],login[0]);
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName(member[0]);
		Assert.assertEquals(true,common.isTextPresent(member[0]));
		common.clickLinkByName("Appliance Categories");
		//checks to see if all the appliances' links are present
		for(int k=0;k<parentAppliances.length;k++)
			Assert.assertEquals(true,common.isLinkPresent(parentAppliances[k]));
		Assert.assertEquals(true,common.isTextPresent("Inherited"));
		common.clickButtonBySpanText("Create");
		common.enterInputText("save", "name", "AC - Member");
		common.selectDropDownMenu("save", "applianceType", "Air Conditioner");
		common.selectDropDownMenuByIdName("iconChooserIconSelect", "Air Conditioner");
		common.enterTextInTextArea("Description:", "AC - Member Test");
		common.clickFormButton("save", "save");
		
	    common.clickButtonBySpanTextWithElementWait("Add","//div[@id='picker_programPicker_resultAreaFixed']");
	    programPopup.clickMenuItem(program[0]);
        programPopup.clickButton("OK");

		common.selectCheckBox("Same as Program Name");
		common.clickButtonBySpanText("OK");
		Assert.assertEquals(true,common.isTextPresent(program[0]));
		common.clickLinkByName("Appliance Categories");
		Assert.assertEquals(true,common.isLinkPresent("AC - Member"));
		common.end();
	}
	/**
	 * Verifies appliances don't get inherited from the parent EC
	 * This test depend on data in input file {@link TestECzMemProp2Selenium.xml}
	 */
	@Test
	public void memberApplianceNONInheritanceCheck(){
		start();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		PopupMenuSolvent programPopup = PickerFactory.createPopupMenuSolvent("program", PickerType.MultiSelect);
		
		String[] login = getParamStrings("Member Login 2");
		String[] program = getParamStrings("Program 2");
		String[] member = getParamStrings("Member EC 2");
		String[] parentAppliances = getParamStrings("Parent Appliances");
		
		loginlogoutsolvent.cannonLogin(login[0],login[0]);
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName(member[0]);
		Assert.assertEquals(true,common.isTextPresent(member[0]));
		common.clickLinkByName("Appliance Categories");
		//checks to see if all the appliances' links are present
		for(int k=0;k<parentAppliances.length;k++)
			Assert.assertEquals(true,common.isLinkPresent(parentAppliances[k]));
		Assert.assertEquals(true,common.isTextPresent("Inherited"));
		//checks to make sure the other member's appliance is not inherited
		common.assertEqualsLinkTextNotPresent("AC - Member");
		common.clickButtonBySpanText("Create");
		common.enterInputText("save", "name", "AC-Member2");
		common.selectDropDownMenu("save", "applianceType", "Air Conditioner");
		common.selectDropDownMenuByIdName("iconChooserIconSelect", "Air Conditioner");
		common.enterTextInTextArea("Description:", "AC-Member2 Test");
		common.clickFormButton("save", "save");

        common.clickButtonBySpanTextWithElementWait("Add","//div[@id='picker_programPicker_resultAreaFixed']");
        programPopup.clickMenuItem(program[0]);
        programPopup.clickButton("OK");

		common.selectCheckBox("Same as Program Name");
		common.clickButtonBySpanText("OK");
		Assert.assertEquals(true,common.isTextPresent(program[0]));
		common.clickLinkByName("Appliance Categories");
		Assert.assertEquals(true,common.isLinkPresent("AC-Member2"));
		common.assertEqualsLinkTextNotPresent("AC - Member");
		loginlogoutsolvent.yukonLogout();
		common.end();
	}
	/**
	 * removes a member EC from its parent (from the parent general info page)
	 * This test depend on data in input file {@link TestECzMemProp2Selenium.xml}
	 */
	@Test
	public void removeMemberEC1020(){
		start();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		EnergyCompanySolvent energy = new EnergyCompanySolvent();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Remove Company Name");
		String[] parent = getParamStrings("Parent EC");
		String[] login = getParamStrings("EC Login");
		
		for(int i=0;i<login.length;i++){
			loginlogoutsolvent.cannonLogin(login[i],login[i]);
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			Assert.assertEquals(true, common.isLinkPresent(parent[i]));
			common.clickLinkByName(parent[i]);
			energy.removeMemberEnergyCompany(name[i]);
			Assert.assertEquals(true, common.isTextPresent("Member Removed Successfully"));
			common.assertEqualsLinkTextNotPresent(name[i]);
			loginlogoutsolvent.yukonLogout();
		}
	common.end();
	}
	/**
	 * deletes member Energy Companies
	 * This test depend on data in input file {@link TestECzMemProp2Selenium.xml}
	 */
	@Test
	public void deleteMemberEC1020(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Delete Company Name");
		
		for(int i=0;i<name.length;i++){
			common.clickLinkByName("System Administration");
			common.clickLinkByName("Energy Company");
			common.clickLinkByName(name[i]);
			common.clickFormButton("update", "edit");
			common.clickFormButton("update", "deleteConfirmation");
			common.clickFormButton("delete", "delete");
			Assert.assertEquals(true, common.isTextPresent("The energy company " + name[i] + " was deleted successfully"));
		}
	common.end();
	}
	/**
	 * deletes the Energy Company QA_Test20
	 */
	@Test
	public void deleteParentEC20(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test20");
		common.clickFormButton("update", "edit");
		common.clickFormButton("update", "deleteConfirmation");
		common.clickFormButton("delete", "delete");
		Assert.assertEquals(true, common.isTextPresent("The energy company QA_Test20 was deleted successfully"));
		common.end();
	}
}
