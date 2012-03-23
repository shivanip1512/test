/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;

/**
 * This class handle function in all three opt out tables in stars operator page.
 * @author anuradha.uduwage
 *
 */
public class OptOutTableSolvent extends YukonTableSolvent {

	/**
	 * Default constructor sets the table id for all the Opt Out Tables.
	 * @param params
	 */
	public OptOutTableSolvent(String... params) {
		super("tableId=deviceTable");
	}
	
	/**
	 * Method remove a device by clicking the remove icon, helper methods are used to check if the table, and the device 
	 * exists prior to any action.  
	 * @param tableName name of the table header.
	 * @param deviceName value/name of the device.
	 * @return
	 */
	public OptOutTableSolvent removeDeviceByTableAndDeviceName(String tableName, String deviceName) {
		String deviceRemoveLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='" + tableName + "']/following::td[normalize-space(text())='" + deviceName + "'][1]/following::img[1]";	
		if(isTableExist(tableName)) {
			if(isDeviceExist(tableName, deviceName)) {
				selenium.waitForElement(deviceRemoveLocator);
				if(!selenium.isElementPresent(deviceRemoveLocator))
					throw new SolventSeleniumException("Unable to find X (Remove) icon at " + deviceRemoveLocator);
				selenium.click(deviceRemoveLocator);
			}
			else
				throw new SolventSeleniumException("Unable to to find device " + deviceName + " at " + deviceRemoveLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find table with a table header " + tableName + " at " + deviceRemoveLocator);
		return this;
	}
	
	/**
	 * Method click on the resend button to Resend opt outs based on the table name and the device name. 
	 * @param tableName name of the table header.
	 * @param deviceName value/name of the device.
	 * @return
	 */
	public OptOutTableSolvent resendOptOut(String tableName, String deviceName) {
		String resendButtonLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='" + tableName + "']/following::td[normalize-space(text())='" + deviceName + "'][1]/following::img[contains(@title, 'Resend')]";
		if(isTableExist(tableName)) {
			if(isDeviceExist(tableName, deviceName)) {
				selenium.waitForElement(resendButtonLocator);
				if(!selenium.isElementPresent(resendButtonLocator))
					throw new SolventSeleniumException("Unable to find Resend icon at " + resendButtonLocator);
				selenium.click(resendButtonLocator);
			}
			else
				throw new SolventSeleniumException("Unable to to find device " + deviceName + " at " + resendButtonLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find table with a table header " + tableName + " at " + resendButtonLocator);
		return this;
	}
	
	/**
	 * Method click on the reset button to Reset opt outs based on the table name and the device name. 
	 * @param tableName name of the table header.
	 * @param deviceName value/name of the device.
	 * @return
	 */
	public OptOutTableSolvent resetOptOut(String tableName, String deviceName) {
		String resetButtonLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='" + tableName + "']/following::td[normalize-space(text())='" + deviceName + "'][1]/following::img[contains(@title, 'Reset')]";
		if(isTableExist(tableName)) {
			if(isDeviceExist(tableName, deviceName)) {
				selenium.waitForElement(resetButtonLocator);
				if(!selenium.isElementPresent(resetButtonLocator))
					throw new SolventSeleniumException("Unable to find Reset icon at " + resetButtonLocator);
				selenium.click(resetButtonLocator);
			}
			else
				throw new SolventSeleniumException("Unable to to find device " + deviceName + " at " + resetButtonLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find table with a table header " + tableName + " at " + resetButtonLocator);
		return this;
	}
	
	/**
	 * Method click the add button to add an extra opt out device in opt out limits table.
	 * @param deviceName name of the device.
	 * @return
	 */
	public void addExtraOptOut(String deviceName) {
		// TODO This would be nice if we had a friendly div ide for this.  Software should add this.
		String optOutLimitDiv = "//div[normalize-space(text())='Opt Out Limits']/../..";
		String addButtonLocator = optOutLimitDiv+"//td[normalize-space(text())='"+deviceName+"']/..//td/*[contains(@id,'allowOne')]";

		selenium.waitForElement(addButtonLocator);
		selenium.click(addButtonLocator);
		selenium.waitForElement("//div[contains(@class, 'popUpDiv')]");
	}
	
	/**
	 * Method return the status of the opt out device in current opt outs table.
	 * @param deviceName value/name of the device.
	 * @return status status of the opt out device.
	 */
	public String getCurrntOptOutStatusByDevice(String deviceName) {
		String tableName = "Current Opt Outs";
		String statusLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='" + tableName + "']/following::td[normalize-space(text())='" + deviceName + "'][1]/following::td[2]";
		String status = null;
		if(isTableExist(tableName)) {
			if(isDeviceExist(tableName, deviceName)) {
				selenium.waitForElement(statusLocator);
				if(!selenium.isElementPresent(statusLocator))
					throw new SolventSeleniumException("Unable to find Add Opt Out icon at " + statusLocator);
				status = selenium.getText(statusLocator);
			}
			else
				throw new SolventSeleniumException("Unable to to find device " + deviceName + " at " + statusLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find table with a table header " + tableName + " at " + statusLocator);
		return status;			
	}
	
	/**
	 * Method check if the device is available in the appropriate table prior to any action on that table.
	 * @param tableName name of the table header.
	 * @param deviceName value/name of the device.
	 * @return <b>true</b> or <b>false</b> based on the availability of the device inside a table.
	 */
	private boolean isDeviceExist(String tableName, String deviceName) {
		String deviceLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='" + tableName + "']/following::td[normalize-space(text())='" + deviceName + "'][1]";
		boolean isDeviceExist; 
		if(selenium.isElementPresent(deviceLocator))
			isDeviceExist = true;
		else
			isDeviceExist = false;
		return isDeviceExist;
	}
	
	/**
	 * Check if the appropriate table is available prior to any action on that table.
	 * @param tableName name of the table header.
	 * @return return <b>true</b> or <b>false</b> based on the availability of the table.
	 */
	private boolean isTableExist(String tableName) {
		String tableLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='" + tableName + "']";
		boolean isTableExist;
		if(selenium.isElementPresent(tableLocator))
			isTableExist = true;
		else {
			isTableExist = false;
			throw new SolventSeleniumException("Unable to find the table with " + tableName + " at " + tableLocator);
		}
		return isTableExist;
	}

	/**
	 * Method clicks on the decrement icon to Decrement opt out allowance for the device.
	 * @param deviceName name of the device.
	 * @return 
	 */
	public OptOutTableSolvent decrementOptOut(String deviceName) {
		String tableName = "Opt Out Limits";
		String decrementButtonLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='Opt Out Limits']/following::td[normalize-space(text())='" + deviceName + "'][1]/following::img[contains(@title, 'Decrement')]";
		if(isTableExist(tableName)) {
			if(isDeviceExist(tableName, deviceName)) {
				selenium.waitForElement(decrementButtonLocator);
				if(!selenium.isElementPresent(decrementButtonLocator))
					throw new SolventSeleniumException("Unable to find Decrement Opt Out icon at " + decrementButtonLocator);
				selenium.click(decrementButtonLocator);
			}
			else	
				throw new SolventSeleniumException("Unable to find device " + deviceName + " at " + decrementButtonLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find table with a table header " + tableName + " at " + decrementButtonLocator);
		return this;
	}
	
	/**
	 * Method verifies the Used field value based on the Opt Out Limits table based on the device name on the Operator side.
	 * @param deviceName name of the device
	 * @return
	 */
	public String getOperatorOptOutUsedValue(String deviceName) {
		String tableName = "Opt Out Limits";
		String usedValueLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='" + tableName + "']/following::td[normalize-space(text())='" + deviceName + "'][1]/following::td[1]"; ;
		String used = null;
		if(isTableExist(tableName)) {
			if(isDeviceExist(tableName, deviceName)) {
				selenium.waitForElement(usedValueLocator);
				if(!selenium.isElementPresent(usedValueLocator))
					throw new SolventSeleniumException("Unable to find Used value at" + usedValueLocator);
				used = selenium.getText(usedValueLocator);
			}
			else
				throw new SolventSeleniumException("Unable to find device " + deviceName + " at " + usedValueLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find table with a table header " + tableName + " at " + usedValueLocator);
		return used;
	}
	
	/**
	 * Method verifies the Remaining field value based on the Opt Out Limits table based on the device name  on the Operator side.
	 * @param deviceName name of the device
	 * @return
	 */
	public String getOperatorOptOutRemainingValue(String deviceName) {
		String tableName = "Opt Out Limits";
		String remainingValueLocator = getXpathRoot() + "/preceding::div[normalize-space(text())='" + tableName + "']/following::td[normalize-space(text())='" + deviceName + "'][1]/following::td[2]";
		String remaining = null;
		if(isTableExist(tableName)) {
			if(isDeviceExist(tableName, deviceName)) {
				selenium.waitForElement(remainingValueLocator);
				if(!selenium.isElementPresent(remainingValueLocator))
					throw new SolventSeleniumException("Unable to find Consumer Opt Outs Remaining value at" + remainingValueLocator);
				remaining = selenium.getText(remainingValueLocator);
			}
			else
				throw new SolventSeleniumException("Unable to find device " + deviceName + " at " + remainingValueLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find table with a table header " + tableName + " at " + remainingValueLocator);
		return remaining;
	}
	
	/**
	 * Method checks if the column header is available 
	 * @param deviceName name of device
	 * @param columnHeader name of column
	 * @return
	 */
	private boolean isColumnHeaderExist(String deviceName, String columnHeader) {
		String columnLocator = "//*[@id='deviceTable']//th[text()='" + columnHeader + "']//following::td[normalize-space(text())='" +deviceName + "'][1]";
		boolean isColumnHeaderExist;
		if(selenium.isElementPresent(columnLocator))
			isColumnHeaderExist = true;
		else
			isColumnHeaderExist = false;
		return isColumnHeaderExist;
		}
	
	/**
	 * Method verifies the Used value in the Opt Out Limits table based on the device name and column header in the Customer side.
	 * @param deviceName name of the device
	 * @param columnHeader name of the column
	 * @return
	 */
	public String getCustomerOptOutUsedValue(String deviceName, String columnHeader) {
		String usedValueLocator = "//*[@id='deviceTable']//th[text()='"+ columnHeader + "']//following::td[normalize-space(text())='" + deviceName + "'][1]/following::td[1]";
		String values = null;
		if(isColumnHeaderExist(deviceName, columnHeader)) {
			selenium.waitForElement(usedValueLocator);
			if(!selenium.isElementPresent(usedValueLocator))
				throw new SolventSeleniumException("Unable to find " + deviceName + " at " + usedValueLocator);
			values = selenium.getText(usedValueLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find column header " + columnHeader + " at " + usedValueLocator);
		return values;
	}
	
	/**
	 * Method verifies Remaining value in the Opt Out Limits table based on the device name and column header in the Customer side.
	 * @param deviceName name of the device
	 * @param columnHeader name of the column
	 * @return
	 */
	public String getCustomerOptOutRemainingValue(String deviceName, String columnHeader) {
		String remainingValueLocator = "//*[@id='deviceTable']//th[text()='"+ columnHeader + "']//following::td[normalize-space(text())='" + deviceName + "'][1]/following::td[2]";
		String values = null;
		if(isColumnHeaderExist(deviceName, columnHeader)) {
			selenium.waitForElement(remainingValueLocator);
			if(!selenium.isElementPresent(remainingValueLocator))
				throw new SolventSeleniumException("Unable to find " + deviceName + " at " + remainingValueLocator);
			values = selenium.getText(remainingValueLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find column header " + columnHeader + " at " + remainingValueLocator);
		return values;
	}
	
	/**
	 * Method verifies the Status in Current Opt Outs table based on the device name and column header in the Customer side
	 * @param deviceName name of device
	 * @param columnHeader name of column
	 * @return
	 */
	public String getCustomerOptOutStatusValue(String deviceName, String columnHeader) {
		String statusLocator = "//*[@id='deviceTable']//th[text()='" + columnHeader + "']//following::td[normalize-space(text())='" + deviceName + "'][1]/following::td[2]";
		String values = null;
		if(isColumnHeaderExist(deviceName, columnHeader)) {
			selenium.waitForElement(statusLocator);
			if(!selenium.isElementPresent(statusLocator))
				throw new SolventSeleniumException("Unable to find " + deviceName + " at " + statusLocator);
			values = selenium.getText(statusLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find column header " + columnHeader + " at " + statusLocator);
		return values;
	}

	/**
	 * Method clicks on OK or Cancel button on the Opt Out Confirm popup windows.
	 * <b>Example:<b><i> popupWindow = Confirm Allow Another Opt Out, name=submit(OK) or button(Cancel) </i><br>
	 * @param popupWindow name of the popup window 
	 * @buttonType name of the button 
	 * @return
	 */
	public OptOutTableSolvent confirmOKOrCancel(String popUpMessage, String buttonType) {
		String confirmButtonLocator = "//p[contains(normalize-space(text()),'" + popUpMessage + "')]/following::button[@type='" + buttonType + "'][1]";
		if(!selenium.isElementPresent(confirmButtonLocator,2000))
			throw new SolventSeleniumException ("Unable to find the button at " + confirmButtonLocator);
		selenium.click(confirmButtonLocator);
		selenium.waitForPageToLoad();
		return this;
	}
	/**
	 * Method returns the previous opt out history duration from the Enrollment page of a customer
	 * @param device - the thermostat's number
	 * @return returns the last opt out duration
	 */
	public String getOptOutHistoryDurationByDevice(String device) {
		String textFieldLocator = "//div[normalize-space(text())='Opt Out History']//following::td[normalize-space(text())='" + device + "']/following::td[3]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(device)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + device + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator);
		}
		return textFieldValue;
	}
	/**
	 * Method returns the previous opt out history action from the Enrollment page of a customer
	 * @param device - the thermostat's number
	 * @return returns the last opt out action
	 */
	public String getOptOutHistoryActionsByDevice(String device) {
		String textFieldLocator = "//div[normalize-space(text())='Opt Out History']//following::td[normalize-space(text())='" + device + "']/following::td[4]";
		String textFieldValue = null;
		if(new CommonSolvent().isTextPresent(device)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + device + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator).split(" at ")[0];
		}
		return textFieldValue;
	}
}
