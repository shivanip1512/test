/**
 * 
 */
package com.cannontech.selenium.solvents.demandresponse;

import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * This class handles components in Demand Response programs table.
 * @author anuradha.uduwage
 *
 */
public class DRProgramsTableSolvent extends DemandResponseTableSolvent {

	/**
	 * Sets the table id value for the programs table.
	 * @param params
	 */
	public DRProgramsTableSolvent(String... params) {
		super("tableId=programList");
	}
	
	/**
	 * Click program by name, it throws an exception if the program can't be found.
	 * @param programName name of the program.
	 * @return
	 */
	public DemandResponseTableSolvent clickProgramByName(String programName) {
		String nameLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + programName + "']";
		waitForJSObject();
		selenium.waitForElement(nameLocator);
		if(!selenium.isElementPresent(nameLocator))
			throw new SolventSeleniumException("Program with the name " + programName + 
					" is not available in scenario table with table ID " + this.getTableId());
		selenium.click(nameLocator);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Method return the value of the Current Gear based on given program name.
	 * @param programName name of the program.
	 * @return
	 */
	public String getCurrentGearbyProgram(String programName) {
		String gearLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + programName + "']//following::td[5]";
		String currentGear = null;
		if(!isDrObjectAvailable(programName))
			throw new SolventSeleniumException("Program with the name " + programName + 
					" is not available in Programs table with table ID " + this.getTableId());
		else
			if(!selenium.isElementPresent(gearLocator))
				throw new SolventSeleniumException("Unable to get Current Gear for the program " + programName);
			else
				currentGear = selenium.getText(gearLocator);
		return currentGear;
	}
	
	/**
	 * Method return the value of the Reduction column based on the given program name.
	 * @param programName name of the program.
	 * @return
	 */
	public String getReductionByProgram(String programName) {
		String reductionLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + programName + "']//following::td[7]";
		String reductionValue = null;
		if(!isDrObjectAvailable(programName))
			throw new SolventSeleniumException("Program with the name " + programName + 
					" is not available in Programs table with table ID " + this.getTableId());
		else
			if(!selenium.isElementPresent(reductionLocator))
				throw new SolventSeleniumException("Unable to get Reduction for the program " + programName);
			else
				reductionValue = selenium.getText(reductionLocator);
		return reductionValue;		
	}
	


}
