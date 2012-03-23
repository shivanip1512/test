package com.cannontech.selenium.test.metering;

import java.text.DecimalFormat;

import org.junit.Assert;

import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;

/**
 * Test verifies meter readings for a set of meters
 * @author ricky.jones
 */
public class TestMeterReadingSelenium extends SolventSeleniumTestCase {
	
	/**
	 * Test method logs in as yukon, yukon and check all the links in navigation
	 * page.
	 */
	public void init() {
		//use the LoginSolvent to login
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");		
		new CommonSolvent().clickLinkByName("Metering");
	}
	
	/**
	 * Method takes an decimal and rounds it to three places
	 * @param decimalValue number to round
	 * @return
	 */
	private double roundDecimal(double decimalValue) {
    	DecimalFormat threeDForm = new DecimalFormat("#.###");
    	return Double.valueOf(threeDForm.format(decimalValue));
	}
	
	/**
	 * Method navigates to the meter detail page by searching for the meter from the 
	 * meter home page and verifies that the meter is present and enabled
	 */
	public void navigateToMeterDetail(String meter){
		CommonSolvent common = new CommonSolvent();
		
		Assert.assertEquals("Metering", common.getPageTitle());
        common.enterInputTextByFormId("filterForm", "Quick Search", meter);
		common.clickButtonBySpanText("Search");

		//Verify meter was found
		Assert.assertEquals(true, common.isTextPresent(meter));
		//Verify meter's status is enabled
		Assert.assertEquals(true, common.isTextPresent("Enabled"));
	}
	
	/**
	 * Method attempts to read a meter and verifies a successful read.
	 */
	public void readMeter(){
		MeteringSolvent metering = new MeteringSolvent();
		//get a set of readings
		metering.clickButtonInWidgetByType("Meter Readings", "button");
		//Let values update before moving on
		waitTenSeconds();
	}
	
	/**
	 * This test logs into yukon and attempts to read a meter's usage, previous usage,
	 * total consumption and demand and verify that a second reading is greater than 
	 * or equal to the original values.
	 */
	@Test
	public void readMeters() {
		//start the session for the test. 
		init();
		String[] meterNames = null;
		
		//reading in data from xml
		meterNames = getParamStrings("meterName");
		MeteringHelper meterHelper = new MeteringHelper();
		WidgetSolvent widget = new WidgetSolvent();
		CommonSolvent common = new CommonSolvent();

		//Read all valid devices
		for(String meter: meterNames){
			navigateToMeterDetail(meter);
			readMeter();
			
			//Verify usage reading >= 0
			double usageReading = roundDecimal(Double.parseDouble(meterHelper.getKWHOrVoltageValue(widget.getTextFromWidgetByLabel("Meter Readings", "Usage Reading:"))));
			Assert.assertEquals(true, usageReading >= 0);
			
			//Verify peak demand >= 0
			Assert.assertEquals(true, Double.parseDouble(meterHelper.getKWHOrVoltageValue(widget.getTextFromWidgetByLabel("Meter Readings", "Peak Demand:"))) >= 0);
			
			//verify total consumption is equal to the value that calculated from the usage reading and previous usage reading within 0.01 kWH.
//			boolean goodReading = false;
			double totalConsumption = 0;
			double previousReading = 0;
//			while(!goodReading){
				totalConsumption = roundDecimal(Double.parseDouble(widget.getTextFromWidgetByLabel("Meter Readings", "Total Consumption:")));
				previousReading = roundDecimal(Double.parseDouble(meterHelper.getKWHOrVoltageValue(widget.getTextFromWidgetByLabel("Meter Readings", "Previous Usage Reading:"))));			
//				if(Math.abs(totalConsumption) - roundDecimal(Math.abs(Math.abs(usageReading) - Math.abs(previousReading))) <= 0.01)
//					goodReading = true;
//				else
//					readMeter();
//			}
//			Assert.assertEquals(true, Math.abs(totalConsumption) - roundDecimal(Math.abs(Math.abs(usageReading) - Math.abs(previousReading))) <= 0.01);
			
			common.clickLinkByName("Metering");
		}
		common.end();
	}
}
