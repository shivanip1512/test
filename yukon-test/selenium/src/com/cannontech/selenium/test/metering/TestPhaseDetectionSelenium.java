package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * QA-462: Test phase detection's basic ui navigation and procedure.
 * @author jon.narr
 *
 */
class LocalSolvent extends AbstractSolvent {
	/**
	 * @param params
	 */
	public LocalSolvent(String... params) {
		super(params);
	}
	@Override
	public void prepare() {
		selenium.waitForPageToLoad(2000);
	}
	/**
	 * Enter text in the Find textbox and click on GO button
	 * @param enterText searchable text.
	 * @return
	 */
	public void selectCheckBox(String chkBoxLabel) {
		String searchTextField = "//table[@class='resultsTable']//following::td[2]/input[1]";
		selenium.waitForElement(searchTextField);
		if(!selenium.isElementPresent(searchTextField))
			throw new SeleniumException("Search text field " + searchTextField + "is not available");
		selenium.click(searchTextField);
	}
}
public class TestPhaseDetectionSelenium extends SolventSeleniumTestCase {
	public void init() {
		//Starts the new session
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");
	}
	/**
	 * Method verifies the  
	 */
	@Test
	public void aTest() {
		init();
		CommonSolvent common = new CommonSolvent();
		
		// Enter metering section
		common.clickLinkByName("Metering");	
		Assert.assertEquals("Metering", common.getPageTitle());
		
		// Enter phase detection area
		common.clickLinkByName("Phase Detect");
		
		// Select a substation
		common.selectDropDownMenuByIdName("substations", "QA_Substation");
		
		// Select radio button to read after phase detection
		common.clickRadioButtonByLabelValue("after");
		common.clickFormButton("phaseDetect/saveSubstationAndReadMethod", "next");
		Assert.assertEquals("Metering: Phase Detection ()", common.getPageTitle());
		new LocalSolvent().selectCheckBox("CCU-711-01(11)");
		common.clickFormButtonByButtonId("phaseDetect/saveBroadcastRoutes", "nextButton");
		Assert.assertEquals("Metering: Phase Detection (Clear Phase Data)", common.getPageTitle());
		
		// Clear existing phase data
		common.clickButtonByNameWithPageLoadWait("Clear Phase Data");
		Assert.assertEquals("Metering: Phase Detection (Test Settings)", common.getPageTitle());
		common.clickFormButton("phaseDetect/saveTestSettings", "next");
		Assert.assertEquals("Metering: Phase Detection (Phase Detection Test)", common.getPageTitle());
		
		// Run phase detection test
		common.clickButtonByName("Send");
		waitTenSeconds();
		waitFiveSeconds();
		Assert.assertEquals(true, common.isTextPresent("until you should tell the lineman to adjust the voltage regulators."));
		Assert.assertEquals(true, common.isTextPresent("until you can tell the lineman to return the voltage regulators to normal."));
		common.clickFormButton("phaseDetect/phaseDetectResults", "cancel");
    	common.end();
	}
}
