/**
 * 
 */
package com.cannontech.selenium.test.common;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;

/**
 * This class holds common methods that can be used in test scripts.
 * @author anuradha.uduwage
 *
 */
public class CommonHelper extends SolventSeleniumTestCase {

	/**
	 * This method will rebuild the indexes. 
	 */
	public void buildAllIndexes() {
		String[] indexes = {"loginGroupPicker", "pointPicker", "paoPicker", "userPicker"};
		CommonSolvent common = new CommonSolvent();
		for (String index : indexes) {
			common.buildIndexBuyIndexName(index);
		}
	}
}
