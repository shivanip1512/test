/**
 * 
 */
package com.cannontech.selenium.test.metering;


import org.junit.Assert;
import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.OperationsPageSolvent;
import com.cannontech.selenium.solvents.metering.MeterSearchTableSolvent;

/**
 * Test meter search results table.
 * @author anuradha.uduwage
 *
 */

public class TestMeterSearchResultsSelenium extends SolventSeleniumTestCase {

	/**
	 * Use search table results to navigate to Meter Details page.
	 */
/*
	@Test
	public void navigateToMeterDetailsFromSearchResults() {
		LoginLogoutSolvent operationPage = this.start(this.getAuthenticatedSeleniumSession(), new LoginLogoutSolvent());
		operationPage.cannonLogin("syukon", "syukon").navigateTo(new OperationsPageSolvent()).clickLinkItem("Metering");
		CommonSolvent common = new CommonSolvent();
		
		MeterSearchTableSolvent meterSearch = new MeterSearchTableSolvent();
        common.enterInputTextByFormId("filterForm", "Quick Search", "a_MCT");
        common.clickButtonBySpanText("Search");
		
		common.clickLinkByName("a_MCT-470");
		meterSearch.navigateTo(common.clickLinkByName("« Back To Search Results"));
		String meterName = meterSearch.getTextInCell(5, 1);
		Assert.assertEquals("a_MCT-470", meterName);
		meterSearch.mouseOver(6);
		meterSearch.mouseOver("a_MCT-410FL");
		common.clickLinkByName("a_MCT-310IL");
		Assert.assertEquals(true, common.isTextPresent("a_MCT-310IL"));
		meterSearch.end();
	}
*/	
	/**
	 * Sort Meter Results table by column.
	 */
/*
 	@Test
	public void sortMeterSearchTable() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		CommonSolvent common = new CommonSolvent();
		login.cannonLogin("syukon", "syukon");
		new OperationsPageSolvent().clickLinkItem("Metering");
		common.clickButtonBySpanText("Search");
		MeterSearchTableSolvent meterSearch = new MeterSearchTableSolvent();
		meterSearch.sortTableByColumn("Device Name");
		String beforeSort = meterSearch.getTextInCell(2, 1);
		meterSearch.sortTableByColumn("Device Name");
		String afterSort = meterSearch.getTextInCell(2, 1);
		Assert.assertNotSame(beforeSort, afterSort);
		meterSearch.end();
	}
	*/
}
