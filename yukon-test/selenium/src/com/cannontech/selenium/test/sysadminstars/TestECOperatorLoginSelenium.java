package com.cannontech.selenium.test.sysadminstars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.stars.EnergyCompanySolvent;
/**
 * This Class adds, then edits the properties of the appliance category
 * @author Kevin Krile	
 */
public class TestECOperatorLoginSelenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as starsop11/starsop11
	 */
	public void init(){
		start();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		loginlogoutsolvent.cannonLogin("starsop11", "starsop11");
	}
	/**
	 * This test logs in as starsop11 and creates an operator login for QA_Test10
	 */
	@Test
	public void addLogin(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = {"TestOper10-01","TestOper10-02"};

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Operator Logins");
		for(int k=0;k<name.length;k++){
			common.clickButtonBySpanText("Create");
			common.enterInputText("operatorLogin/update", "username", name[k]);
			common.enterInputText("operatorLogin/update", "password1", name[k]);
			common.enterInputText("operatorLogin/update", "password2", name[k]);
			common.clickFormButton("operatorLogin/update", "create");
			Assert.assertEquals("Successfully created the login.", common.getYukonText("Successfully created the login."));	
			Assert.assertEquals(false, common.isLinkPresent("DISABLED"));
			Assert.assertEquals(true, common.isLinkPresent(name[k]));
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and edits the operator logins for QA_Test10
	 */
	@Test
	public void editLogin(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = {"TestOper10-01","TestOper10-02"};
		String[] edit = {"TestOper10-01Edit","TestOper10-02Edit"};
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Operator Logins");
		for(int k=0;k<name.length;k++){
			common.clickLinkByName(name[k]);
			common.clickButtonBySpanText("Edit");
			common.enterInputText("operatorLogin/update", "username", edit[k]);
			common.enterInputText("operatorLogin/update", "password1", edit[k]);
			common.enterInputText("operatorLogin/update", "password2", edit[k]);
			common.clickButtonBySpanText("Save");
			Assert.assertEquals("Successfully updated the login.", common.getYukonText("Successfully updated the login."));	
			common.assertEqualsLinkTextNotPresent("DISABLED");
			Assert.assertEquals(true, common.isLinkPresent(edit[k]));
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and clicks the disable button for the operator login for QA_Test10
	 */
	@Test
	public void disableLogin(){
		init();
		CommonSolvent common = new CommonSolvent();
		EnergyCompanySolvent energy = new EnergyCompanySolvent();
		String[] edit = {"TestOper10-01Edit","TestOper10-02Edit"};

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Operator Logins");
		for(int k=0;k<edit.length;k++){
			energy.disableOperatorLogin(edit[k]);
			//need to refresh to see the (button) Link say DISABLED
			common.clickLinkByName("Operator Logins");
			Assert.assertEquals(true, common.isLinkPresent("DISABLED"));
		}
	common.end();
	}
	/**
	 * This test tries to login with the disabled logins' username/password
	 */
	@Test
	public void failLoginDisabledLogin(){
		start();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		String[] edit = {"TestOper10-01Edit","TestOper10-02Edit"};
		
		for(int i=0;i<edit.length;i++){
			loginlogoutsolvent.cannonLogin(edit[i], edit[i]);
			Assert.assertEquals("* Invalid username/password",common.getYukonText("* Invalid username/password"));
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and deletes some logins for QA_Test10
	 */
	@Test
	public void deleteLogin(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] edit = {"TestOper10-01Edit","TestOper10-02Edit"};

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Operator Logins");
		for(int i = 0;i < edit.length; i++){
			common.clickLinkByName(edit[i]);
			common.clickButtonBySpanText("Edit");
			common.clickButtonBySpanTextWithElementWait("Delete");
			common.clickButtonBySpanText("OK");
			Assert.assertEquals("Successfully deleted the login.", common.getYukonText("Successfully deleted the login."));	
			common.assertEqualsLinkTextNotPresent(edit[i]);
		}
		common.end();
	}
}
