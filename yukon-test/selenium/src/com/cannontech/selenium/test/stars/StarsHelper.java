/**
 * 
 */
package com.cannontech.selenium.test.stars;

import org.junit.Assert;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.OperationsPageSolvent;

/**
 * TODO DELETE THIS METHOD, NO CLASS USES IT
 * This class will have helper methods to access functionalities that are common to stars and 
 * stars operator section.
 * @author anuradha.uduwage
 *
 */
public class StarsHelper extends SolventSeleniumTestCase {
	
	/**
	 * Method to handle insert account function under stars operator.
	 * XML file should have the name of the Test Class not the name of this method.
	 * @param xmlParameterName value of the name where we define the location of the input file in xml file.
	 */
	public void insertImportAccounts(String xmlParameterName) {	
		new OperationsPageSolvent().clickLinkItem("Import Account");	
		CommonSolvent common = new CommonSolvent();
		Assert.assertEquals("IMPORT ACCOUNT DATA", common.getYukonText("IMPORT ACCOUNT DATA"));

		common.enterText("Customer File:",getParamString(xmlParameterName));
		common.clickButtonByName("Submit");
		if(common.isTextPresent("56 customer accounts imported successfully"))
			common.clickLinkByName("Home");
		common.end();
	}
	
	/**
	 * Method to handle delete account action as a part of import account functionality under stars operator.
	 * XML file should have the name of the Test Class not the name of this method.
	 * @param xmlParameterName value of the name where we define the location of the input file in xml file.
	 */
	public void deleteImportAccounts(String xmlParameterName) {
		new OperationsPageSolvent().clickLinkItem("Import Account");	
		CommonSolvent common = new CommonSolvent();
		Assert.assertEquals("IMPORT ACCOUNT DATA", common.getYukonText("IMPORT ACCOUNT DATA"));

		common.enterText("Customer File:",getParamString(xmlParameterName));
		common.clickButtonByName("Submit");
		if(common.isTextPresent("(0 added, 0 updated, 56 removed)"))
			common.clickLinkByName("Home");
		common.end();
	}	
	
	

}
