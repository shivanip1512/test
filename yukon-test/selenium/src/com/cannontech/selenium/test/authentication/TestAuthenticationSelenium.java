package com.cannontech.selenium.test.authentication;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
import com.google.common.collect.Lists;
import com.thoughtworks.selenium.SeleniumException;

/**
 * This class handles authentication testing in Yukon Web application.
 * It has methods to import stars account and at the end of this test stars accounts
 * get deleted.
 * 
 * @author anuradha.uduwage
 *
 */

public class TestAuthenticationSelenium extends SolventSeleniumTestCase {
	/**
	 * Create stars account to test user authentication.
	 */
	@Test
	public void createLoginAccounts(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");	
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Import Account");
		Assert.assertEquals("Operator: Account Import", common.getPageTitle());
		common.enterInputText("account/uploadImportFiles", "accountImportFile", getParamString("ImportInsertAccount"));
		common.clickButtonBySpanText("Prescan");
		if(common.isTextPresent("Finished - Passed"))
			common.clickFormButtonByButtonId("account/doAccountImport", "importButton");
		if(common.isTextPresent("56 Added, 0 Updated, 0 Removed"))
			common.clickLinkByName("Back to Account Import");
		common.end();
	}
	/**
	 * Navigate from Operations main page.
	 */
	@Test
	public void navigateFromOperationsPage() {
		start();
		new LoginLogoutSolvent().cannonLogin(getParamString("username"), getParamString("password"));
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent menuSolvent = new YukonTopMenuSolvent();
		
		common.clickLinkByName("Volt/Var Management");
		menuSolvent.clickHome();
		menuSolvent.selectALocation("Bulk Importer");
		Assert.assertEquals(": Bulk Importer", common.getPageTitle());
		Assert.assertEquals(": Bulk Importer", common.getYukonText("Bulk Importer"));//weird
		menuSolvent.clickBreadcrumb("Home");
		menuSolvent.clickHome();
		menuSolvent.selectALocation("Reporting");
		menuSolvent.clickHome();
		menuSolvent.selectALocation("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		menuSolvent.clickHome();
		menuSolvent.selectALocation("Bulk Operations");
		//test if we are in Bulk Operations page
		Assert.assertEquals("Bulk Operations", common.getPageTitle());
		Assert.assertEquals("Bulk Operations", common.getYukonText("Bulk Operations"));
		common.end();
	}
	/**
	 * The test login as stars customer and navigate to default page and check available links.
	 */
	@Test
	public void testStarsCustomerLogin() {
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		StarsCommonSolvent generalSolvent = new StarsCommonSolvent();
		String[] users = getParamStrings("users");
		String[] passwords = getParamStrings("passwords");
		for(int i=0; i < users.length; i++) {
			if((passwords.length != 0) && (passwords.length == users.length)) {
				if(users[i] != null) {
					loginLogoutSolvent.cannonLogin(users[i], passwords[i]);
					generalSolvent.clickGeneral();
					generalSolvent.clickStarsLeftMenuLink("Contact Us");
					generalSolvent.clickStarsLeftMenuLink("Control History");
					loginLogoutSolvent.yukonLogout();
				}
				else 
					throw new SeleniumException("Something wrong with the users and password check the xml file.");
				
			}
		}
		loginLogoutSolvent.end();
	}
	/**
	 * Security test to if the stars customer account has access to setup.jsp page.
	 * Stars Customer accounts should not have accesss to setup.jsp page.
	 */
	@Test
	public void testCustomerAccessToSetupPage() {
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		List<String> users = Lists.newArrayList(getParamStrings("users"));
		List<String> passwords = Lists.newArrayList(getParamStrings("passwords"));
		CommonSolvent common= new CommonSolvent();
		
		for(int i = 0; i < users.size(); i++) {
			if((passwords.size() != 0) && (passwords.size() == users.size())) {
				if(users.get(i) != null) {
					loginLogoutSolvent.cannonLogin(users.get(i), passwords.get(i));
					common.openURL(common.getBaseURL() + "/setup.jsp");
					Assert.assertEquals("HTTP Status 403 -", common.getYukonText("HTTP Status 403"));
					Assert.assertEquals("type", common.getYukonText("type"));
					Assert.assertEquals("type", common.getYukonText("type"));
					Assert.assertEquals("message", common.getYukonText("message"));
					Assert.assertEquals("description", common.getYukonText("description"));
					common.openURL(common.getBaseURL() + "");
					loginLogoutSolvent.yukonLogout();
				}
				else 
					throw new SeleniumException("Users Didn't get Extracted properly from XML");
			}
		}
		loginLogoutSolvent.cannonLogin(getParamString("username"), getParamString("password"));
		common.openURL(common.getBaseURL()+ "setup.jsp");
		String pageTitle = common.getPageTitle();
		Assert.assertFalse(pageTitle.contains("Error Report"));
		Assert.assertFalse(common.isPageTitle("HTTP Status 403 - "));		
		common.end();
	}
	/**
	 * Test invalid username with a valid password for authentication testing.
	 */
	@Test
	public void testInvalidUserLogin() {
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		loginLogoutSolvent.cannonLogin(getParamString("invaliduser"), getParamString("password"));
		loginLogoutSolvent.navigateTo(new CommonSolvent()).getYukonText("Invalid username");
		loginLogoutSolvent.end();
	}
	/**
	 * Test contact us page.
	 */
	@Test
	public void verifyContactUs() {
		setCustomURL("http://pspl-qa011:8080/login.jsp");
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		loginLogoutSolvent.cannonLogin(getParamStrings("users")[1], getParamStrings("passwords")[1]);
		common.clickLinkByName("Contact Us");
		String s = "For Nepal Tourist Information call Anjana at 555-555-5551\n" +
		" For Floor Laying Expertise call Dave at 555-555-5552\n" +
		" For Reasons NOT to Move to California call Steve at 555-555-5553";
		Assert.assertEquals(s, common.getYukonText("For Nepal Tourist Information call Anjana at 555-555-5551"));
		loginLogoutSolvent.end();
	}
	/**
	 * Test frequently asked questions page.
	 */
	@Test
	public void verifyFAQ() {
		setCustomURL("http://pspl-qa011:8080/login.jsp");
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		loginLogoutSolvent.cannonLogin(getParamStrings("users")[1], getParamStrings("passwords")[1]);
		common.clickLinkByName("FAQ");
		Assert.assertEquals("Who do you call not found", true, common.isLinkPresent("Who do you call when the going gets tough?"));
		Assert.assertEquals("Ultimate answer not found", true, common.isLinkPresent("What is the answer to every question?"));
		Assert.assertEquals("Worst Discussion Topics answer not found", true, common.isTextPresent("Who has the loudest sneeze - Dave or Steve"));
		Assert.assertEquals("Who do you call answer not found", true, common.isTextPresent("The QA group, of course"));
		Assert.assertEquals("Ultimate answer answer not found", true, common.isTextPresent("42"));
		
		loginLogoutSolvent.end();
	}
	
	/**
	 * As a cleanup process for the rest of automation and future automation scripts, this 
	 * method delete all the stars account got created in @see createLoginAccounts.
	 */
	@Test
	public void deleteAccounts() {
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Import Account");
		Assert.assertEquals("Operator: Account Import", common.getYukonText("Operator: Account Import"));

		common.enterInputText("account/uploadImportFiles", "accountImportFile", getParamString("ImportInsertAccount"));
		common.clickButtonBySpanText("Prescan");
		if(common.isTextPresent("Finished - Passed"))
			common.clickFormButtonByButtonId("account/doAccountImport", "importButton");
		if(common.isTextPresent("0 Added, 0 Updated, 56 Removed"))
			common.clickLinkByName("Back to Account Import");
		common.end();
	}	

}
