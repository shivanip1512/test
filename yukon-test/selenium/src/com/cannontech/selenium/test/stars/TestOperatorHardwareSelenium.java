package com.cannontech.selenium.test.stars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.EnrollmentTableSolvent;
import com.cannontech.selenium.solvents.stars.HardwarePageSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;

public class TestOperatorHardwareSelenium extends SolventSeleniumTestCase{
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and Adds a new ZigBee Hardware Thermostat to the Customer.
	 */
	@Test
	public void addZigbeeHardware() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		String[] installCodes = {"00:0C:C1:00:27:19:C4:CA", "00:0C:C1:00:27:19:C4:CB", "00:0C:C1:00:27:19:C4:E2", "00:11:22:33:44:55:66:77"};
		String[] MACAddresses = {"00:0C:C1:00:27:19:C4:C9", "00:0C:C1:00:27:19:C4:CB", "00:0C:C1:00:27:19:C4:E2", "00:11:22:33:44:55:66:77"};
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		for(int i=1;i<5;i++){
				
			//Add Hardware to Customer 
			common.clickLinkByName("Hardware");
			Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
			common.selectDropDownMenuByIdName("tstatTypeToAdd", "UtilityPRO ZigBee");
			
			hardware.clickAddByTableHeader("Thermostats");
			hardware.enterSerialNumber("Serial Number:", "51190000" + i);
			hardware.clickInventoryButton();
			common.clickButtonBySpanText("OK");
	
			Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
			common.clickFormButton("create", "save");
			Assert.assertEquals("Errors found, check fields.", common.getYukonText("Errors found, check fields"));
			Assert.assertEquals("An Install Code is required.", common.getYukonText("An Install Code is required."));
			Assert.assertEquals("A MAC Address is required.", common.getYukonText("A MAC Address is required."));
			common.enterInputText("create", "installCode", "QA_Service");
			common.enterInputText("create", "macAddress", "QA_Service");
			common.clickFormButton("create", "save");
			Assert.assertEquals("Invalid Install Code format. Expected XX:XX:XX:XX:XX:XX:XX:XX", common.getYukonText("Invalid Install Code format. Expected XX:XX:XX:XX:XX:XX:XX:XX"));
			Assert.assertEquals("Invalid MAC Address format. Expected XX:XX:XX:XX:XX:XX:XX:XX", common.getYukonText("Invalid MAC Address format. Expected XX:XX:XX:XX:XX:XX:XX:XX"));
			common.enterInputText("create", "installCode", installCodes[i-1]);
			common.enterInputText("create", "macAddress", MACAddresses[i-1]);
			common.clickFormButton("create", "save");
			Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));
			Assert.assertEquals("Operator: Hardware (UtilityPRO ZigBee 51190000" + i + ")", common.getPageTitle());
		}
		common.end();
	}/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and edits a ZigBee Hardware Thermostat.
	 */
	@Test
	public void editZigbeeHardware() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Add Hardware to Customer 
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		
		common.clickLinkByName("511900002");
		Assert.assertEquals("Operator: Hardware (UtilityPRO ZigBee 511900002)", common.getPageTitle());
		Assert.assertEquals("Decommissioned not found ", true, common.isTextPresent("Decommissioned"));

		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.enterInputText("update", "altTrackingNumber", "511900002alt");
		common.selectDropDownMenu("update", "serviceCompanyId", "QA_Service");
		common.clickFormButton("update", "save");
		Assert.assertEquals("Hardware Updated", common.getYukonText("Hardware Updated"));
		Assert.assertEquals("Operator: Hardware (UtilityPRO ZigBee 511900002)", common.getPageTitle());
		
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and deletes 2 Zigbee Hardware Thermostats.
	 */
	@Test
	public void deleteZigbeeHardware(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		//Add Hardware to Customer
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		common.clickLinkByName("511900001");
		Assert.assertEquals("Operator: Hardware (UtilityPRO ZigBee 511900001)", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…", "//div[@id='deleteHardwarePopup']");    //  not displaying delete device ## popup window   qa-415
		Assert.assertEquals("Delete Device UtilityPRO ZigBee 511900001", common.getYukonText("Delete Device UtilityPRO ZigBee 511900001"));
		common.clickFormButton("delete", "delete");
		Assert.assertEquals("Hardware Removed From Account", common.getYukonText("Hardware Removed From Account"));
		common.clickLinkByName("511900002");
		Assert.assertEquals("Operator: Hardware (UtilityPRO ZigBee 511900002)", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…", "//div[@id='deleteHardwarePopup']");    //  not displaying delete device ## popup window   qa-415
		Assert.assertEquals("Delete Device UtilityPRO ZigBee 511900002", common.getYukonText("Delete Device UtilityPRO ZigBee 511900002"));
		common.clickRadioButtonByName("Delete it from inventory permanently.");
		common.clickFormButton("delete", "delete");
			Assert.assertEquals("Hardware Deleted", common.getYukonText("Hardware Deleted"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and Adds a new Switch to the Customer.
	 */
	@Test
	public void addSwitches(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		String[] serial = {"512300001", "512300002", "512400001", "512400002"};
		String[] type = {"LCR-6200(EXPRESSCOM)", "LCR-6600(EXPRESSCOM)"};
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		for(int i=0;i<serial.length;i++){
			//Add Hardware to Customer 
			common.clickLinkByName("Hardware");
			Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
			common.selectDropDownMenuByIdName("switchTypeToAdd", type[i/2]);
			
			hardware.clickAddByTableHeader("Switches");
			hardware.enterSerialNumber("Serial Number:", serial[i]);
			hardware.clickInventoryButton();
			common.clickButtonBySpanText("OK");

			Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
			common.selectDropDownMenu("create", "serviceCompanyId", "QA_Service");
			common.selectDropDownMenu("create", "deviceStatusEntryId", "Installed");
			common.enterInputText("create", "displayLabel", serial[i]);
			common.clickFormButton("create", "save");
			Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));
			Assert.assertEquals("Operator: Hardware (" + type[i/2] + " " + serial[i] + ")", common.getPageTitle());
		}
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and edits a Switch.
	 */
	@Test
	public void editSwitches(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		String[] serial = {"512300001", "512300002", "512400001", "512400002"};
		String[] type = {"LCR-6200(EXPRESSCOM)", "LCR-6600(EXPRESSCOM)"};
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		for(int i=0;i<serial.length;i++){
			//Add Hardware to Customer 
			common.clickLinkByName("Hardware");
			Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
			common.clickLinkByName(serial[i]);
			Assert.assertEquals("Operator: Hardware (" + type[i/2] + " " + serial[i] + ")", common.getPageTitle());
			common.clickButtonByTitleWithPageLoadWait("Edit");
			Assert.assertEquals("Operator: Edit Hardware (" + type[i/2] + " " + serial[i] + ")", common.getPageTitle());
			common.enterInputText("update", "altTrackingNumber", (serial[i] + "alt"));
			common.clickFormButton("update", "save");
			Assert.assertEquals("Hardware Updated", common.getYukonText("Hardware Updated"));
			Assert.assertEquals("Operator: Hardware (" + type[i/2] + " " + serial[i] + ")", common.getPageTitle());
		}
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and enrolls a Switch in a program.
	 */
	@Test
	public void enrollSwitches(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		String[] serial = {"512300001", "512300002", "512400001", "512400002"};
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Operator: Enrollment (51000000)", common.getPageTitle());
		PopupMenuSolvent programPicker = PickerFactory.createPopupMenuSolvent("program", PickerType.SingleSelect);
		common.clickButtonByTitle("Add");
		programPicker.clickMenuItem("LdPgm07-2Gear");
		common.selectDropDownMenu("enrollment/confirmSave", "loadGroupId", "LdGrp04-Expresscom");
		for(int i=1;i<serial.length;i+=2){
			common.selectCheckBox(serial[i]);
			common.selectDropDownMenu("enrollment/confirmSave", "inventoryEnrollments[" + i + "].relay", "1");
		}
		common.clickButtonBySpanTextWithElementWait("OK");
		Assert.assertEquals(true, common.isTextPresent("Add Enrollment"));
		common.clickButtonBySpanTextWithElementWait("OK");
		Assert.assertEquals("Successfully enrolled in LdPgm07-2Gear.", common.getYukonText("Successfully enrolled in LdPgm07-2Gear."));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and edits a switch's configuration.
	 */
	@Test
	public void editSwitchConfiguration(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		EnrollmentTableSolvent enroll = new EnrollmentTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		hardware.clickEditConfiguration("512300002");
		Assert.assertEquals("Operator: Hardware Configuration (512300002)", common.getPageTitle());
		Assert.assertTrue(common.isTextPresent("LdPgm07-2Gear"));
		common.clickButtonByNameWithPageLoadWait("Save Config Only");
		Assert.assertTrue(common.isTextPresent("Configuration saved to database (command not sent to device)."));
		common.selectDropDownMenu("config/commit", "programEnrollments[0].relay", "2");
		common.clickButtonByNameWithPageLoadWait("Save to Batch");
		Assert.assertTrue(common.isTextPresent("Configuration saved to batch."));
		common.selectDropDownMenu("config/commit", "programEnrollments[0].relay", "3");
		common.clickButtonByNameWithPageLoadWait("Config");
		Assert.assertTrue(common.isTextPresent("Configuration saved and updated."));
		common.selectDropDownMenu("config/commit", "programEnrollments[0].relay", "4");
		common.clickButtonByNameWithPageLoadWait("Cancel");
		hardware.clickEditConfiguration("512300002");
		common.clickButtonByNameWithPageLoadWait("Disable");
		Assert.assertTrue(common.isTextPresent("Disable command sent."));
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("3\n None", enroll.getEnrollmentTableDataByProgramName("LdPgm07-2Gear", "relay"));
		Assert.assertEquals("Out of Service\n In Service", enroll.getEnrollmentTableDataByProgramName("LdPgm07-2Gear", "status"));
		
		common.clickLinkByName("Hardware");
		hardware.clickEditConfiguration("512300002");
		common.selectDropDownMenu("config/commit", "programEnrollments[0].relay", "1");
		common.clickButtonByNameWithPageLoadWait("Config");
		Assert.assertTrue(common.isTextPresent("Configuration saved and updated."));
		common.clickButtonByNameWithPageLoadWait("Enable");
		Assert.assertTrue(common.isTextPresent("Enable command sent."));
		//Enrollment History
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and unenrolls a Switch from a program.
	 */
	@Test
	public void unEnrollSwitches(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		EnrollmentTableSolvent enroll = new EnrollmentTableSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		common.clickLinkByName("Enrollment");
		Assert.assertEquals("Operator: Enrollment (51000000)", common.getPageTitle());
		enroll.deleteProgramByName("LdPgm07-2Gear");
		enroll.clickOkToDeleteProgram();
		Assert.assertEquals("Successfully unenrolled from LdPgm07-2Gear.", common.getYukonText("Successfully unenrolled from LdPgm07-2Gear."));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and deletes 2 Switches.
	 */
	@Test
	public void deleteSwitches(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		//Add Hardware to Customer
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		common.clickLinkByName("512300001");
		Assert.assertEquals("Operator: Hardware (LCR-6200(EXPRESSCOM) 512300001)", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…", "//div[@id='deleteHardwarePopup']");    //  not displaying delete device ## popup window   qa-415
		Assert.assertEquals("Delete Device LCR-6200(EXPRESSCOM) 512300001", common.getYukonText("Delete Device LCR-6200(EXPRESSCOM) 512300001"));
		common.clickFormButton("delete", "delete");
		Assert.assertEquals("Hardware Removed From Account", common.getYukonText("Hardware Removed From Account"));
		
		common.clickLinkByName("512400001");
		Assert.assertEquals("Operator: Hardware (LCR-6600(EXPRESSCOM) 512400001)", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…", "//div[@id='deleteHardwarePopup']");    //  not displaying delete device ## popup window   qa-415
		Assert.assertEquals("Delete Device LCR-6600(EXPRESSCOM) 512400001", common.getYukonText("Delete Device LCR-6600(EXPRESSCOM) 512400001"));
		common.clickRadioButtonByName("Delete it from inventory permanently.");
		common.clickFormButton("delete", "delete");
		Assert.assertEquals("Hardware Deleted", common.getYukonText("Hardware Deleted"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and Adds, Edits, Deletes a Switch device to the account.
	 * TODO not ready yet
	 */
	@Test
	public void addEditDeleteMeter() { 
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		
		//Add Meter Hardware to Customer 
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		
		PopupMenuSolvent meterPicker = PickerFactory.createPopupMenuSolvent("meter", PickerType.SingleSelect);
		hardware.clickAddByTableHeader("Meters");
		meterPicker.clickMenuItem("MCT-310IL (230153)");

		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		Assert.assertEquals("Hardware Added", common.getYukonText("Hardware Added"));

		common.clickLinkByName("MCT-310IL MCT-310IL (230153)");
		common.clickButtonByTitleWithPageLoadWait("Edit");	
		common.selectDropDownMenu("update", "deviceStatusEntryId", "Installed");
		common.clickFormButton("update", "save");
		Assert.assertEquals("Hardware Updated", common.getYukonText("Hardware Updated"));
		Assert.assertEquals("Operator: Hardware (MCT-310IL MCT-310IL (230153))", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…", "//div[@id='deleteHardwarePopup']");
		Assert.assertEquals("Delete Device MCT-310IL MCT-310IL (230153)", common.getYukonText("Delete Device MCT-310IL MCT-310IL (230153)"));
		common.clickFormButton("delete", "delete");
		Assert.assertEquals("Hardware Removed From Account", common.getYukonText("Hardware Removed From Account"));
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and Adds a new Switch to the Customer.
	 */
	@Test
	public void addGateway(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		String[] serial = {"519100001", "519100002"};
		String[] MACAddresses = {"00:40:9D:45:84:34", "00:40:9D:45:84:35"};
		String[] firmware = {"2.10.0.3", ""};
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		for(int i=0;i<serial.length;i++){
			//Add Hardware to Customer
			common.clickLinkByName("Hardware");
			Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
			
			hardware.clickAddByTableHeader("Gateways");
			hardware.enterSerialNumber("Serial Number:", serial[i]);
			hardware.clickInventoryButton();
			common.clickButtonBySpanText("OK");
	
			Assert.assertEquals("Operator: Create Hardware", common.getPageTitle());
			common.clickFormButton("create", "save");
			Assert.assertEquals("Error found, check fields.", common.getYukonText("Error found, check fields."));
			Assert.assertEquals("A MAC Address is required.", common.getYukonText("A MAC Address is required."));
			common.enterInputText("create", "macAddress", "QA_Service");
			common.clickFormButton("create", "save");
			Assert.assertEquals("Invalid MAC Address format. Expected XX:XX:XX:XX:XX:XX", common.getYukonText("Invalid MAC Address format. Expected XX:XX:XX:XX:XX:XX"));
			
			common.enterInputText("create", "macAddress", MACAddresses[i]);
			common.enterInputText("create", "firmwareVersion", firmware[i]);
			common.clickFormButton("create", "save");
			Assert.assertEquals("Hardware Created", common.getYukonText("Hardware Created"));
			Assert.assertEquals("Operator: Hardware (Digi Gateway " + serial[i] + ")", common.getPageTitle());
		}
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and edits a Switch.
	 */
	@Test
	public void editGateway(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		//Edit Hardware to Customer 
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		common.clickLinkByName("519100001");
		Assert.assertEquals("Operator: Hardware (Digi Gateway 519100001)", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		Assert.assertEquals("Operator: Edit Hardware (Digi Gateway 519100001)", common.getPageTitle());
		common.enterInputText("update", "altTrackingNumber", ("519100001alt"));
		common.selectDropDownMenu("update", "serviceCompanyId", "QA_Service");
		common.clickFormButton("update", "save");
		Assert.assertEquals("Hardware Updated", common.getYukonText("Hardware Updated"));
		Assert.assertEquals("Operator: Hardware (Digi Gateway 519100001)", common.getPageTitle());
		common.end();
	}
	/**
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and edits a Switch.
	 */
	@Test
	public void attachGatewayToZigbee(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		String[] serial = {"519100001", "519100002"};
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		for(int i=0;i<serial.length;i++){
			common.clickLinkByName("Hardware");
			Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
			common.clickLinkByName(serial[i]);
			Assert.assertEquals("Operator: Hardware (Digi Gateway " + serial[i] + ")", common.getPageTitle());
			common.selectDropDownMenu("hardware/zb/addDeviceToGateway", "deviceId", "51190000" + (i+3));
			common.clickButtonByTitleWithPageLoadWait("Add");
			Assert.assertEquals("Thermostat 51190000" + (i+3) + " assigned to gateway " + serial[i],
					common.getYukonText("Thermostat 51190000" + (i+3) + " assigned to gateway " + serial[i]));
		}
		//remove from zigbee error - zigbee web services not enabled
		common.end();
	}
	/**
	 * TODO write method that removes device from gateway
	 * This method navigates to the Hardware page in the Operator side <br>
	 * and deletes 2 Switches.
	 */
	@Test
	public void deleteGateway(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		
		stars.enterTextToSearchByMagnifyGlass("Search for existing customer:", "51000000");
		common.clickMagnifyingIcon("Search for existing customer:");
		Assert.assertEquals("Operator: Account (51000000)", common.getPageTitle());
		//Add Hardware to Customer
		common.clickLinkByName("Hardware");
		Assert.assertEquals("Operator: Hardware (51000000)", common.getPageTitle());
		common.clickLinkByName("519100001");
		Assert.assertEquals("Operator: Hardware (Digi Gateway 519100001)", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…", "//div[@id='deleteHardwarePopup']");    //  not displaying delete device ## popup window   qa-415
		Assert.assertEquals("Delete Device Digi Gateway 519100001",
				common.getYukonText("Delete Device Digi Gateway 519100001"));
		common.clickFormButton("delete", "delete");
		Assert.assertEquals("Hardware Removed From Account",
				common.getYukonText("Hardware Removed From Account"));
		
		common.clickLinkByName("519100002");
		Assert.assertEquals("Operator: Hardware (Digi Gateway 519100002)", common.getPageTitle());
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete…", "//div[@id='deleteHardwarePopup']");    //  not displaying delete device ## popup window   qa-415
		Assert.assertEquals("Delete Device Digi Gateway 519100002",
				common.getYukonText("Delete Device Digi Gateway 519100002"));
		common.clickRadioButtonByName("Delete it from inventory permanently.");
		common.clickFormButton("delete", "delete");
		Assert.assertEquals("Hardware Deleted", common.getYukonText("Hardware Deleted"));
		common.end();
	}
}