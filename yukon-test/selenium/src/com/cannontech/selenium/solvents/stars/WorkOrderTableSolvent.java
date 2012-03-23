package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handles call information table under call tracking option.
 * @author anjana.manandhar
 *
 */
public class WorkOrderTableSolvent extends CommonTableSolvent {

	/**
	 * Default constructor setting the tableClass value.
	 * @param params
	 */
	public WorkOrderTableSolvent(String... params) {
		super("tableClass=compactResultsTable callListTable rowHighlighting");
	}
	
	/**
	 * Method remove the Work Order based on the given Work Order Number.
	 * @param number value of the Work Order Number.
	 * @return
	 */
	public WorkOrderTableSolvent deleteByWorkOrderNumber(String number) {
		String numberRemoveLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + number + "']//following::a[@class='icon icon_remove'][1]";
		String numberLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + number + "']";
		selenium.waitForElement(numberLocator);
		selenium.waitForElement(numberRemoveLocator);
		selenium.click(numberRemoveLocator);
		return this;
	}
	
}
