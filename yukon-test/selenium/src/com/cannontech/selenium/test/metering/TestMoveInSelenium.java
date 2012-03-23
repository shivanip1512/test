package com.cannontech.selenium.test.metering;
import org.junit.Test;
import org.testng.Assert;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;
/**
 * This class tests verifies the ability to Set and Read an MCT-410IL, MCT410CL and MCT410FL MoveIn.
 * @author anjana.manandhar
 */
public class TestMoveInSelenium extends SolventSeleniumTestCase {
	public void init() {
		//Starts the session for the test. 
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");		
	}
	/**
	 * Method tests MoveIn function on MCT-410 Meters
	 */
	@Test
	public void moveIn() {
		//start the session for the test. 
		init();
		String[] meterNames = {"MCT-410iL-02 (1471788)", "MCT-410CL-01 (130393)", "MCT-410FL-04 (100600)"};
		CommonSolvent common = new CommonSolvent();
		EventsByTypeSolvent events = new EventsByTypeSolvent();
		
		//Move out a 410 IL, CL, and FL
		for(String meter : meterNames) {
			common.clickLinkByName("Metering");
			Assert.assertEquals("Metering", common.getPageTitle());
            common.enterInputTextByFormId("filterForm", "Quick Search", meter);
			common.clickButtonBySpanText("Search");

			//Verify meter was found
			Assert.assertEquals(true, common.isTextPresent(meter));
			//Verify meter's status is enabled
			Assert.assertEquals(true, common.isTextPresent("Enabled"));
			new MeteringSolvent().clickLinkByWidget("Actions", "Move In");
			common.enterInputText("meter/moveInRequest", "moveInDate", events.returnDate(0));
			common.clickButtonByNameWithPageLoadWait("Move In");
			//Verify meter reading message
			Assert.assertEquals(true, common.isTextPresent("A meter reading for "+meter+" is scheduled to be retrieved for "+events.returnDate(0)+" 00:00:00."));
		}
		common.end();
	}
}
