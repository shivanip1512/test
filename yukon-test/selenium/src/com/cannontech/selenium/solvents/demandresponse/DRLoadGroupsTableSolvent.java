/**
 * 
 */
package com.cannontech.selenium.solvents.demandresponse;

import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * This class handles components in Demand Response load groups table.
 * @author anuradha.uduwage
 *
 */
public class DRLoadGroupsTableSolvent extends DemandResponseTableSolvent {

	/**
	 * Default constructor setting table id.
	 * @param params
	 */
	public DRLoadGroupsTableSolvent(String... params) {
		super("tableId=loadGroupList");
	}

	/**
	 * Click Load Group by name, it throws an exception if the program can't be found.
	 * @param loadGroupName name of the program.
	 * @return
	 */
	public DemandResponseTableSolvent clickLoadGroupByName(String loadGroupName) {
		String nameLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + loadGroupName + "']";
		waitForJSObject();
		selenium.waitForElement(nameLocator);
		if(!selenium.isElementPresent(nameLocator))
			throw new SolventSeleniumException("Program with the name " + loadGroupName + 
					" is not available in scenario table with table ID " + this.getTableId());
		selenium.click(nameLocator);
		selenium.waitForPageToLoad();
		return this;
	}

	/**
	 * Method return the value for reduction column based on the Load Group name.
	 * @param loadGroupName name of the load group.
	 * @return
	 */
	public String getReductionByLoadGroup(String loadGroupName) {
		String reductionLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + loadGroupName + "']//following::td[5]";
		String reductionValue = null;
		if(!isDrObjectAvailable(loadGroupName))
			throw new SolventSeleniumException("Load Group with the name " + loadGroupName + 
					" is not available in Programs table with table ID " + this.getTableId());
		else
			if(!selenium.isElementPresent(reductionLocator))
				reductionValue = selenium.getText(reductionLocator);
		return reductionValue;		
	}
}
