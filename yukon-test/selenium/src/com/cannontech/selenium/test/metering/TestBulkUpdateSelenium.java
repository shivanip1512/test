package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;

/**
 * @author Jon Narr
 * Jira Id: QA-155
 * The following tests verify the ability to update multiple meters.
 */
public class TestBulkUpdateSelenium extends SolventSeleniumTestCase {
	public void init() {
		//Starts the session for the test. 
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");		
	}
	
	@Test
	public void testBulkUpdate() {
		
		init();
		// Navigate to the Bulk Operations page
		WidgetSolvent widget = new WidgetSolvent();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Bulk Operations");
		Assert.assertEquals("Bulk Operations", common.getPageTitle());
		Assert.assertEquals("Choose Type Of Bulk Operation:", widget.getWidgetTitle("Choose Type Of Bulk Operation:"));
		
		// Navigate to the Bulk Update page
		common.clickButtonByNameWithPageLoadWait("Bulk Update");
		Assert.assertEquals("Bulk Update", common.getPageTitle());
		
		// Import a file
		common.enterInputText("update/parseUpload", "dataFile",getParamString("BulkUpdateFile"));
		common.clickButtonByNameWithPageLoadWait("Load");
		Assert.assertEquals("Bulk Update Confirmation", common.getPageTitle());
		Assert.assertEquals("Confirm you wish to perform the following update:", widget.getWidgetTitle("Confirm you wish to perform the following update:"));

		common.clickButtonByNameWithPageLoadWait("Update");
		Assert.assertEquals("Bulk Update Results", common.getPageTitle());
		Assert.assertEquals("Results of update:", widget.getWidgetTitle("Results of update:"));
		common.end();
	}
}