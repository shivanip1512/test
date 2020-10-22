package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupHoneywellCreateBuilder;
import com.eaton.elements.Section;
import com.eaton.elements.modals.gears.CreateHoneywellPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;

public class LoadProgramHoneywellCreateTests extends SeleniumTestSetup {

	private LoadProgramCreatePage createPage;
	private DriverExtensions driverExt;
	private String ldGrpName;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();

		Pair<JSONObject, JSONObject> pair = new LoadGroupHoneywellCreateBuilder.Builder(Optional.empty())
				.withKwCapacity(Optional.empty()).create();

		JSONObject response = pair.getValue1();

		ldGrpName = response.getString("name");

		createPage = new LoadProgramCreatePage(driverExt);
		navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod() {
		refreshPage(createPage);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_RequiredFieldsOnly_Success() {

		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT LM Honeywell Program " + timeStamp;

		final String EXPECTED_MSG = name + " saved successfully.";

		createPage.getName().setInputValue(name);
		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();

		modal.getGearName().setInputValue("TestGear " + timeStamp);
		modal.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();
		modal.clickOkAndWaitForModalCloseDisplayNone();

		LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

		groupsTab.clickTabAndWait("Load Groups");
		groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

		createPage.getSaveBtn().click();

		waitForPageToLoad("Load Program: " + name, Optional.empty());

		LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);

		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_AllFields_Success() {

		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT LM Honeywell Program " + timeStamp;

		final String EXPECTED_MSG = name + " saved successfully.";

		createPage.getName().setInputValue(name);
		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();

		modal.getGearName().setInputValue("TestGear " + timeStamp);
		modal.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();
		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getTriggerOffset().setInputValue("78");
		createPage.getRestoreOffset().setInputValue("1000");

		createPage.getUseWindowOne().selectValue("Yes");
		createPage.getStartTimeWindowOne().setValue("12:57");
		createPage.getStopTimeWindowOne().setValue("23:59");
		createPage.getUseWindowTwo().selectValue("No");

		LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

		groupsTab.clickTabAndWait("Load Groups");
		groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

		createPage.getSaveBtn().click();

		waitForPageToLoad("Load Program: " + name, Optional.empty());

		LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);

		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_GearTypeHoneywellCycle_Success() {

		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT LM Honeywell Program " + timeStamp;

		final String EXPECTED_MSG = name + " saved successfully.";

		createPage.getName().setInputValue(name);
		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();

		modal.getGearName().setInputValue("TestGear " + timeStamp);
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();
		modal.clickOkAndWaitForModalCloseDisplayNone();

		LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

		groupsTab.clickTabAndWait("Load Groups");
		groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

		createPage.getSaveBtn().click();

		waitForPageToLoad("Load Program: " + name, Optional.empty());

		LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);

		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_GearTypeValues_Correct() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();

		List<String> actualDropDownValues = modal.getGearType().getOptionValues();

		List<String> expectedDropDownValues = new ArrayList<>(
				List.of("Select", "Honeywell Cycle", "Honeywell Setpoint"));

		assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);

	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_SetpointGearControlParamSection_LabelsCorrect() {
		
		String sectionName = "Control Parameters";

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		modal.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();

		List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
		List<String> expectedLabels = new ArrayList<>(
				List.of("Mandatory:", "Mode:", "Setpoint Offset:", "How To Stop Control:"));

		assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_CycleGearControlParamSection_LabelsCorrect() {
		
		String sectionName = "Control Parameters";

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
		List<String> expectedLabels = new ArrayList<>(
				List.of("Mandatory:", "Control Percent:", "Cycle Period:", "How To Stop Control:"));
		assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_SetpointGearSetpointOffset_RequiredValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();

		modal.getSetpointOffset().clearInputValue();
		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.getGears().clickGearByName(gearName);
		modal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modal.getSetpointOffset().getValidationError()).isEqualTo("Setpoint Offset is required.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_SetpointGearSetpointOffset_MaxRangeValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();

		modal.getSetpointOffset().setInputValue("20");
		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.getGears().clickGearByName(gearName);
		modal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modal.getSetpointOffset().getValidationError()).isEqualTo("Must be between -10 and 10.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_SetpointGearSetpointOffset_MinRangeValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();

		modal.getSetpointOffset().setInputValue("-100");
		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.getGears().clickGearByName(gearName);
		modal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modal.getSetpointOffset().getValidationError()).isEqualTo("Must be between -10 and 10.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_CycleGearControlPercent_RequiredValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		modal.getControlPercent().clearInputValue();
		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.getGears().clickGearByName(gearName);
		modal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modal.getControlPercent().getValidationError()).isEqualTo("Control Percent is required.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_CycleGearControlPercent_MaxRangeValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		modal.getControlPercent().setInputValue("200");
		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.getGears().clickGearByName(gearName);
		modal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modal.getControlPercent().getValidationError()).isEqualTo("Must be between 0 and 100.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_CycleGearControlPercent_MinRangeValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		modal.getControlPercent().setInputValue("-100");
		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.getGears().clickGearByName(gearName);
		modal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modal.getControlPercent().getValidationError()).isEqualTo("Must be between 0 and 100.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_InvalidGearNameErrorMessage_Displayed() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String gearName = "/sda@3#";
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.getGears().clickGearByName(gearName);
		modal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modal.getGearName().getValidationError())
				.isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
	}

	@Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_CycleGearRampInRampOutSection_TitleCorrect() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		Section rampSection = modal.getPageSection("Ramp In / Ramp Out");

		assertThat(rampSection.getSection()).isNotNull();
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmHoneywellCreate_CycleGearRampInRampOutSection_LabelsCorrect() {
		
		String sectionName = "Ramp In / Ramp Out";

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
		List<String> expectedLabels = new ArrayList<>(List.of("Ramp In/Out:"));
		assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	}
}
