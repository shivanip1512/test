/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import com.cannontech.selenium.solvents.common.YukonTableSolvent;

/**
 * This solvent handle Substation table in Cap Control.<br>
 * Class extends the {@link YukonTableSolvent} and sets the table id in constructor.
 * 
 * @author anuradha.uduwage
 *
 */
public class CCSubStationTableSolvent extends YukonTableSolvent {

	/**
	 * Default constructor sets the table id of the substation table to override settings in @link {@link YukonTableSolvent}.<br>
	 * @param params
	 */
	public CCSubStationTableSolvent(String... params) {
		super("tableId=substationTable");
	}

}
