package com.cannontech.selenium.test.sysadminstars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.ApplianceTableSolvent;
/**
 * This Class adds, then edits the properties of the appliance category
 * @author Kevin Krile	
 */
public class TestECoApplianceSelenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as starsop11/starsop11
	 */
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop11", "starsop11");
	}
	/**
	 * This test logs in as starsop11 and creates an AC appliance for QA_Test10
	 * This test depend on data in input file {@link TestECoApplianceSelenium.xml}
	 */
	@Test
	public void addAppliance(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Appliance Name");
		String[] type = getParamStrings("Appliance Type");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Appliance Categories");
		//loops for the 6 different appliances to be created
		for(int i=0;i<name.length;i++){
			common.clickButtonBySpanText("Create");
			common.enterInputText("save", "name", name[i]);
			common.selectDropDownMenu("save", "applianceType", type[i]);
			common.selectDropDownMenuByIdName("iconChooserIconSelect", type[i]);
			common.enterTextInTextArea("Description:", name[i] + " " + type[i]);
			common.clickFormButton("save", "save");
			Assert.assertEquals("System Administration: Appliance Category (" + name[i] + ")", common.getPageTitle());
			common.clickLinkByName("Appliance Categories");
			//checks to see if all of the links previously created are present
			for(int j=0;j<=i;j++)
				Assert.assertEquals(true, common.isLinkPresent(name[j]));
		}
	common.end();
	}
	/**
	 * This test logs in as starsop11 and adds programs for the AC/WH/I appliance for QA_Test10
	 * This test depend on data in input file {@link TestECoApplianceSelenium.xml}
	 */
	@Test
	public void addPrograms(){
		init();
		CommonSolvent common = new CommonSolvent();
		PopupMenuSolvent programPopup = PickerFactory.createPopupMenuSolvent("program", PickerType.MultiSelect);
        
		String[] programs = getParamStrings("Programs");
		String[] appliance = getParamStrings("App Program Add");
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		//loops for QA_Test10
		for(int i = 0; i < appliance.length; i++){
			common.clickLinkByName("Appliance Categories");
			common.clickLinkByName(appliance[i]);
			
		    common.clickButtonBySpanTextWithElementWait("Add","//div[@id='picker_programPicker_resultAreaFixed']");
            programPopup.clickMenuItem(programs[i]);
		    programPopup.clickButton("OK");

		    common.clickButtonBySpanText("OK");
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and edits programs for the AC/WH/I appliance for QA_Test10
	 * This test depend on data in input file {@link TestECoApplianceSelenium.xml}
	 */
	@Test
	public void editPrograms(){
		init();
		CommonSolvent common = new CommonSolvent();
		ApplianceTableSolvent applianceTableSolvent = new ApplianceTableSolvent();
		String[] programs = getParamStrings("Programs");
		String[] appliance = getParamStrings("App Program Add");
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		//loops for both QA_Test10
		for(int k=0;k<programs.length;k++){
			common.clickLinkByName("Appliance Categories");
			common.clickLinkByName(appliance[k]);
			applianceTableSolvent.editProgramByProgramName(programs[k]);
			common.enterTextInTextArea("Description:", "Adding the Chance of Control");
			common.selectDropDownMenu("saveAssignedProgram", "assignedProgram.chanceOfControlId", "Likely");
			common.clickButtonBySpanText("OK");
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and removes programs for the AC/WH/I appliance for QA_Test10
	 * This test depend on data in input file {@link TestECoApplianceSelenium.xml}
	 */
	@Test
	public void removePrograms(){
		init();
		CommonSolvent common = new CommonSolvent();
		ApplianceTableSolvent applianceTableSolvent = new ApplianceTableSolvent();
		String[] programs = getParamStrings("Programs");
		String[] appliance = getParamStrings("App Program Add");
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		//loops for both QA_Test10
		for(int k=0;k<programs.length;k++){
			common.clickLinkByName("Appliance Categories");
			common.clickLinkByName(appliance[k]);
			applianceTableSolvent.removeProgramByProgramName(programs[k]);
			common.clickButtonBySpanText("OK");
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and deletes the test appliances for QA_Test10
	 * This test depend on data in input file {@link TestECoApplianceSelenium.xml}
	 */
	@Test
	public void deleteAppliances(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Delete Appliance");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		//looping to deleting the appliances
		for(int i=0;i<name.length;i++){
			common.clickLinkByName("Appliance Categories");
			common.clickLinkByName(name[i]);
			common.clickButtonBySpanText("Edit");
			common.clickButtonBySpanTextWithElementWait("Delete");
			common.clickFormButton("save","delete");
		}
		common.end();
	}
}
