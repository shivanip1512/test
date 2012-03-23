package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;

/**
 * This test case contains test procedures for testing the integration of Eka 
 * meters for meter information and meter data supported in the Yukon 5.2 metering web user-interface.
 * @author Ricky Jones
 */
public class TestEkaMeterInformationSelenium extends SolventSeleniumTestCase {
	/**
	 * This test logs into yukon and verifies Eka meter information
	 */
	@Test
	public void configureDevice() {
		setCustomURL("http://pspl-qa008.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");	
		CommonSolvent common = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		MeteringHelper metering = new MeteringHelper();
		
		//reading in data from xml
		String[] meterNames = getParamStrings("meterName");

		//Read all valid devices
		for(String meter: meterNames){
			common.clickLinkByName("Metering");
			metering.navigateToMeterDetail(meter);
			
			// verify meter information
			Assert.assertEquals("Device name is present.",
					widget.getTextFromWidgetByLabel("Meter Information", "Device Name:").isEmpty(),
					false);
			Assert.assertEquals("Meter number is present.",
					widget.getTextFromWidgetByLabel("Meter Information", "Meter Number:").isEmpty(),
					false);
			Assert.assertEquals("Type is present.",
					widget.getTextFromWidgetByLabel("Meter Information", "Type:").isEmpty(),
					false);
			Assert.assertEquals("Serial number is present.",
					widget.getTextFromWidgetByLabel("Meter Information", "Serial Number:").isEmpty(),
					false);
			Assert.assertEquals("Model is present.",
					widget.getTextFromWidgetByLabel("Meter Information", "Model:").isEmpty(),
					false);
			Assert.assertEquals("Manufacturer is present.",
					widget.getTextFromWidgetByLabel("Meter Information", "Manufacturer:").isEmpty(),
					false);
			Assert.assertEquals("Status is present.",
					widget.getTextFromWidgetByLabel("Meter Information", "Status:").isEmpty(),
					false);
			
			new YukonTopMenuSolvent().clickTopMenuItem("Home");
		}
		common.end();
	}
}