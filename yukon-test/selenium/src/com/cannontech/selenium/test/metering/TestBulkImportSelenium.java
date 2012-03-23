package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;

/**
 * @author Jon Narr
 * Jira Item: QA-154
 * The following tests exercise the use of Bulk Operations Bulk Import feature.
 */
public class TestBulkImportSelenium extends SolventSeleniumTestCase {
	public void init() {
		//Starts the session for the test. 
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");		
	}
	@Test
	public void testBulkImport() {
		init();
		// Navigate to the Bulk Operations page
		CommonSolvent common= new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		common.clickLinkByName("Bulk Operations");
		Assert.assertEquals("Bulk Operations", common.getPageTitle());
		Assert.assertEquals("Choose Type Of Bulk Operation:", widget.getWidgetTitle("Choose Type Of Bulk Operation:"));
		
		// Navigate to the Bulk Import page
		common.clickButtonByName("Bulk Import");
		
		// Import a file
		common.enterInputText("import/parseUpload", "dataFile",getParamString("BulkImportFile"));
		common.clickButtonByName("Load");
		Assert.assertEquals("Confirm you wish to perform the import, and set the following fields:",
				widget.getWidgetTitle("Confirm you wish to perform the import, and set the following fields:"));
		common.clickButtonByName("Import");
		Assert.assertEquals("Results of import:", widget.getWidgetTitle("Results of import:"));
		
		common.end();
	}
}