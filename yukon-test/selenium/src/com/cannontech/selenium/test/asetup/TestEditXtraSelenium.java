package com.cannontech.selenium.test.asetup;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.HardwarePageSolvent;
import com.google.common.collect.Lists;
/**
 * This Class adds, then edits the properties of the appliance, service company, substation, and device type categories
 * @author Kevin Krile
 */
public class TestEditXtraSelenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as starsop/starsop
	 */
	public void init(){
		start();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		loginlogoutsolvent.cannonLogin("starsop", "starsop");
	}
	/**
	 * This test logs in as starsop and creates an AC appliance for QA_Test
	 */
	@Test
	public void addAppliance(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals("QA_Test", common.getYukonText("QA_Test"));
		common.clickLinkByName("QA_Test");
		common.clickLinkByName("Appliance Categories");
		common.clickButtonBySpanText("Create");
		
		common.enterInputText("save", "name", "AC");
		common.selectDropDownMenu("save", "applianceType", "Air Conditioner");
		common.selectDropDownMenuByIdName("iconChooserIconSelect", "Air Conditioner");
		common.enterTextInTextArea("Description:", "AC Appliance Category");
		common.clickFormButton("save", "save");
		Assert.assertEquals("System Administration: Appliance Category (AC)", common.getPageTitle());
		common.clickLinkByName("Appliance Categories");
		Assert.assertEquals("AC", common.getYukonText("AC"));
		common.end();
		}
	/**
	 * This test logs in as starsop and adds programs for the AC appliance for QA_Test
	 * This test depend on data in input file {@link TestEditXtraSelenium.xml}
	 */
	@Test
	public void addPrograms(){
		init();
		CommonSolvent common = new CommonSolvent();
		PopupMenuSolvent programPopup = PickerFactory.createPopupMenuSolvent("program", PickerType.MultiSelect);
		
		List<String> programs = Lists.newArrayList(getParamStrings("Programs"));

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals("QA_Test", common.getYukonText("QA_Test"));
		common.clickLinkByName("QA_Test");
		common.clickLinkByName("Appliance Categories");
		common.clickLinkByName("AC");
		
		common.clickButtonByTitle("Add one or more programs to this appliance category.");
		for(String program : programs) {
		    programPopup.clickMenuItem(program);
		}
		programPopup.clickButton("OK");

		common.clickButtonBySpanText("OK");
		common.end();
	}
	/**
	 * This test logs in as starsop and creates a Substation for QA_Test
	 */
	@Test
	public void createSubstation(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Substations");
		Assert.assertEquals("System Administration: Substation To Route Mapping", common.getPageTitle());
		common.enterTextByAttribute("name","name", "QA_Substation");
		common.clickFormButton("routeMapping/edit", "add");
		common.end();
	}
	/**
	 * This test logs in as starsop and links a route to a substation for QA_Test
	 */
	@Test
	public void linkSubstationToRoute(){
		init();
		CommonSolvent common = new CommonSolvent();
	
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Substations");
		Assert.assertEquals("System Administration: Substation To Route Mapping", common.getPageTitle());
		common.selectDropDownMenu("routeMapping/edit", "selection_list","QA_Substation");
		common.selectDropDownMenu("routeMapping/viewRoute", "avroutes", "a_CCU-710A");
		common.clickButtonByAttribute("onclick", "javascript:SubstationToRouteMappings_addRoute()");
		common.selectDropDownMenu("routeMapping/viewRoute", "avroutes", "a_CCU-721");
		common.clickButtonByAttribute("onclick", "javascript:SubstationToRouteMappings_addRoute()");
		common.clickButtonByAttribute("value","Apply");
		common.end();
	}
	/**
	 * This test logs in as starsop and adds the substation and route for QA_Test
	 */
	@Test
	public void addSubstationAndRoute(){
		init();
		HardwarePageSolvent hardware = new HardwarePageSolvent();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test");
		common.clickLinkByName("Substations And Routes");
		common.selectDropDownMenuByAttributeName("routeId", "a_CCU-710A");
		hardware.clickAddByTableHeader("Assigned Routes");
		Assert.assertEquals("Route Added Successfully", common.getYukonText("Route Added Successfully"));
		common.selectDropDownMenuByAttributeName("substationId", "QA_Substation");
		hardware.clickAddByTableHeader("Assigned Substations");
		Assert.assertEquals("Substation Added Successfully", common.getYukonText("Substation Added Successfully"));
		common.end();
	}
	/**
	 * This test logs in as starsop and creates an Service Company for QA_Test
	 */
	@Test
	public void addServiceCompany(){
		init();
		CommonSolvent common = new CommonSolvent();
		String action = "serviceCompany/update";
	
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test");
		common.clickLinkByName("Service Companies");
		common.clickButtonBySpanText("Create");
		common.enterInputText(action, "companyName", "QA_Service");
		common.enterInputText(action, "mainPhoneNumber", "555-555-1414");
		common.enterInputText(action, "mainFaxNumber", "555-555-1515");
		common.enterInputText(action, "emailContactNotification", "pspl-qa200@gmail.com");
		common.enterInputText(action, "hiType", "Install & Serv");
		common.enterInputText(action, "address.locationAddress1", "123 QA St");
		common.enterInputText(action, "address.locationAddress2", "STE 1200");
		common.enterInputText(action, "address.cityName", "QA City");
		common.enterInputText(action, "address.stateCode", "MN");
		common.enterInputText(action, "address.zipCode", "99123");
		common.enterInputText(action, "primaryContact.contFirstName", "Quirky");
		common.enterInputText(action, "primaryContact.contLastName", "QA");
		common.selectDropDownMenu(action, "primaryContact.loginID", "(none)");
		common.clickFormButton(action, "create");
		Assert.assertEquals("System Administration: Service Companies (QA_Test)", common.getPageTitle());
		common.end();
		}
	/**
	 * This test logs in as starsop and adds device types for QA_Test
	 * Restores to the default, then adds the text entries and menu options
	 * This test depend on data in input file {@link TestEnergyCompanyDeviceType.xml}
	 */
	@Test
	public void defaultSchedules(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		List<String> scheduleNames = Lists.newArrayList(getParamStrings("Schedule Name"));
		List<String> scheduleTypes = Lists.newArrayList(getParamStrings("Schedule Type"));
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test");
		for (int i = 0;i < scheduleNames.size(); i++){
			common.clickLinkByName("Schedules");
			common.clickLinkByName(scheduleNames.get(i));
			
			// TODO This should be using some sort of wizard popup testing functionality.
			common.clickButtonByTitle("Edit");
            common.clickButtonBySpanTextWithElementWait("Choose Mode", "//Div[@class='f_page page_0']");
			common.clickRadioButtonByLabelValue(scheduleTypes.get(i));
            common.clickButtonBySpanTextWithElementWait("Next", "//Div[@class='f_page page_1']");
            common.clickButtonBySpanText("Save");
		}
		common.end();
	}
}