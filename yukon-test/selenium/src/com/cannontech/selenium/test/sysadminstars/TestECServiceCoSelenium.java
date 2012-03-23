package com.cannontech.selenium.test.sysadminstars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
/**
 * This Class adds, then edits the properties of the appliance category
 * @author Kevin Krile	
 */
public class TestECServiceCoSelenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as starsop11/starsop11
	 */
	public void init(){
		start();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		loginlogoutsolvent.cannonLogin("starsop11", "starsop11");
	}
	private String action = "serviceCompany/update";
	/**
	 * This test logs in as starsop11 and creates a Service Company for QA_Test10
	 */
	@Test
	public void addServiceCompany(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Name");
		String[] mainPh = getParamStrings("Main Phone");
		String[] mainF = getParamStrings("Main Fax");
		String[] email = getParamStrings("Email");
		String[] coType = getParamStrings("Company Type");
		String[] addr1 = getParamStrings("Street Address 1");
		String[] addr2 = getParamStrings("Street Address 2");
		String[] city = getParamStrings("City");
		String[] state = getParamStrings("State");
		String[] zip = getParamStrings("Zip");
		String[] first = getParamStrings("First Name");
		String[] last = getParamStrings("Last Name");
		String[] login = getParamStrings("Associated Login");
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		for(int i=0;i<name.length;i++){
			common.clickLinkByName("Service Companies");
			common.clickButtonBySpanText("Create");
			common.enterInputText(action, "companyName", name[i]);
			common.enterInputText(action, "mainPhoneNumber", mainPh[i]);
			common.enterInputText(action, "mainFaxNumber", mainF[i]);
			common.enterInputText(action, "emailContactNotification", email[i]);
			common.enterInputText(action, "hiType", coType[i]);
			common.enterInputText(action, "address.locationAddress1", addr1[i]);
			common.enterInputText(action, "address.locationAddress2", addr2[i]);
			common.enterInputText(action, "address.cityName", city[i]);
			common.enterInputText(action, "address.stateCode", state[i]);
			common.enterInputText(action, "address.zipCode", zip[i]);
			common.enterInputText(action, "primaryContact.contFirstName", first[i]);
			common.enterInputText(action, "primaryContact.contLastName", last[i]);
			common.selectDropDownMenu(action, "primaryContact.loginID", login[i]);
			common.clickFormButton(action, "create");
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and edits the service companies' names
	 * This test depend on data in input file {@link TestECSubstationSelenium.xml}
	 */
	@Test
	public void editServiceCompany(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Name");
		String[] mainPh = getParamStrings("Main Phone Edit");
		String[] mainF = getParamStrings("Main Fax Edit");
		String[] nameEdit = getParamStrings("Name Edit");
		String[] email = getParamStrings("Email");
		String[] coType = getParamStrings("Company Type");
		String[] addr1 = getParamStrings("Street Address 1");
		String[] addr2 = getParamStrings("Street Address 2");
		String[] city = getParamStrings("City");
		String[] state = getParamStrings("State");
		String[] zip = getParamStrings("Zip");
		String[] first = getParamStrings("First Name");
		String[] firstEdit = getParamStrings("First Name Edit");
		String[] last = getParamStrings("Last Name");
		String[] lastEdit = getParamStrings("Last Name Edit");
		String[] login = getParamStrings("Associated Login");
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		for(int i=0;i<name.length;i++){
			common.clickLinkByName("Service Companies");
			common.clickLinkByName(name[i]);
			Assert.assertEquals(true, common.isTextPresent(name[i]));
			Assert.assertEquals(mainPh[i], common.getYukonText(mainPh[i]));
			Assert.assertEquals(mainF[i], common.getYukonText(mainF[i]));
			Assert.assertEquals(email[i], common.getYukonText(email[i]));
			Assert.assertEquals(coType[i], common.getYukonText(coType[i]));
			Assert.assertEquals(addr1[i], common.getYukonText(addr1[i]));
			Assert.assertEquals(addr2[i], common.getYukonText(addr2[i]));
			Assert.assertEquals(city[i], common.getYukonText(city[i]));
			Assert.assertEquals(state[i], common.getYukonText(state[i]));
			Assert.assertEquals(zip[i], common.getYukonText(zip[i]));
			Assert.assertEquals(first[i], common.getYukonText(first[i]));
			Assert.assertEquals(true, common.isTextPresent(last[i]));
			Assert.assertEquals(login[i], common.getYukonText(login[i]));
			common.clickButtonBySpanText("Edit");
			common.enterInputText(action, "companyName", nameEdit[i]);
			common.enterInputText(action, "primaryContact.contFirstName", firstEdit[i]);
			common.enterInputText(action, "primaryContact.contLastName", lastEdit[i]);
			common.clickFormButton(action, "update");
			Assert.assertEquals(true, common.isTextPresent(nameEdit[i]));
			Assert.assertEquals(firstEdit[i], common.getYukonText(firstEdit[i]));
			Assert.assertEquals(lastEdit[i], common.getYukonText(lastEdit[i]));
			Assert.assertEquals("System Administration: Service Company (" + nameEdit[i] + ")", common.getPageTitle());
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and deletes the service Company
	 * This test depend on data in input file {@link TestECSubstationSelenium.xml}
	 */
	@Test
	public void deleteServiceCompany(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Name Edit");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Service Companies");
		common.clickLinkByName(name[1]);
		Assert.assertEquals("System Administration: Service Company (" + name[1] + ")", common.getPageTitle());
		common.clickButtonBySpanText("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete");
		common.clickFormButton(action, "delete");
		Assert.assertFalse( common.isLinkPresent(name[1]));
		Assert.assertEquals("Successfully deleted the Service Company.", common.getYukonText("Successfully deleted the Service Company."));
		common.end();
	}	
}
