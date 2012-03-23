/**
 * 
 */
package com.cannontech.selenium.solvents.metering;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonSolvent;

/**
 * This solvent handle function inside the create schedule page. <br>
 * Create schedule page has two tabs supported by forms. This classed is generated to 
 * handle function for each form.
 * 
 * @author anuradha.uduwage
 *
 */
public class MeterScheduleSolvent extends MeteringSolvent {

	public MeterScheduleSolvent(String... params) {
		super(params);
	}
	
	@Override
	public void prepare() {
		//nothing at this time.
	}
	
	/**
	 * Return start time of the schedule if both radio button and value attribute is available.
	 * @return
	 */
	public String getScheduleStartTime() {
		String fieldLocator = "//input[@type='radio']//following::td/input[@name='starttime']";
		selenium.waitForElement(fieldLocator);
		if(!selenium.isElementPresent(fieldLocator))
			throw new SolventSeleniumException("Unable to find input field at " + fieldLocator + " next to radio button.");
		if(!selenium.isElementPresent(fieldLocator + "/@value"))
			throw new SolventSeleniumException("Value attribute is not available to capture start time.");
		return selenium.getAttribute(fieldLocator + "/@value");
	}
	
	/**
	 * Set schedule start.
	 * @param time string value for the text field.
	 * @return
	 */
	public MeteringSolvent setScheduleStartTime(String time) {
		CommonSolvent common = new CommonSolvent();
		common.clickRadioButtonByName("Time");
		String inputLocator = "//input[@type='radio']//following::td/input[@name='starttime']";
		selenium.waitForElement(inputLocator);
		if(!selenium.isElementPresent(inputLocator))
			throw new SolventSeleniumException("Unable to find the input field at " + inputLocator );
		selenium.type(inputLocator, time);
		return new MeteringSolvent();
	}
	
	/**
	 * Set schedule stop.
	 * @param time string value for the text field.
	 * @return
	 */
	public MeteringSolvent setScheduleStopTime(String time) {
		String inputLocator = "//input[@type='radio']//following::td/input[@name='stoptime']";
		selenium.waitForElement(inputLocator);
		if(!selenium.isElementPresent(inputLocator))
			throw new SolventSeleniumException("Unable to find the input field at " + inputLocator );
		selenium.type(inputLocator, time);
		return new MeteringSolvent();
	}
	
	/**
	 * This method handles the input fields inside attribute from under schedules section of metering page.<br>
	 * This page use EXT tabs and each tab has input fields with same input field label name.
	 * @param inputField name of the input field.
	 * @param inputText text to enter in input field.
	 * @return
	 */
	public MeteringSolvent enterTxtToAttributeFormField(String inputField, String inputText) {
		String attFormInputField = attributeRootXpath() + "//td[normalize-space(text())='" + inputField + "']/following::input[1]";
		selenium.waitForElement(attFormInputField);
		if(!selenium.isElementPresent(attFormInputField))
			throw new SolventSeleniumException("Unable to find input field for " + inputField + " at " + attFormInputField);
		selenium.type(attFormInputField, inputText);
		return new MeteringSolvent();
	}
	
	/**
	 * This method handles the input field inside command form under schedules section of metering page.<br>
	 * This page use EXT tabs and each tab has input fields with same field label name.
	 * @param inputField
	 * @param inputText
	 * @return
	 */
	public MeteringSolvent enterTxtToCommandFormField(String inputField, String inputText) {
		String cmdFormInputField = commandRootXpath() + "//td[normalize-space(text())='" + inputField + "']/following::input[1]";
		selenium.waitForElement(cmdFormInputField);
		if(!selenium.isElementPresent(cmdFormInputField))
			throw new SolventSeleniumException("Unable to find input field for " + inputField + " at " + cmdFormInputField);
		selenium.type(cmdFormInputField, inputText);
		return new MeteringSolvent();
	}
	
	/**
	 * Click check box in Attribute Form with in the create schedule page.
	 * @param labelName name of the label of the check box.
	 * @return
	 */
	public MeteringSolvent clickCheckBoxInAttributteFormByLabel(String labelName) {
		String labelLocator = attributeRootXpath() + "//input[@type='checkbox']//following::td[normalize-space(text())='" + labelName + "']/following::input[1]";
		selenium.waitForElement(labelLocator);
		if(!selenium.isElementPresent(labelLocator))
			throw new SolventSeleniumException("Unable to find check box with the label " + labelName);
		selenium.click(labelLocator);
		return new MeteringSolvent();
	}
	
	/**
	 * Click check box in command form with in the create schedule page. 
	 * @param labelName label of the check box.
	 * @return
	 */
	public MeteringSolvent clickCheckBoxInCommandFormByLabel(String labelName) {
		String labelLocator = commandRootXpath() + "//input[@type='checkbox']//following::td[normalize-space(text())='" + labelName + "']/following::input[1]";
		selenium.waitForElement(labelLocator);
		if(!selenium.isElementPresent(labelLocator))
			throw new SolventSeleniumException("Unable to find check box with the label " + labelName);
		selenium.click(labelLocator);
		return new MeteringSolvent();		
	}
	
	/**
	 * Click check box in attribute form with the create schedule page.
	 * @param labelName label of the check box.
	 * @return
	 */
	public MeteringSolvent clickCheckBoxInAttributeFormByLabel(String labelName) {
		String labelLocator = attributeRootXpath() + "//input[@type='checkbox']//following::td[normalize-space(text())='" + labelName + "']/following::input[1]";
		selenium.waitForElement(labelLocator);
		if(!selenium.isElementPresent(labelLocator))
			throw new SolventSeleniumException("Unable to find check box with the label " + labelName);
		selenium.click(labelLocator);
		return new MeteringSolvent();
	}
	
	/**
	 * Click button in attribute form.
	 * @param buttonName name of the button.
	 * @return
	 */
	public MeteringSolvent clickButtonInAttributeTab(String buttonName) {
		String buttonLocator = attributeRootXpath() + "//following::input[contains(@value, '" + buttonName + "')][1]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Button with the name of '" + buttonName + "' is not available.");
		selenium.click(buttonLocator);
		return new MeteringSolvent();
	}
	
	/**
	 * Click button in command form.
	 * @param buttonName name of the button.
	 * @return
	 */
	public MeteringSolvent clickButtonInCommandTab(String buttonName) {
		String buttonLocator = commandRootXpath() + "//following::input[contains(@value, '" + buttonName + "')][1]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Button with the name of '" + buttonName + "' is not available.");
		selenium.click(buttonLocator);
		return new MeteringSolvent();
	}
	
	/**
	 * Click Attribute tab.
	 * @return
	 */
	public MeteringSolvent clickAttributeTab() {
		String tabLocator = "//a//span[normalize-space(text())='Attribute']";
		if(!selenium.isElementPresent(tabLocator))
			throw new SolventSeleniumException("Unable to find link for the tab at " + tabLocator);
		selenium.click(tabLocator);
		return new MeteringSolvent();
	}
	
	/**
	 * Click Command tab.
	 * @return
	 */
	public MeteringSolvent clickCommandTab() {
		String tabLocator = "//a//span[normalize-space(text())='Command']";
		if(!selenium.isElementPresent(tabLocator))
			throw new SolventSeleniumException("Unable to find link for the tab at " + tabLocator);
		selenium.click(tabLocator);
		return new MeteringSolvent();
	}
	
	/**
	 * Select time frequency from attribute form.
	 * @param timeOption time option.
	 * @return
	 */
	public MeteringSolvent selectAttributeTimeFrequency(String timeOption) {
		String selectLocator = attributeRootXpath() + "//td[normalize-space(text())='Time/Frequency:']//following::select[1]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Combo box is not available at " + selectLocator);
		selenium.select(selectLocator, timeOption);
		return new MeteringSolvent();
	}
	
	/**
	 * Select time frequency from attribute form.
	 * @param timeOption time option.
	 * @return
	 */
	public MeteringSolvent selectCommandTimeFrequency(String timeOption) {
		String selectLocator = commandRootXpath() + "//td[normalize-space(text())='Time/Frequency:']//following::select[1]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Combo box is not available at " + selectLocator);
		selenium.select(selectLocator, timeOption);
		return new MeteringSolvent();
	}
	
	/**
	 * Select hours from drop down menu for attribute form. 
	 * @param hours value for hours.
	 * @return
	 */
	public MeteringSolvent selectHoursForAttribute(String hours) {
		String selectLocator = attributeRootXpath() + "//td[normalize-space(text())='Time/Frequency:']//following::select[1]/following::select[1]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Combo box to select value for hour is not available at " + selectLocator);
		selenium.select(selectLocator, hours);
		return new MeteringSolvent();
	}
	
	/**
	 * Select minutes from drop down menu in attribute form.
	 * @param minutes value for minutes.
	 * @return
	 */
	public MeteringSolvent selectMinutesForAttribute(String minutes) {
		String selectLocator = attributeRootXpath() + "//td[normalize-space(text())='Time/Frequency:']//following::select[1]/following::select[2]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Combo box to select value for hour is not available at " + selectLocator);
		selenium.select(selectLocator, minutes);
		return new MeteringSolvent();
	}
	
	/**
	 * Select am or pm for hour in attribute form.
	 * @param amORpm
	 * @return
	 */
	public MeteringSolvent selectAM_PMForAttribute(String amORpm) {
		String selectLocator = attributeRootXpath() + "//td[normalize-space(text())='Time/Frequency:']//following::select[1]/following::select[3]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Combo box to select value for hour is not available at " + selectLocator);
		selenium.select(selectLocator, amORpm);
		return new MeteringSolvent();		
	}
	
	/**
	 * Select hours from drop down menu for command form. 
	 * @param hours value for hours.
	 * @return
	 */
	public MeteringSolvent selectHoursForCommand(String hours) {
		String selectLocator = commandRootXpath() + "//td[normalize-space(text())='Time/Frequency:']//following::select[1]/following::select[1]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Combo box to select value for hour is not available at " + selectLocator);
		selenium.select(selectLocator, hours);
		return new MeteringSolvent();
	}
	
	/**
	 * Select minutes from drop down menu in command form.
	 * @param minutes value for minutes.
	 * @return
	 */
	public MeteringSolvent selectMinutesForCommand(String minutes) {
		String selectLocator = commandRootXpath() + "//td[normalize-space(text())='Time/Frequency:']//following::select[1]/following::select[2]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Combo box to select value for hour is not available at " + selectLocator);
		selenium.select(selectLocator, minutes);
		return new MeteringSolvent();
	}
	
	/**
	 * Select am or pm for hour in command form.
	 * @param amORpm
	 * @return
	 */
	public MeteringSolvent selectAM_PMForCommand(String amORpm) {
		String selectLocator = commandRootXpath() + "//td[normalize-space(text())='Time/Frequency:']//following::select[1]/following::select[3]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Combo box to select value for hour is not available at " + selectLocator);
		selenium.select(selectLocator, amORpm);
		return new MeteringSolvent();		
	}		
	
	private String attributeRootXpath() {
		return "//form[@id='scheduledGroupRequestExecutionForm_attr']";
	}
	
	private String commandRootXpath() {
		return "//form[@id='scheduledGroupRequestExecutionForm_cmd']";
	}	
}
