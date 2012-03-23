/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handles three tables in Stars Hardware page.
 * @author anuradha.uduwage
 *
 */
public class HardwarePageSolvent extends CommonTableSolvent {

	/**
	 * Default constructor sets the table class value for all three tables.
	 * @param params
	 */
	public HardwarePageSolvent(String... params) {
		super("tableClass=compactResultsTable hardwareListTable");
	}
	
	/**
	 * This method click the Change Device Icon in all three table such as Switches, Thermostats and Meters tables.
	 * @param serialNumber value of the serial number.
	 * @return
	 */
	public HardwarePageSolvent clickChangeDeviceBySerialNumber(String serialNumber) {
		String imageLocator = getXpathRoot() + "//a//preceding::a[normalize-space(text())='" + serialNumber + "']/following::a[@class='imgLink'][1]";

		selenium.waitForElement(imageLocator);
		selenium.click(imageLocator);
		return this;
	}
	 /**
	  * Method to navigate to Edit Hardware Configuration page such as Switches and Thermostats from the Operator side
	  * @param serialNumber to identify the imageLocator
	  */
	 public HardwarePageSolvent clickEditConfiguration(String serialNumber) {
		 String imageLocator = getXpathRoot() + "//a//preceding::a[normalize-space(text())='" + serialNumber + "']/following::a[@class='simpleLink'][1]";

		 selenium.waitForElement(imageLocator);
		 selenium.click(imageLocator);
		 selenium.waitForPageToLoad();
		 return this;
	 }
	 
	 /**
	  * Method to navigate to the Thermostat Schedule page from the Operator side Hardware page
	  * @param serialNumber to identity the imageLocator 
	  */
	 public HardwarePageSolvent clickEditSchedule(String serialNumber) {
		 String imageLocator = getXpathRoot() + "//a//preceding::a[normalize-space(text())='" + serialNumber + "']/following::a[@class='simpleLink'][2]";

		 selenium.waitForElement(imageLocator);
		 selenium.click(imageLocator);
		 selenium.waitForPageToLoad();
		 return this;		 
	 }
	 
	 /**
	  * Method to navigate to Saved Schedule page from the Operator side Hardware page
	  * @param serialNumber to identity the imageLocator 
	  */
	 public HardwarePageSolvent clickSavedSchedule(String serialNumber) {
		 String imageLocator = getXpathRoot() + "//a//preceding::a[normalize-space(text())='" + serialNumber + "']/following::a[@class='simpleLink'][3]";

		 selenium.waitForElement(imageLocator);
		 selenium.click(imageLocator);
		 return this;		 
	 }
	 
	 /**
	  * Method to navigate to Thermostat Manual page from the Operator side Hardware page
	  * @param serialNumber to identity the imageLocator 
	  */
	 public HardwarePageSolvent clickManualAdjustment(String serialNumber) {
		 String imageLocator = getXpathRoot() + "//a//preceding::a[normalize-space(text())='" + serialNumber + "']/following::a[@class='simpleLink'][4]";

		 selenium.waitForElement(imageLocator);
		 selenium.click(imageLocator);
		 return this;		 
	 }
	 
	 /**
	  * Method to navigate to Meter Details page from the Operator side Hardware page
	  * @param serialNumber to identity the imageLocator 
	  */
	 public HardwarePageSolvent clickMeterDetails(String serialNumber) {
		 String imageLocator = getXpathRoot() + "//a//preceding::a[normalize-space(text())='" + serialNumber + "']/following::a[@class='simpleLink'][9]";

		 selenium.waitForElement(imageLocator);
		 selenium.click(imageLocator);
		 return this;		 
	 }
	 
	 /**
	  * Click add button based on the table head in stars hardware page.
	  * @param tableHeader header of the table.
	  * @return
	  */
	 public HardwarePageSolvent clickAddByTableHeader(String tableHeader) {
		 String buttonLocator = "//div[normalize-space(text())='" + tableHeader  + "']//following::button[contains(@title, 'Add')][1]";
		 waitForJSObject();

		 selenium.waitForElement(buttonLocator);
		 selenium.click(buttonLocator);
		 return this;
	 }
	 
	 /**
	  * Method to handle popup window where user can enter serial number.
	  * @param serialNumber value for the serial number.
	  * @return
	  */
	 @Deprecated
	 public HardwarePageSolvent enterSerialNumber(String labelName, String serialNumber) {
		 String addSwitch = "//div[normalize-space(text())='Add Switch']/following::td[normalize-space(text())='" + labelName + "'][1]/following::input[@type='text'][1]";
		 String addThermostat = "//div[normalize-space(text())='Add Thermostat']/following::td[normalize-space(text())='" + labelName + "'][1]/following::input[@type='text'][1]";
		 String addGateway = "//div[normalize-space(text())='Add Gateway']/following::td[normalize-space(text())='" + labelName + "'][1]/following::input[@type='text'][1]";
		 selenium.pause(9000);
		 if(selenium.isElementPresent(addSwitch))
			 selenium.type(addSwitch, serialNumber);
		 if(selenium.isElementPresent(addThermostat))
			 selenium.type(addThermostat, serialNumber);
		 if(selenium.isElementPresent(addGateway))
			 selenium.type(addGateway, serialNumber);
		 if(!selenium.isElementPresent(addSwitch) && !selenium.isElementPresent(addThermostat) && !selenium.isElementPresent(addGateway))
			 throw new SolventSeleniumException("Unable to find input field at " + addSwitch + " or " + addThermostat + " or " + addGateway);
		 return this;
	 }
	 
	 /**
	  * Method click the inventory button in Operator:Hardware page since these popup windows dom structure is inconsistent 
	  * with the rest of the popup windows.
	  * @return
	  */
	 public HardwarePageSolvent clickInventoryButton() {
		 String switchButtonLocator = "//div[normalize-space(text())='Add Switch']/following::span[contains(text(),'Check Inventory')][1]/ancestor::button[1]";
		 String thermoButtonLocator = "//div[normalize-space(text())='Add Thermostat']/following::span[contains(text(),'Check Inventory')][1]/ancestor::button[1]";
		 String gatewayButtonLocator = "//div[normalize-space(text())='Add Gateway']/following::span[contains(text(),'Check Inventory')][1]/ancestor::button[1]";
		 selenium.pause(9000);
		 if(selenium.isElementPresentAndVisible(switchButtonLocator,3000))
			 selenium.click(switchButtonLocator);
		 else if(selenium.isElementPresentAndVisible(thermoButtonLocator,3000))
			 selenium.click(thermoButtonLocator);
		 else if(selenium.isElementPresentAndVisible(gatewayButtonLocator,3000))
			 selenium.click(gatewayButtonLocator);
		 else if(!selenium.isElementPresent(switchButtonLocator) && !selenium.isElementPresent(thermoButtonLocator) && !selenium.isElementPresent(gatewayButtonLocator))
			 throw new SolventSeleniumException("Unable to find Check Inventory Button under Add Switch or Add Thermostat at xpath " + 
					 switchButtonLocator + " or " + thermoButtonLocator);
		 
		 //selenium.waitForPageToLoad();
		 return this;
	 }
	 
	 /**
	  * Method clicks the link by name on Thermostats table. This method is implemented for Operator side Hardware page <br> 
	  * due to multiple same serial number links on the page.Also it can be used to click the device name link under Meters.
	  * @param
	  * @return
	  */
	 public HardwarePageSolvent clickLinkByName(String linkName) {
		String deviceLocator = getXpathRoot() + "//a[normalize-space(text())='" + linkName + "']";

		selenium.waitForElement(deviceLocator);
		selenium.click(deviceLocator);
		return this;
	 }
}
 