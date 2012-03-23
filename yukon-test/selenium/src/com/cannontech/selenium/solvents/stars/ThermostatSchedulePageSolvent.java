/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonSolvent;

/**
 * This class handles the Thermostat settings for Wake, Leave, Return, Sleep Start <br> 
 * At time,along with Cooling Temperature and Heating Temperature on the Stars Customer side Thermostat-Schedule page.
 * @author anjana.manandhar
 *
 */
public class ThermostatSchedulePageSolvent extends AbstractSolvent {

	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}

	/**
	 * Method allows user to enter the Wake Start Time
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Wake Start At time
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputWakeStartTime(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][1]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][1]//following::input[@type='text']";
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);
		return this;
	}
	
	/**
	 * Method allows user to enter the Leave Start Time
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Leave Start At time
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputLeaveStartTime(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][2]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][2]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Return Start Time
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Return Start At time
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputReturnStartTime(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][3]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][3]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Sleep Start Time
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Sleep Start At time
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputSleepStartTime(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][4]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][4]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Cooling Temperature at Wake up time.
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Wake Cooling Temperature
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputWakeCoolingTemp(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][1]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][1]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Cooling Temperature at Leave time.
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Leave Cooling Temperature
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputLeaveCoolingTemp(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][2]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][2]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Cooling Temperature at Return time. 
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Return Cooling Temperature
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputReturnCoolingTemp(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][3]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][3]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Cooling Temperature at Sleep time.
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Sleep Cooling Temperature
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputSleepCoolingTemp(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][4]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][4]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Heating Temperature at Wake up time.
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Wake Heating Temperature
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputWakeHeatingTemp(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][1]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][1]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Heating Temperature at Leave time.
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Leave Heating Temperature
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputLeaveHeatingTemp(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][2]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][2]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Heating Temperature at Return time.
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Return Heating Temperature
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputReturnHeatingTemp(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][3]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][3]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Heating Temperature at Sleep time.
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Sleep Start At
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputSleepHeatingTemp(String schedulePopupTitle, String schedDayType, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][4]//following::input[@type='text']";;
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][4]//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}
	
	/**
	 * Method allows user to enter the Thermostat Schedule Name
	 * schedDayTypeXpathHelper is the route that the locater needs to take if creating a schedule.
	 * This path is saved when clicking the radio button in the new schedule
	 * @param text string value for Thermostat Schedule Name
	 * @param schedulePopupTitle string value for the title of the schedule popup (i/e Create Schedule, Edit Schedule - 'schedule name')
	 * @param schedDayType string value for the day you are going to edit - i/e M-F, Sa/Su, All etc
	 */
	public ThermostatSchedulePageSolvent inputScheduleName(String schedulePopupTitle, String text) {
		String locator1="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::div[contains(@class,'schedule_editor') and contains(@class,'active')][1]//following::label[normalize-space(text())='Schedule Name:']//following::input[@type='text']";
		String locator2="//div[normalize-space(text())='" + schedulePopupTitle + "']//following::label[normalize-space(text())='Schedule Name:']//following::input[@type='text']";
		
			
		if(schedulePopupTitle.equals("Create Schedule")&&selenium.isElementPresent(locator1,3000))
			selenium.type(locator1, text);
		else if(selenium.isElementPresent(locator2,3000))
			selenium.type(locator2, text);
		else throw new SolventSeleniumException("Unable to find the deviceLocator " + schedulePopupTitle + " at " + locator1 + " or " + locator2);;
		return this;
	}

	/**
	 * MAKE THIS A COMMON METHOD
	 * This method will click a button (located by span text) in a popup window
	 * @param popupTitle the title of the popup window (div)
	 * @param buttonName the span of the button to be clicked
	 */
	@Deprecated
	public ThermostatSchedulePageSolvent clickPopupButtonBySpanText(String popupTitle, String spatTextValue) {
		String locator = "//div[normalize-space(text())='" + popupTitle + "']//following::span[contains(text(), '" + spatTextValue + "')]/ancestor::button[1]";
		selenium.waitForElement(locator);
		if(!selenium.isElementPresentAndVisible(locator, 7000))
			throw new SolventSeleniumException("Unable to find the " + spatTextValue + " button at " + locator);
		selenium.click(locator);
		return this;
	}
	/**
	 * Method clicks the radio button for the create schedule popup when deciding what type of schedule to create
	 * @param radioButtonValue the value of the radio button to be clicked.  This will be saved and used when inputing schedule values
	 */
	public ThermostatSchedulePageSolvent clickRadioButtonScheduleType(String radioButtonValue) {
		String buttonLocator = "//input[@value='" + radioButtonValue + "' and @type='radio']";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find radio button with " + radioButtonValue + " at " + buttonLocator);
		selenium.click(buttonLocator);
		return this;
	}

	/**
	 * Method allows user to click the edit button following the schedule name
	 * @param scheduleName string value for Thermostat Schedule Name
	 */
	public ThermostatSchedulePageSolvent editTstatSchedule(String scheduleName) {
		String buttonLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::span[contains(text(), 'Edit')]/ancestor::button[1]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find edit button for schedule " + scheduleName);
		selenium.click(buttonLocator);
		return this;
	}

	/**
	 * Method allows user to click the copy button following the schedule name
	 * @param scheduleName string value for Thermostat Schedule Name
	 */
	public ThermostatSchedulePageSolvent clickCopyTstatSchedule(String scheduleName) {
		String buttonLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::span[contains(text(), 'Copy')]/ancestor::button[1]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find copy button for schedule " + scheduleName);
		selenium.click(buttonLocator);
		return this;
	}

	/**
	 * Method allows user to click the send now button following the schedule name
	 * @param scheduleName string value for Thermostat Schedule Name
	 */
	public ThermostatSchedulePageSolvent clickSendNowTstatSchedule(String scheduleName) {
		String buttonLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::span[contains(text(), 'Send Now')]/ancestor::button[1]";
		selenium.waitForElement(buttonLocator);
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Method return the value in text field based on given state of the Thermostat and the value of 
	 * the text field label.<br>
	 * <i>Example:</i><br>
	 * To get the value of text field under Wake (W) user should pass value of the text field label and the state as Wake (W).<br>
	 * 
	 * @param labelValue label value of the text field.
	 * @param state state of the thermostat.
	 * @return
	 */
	public String getValueByThermostatStateAndLabel(String labelValue, String state) {
		String textFieldLocator = "//input//preceding::td[1][normalize-space(text())='" + labelValue + 
			"']//preceding::td[normalize-space(text())='" + state + "']//following::input[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(labelValue)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field with the label " + labelValue + " at " + textFieldLocator);
			textFieldValue = selenium.getValue(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Wake Start Time
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getWakeStartTime (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][1]//following::span[@class='time']";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Leave Start Time.
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getLeaveStartTime (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][2]//following::span[@class='time']";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Return Start Time.
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getReturnStartTime (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][3]//following::span[@class='time']";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Sleep Start Time
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getSleepStartTime (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'time')][4]//following::span[@class='time']";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Wake Cooling Temperature
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getWakeCoolingTemp (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][1]//following::span[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Leave Cooling Temperature
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getLeaveCoolingTemp (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][2]//following::span[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Return Cooling Temperature
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getReturnCoolingTemp (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][3]//following::span[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Sleep Cooling Temperature
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getSleepCoolingTemp (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'cool')][4]//following::span[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}

	/**
	 * Method to return the value of Wake Heating Temperature
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getWakeHeatingTemp (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][1]//following::span[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Leave Heating Temperature
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getLeaveHeatingTemp (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][2]//following::span[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Return Heating Temperature
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getReturnHeatingTemp (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][3]//following::span[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	
	/**
	 * Method to return the value of Sleep Heating Temperature
	 * @param scheduleName the name of the schedule to be read from
	 * @param schedDayType string value for the day you are going to return - i/e M-F, Sa/Su, All etc
	 * @return
	 */
	public String getSleepHeatingTemp (String scheduleName, String schedDayType) {
		String textFieldLocator = "//span[normalize-space(text())='" + scheduleName + "']//following::label[normalize-space(text())='" + schedDayType + "']//following::div[contains(@class,'heat')][4]//following::span[1]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(scheduleName)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + scheduleName + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
}