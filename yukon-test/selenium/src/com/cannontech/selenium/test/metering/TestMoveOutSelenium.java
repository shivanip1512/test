package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.*;
import com.cannontech.selenium.solvents.metering.*;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;

/**
 * This test moves out three 410 meters and verifies that a meter reading is scheduled for tomorrow
 * at 00:00:00
 * @author ricky.jones
 */
public class TestMoveOutSelenium extends SolventSeleniumTestCase {
	/**
	 * Test method logs in as syukon, syukon and check all the links in navigation
	 * page.
	 */
	private void init() {
		//use the LoginSolvent to login
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	/**
	 * Test that all meters are moved out and verify the scheduled meter reading
	 */
	@Test
	public void testMoveOut() {
		//start the session for the test. 
		init();
		String[] meterNames = getParamStrings("meterName");
		CommonSolvent common = new CommonSolvent();
		EventsByTypeSolvent events = new EventsByTypeSolvent();
		
		//Move out a 410 IL, CL, and FL TODO put IL into the xml file
		for(String meter : meterNames) {
			common.clickLinkByName("Metering");
			Assert.assertEquals("Metering", common.getPageTitle());
            common.enterInputTextByFormId("filterForm", "Quick Search", meter);
			common.clickButtonBySpanText("Search");

			//Verify meter was found
			Assert.assertEquals(true, common.isTextPresent(meter));
			//Verify meter's status is enabled
			Assert.assertEquals(true, common.isTextPresent("Enabled"));
			new MeteringSolvent().clickLinkByWidget("Actions", "Move Out");
			common.enterInputText("meter/moveOutRequest", "moveOutDate", events.returnDate(0));
			common.clickButtonByNameWithPageLoadWait("Move Out");
			//Verify meter reading message
			Assert.assertEquals(true, common.isTextPresent("A meter reading for "+meter+" is scheduled to be retrieved for "+events.returnDate(1)+" 00:00:00."));
		}
		common.end();
	}
}
