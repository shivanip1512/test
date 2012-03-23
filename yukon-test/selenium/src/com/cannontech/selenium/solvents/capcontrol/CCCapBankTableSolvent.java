/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import com.cannontech.selenium.solvents.common.YukonTableSolvent;

/**
 * This solvent handles CapBank table in Cap Control.<br>
 * Class extends the {@link YukonTableSolvent} and sets the table id in constructor.
 * 
 * @author anjana.manandhar
 *
 */
public class CCCapBankTableSolvent extends YukonTableSolvent {
	
	/**
	 * Default constructor sets the table id of the CapBank table to override settings in @link {@link YukonTableSolvent}.<br>
	 * @param params
	 */
	public CCCapBankTableSolvent(String... params) {
		super("tableId=capBankTable");

	}

}
