/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import com.cannontech.selenium.solvents.common.YukonTableSolvent;

/**
 * This solvent handles Substation in Area table in Cap Control.<br>
 * Class extends the {@link YukonTableSolvent} and sets the table id in constructor.
 * 
 * @author anjana.manandhar
 *
 */
public class CCSubstationInAreaTableSolvent extends YukonTableSolvent {
	
	/**
	 * Default constructor sets the table id of the substation in area table to override settings in @link {@link YukonTableSolvent}.<br>
	 * @param params
	 */
	public CCSubstationInAreaTableSolvent(String... params) {
		super("tableId=subTable");
	}

}
