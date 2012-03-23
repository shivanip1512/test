package com.cannontech.selenium.test.stars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;

/**
 * This class use xml file to store the location of the data files and use that 
 * file to import accounts. The idea of this class to test import and delete accounts scenarios. 
 *  
 * @author anuradha.uduwage
 * s junod  -  edit 9/22/2010 - new Import Account pages  
 */
public class TestImportAccountsSelenium extends SolventSeleniumTestCase {

	/**
	 * Method logs in as "Starsop" user and import accounts.
	 */
	@Test
	public void testInsertAccounts() {	
		start();
		CommonSolvent common = new CommonSolvent();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
		
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
	 * Delete accounts using csv file's CUS_ACTION column value. For this test that 
	 * value has been set to REMOVE.
	 */
	@Test
	public void testDeleteAccounts() {
		start();
		CommonSolvent common = new CommonSolvent();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
		
		common.clickLinkByName("Import Account");
		Assert.assertEquals("Operator: Account Import", common.getPageTitle());
		common.enterInputText("account/uploadImportFiles", "accountImportFile", getParamString("ImportDeleteAccount"));
		common.clickButtonBySpanText("Prescan");
		if(common.isTextPresent("Finished - Passed"))
			common.clickFormButtonByButtonId("account/doAccountImport", "importButton");
		if(common.isTextPresent("0 Added, 0 Updated, 56 Removed"))
			common.clickLinkByName("Back to Account Import");
		common.end();
	}
}