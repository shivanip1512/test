/**
 * 
 */
package com.cannontech.selenium.solvents.demandresponse;

import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * This class handles Demand Response Control Areas components.
 * @author anuradha.uduwage
 *
 */
public class DRControlAreasTableSolvent extends DemandResponseTableSolvent {

	/**
	 * Sets the table id value for the control area table.
	 * @param params
	 */
	public DRControlAreasTableSolvent(String... params) {
		super("tableId=controlAreaList");
	}
	
	/**
	 * Click Control Area by name, it throws an exception if the control area can't be found.
	 * @param controlAreaName name of the control area.
	 * @return
	 */
	public DemandResponseTableSolvent clickControlAreaByName(String controlAreaName) {
		String nameLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + controlAreaName + "']";
		waitForJSObject();
		selenium.waitForElement(nameLocator);
		if(!selenium.isElementPresent(nameLocator))
			throw new SolventSeleniumException("Control Area with the name " + controlAreaName + 
					" is not available in scenario table with table ID " + this.getTableId());
		selenium.click(nameLocator);
		selenium.waitForPageToLoad();
		return this;
	}	

}
