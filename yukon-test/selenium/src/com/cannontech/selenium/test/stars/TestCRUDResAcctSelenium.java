package com.cannontech.selenium.test.stars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;

/**
 * This test class creates, verifies, updates, and deletes
 * Stars Residential customer accounts.
 * @author Steve.Junod
 *
 */
public class TestCRUDResAcctSelenium extends SolventSeleniumTestCase {

	/*
	 * Create an array variable for Res acct info
	 * and assigning its value to null.
	 */
	String[] acctNum = null;
	String[] custNum = null;
	String[] lastName = null;
	String[] firstName = null;
	String[] addr1 = null;
	String[] city = null;
	String[] state = null;
	String[] zip = null;
	String[] userName = null;
	String[] password = null;
	String[] confPass = null;

	/**
	 * this method creates the Stars Res account
	 */
	@Test
	public void createResAccounts() {	
		start();
				
		//  login as Stars operator
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
		CommonSolvent common = new CommonSolvent();

		// read from the  XML  file
		acctNum = getParamStrings("AcctNum");
		custNum = getParamStrings("CustNum");
		lastName = getParamStrings("LastName");
		firstName = getParamStrings("FirstName");
		addr1 = getParamStrings("Addr1");
		city = getParamStrings("City");
		state = getParamStrings("State");
		zip = getParamStrings("Zip");
		userName = getParamStrings("UserName");
		password = getParamStrings("Password");
		confPass = getParamStrings("ConfPass");

		// get to New Accounts page, read data from array 
		// and create Stars Res account(s)
		for(int i=0; i < acctNum.length; i++) {
			
			new CommonSolvent().clickLinkByName("New Account");	
			Assert.assertEquals("Operator: Create Account", common.getPageTitle());

			if(acctNum[i] != null) {
				common.enterInputText("account/createAccount", "accountDto.accountNumber", acctNum[i]);
				common.enterInputText("account/createAccount", "accountDto.customerNumber", custNum[i]);
				common.enterInputText("account/createAccount", "accountDto.lastName", lastName[i]);
				common.enterInputText("account/createAccount", "accountDto.firstName", firstName[i]);
				common.enterInputText("account/createAccount", "accountDto.streetAddress.locationAddress1",  addr1[i]);
				common.enterInputText("account/createAccount", "accountDto.streetAddress.cityName", city[i]);
				common.enterInputText("account/createAccount", "accountDto.streetAddress.stateCode", state[i]);
				common.enterInputText("account/createAccount", "accountDto.streetAddress.zipCode", zip[i]);
				common.selectCheckBox("Same As Above");
				common.enterInputText("account/createAccount", "loginBackingBean.username", userName[i]);
				common.enterInputText("account/createAccount", "loginBackingBean.password1", password[i]);
				common.enterInputText("account/createAccount", "loginBackingBean.password2", confPass[i]);
										
				//  save the acct info 
				common.clickButtonBySpanText("Create");

				//  Account Edit page displays, verify Page Title (yuk-9134)
				Assert.assertEquals("Operator: Account (" + acctNum[i] + ")", common.getPageTitle());
				Assert.assertEquals("Account Created", common.getYukonText("Account Created"));
				common.clickLinkByName("Home");
			}
		}
		common.end();
	}	
	
	/**
	 * this method deletes the Accounts created in above method
	 */
	@Test
	public void deleteResAccounts() {	
		start();
				
		//  login as Stars operator
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();

		// read Account Numbers from the  XML  file
		acctNum = getParamStrings("AcctNum");

		// perform Account # search, delete each account
	
		for(int i=0; i < acctNum.length; i++) {
			
			stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", acctNum[i]);
			common.clickMagnifyingIcon("Search for existing customer:");
			// Account Edit page displays, verify Page Title (yuk-9134)
			Assert.assertEquals("Operator: Account (" + acctNum[i] + ")", common.getPageTitle());
										
			//  delete the acct 
			common.clickButtonByTitleWithPageLoadWait("Edit");
			common.clickStarsButtonByName("Delete");
			Assert.assertEquals("Confirm Delete", common.getYukonText("Confirm Delete"));
			common.clickButtonBySpanText("OK");
			
			Assert.assertEquals("Account Deleted", common.getYukonText("Account Deleted"));
			common.clickLinkByName("Home");
		}
		common.end();
	}
}
