package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;

/**
 * @author jon.narr
 *
 */
public class TestHighBillComplaintSelenium extends SolventSeleniumTestCase {
	/**
	 * Method verifies the ability to get a High Bill Complaint report. 
	 */
	@Test
	public void getHBCReport() {
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
        common.enterInputTextByFormId("filterForm", "Quick Search", "MCT-410iL-02 (1471788)");
        common.clickButtonBySpanText("Search");

		Assert.assertEquals("MCT-410iL-02 (1471788)", common.getPageTitle());
		common.clickLinkByName("High Bill Complaint");
		Assert.assertEquals("Metering: High Bill Complaint", common.getPageTitle());
		common.clickButtonByExactName("Get Report");
		//TODO can't pull up report at this time 10/28/11
    	common.end();
	}
}