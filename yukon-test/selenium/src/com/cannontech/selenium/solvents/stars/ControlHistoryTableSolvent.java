/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handle control history table in stars operator section.
 * @author anjana.manandhar
 *
 */
public class ControlHistoryTableSolvent extends CommonTableSolvent {

	/**
	 * Default Constructor set the table class for contact info table.
	 * @param params
	 */
	public ControlHistoryTableSolvent(String... params) {
		super("tableClass=resultsTable controlHistorySummaryTable");
	}
}
