package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * Test verifies meter profiles for a set of meters
 * @author ricky.jones
 */
public class TestLoadProfileSelenium extends SolventSeleniumTestCase {
	/**
	 * This test verifies configurations and reports for load and voltage profile
	 */
	@Test
	public void startProfileCollection() {
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		//reading in data from xml
		String[] meterNames = getParamStrings("meterName");
		MeteringHelper meterHelper = new MeteringHelper();
		WidgetSolvent widget = new WidgetSolvent();
		LoadProfileSolvent local = new LoadProfileSolvent();
		CommonSolvent common = new CommonSolvent();
		

		//For all specified devices
		for(String meter: meterNames){
			common.clickLinkByName("Metering");
			//Verify meter details
			meterHelper.navigateToMeterDetail(meter);
			//Verify device name
			Assert.assertEquals(meter, widget.getTextFromWidgetByLabel("Meter Information", "Device Name:"));
			//Verify status
			Assert.assertEquals("Enabled", widget.getTextFromWidgetByLabel("Meter Information", "Status:"));

			//Verify load and voltage profile collection
			widget.clickLinkByWidgetWithPageLoadWait("Actions", "Profile");
			Assert.assertEquals("Metering: Profile", common.getPageTitle());
			Assert.assertEquals("Load profile contains a value", true, meterHelper.valueExists(widget.getTextFromWidgetByLabel("Profile Collection", "Load Profile")));
			Assert.assertEquals("Voltage profile contains a value", true, meterHelper.valueExists(widget.getTextFromWidgetByLabel("Profile Collection", "Voltage Profile")));

			//Verify Action link of specified channels
			local.clickActionLinkByChannel("Load Profile");
			local.clickActionLinkByChannel("Voltage Profile");
			
			//Request past load profile
			common.selectDropDownMenuByAttributeName("channel", "Load Profile");
			common.enterTextByAttribute("id", "email", "psplqa011@gmail.com");
			common.clickButtonBySpanTextWithElementWait("Start");
			
			//Request past voltage profile
			common.selectDropDownMenuByAttributeName("channel", "Voltage Profile");
			common.enterTextByAttribute("id", "email", "psplqa011@gmail.com");
			common.clickButtonBySpanTextWithElementWait("Start");
			waitFiveSeconds();
			
			//Daily usage report
			common.clickButtonBySpanText("View Report");
			Assert.assertEquals("Daily Usage", common.getPageTitle());

			//A back button could be useful here
			common.clickLinkByName("Metering");
			meterHelper.navigateToMeterDetail(meter);
			widget.clickLinkByWidgetWithPageLoadWait("Actions", "Profile");
			Assert.assertEquals("Metering: Profile", common.getPageTitle());
			
			//Peak Summary report - peak daily usage
			common.selectDropDownMenuByAttributeName("peakType", "Peak Daily Usage");
			common.clickButtonBySpanTextWithElementWait("Get Report");
			waitTenSeconds();
			Assert.assertEquals("Verify Peak Daily Usage is displayed", true, common.isTextPresent("Peak Daily Usage"));
			
			//Peak Summary report - peak hourly usage
			common.selectDropDownMenuByAttributeName("peakType", "Peak Hourly Usage");
			common.clickButtonBySpanTextWithElementWait("Get Report");
			waitTenSeconds();
			Assert.assertEquals("Verify Peak Hourly Usage is displayed", true, common.isTextPresent("Peak Hourly Usage"));
			
			//Peak Summary report - peak interval
			common.selectDropDownMenuByAttributeName("peakType", "Peak Interval");
			common.clickButtonBySpanTextWithElementWait("Get Report");
			waitTenSeconds();
			Assert.assertEquals("Verify Peak Interval is displayed", true, common.isTextPresent("Peak Interval"));
		}
		common.end();
	}
}

/**
 * Solvent for local test development
 * @author ricky.jones
 */
class LoadProfileSolvent extends AbstractSolvent {

	/**
	 * @param params
	 */
	public LoadProfileSolvent(String... params) {
		super(params);
	}

	@Override
	public void prepare() {
		selenium.waitForPageToLoad(2000);
	}
	
	/**
	 * Clicks a button that is inside a widget by its value attribute. Since the Action link is also a button,
	 * this method verifies that it isn't a toggle button to avoid confusion with the "Start" action link
	 * and "Start" button in the Profile Collection widget.
	 * @param widgetTitle name/title of the widget.
	 * @param buttonValue value attribute of button
	 * @return
	 */
	public LoadProfileSolvent clickButtonInWidgetByValue(String widgetTitle, String buttonValue) {
		String buttonLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::input[not(contains(@name, 'toggle')) and @value ='" + buttonValue + "']";
		String widgetName = getXpathRootForWidgetTitle(widgetTitle);
		selenium.waitForAjax();
		selenium.waitForElement(widgetName, 5000);
		if(!selenium.isElementPresentAndVisible(widgetName, 5000))
			throw new SolventSeleniumException("Widget with the name " + widgetTitle + " is not available");
		selenium.waitForElementToBecomeVisible(buttonLocator, 5000);
		if(!selenium.isElementPresentAndVisible(buttonLocator, 5000))
			throw new SolventSeleniumException("Button " + buttonValue + " is not available in widget " + widgetTitle);
		selenium.waitForAjax();
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Clicks the Action link that is inside the Profile Collection widget by its channel name and cancels.
	 * @param channel name of profile channel
	 * @return
	 */
	public LoadProfileSolvent clickActionLinkByChannel(String channel) {				
		String linkLocator = getXpathRootForWidgetTitle("Profile Collection") 
				+ "/following::td[contains(text(), '"+channel+"')]/following::a[contains(@title, 'Stop') or contains(@title, 'Start')][1]";
		String cancelLinkLocator = linkLocator + "/following::a[contains(text(), 'Cancel')]";
		String widgetName = getXpathRootForWidgetTitle("Profile Collection");
		selenium.waitForAjax();
		selenium.waitForElement(widgetName, 5000);
		if(!selenium.isElementPresentAndVisible(widgetName, 5000))
			throw new SolventSeleniumException("Widget with the name " + "Profile Collection" + " is not available");
		selenium.waitForElementToBecomeVisible(linkLocator, 5000);
		if(!selenium.isElementPresentAndVisible(linkLocator, 5000))
			throw new SolventSeleniumException("Button " + channel + " is not available in widget " + "Profile Collection");
		selenium.waitForAjax();
		selenium.click(linkLocator);
		selenium.waitForAjax();
		selenium.waitForElementToBecomeVisible(cancelLinkLocator, 5000);
		if(!selenium.isElementPresentAndVisible(cancelLinkLocator, 5000))
			throw new SolventSeleniumException("cancel link is not available in widget " + "Profile Collection");
		selenium.click(cancelLinkLocator);
		return this;
	}
	
	/**
	 * Construct Xpath root for the title of the widget.
	 * @param widgetTitle title of the wideget.
	 * @return root xpath for the title of the widget.
	 */
	protected String getXpathRootForWidgetTitle(String widgetTitle) {
		String root = "//div[@class='title boxContainer_title' and contains(text(),'" + widgetTitle + "')]";
		return root;
	}
}