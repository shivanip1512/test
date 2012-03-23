package com.cannontech.selenium.test.metering;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;

/**
 * @author Jon Narr
 * Jira Id: QA-102
 * The following tests the ability to ping all MCT-410, 430, and 470 meters
 * and verify their status.
 */
public class TestMeterInformationSelenium extends SolventSeleniumTestCase {
	
	/**
	 * This test logs into yukon and attempts to read a meter's usage, previous usage,
	 * total consumption and demand and verify that a second reading is greater than 
	 * or equal to the original values.
	 */
	@Test
	public void pingMeters() {
		//start the session for the test. 
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		//reading in data from xml
		String[] meterNames = getParamStrings("meterName");

		//Read all valid devices
		for(String meter: meterNames){
			common.clickLinkByName("Metering");
			Assert.assertEquals("Metering", common.getPageTitle());
	        common.enterInputTextByFormId("filterForm", "Quick Search", meter);
			common.clickButtonBySpanText("Search");

			//Verify meter was found
			Assert.assertEquals(meter, common.getYukonText(meter));
			//Verify meter's status is enabled
			Assert.assertEquals("Enabled", common.getYukonText("Enabled"));
			
			// ping meter
			common.clickButtonBySpanTextWithAjaxWait("Ping");
//			waitTenSeconds();
			Assert.assertEquals("Successful Read", common.getYukonText("Successful Read"));	
			common.clickLinkByName("Metering");
		}
		common.end();
	}
}
