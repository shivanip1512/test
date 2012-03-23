package com.cannontech.selenium.test.capcontrol;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.capcontrol.CapControlTableSolvent;
import com.cannontech.selenium.solvents.common.*;
/**
 * This class tests Cap Bank Control Bulk Import functionality. The test imports two sets of 
 * import file using Cap Control importer; the first import file contains Subarea, Substation, 
 * Substation Bus, Feeder and Cap Banks. The second import file imports the 2-way Cap Bank Controllers. 
 * After the successful import of both import files, all the devices should be attached 
 * correctly to the associated parent devices.At least, it deletes all devices.
 * This test depends on data in input file {@link TestCCBulkImportSelenium.xml}
 * 
 * @author anjana.manandhar
 */
public class TestCCBulkImportSelenium extends SolventSeleniumTestCase {
	/**
	 * Creating an array variable 'devices' and assigning its value to null.
	 */
	String[] devices = null;
	/**
	 * Test method logs in as yukon, yukon and check all the links in navigation
	 * page.
	 */
	public void init() {
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		loginLogoutSolvent.cannonLogin("syukon", "syukon");
	}
	/**
	 * This method imports all Cap Control devices in the CSV file.
	 * 
	 */
	@Test
	public void testCCBulkImport() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.openURL(common.getBaseURL() + "/spring/capcontrol/importer");
		String resultsDivXpath = "//div[contains(@id, 'titledContainer_') and contains(@id, '_content')]";
		
		Assert.assertEquals("CapControl Importer", common.getYukonText("CapControl Importer"));
		common.enterInputText("tools/importFile", "dataFile", getParamString("ImportCCDevice"));
		common.clickButtonByNameWithElementWait("Process", resultsDivXpath+"//div[@class='okGreen']");
		if(common.isTextPresent("Imported Successfully")) {
			common.enterInputText("tools/importFile", "dataFile", getParamString("ImportCBCDevice"));
		}
		common.clickButtonByNameWithElementWait("Process", resultsDivXpath+"//div[@class='okGreen']");
		Assert.assertEquals("Imported Successfully", common.getYukonText("Imported Successfully"));
		// I'm not a fan of using these, but in this case we have to.  There is no way for us to detect
		// when the cap control pieces are actually in the web server since we create them in the database
		// and wait for a db change to add them to the system.
		waitTenSeconds();
		common.end();
	}
	/**
	 * This method verifies the devices attached by BulkImport test are displayed on the 4-Tier page.  
	 */
	@Test
	public void verifyAttachedSubstations() {
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Home");
		common.clickLinkByName("Volt/Var Management");
		Assert.assertEquals("Substation Areas", common.getPageTitle());
		Assert.assertEquals("North Area", common.getLinkText("North Area"));
		common.clickLinkByName("North Area");
		Assert.assertEquals("North Area - Substations", common.getPageTitle());
		//verify attached Substations 
		String[] substations = getParamStrings("substation");
		for (String substation : substations) {
			Assert.assertEquals(true, new YukonTableSolvent().isTextPresentInRow(substation));
		}
		
		//verify attached Substation Buses
		common.clickLinkByName("Substation N1");
		String[] subbuses = getParamStrings("substationBus");
		for (String subbus : subbuses) {
			Assert.assertEquals(true, new YukonTableSolvent().isTextPresentInRow(subbus));
		}
		
		//verify attached Feeders
		String[] feeders = getParamStrings("feeder");
		for (String feeder : feeders) {
			Assert.assertEquals(true, new YukonTableSolvent().isTextPresentInRow(feeder));
		}
		
		//verify attached CapBanks
		String[] capbanks = getParamStrings("capbank");
		for (String capbank : capbanks) {
			Assert.assertEquals(true, new YukonTableSolvent().isTextPresentInRow(capbank));
		}
		
		//verify attached CBCs
		String[] cbcs = getParamStrings("cbc");
		for (String cbc : cbcs) {
			Assert.assertEquals(true, new YukonTableSolvent().isTextPresentInRow(cbc));
		}
		common.end();
	}
	/**
	 * This method deletes the SubArea which makes all the Cap Control devices Orphan. 
	 * It clicks on Orphaned link to delete Substations, SubBuses, Feeders, CapBanks and CBCs as a bulk operation.
	 */
	@Test
	public void deleteDevices() {
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Volt/Var Management");
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlTableSolvent rt = new CapControlTableSolvent("tableId=resTable");
		
		common.clickFindAndGo("North Area");
		rt.deleteOrphansInBulk();
		
		topMenu.clickTopMenuItem("Orphans", "Orphaned Substations");
		rt.deleteOrphansInBulk();
		
        topMenu.clickTopMenuItem("Orphans", "Orphaned Substation Buses");
		rt.deleteOrphansInBulk();	

        topMenu.clickTopMenuItem("Orphans", "Orphaned Feeders");
		rt.deleteOrphansInBulk();	

        topMenu.clickTopMenuItem("Orphans", "Orphaned Capbanks");
		rt.deleteOrphansInBulk();

        topMenu.clickTopMenuItem("Orphans", "Orphaned CBCs");
		rt.deleteOrphansInBulk();	
		common.end();
	}
}