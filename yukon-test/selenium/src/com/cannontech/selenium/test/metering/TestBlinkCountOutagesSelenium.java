package com.cannontech.selenium.test.metering;
import org.junit.Test;
import org.testng.Assert;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
/**
 * This class tests Blink Counts and Outages.This tests searches for a Meter,Reads the Meter and gets
 * the returned Reading value using one of the Solvent Methods.
 * @author anjana.manandhar
 */
public class TestBlinkCountOutagesSelenium extends SolventSeleniumTestCase {
	/**
	 * Creating an array variable 'meters' and assigning its value to null.
	 */
	String [] meters = null;
	public void init() {
		//Starts the session for the test. 
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	
	/**
	 * Method verifies the ability to read Blink Counts and Outages on MCT-410IL,MCT-410CL,MCT-410FL,
	 * MCT-430A,MCT-430A3,MCT-430S4,MCT-470S4,MCT-470A.
	 */
	@Test
	public void blinkCount() {
		init();
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();

		common.clickLinkByName("Metering");
		String[] meters = getParamStrings("devicename");
		
		for(String meter : meters) {
			common.enterInputTextByFormId("filterForm", "Quick Search", meter);
			common.clickButtonBySpanText("Search");

			Assert.assertEquals(meter, new CommonSolvent().getPageTitle());
			widget.getWidgetTitle("Meter Information");
			widget.expandCollapseWidgetByTitle("Meter Information");
			widget.expandCollapseWidgetByTitle("Meter Information");
			
			Assert.assertEquals("Device Name:", common.getYukonText("Device Name:"));
			Assert.assertEquals("Enabled", common.getYukonText("Enabled"));
			
			widget.getWidgetTitle("Outages");
			widget.clickButtonInWidgetByType("Outages", "button");
			
			String blinkCount = widget.getTextFromWidgetByLabel("Outages", "Blink Count:");
			String[] splitCount = blinkCount.split("[ ]");
			for(String txt : splitCount) {
				if(txt != null) {
				    Assert.assertEquals(splitCount[1], "Counts");
				}
			}
			common.clickLinkByName("Metering");
		}
		common.end();
	}
}
 