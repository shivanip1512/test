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
import com.eaton.elements.modals.gears.CreateHoneywellPrgmGearModal.CreateHoneywellPrgmGearModalInnerClass;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramCreatePage;

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

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_RequiredFieldsOnly_Success() {

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Honeywell Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));

        modalInnerClass.getGearName().setInputValue("TestGear " + timeStamp);
        modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
        waitForLoadingSpinner();
        modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Program: " + name, Optional.empty());

        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_AllFields_Success() {

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Honeywell Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));

        modalInnerClass.getGearName().setInputValue("TestGear " + timeStamp);
        modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
        waitForLoadingSpinner();
        modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

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

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_GearTypeHoneywellCycle_Success() {

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Honeywell Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));

        modalInnerClass.getGearName().setInputValue("TestGear " + timeStamp);
        modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
        waitForLoadingSpinner();
        modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Program: " + name, Optional.empty());

        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_WithMultipleGears_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String loadProgramName = "AT LM Honeywell Program " + timeStamp;
        final String EXPECTED_MSG = loadProgramName + " saved successfully.";

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        createPage.getName().setInputValue(loadProgramName);
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));

        modalInnerClass.getGearName().setInputValue("TestGear " + timeStamp);
        modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
        waitForLoadingSpinner();
        modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();
        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);
        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Program: " + loadProgramName, Optional.empty());
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_GearTypeValues_Correct() {

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));

        List<String> actualDropDownValues = modalInnerClass.getGearType().getOptionValues();

        List<String> expectedDropDownValues = new ArrayList<>(
                List.of("Select", "Honeywell Cycle", "Honeywell Setpoint"));

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);

    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_SetpointGearControlParamSection_TitleCorrect() {

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String gearName = "TestGear " + timeStamp;
        modalInnerClass.getGearName().setInputValue(gearName);
        modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
        waitForLoadingSpinner();

        Section rampSection = modal.getPageSection("Control Parameters");

        assertThat(rampSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_SetpointGearControlParamSection_LabelsCorrect() {

        String sectionName = "Control Parameters";

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
        waitForLoadingSpinner();

        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(
                List.of("Mandatory:", "Mode:", "Setpoint Offset:", "How To Stop Control:"));

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_CycleGearControlParamSection_TitleCorrect() {

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
        waitForLoadingSpinner();

        Section rampSection = modal.getPageSection("Control Parameters");

        assertThat(rampSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_CycleGearControlParamSection_LabelsCorrect() {

        String sectionName = "Control Parameters";

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));

        modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
        waitForLoadingSpinner();

        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(
                List.of("Mandatory:", "Control Percent:", "Cycle Period:", "How To Stop Control:"));
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_SetpointGearOptionalAttributesSection_TitleCorrect() {

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
        waitForLoadingSpinner();

        Section optionalAttributesSection = modal.getPageSection("Optional Attributes");

        assertThat(optionalAttributesSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_SetpointGearOptionalAttributesSection_LabelsCorrect() {

        String sectionName = "Optional Attributes";

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
        waitForLoadingSpinner();

        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(
                List.of("Group Capacity Reduction:", "When to Change:"));
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_CycleGearOptionalAttributesSection_TitleCorrect() {

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
        waitForLoadingSpinner();

        Section rampSection = modal.getPageSection("Optional Attributes");

        assertThat(rampSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_CycleGearOptionalAttributesSection_LabelsCorrect() {

        String sectionName = "Optional Attributes";

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
        waitForLoadingSpinner();

        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(
                List.of("Group Capacity Reduction:", "When to Change:"));
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_CycleGearRampInRampOutSection_TitleCorrect() {

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
        waitForLoadingSpinner();

        Section rampSection = modal.getPageSection("Ramp In / Ramp Out");

        assertThat(rampSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_CycleGearRampInRampOutSection_LabelsCorrect() {

        String sectionName = "Ramp In / Ramp Out";

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt,
                Optional.empty(),
                Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
        modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
        waitForLoadingSpinner();

        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(List.of("Ramp In/Out:"));
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
}