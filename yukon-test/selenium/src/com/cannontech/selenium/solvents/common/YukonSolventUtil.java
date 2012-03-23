/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import com.cannontech.selenium.core.AbstractSolvent;

/**
 * Utility class to get table and trees that are available throughout the 
 * application.
 * TODO: Add similar method to get TreeWithID.
 * <br><br>
 * @author anuradha.uduwage
 *
 */
public class YukonSolventUtil {

	public static YukonTableSolvent getYukonTableWithID(String tableID, AbstractSolvent solvent) {
		return solvent.navigateTo(new YukonTableSolvent("tableId=" + tableID));
	}
}
