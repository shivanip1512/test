/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handles call information table under call tracking option.
 * @author anjana.manandhar
 *
 */
public class ApplianceTableSolvent extends CommonTableSolvent {

	/**
	 * Default constructor setting the tableClass value.
	 * @param params
	 */
	public ApplianceTableSolvent(String... params) {
		super("tableClass=compactResultsTable");
	}
	
	/**
	 * Method remove the Appliance based on the Program Name.
	 * @param programName name of the Program
	 * @return
	 */
	public ApplianceTableSolvent deleteByProgramName(String programName) {
		String nameRemoveLocator = getXpathRoot() + "//tr/td[normalize-space(text())='" + programName + "']//following::img[@title='Delete this appliance'][1]";
		String nameLocator = getXpathRoot() + "//tr/td[normalize-space(text())='" + programName + "']";

		selenium.waitForElement(nameLocator);
		selenium.waitForElement(nameRemoveLocator);
		selenium.click(nameRemoveLocator);
		return this;
	}
	
	/**
	 * Method to click the Appliance Name link.
	 * @param applianceName name of the Appliance
	 * @return
	 */
	public ApplianceTableSolvent clickApplianceName(String applianceName) {
		String nameLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + applianceName + "']";

		selenium.waitForElement(nameLocator);
		selenium.click(nameLocator);
		return this;
	}
	
	
	/**
	 * Method clicks on Remove Assigned Programs by Program Name
	 * @param programName name of the Program Name
	 */
	public ApplianceTableSolvent removeProgramByProgramName(String programName) {
		String removeLocator = "//tr/td[normalize-space(text())='" + programName + "']//following::img[@title='Remove Program from Appliance Category'][1]";
		String programLocator = "//tr/td[normalize-space(text())='" + programName + "']";
		selenium.waitForElement(programLocator);
		selenium.waitForElement(removeLocator);
		selenium.click(removeLocator);
		return this;
	}

	/**
	 * Method clicks on Edit Assigned Programs by Program Name
	 * @param programName name of the Program Name
	 */
	public ApplianceTableSolvent editProgramByProgramName(String programName) {
		String editLocator = "//tr/td[normalize-space(text())='" + programName + "']//following::img[@title='Edit Assigned Program'][1]";
		String programLocator = "//tr/td[normalize-space(text())='" + programName + "']";
		selenium.waitForElement(programLocator);
		selenium.waitForElement(editLocator);
		selenium.click(editLocator);
		return this;
	}

}
