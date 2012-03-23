/**
 * 
 */
package com.cannontech.selenium.test.metering;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.OperationsPageSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;


/**
 * This class tests searches for a Meter,then verifies the ability to disconnect and connect MCT 410.
 * 
 * @author anjana.manandhar
 *
 */
public class TestDisconnectMeterSelenium extends SolventSeleniumTestCase {

	public void init() {
		//Starts the new session
		setCustomURL("http://pspl-qa008.cannontech.com:8080");
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		loginLogoutSolvent.cannonLogin("yukon", "yukon");
	}
	
	/**
	 * Method gets the Current Date and Time using SimpleDateFormat MM/dd/yyyy HH:mm:ss
	 */
	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}	
	
	/**
	 * Method verifies the ability to Disconnect and Connect MCT410CL.
	 */
	@Test
	public void verifyDisconnect() {
		init();
		OperationsPageSolvent operationSolvent = new OperationsPageSolvent();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		
		//Search for the Meter
		operationSolvent.clickLinkItem("Metering");
		Assert.assertEquals("Metering", new CommonSolvent().getPageTitle());
        common.enterInputTextByFormId("filterForm", "Quick Search", "MCT-410CL (325061)");
        common.clickButtonBySpanText("Search");

		Assert.assertEquals("MCT-410CL (325061)", new CommonSolvent().getPageTitle());
		widget.getWidgetTitle("Meter Information");
		widget.expandCollapseWidgetByTitle("Meter Information");
		widget.expandCollapseWidgetByTitle("Meter Information");
		Assert.assertEquals("MCT-410CL (325061)", common.getYukonText("MCT-410CL (325061)"));
		Assert.assertEquals("Enabled",common.getYukonText("Enabled"));
		widget.getWidgetTitle("Disconnect");
		
		//Read meter status and Disconnect meter 
		common.clickButtonBySpanTextWithElementWait("Read Status");
		waitTenSeconds();
		Assert.assertEquals("Connected", widget.getTextFromWidgetByLabel("Disconnect", "Disconnect Status:").split(" " + common.getCurrentDate())[0]);
		common.clickButtonBySpanTextWithElementWait("Disconnect");
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		common.clickButtonBySpanTextWithElementWait("Read Status");
		waitFiveSeconds();
		Assert.assertEquals("Unconfirmed Disconnected", widget.getTextFromWidgetByLabel("Disconnect", "Disconnect Status:").split(" " + common.getCurrentDate())[0]);
		
		//Read meter status and Connect meter
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		common.clickButtonBySpanTextWithElementWait("Read Status");
		waitFiveSeconds();
		Assert.assertEquals("Confirmed Disconnected", widget.getTextFromWidgetByLabel("Disconnect", "Disconnect Status:").split(" " + common.getCurrentDate())[0]);
		common.clickButtonBySpanTextWithElementWait("Connect");
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		Assert.assertEquals("Connect Armed", widget.getTextFromWidgetByLabel("Disconnect", "Disconnect Status:").split(" " + common.getCurrentDate())[0]);
		
		//Read meter status
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		waitTenSeconds();
		common.clickButtonBySpanTextWithElementWait("Read Status");
		waitFiveSeconds();
		Assert.assertEquals("Connected", widget.getTextFromWidgetByLabel("Disconnect", "Disconnect Status:").split(" " + common.getCurrentDate())[0]);
		
		loginLogoutSolvent.end();
	}
}
