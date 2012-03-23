package com.cannontech.selenium.test.metering;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;


/**
 * This test verifies that all reports can be generated and saved to the download folder
 * @author ricky.jones
 */
public class TestMeterReadReportsSelenium extends SolventSeleniumTestCase {
	
	//Please set to your local firefox downloads folder before running this test!
	private String firefoxDownloads = "C:\\Users\\Administrator\\Downloads\\";
	
	/**
	 * Test method logs in as yukon, yukon and check all the links in navigation
	 * page.
	 */
	public void init() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	
	/**
	 * This test verifies all reports can be generated
	 */
	@Test
	public void generateReports() {
		init();
		LocalSolventb local = new LocalSolventb();
		CommonSolvent common = new CommonSolvent();

		common.clickLinkByName("Reporting");
		Assert.assertEquals("Reports", common.getPageTitle());
		common.clickLinkByName("Metering");
		String[] reportFormats = {"Meter Reads", "Meter Outages Log", "Meter Outage Counts", "Meter Disconnect Status",
								  "Load Profile Setup Data", "LP Point Data Summary", "Scheduled Meter Reads (MACS)", "Meter Usage",
								  "MCT 430/470 Config To Device", "Disconnect Collar Data", "Scan Rate Setup Data"};
		
		File previousExisting = new File(firefoxDownloads+"Report.pdf");
	    if(previousExisting.exists())
	    	previousExisting.delete();
	    for(int i = 0;  i < reportFormats.length; i++){
			common.clickRadioButtonByName(reportFormats[i]);
			common.clickButtonByAttribute("name", "Generate");
			if(i == 0)
				local.setSaveAutomatically();
			waitTenSeconds();
			File nextReport = new File(firefoxDownloads+"Report.pdf");
			Assert.assertEquals(reportFormats[i]+" was successfully generated.", true, nextReport.exists());
			Assert.assertEquals(true, nextReport.exists());
		    nextReport.delete();
	    }
		//Stop test script
		common.end();
	}
}

/**
 * Solvent for local test development
 * @author ricky.jones
 */
class LocalSolventb extends AbstractSolvent {

	/**
	 * @param params
	 */
	public LocalSolventb(String... params) {
		super(params);
	}

	@Override
	public void prepare() {
		selenium.waitForPageToLoad(2000);
	}
  /**
   * Presses Alt+s, Alt+a in the firefox download dialog to save pdf files automatically
   */
	protected void setSaveAutomatically(){
		try{
			Thread.sleep(10000);
			Robot saveDialog = new Robot();
			saveDialog.keyPress(KeyEvent.VK_ALT);
			saveDialog.keyPress(KeyEvent.VK_S);
			saveDialog.keyRelease(KeyEvent.VK_ALT);
			saveDialog.keyRelease(KeyEvent.VK_S);
			Thread.sleep(1000);
			saveDialog.keyPress(KeyEvent.VK_ALT);
			saveDialog.keyPress(KeyEvent.VK_A);
			saveDialog.keyRelease(KeyEvent.VK_ALT);
			saveDialog.keyRelease(KeyEvent.VK_A);
			Thread.sleep(1000);
			saveDialog.keyPress(KeyEvent.VK_ENTER);
			saveDialog.keyRelease(KeyEvent.VK_ENTER);
		}catch(Exception e){ e.printStackTrace(); }
	}
}
