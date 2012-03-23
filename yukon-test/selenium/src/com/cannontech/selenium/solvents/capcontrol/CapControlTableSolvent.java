/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import java.util.ArrayList;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.google.common.collect.Lists;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Class take care of specialized areas with the Cap Control tables that are 
 * not common to all the other tables in Yukon Application. 
 * 
 * @author anuradha.uduwage
 *
 */
public class CapControlTableSolvent extends YukonTableSolvent {

	/**
	 * @param params
	 */
	public CapControlTableSolvent(String... params) {
		super("tableId=areaTable");
	}

	/**
	 * Click item in a row by its name/content. 
	 * @param rowContent String value in a table row object.
	 * @return
	 */
	public CapControlTableSolvent clickRowByContent(String rowContent) {
		String locator = getXpathRoot() + "//a[contains(text(), '" + rowContent + "')]";
		if(!selenium.isElementPresent(locator))
			throw new SeleniumException("Unable to find link " + locator + "with the text " + rowContent);
		selenium.click(locator);
		return this;
	}
	
	/**
	 * Method will delete orphan device based on the given name and return to orphans page.
	 * Exception will be thrown if the device unable to delete.
	 * @param deviceName Name of the orphan device.
	 * @return
	 */
	public CapControlTableSolvent deleteOrphansByName(String deviceName) {
		String locator = getXpathRoot() + "//tr[contains(., '" + deviceName + "')]//td/a[@class='tierIconLink'][2]";
		waitForJSObject(2000);
		if(!selenium.isElementPresent(locator)) {
			throw new SeleniumException("Unable to find device name " + deviceName + " in table with ID " + getTableId());
		}
		selenium.click(locator);
		
		String checkApprovalString = "//*[@id='deleteForm:deleteItems']//tr[contains(.,'" + deviceName + "')]//td[text()='yes']";
		if(selenium.isElementPresent(checkApprovalString)) {
			new CCItemDeletionSolvent().clickSubmitButton();
		} else {
			throw new SeleniumException("Unable to Delete this object, Check if the device is attached to another device.");
		}
		
		String checkDeletedString = "//*[@id='deleteForm:deleteItems']//tr[contains(.,'" + deviceName + "')]//td[contains(.,'deleted')]";
		if(selenium.isElementPresent(checkDeletedString)) {
			new CCItemDeletionSolvent().clickReturnButton();
		}
		return this;		
	}
	
	/**
	 * Method will delete all the orphans in a specific device section as a bulk delete operation. 
	 * @return
	 */
	public CapControlTableSolvent deleteOrphansInBulk() {
	    ArrayList<String> devices = Lists.newArrayList();

	    if(!this.getTableId().equals("resTable"))
			super.setXpathRoot("//*[@id='resTable']");
		
	    // The first data row is the second row in the table due to the due to 
		String deviceLocator = getXpathRoot() + "//tr[2]/td[1]";
		for (int i = 3; selenium.isElementPresent(deviceLocator); i++) {
			String orphan = selenium.getText(deviceLocator);
			devices.add(orphan);
			deviceLocator = getXpathRoot() + "//tr[" + i + "]/td[1]";
		}
		
		//remove strange invisible check box that get added.
		for (String device : devices) {
			deleteOrphansByName(device);
		}
		return this;
	}
	
	/**
	 * Click the enable to link in Cap Control areas table. 
	 * @param areaName name of the area.
	 * @return
	 */
	public CapControlTableSolvent clickEnableByAreaName(String areaName) {
		String enableLocator = getXpathRoot() + "//a[contains(text(), '" + areaName + "')]/following::span[text()='ENABLED'][1]";
		waitForJSObject();
		selenium.waitForElement(enableLocator);
		if(!selenium.isElementPresent(enableLocator))
			throw new SolventSeleniumException("Area with the name " + areaName + " is not available at " + enableLocator);
		selenium.click(enableLocator);
		return this;
	}
	
	/**
	 * Method clicks the state link for all Cap Control devices and states based on the Device name
	 * @return
	 */
	public CapControlTableSolvent clickStateLinkByDeviceName(String deviceName, String stateLink) {
		String stateLocator = "//td[contains(., '" + deviceName + "')]/following::span[text()='" + stateLink + "'][1]";
		selenium.waitForElement(stateLocator);
		if(!selenium.isElementPresent(stateLocator))
			throw new SolventSeleniumException("State Link with " + deviceName + " is not available at " + stateLocator);
		selenium.click(stateLocator);
		return this;
	}	
}
