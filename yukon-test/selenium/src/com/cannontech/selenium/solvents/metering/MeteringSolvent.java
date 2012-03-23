/**
 * 
 */
package com.cannontech.selenium.solvents.metering;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Handle functionalities in Metering page. Idea is to have sperate methods 
 * for each widget.
 * TODO: Should this be MeterWidgetSolvent
 * @author anuradha.uduwage
 *
 */
public class MeteringSolvent extends WidgetSolvent {

	/**
	 * @param params
	 */
	public MeteringSolvent(String... params) {
		super(params);
	}

	@Override
	public void prepare() {
		selenium.waitForAjax();
	}
	
	/**
	 * TODO: This is not good but due to crazy dom structure we can't do much for now.
	 * TODO: Should implement a method in super class to get widget by its header.
	 * @return
	 */
	public MeteringSolvent clickMeterSearch() {
		String locator = "//input[@type='submit' and @value='Search']";
		if(!selenium.isElementPresent(locator))
			throw new SeleniumException("Search Button is not available ");
		String elementInForm = "//form[@id='filterForm']/input[@value='Filter']";
		selenium.click(locator);
		selenium.waitForPageToLoad();
		selenium.waitForElementToBecomeVisible(elementInForm, 2000);
		return this;
	}
	
	/**
	 * This method specifically for filter criteria on the meter search page.
	 * @param filter name of the text box of filter criteria
	 * @param text input text.
	 * @return YukonTableSolvent 
	 */
	public YukonTableSolvent enterMeterFilterCriteria(String filter, String text) {
		String inputLocator = "//div[contains(text(), '"+ filter + "')]/following::input[1]";
		selenium.waitForElement(inputLocator, 2000);
		if(!selenium.isElementPresent(inputLocator))
			throw new SeleniumException("Can not find input text box for '" + filter +"' to type '" + text + "'.");
		selenium.type(inputLocator, text);
		return new YukonTableSolvent();
	}
	
	/**
	 * This method specifically selects the checkbox on the Device Group Upload page.
	 * @return MeteringSolvent 
	 */
	public MeteringSolvent selectOptionsCheckbox() {
		String locator = "//input[@type='checkbox']";
		selenium.waitForElement(locator, 2000);
		if(!selenium.isElementPresent(locator))
			throw new SeleniumException("Can not find checkboxbox");
		selenium.click(locator);
		return this;
	}
	
	/**
	 * Click create button inside any widget available in Metering page.
	 * @param widgetHeader Title of the widget.
	 * @return
	 */
	public MeteringSolvent clickCreateByWidget(String widgetHeader) {
		String inputLocator = "//input/following::div[contains(@id, 'widgetTitledContainer')]" + 
								getXpathRootForWidgetTitle(widgetHeader) +
									"/following::span[@class='rightOfImageLabel' and contains(., 'Create')][1]";
		selenium.waitForElement(inputLocator);
		if(!selenium.isElementPresent(inputLocator))
			throw new SeleniumException("Can not find create button under '" + widgetHeader + "'Widget.");
		selenium.click(inputLocator);
		return this;
	}
	
	/**
	 * Click Device Group Icon in Temper Flag monitor/Schedule/billing page.
	 * @return
	 */
	public MeteringSolvent clickDeviceGroupIcon() {
		String locator = "//td[text()='Select Device Group:']//following::a[contains(@id, 'chooseGroupIcon_')]";
		String groupLocator = "//td[text()='Group:']//following::a[contains(@id, 'chooseGroupIcon_')]";
		String deviceGroupLocator = "//td[text()='Device Group:']//following::a[contains(@id, 'chooseGroupIcon_')]";
		String newGroupIconFolder = "//span[text()='Select Device Group']//following::a[contains(@id, 'chooseGroupIcon_')]";
		if(selenium.isElementPresent(newGroupIconFolder))
			selenium.click(newGroupIconFolder);
		else if(selenium.isElementPresent(locator))
			selenium.click(locator);
		else if(selenium.isElementPresent(groupLocator))
			selenium.click(groupLocator);
		else if(selenium.isElementPresent(deviceGroupLocator))
			selenium.click(deviceGroupLocator);
		else
			throw new SolventSeleniumException("Device group icon is not available at " + locator + " or " + groupLocator);			
		
		return this;
	}
	
	/**
	 * Click Device Group Icon in Attribute form section under Meter Schedule create page.
	 * @return {@link MeterScheduleSolvent}
	 */
	public MeterScheduleSolvent clickDeviceGroupIconAttributeForm() {
		String selectGroupAttribute = "//form[@id='scheduledGroupRequestExecutionForm_attr']//span[text()='Select Device Group']/following::img[1]";
		if(!selenium.isElementPresent(selectGroupAttribute))
			throw new SolventSeleniumException("Unable to find Select Device Group Folder Icon at " + selectGroupAttribute);
		selenium.click(selectGroupAttribute);
		return new MeterScheduleSolvent();
	}
	
	/**
	 * Click Device Group Icon in Command Form section under Meter Schedule Create page.
	 * @return {@link MeterScheduleSolvent}
	 */
	public MeterScheduleSolvent clickDeviceGroupIconCmdForm() {
		String selectGroupCmd = "//form[@id='scheduledGroupRequestExecutionForm_cmd']//span[text()='Select Device Group']/following::img[1]";
		if(!selenium.isElementPresent(selectGroupCmd))
			throw new SolventSeleniumException("Unable to find Select Device Group Folder Icon at " + selectGroupCmd);
		selenium.click(selectGroupCmd);
		return new MeterScheduleSolvent();
	}
	
	/**
	 * This method delete devices from group using the Member widget in Device Group page 
	 * as the root location.
	 * @param deviceName name of the device to be delete.
	 * @return
	 */
	public WidgetSolvent clickDeleteImgByDeviceName(String deviceName) {
		String locator = getXpathRootNonMixMaxWidget("Members") + "//following::a[text()='" + 
			deviceName + "']/following::input[@title='Remove device from group']";
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator))
			throw new SolventSeleniumException("Device " + deviceName + "is not available at " + locator);
		selenium.click(locator);
		return new WidgetSolvent();
	}
	
	/**
	 * Method remove a device from the group using option in side the Device Groups widget. 
	 * @param widgetTitle title of the widget in this case its "Device Groups"/
	 * @param currentGroup current group (link for the group).
	 * @return
	 */
	public WidgetSolvent removeGroupFromDeviceGroup(String widgetTitle, String currentGroup) {
		String removeImgLocator = getXpathRootForWidgetTitle(widgetTitle) + "//following::a[normalize-space(text())='" + 
			currentGroup + "']//following::a[@title='Remove'][1]";
		selenium.waitForElement(removeImgLocator);
		if(!selenium.isElementPresent(removeImgLocator))
			throw new SolventSeleniumException("Current group with name " + currentGroup + " is not available at " + removeImgLocator);
		selenium.click(removeImgLocator);
		return new WidgetSolvent();
	}
		
}
