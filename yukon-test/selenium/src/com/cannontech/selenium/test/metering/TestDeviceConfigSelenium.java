package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
/**
 * @author jon.narr
 */
public class TestDeviceConfigSelenium extends SolventSeleniumTestCase {
	public void init() {
		//Starts the new session
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");
	}
	/**
	 * Method verifies the creates, copies, and deletes a MCT 470 Configuration 
	 */
	@Test
	public void createMCT470Config() {
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Device Configuration");
		Assert.assertEquals("Device Configuration Page", common.getPageTitle());
		
		// Create MCT-470 Configuration
		common.selectDropDownMenu("deviceConfiguration", "configurationTemplate", "MCT 470 Configuration");
		common.clickButtonByName("Create");
		Assert.assertEquals("Device Configuration Page", common.getPageTitle());
		common.enterInputTextByFormId("configurationForm", "name", "MCT 470 AutoTest Config");
		common.clickButtonByName("Save");
		common.clickOkOnConfirmation();	
		
		// Copy MCT-470 Configuration
		common.selectDropDownMenu("deviceConfiguration", "configuration", "MCT 470 AutoTest Config");
		common.clickButtonByName("Copy");
		common.enterInputTextByFormId("configurationForm", "name", "Config MCT 470");
		common.clickButtonByName("Save");	
		common.clickOkOnConfirmation();	
		
		// Delete MCT-470 Configuration
		common.selectDropDownMenu("deviceConfiguration", "configuration", "Config MCT 470");
		common.clickButtonByName("Delete");
		common.clickOkOnConfirmation();	
	
		// Cleanup
		common.selectDropDownMenu("deviceConfiguration", "configuration", "MCT 470 AutoTest Config");
		common.clickButtonByName("Delete");
		common.clickOkOnConfirmation();
		
    	common.end();
	}
	/**
	 * Method verifies the creation of an MCT 430 Configuration 
	 */
	@Test
	public void createMCT430Config() {
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Device Configuration");
		Assert.assertEquals("Device Configuration Page", common.getPageTitle());
		
		// Create MCT-430 Configuration
		common.selectDropDownMenu("deviceConfiguration", "configurationTemplate", "MCT 430 Configuration");
		common.clickButtonByName("Create");
		Assert.assertEquals("Device Configuration Page", common.getPageTitle());
		common.enterInputTextByFormId("configurationForm", "name", "MCT 430 AutoTest Config");
		common.clickButtonByName("Save");
		common.clickOkOnConfirmation();	
		
		// Copy MCT-430 Configuration
		common.selectDropDownMenu("deviceConfiguration", "configuration", "MCT 430 AutoTest Config");
		common.clickButtonByName("Copy");
		common.enterInputTextByFormId("configurationForm", "name", "Config MCT 430");
		common.clickButtonByName("Save");	
		common.clickOkOnConfirmation();	
		
		// Delete MCT-430 Configuration
		common.selectDropDownMenu("deviceConfiguration", "configuration", "Config MCT 430");
		common.clickButtonByName("Delete");
		common.clickOkOnConfirmation();	

		// Cleanup
		common.selectDropDownMenu("deviceConfiguration", "configuration", "MCT 430 AutoTest Config");
		common.clickButtonByName("Delete");
		common.clickOkOnConfirmation();
		
    	common.end();
	}
	/**
	 * Method verifies the creation of an MCT 420 Configuration 
	 */
	@Test
	public void createMCT420Config() {
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Device Configuration");
		Assert.assertEquals("Device Configuration Page", common.getPageTitle());
		common.selectDropDownMenu("deviceConfiguration", "configurationTemplate", "MCT 420 Configuration");
		
		// Create MCT-420 Configuration
		common.clickButtonByName("Create");
		Assert.assertEquals("Device Configuration Page", common.getPageTitle());
		common.enterInputTextByFormId("configurationForm", "name", "MCT 420 AutoTest Config");
		common.clickButtonByName("Save");
		common.clickOkOnConfirmation();	
		
		// Copy MCT-420 Configuration
		common.selectDropDownMenu("deviceConfiguration", "configuration", "MCT 420 AutoTest Config");
		common.clickButtonByName("Copy");
		common.enterInputTextByFormId("configurationForm", "name", "Config MCT 420");
		common.clickButtonByName("Save");
		common.clickOkOnConfirmation();	
		
		// Delete MCT-420 Configuration
		common.selectDropDownMenu("deviceConfiguration", "configuration", "Config MCT 420");
		common.clickButtonByName("Delete");
		common.clickOkOnConfirmation();
		
		// Cleanup
		common.selectDropDownMenu("deviceConfiguration", "configuration", "MCT 420 AutoTest Config");
		common.clickButtonByName("Delete");
		common.clickOkOnConfirmation();
		
    	common.end();
	}
}