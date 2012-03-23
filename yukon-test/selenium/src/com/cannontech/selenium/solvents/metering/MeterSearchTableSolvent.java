/**
 * 
 */
package com.cannontech.selenium.solvents.metering;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;

/**
 * This class handles functionalities available in Meter Search Results table.<br>
 * @author anuradha.uduwage
 *
 */
public class MeterSearchTableSolvent extends YukonTableSolvent {

	/**
	 * Constructor setting up the table id for meter results table.
	 * @param params
	 */
	public MeterSearchTableSolvent(String... params) {
		super("tableId=deviceTable");
	}
	
	/**
	 * Click the column header to sort data by column. 
	 * @param columnName name of the column header.
	 * @return
	 */
	public MeterSearchTableSolvent sortTableByColumn(String columnName) {
		String columnHeader = getXpathRoot() + "//tr//a[contains(text(), '" + columnName + "')]";
		selenium.waitForElement(columnHeader);
		if(!selenium.isElementPresent(columnHeader))
			throw new SolventSeleniumException("Column Header " + columnName + "is not available at " + columnHeader);
		selenium.click(columnHeader);
		waitForJSObject();
		return this;
	}
	
	/**
	 * Click the device in meter search results table using 
	 * @param deviceName
	 * @return
	 */
	public MeterSearchTableSolvent clickByDeviceName(String deviceName) {
		String deviceLocator = getXpathRoot() + "//tr//td[contains(text(), '" + deviceName + "')]";
		waitForJSObject();
		selenium.waitForElement(deviceLocator);
		if(!selenium.isElementPresent(deviceLocator))
			throw new SolventSeleniumException("Device with a name " + deviceName + " is not available at " + deviceLocator);
		selenium.click(deviceLocator);
		return this;
	}
	
	/**
	 * Method returns the Meter Number based on given device name.
	 * @param deviceName name of the device.
	 * @return meterNumber value of the meter number.
	 */
	public String getMeterNumberByDeviceName(String deviceName) {
		String deviceLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + deviceName + "']";
		String meterLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + deviceName + "']/following::td[1]";
		selenium.waitForElement(deviceLocator);
		if(!selenium.isElementPresent(meterLocator))
			throw new SolventSeleniumException("Meter number for the device " + deviceName + " is not available at " + meterLocator);
		String meterNumber = selenium.getText(meterLocator);
		return meterNumber;
	}
	
	/**
	 * Method return the device type based on the given device name. 
	 * @param deviceName name of the device.
	 * @return deviceType device type of the device.
	 */
	public String getDeviceTypeByDeviceName(String deviceName) {
		String deviceLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + deviceName + "']";
		String deviceTypeLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + deviceName + "']/following::td[2]";
		selenium.waitForElement(deviceLocator);
		if(!selenium.isElementPresent(deviceTypeLocator))
			throw new SolventSeleniumException("Device Type for the device " + deviceName + " is not available at " + deviceTypeLocator);
		String deviceType = selenium.getText(deviceTypeLocator);
		return deviceType;
	}
	
	/**
	 * Method return the device address based on the given device name.
	 * @param deviceName name of the device.
	 * @return address address of the device.
	 */
	public String getDeviceAddressByDeviceName(String deviceName) {
		String deviceLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + deviceName + "']";
		String deviceAddress = getXpathRoot() + "//tr//td[normalize-space(text())='" + deviceName + "']/following::td[3]";
		selenium.waitForElement(deviceLocator);
		if(!selenium.isElementPresent(deviceAddress))
			throw new SolventSeleniumException("Device Type for the device " + deviceName + " is not available at " + deviceAddress);
		String address = selenium.getText(deviceAddress);
		return address;
	}
	
	/**
	 * Method return the device route based on the given device name.
	 * @param deviceName name of the device.
	 * @return route value of the device route.
	 */
	public String getDeviceRouteByDeviceName(String deviceName) {
		String deviceLocator = getXpathRoot() + "//tr//td[normalize-space(text())='" + deviceName + "']";
		String deviceRoute = getXpathRoot() + "//tr//td[normalize-space(text())='" + deviceName + "']/following::td[4]";
		selenium.waitForElement(deviceLocator);
		if(!selenium.isElementPresent(deviceRoute))
			throw new SolventSeleniumException("Device Route for the device " + deviceName + " is not available at " + deviceRoute);
		String route = selenium.getText(deviceRoute);
		return route;
	}
}
