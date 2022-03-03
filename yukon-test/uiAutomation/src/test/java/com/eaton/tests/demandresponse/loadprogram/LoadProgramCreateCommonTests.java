package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;
import com.eaton.elements.Section;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.eaton.builders.drsetup.loadgroup.LoadGroupCreateService;
import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateService;
import com.eaton.elements.SelectBoxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramCreatePage;

public class LoadProgramCreateCommonTests extends SeleniumTestSetup {

    private static final String TYPE = "LM_ITRON_PROGRAM";
    private String timeStamp;
    private DriverExtensions driverExt;
    private String ldGrpName;
    private LoadProgramCreatePage createPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        ldGrpName = "Before Class " + timeStamp;

        Pair<JSONObject, JSONObject> pair = LoadGroupCreateService.buildAndCreateVirtualItronLoadGroup();

        JSONObject response = pair.getValue1();
        ldGrpName = response.getString("name");

        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
        createPage = new LoadProgramCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_PageTitle_Correct() {
        final String EXPECTED_TITLE = "Create Load Program";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Name_RequiredValidation() {
        throw new SkipException("Development Defect: YUK-23330");
        /*
         * createPage.getName().clearInputValue();
         * 
         * createPage.getSaveBtn().click();
         * 
         * assertThat(createPage.getName().getValidationError()).
         * isEqualTo("Name is required.");
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Name_InvalidCharValidation() {
        createPage.getName().setInputValue("test/,");
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();
        createPage.getSaveBtn().click();

        assertThat(createPage.getName().getValidationError()).isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Name_UniqueValidation() {

        Map<String, Pair<JSONObject, JSONObject>> pair = LoadProgramCreateService
                .createItronProgramAllFieldsWithItronCycleGear();

        Pair<JSONObject, JSONObject> programPair = pair.get("LoadProgram");
        JSONObject request = programPair.getValue0();

        String ldPrgmName = request.getString("name");

        createPage.getName().setInputValue(ldPrgmName);
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();
        createPage.getSaveBtn().click();

        assertThat(createPage.getName().getValidationError()).isEqualTo("Name must be unique.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Name_MaxlengthValidation() {
        String maxLength = createPage.getName().getMaxLength();

        assertThat(maxLength).isEqualTo("60");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Type_RequiredValidation() {
        createPage.getType().selectItemByIndex(0);

        createPage.getSaveBtn().click();

        assertThat(createPage.getType().getValidationError()).isEqualTo("Type is required.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Gear_RequiredValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();
        createPage.getSaveBtn().click();

        assertThat(createPage.getUserMessage()).contains("Program must contain 1 or more gears.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_LoadGroup_RequiredValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();
        createPage.getSaveBtn().click();

        assertThat(createPage.getUserMessage()).contains("At least 1 load group must be present in this current program.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_GeneralSection_TitleCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        Section optAttr = createPage.getPageSection("General");
        assertThat(optAttr.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_GeneralSection_LabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Operational State:", "Constraint:"));

        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_TriggerThresholdSettingsSection_TitleCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        Section optAttr = createPage.getPageSection("Trigger Threshold Settings");
        assertThat(optAttr.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_TriggerThresholdSettingsSection_LabelsCorrect() {
        String sectionName = "Trigger Threshold Settings";
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(List.of("Trigger Offset:", "Restore Offset:"));

        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_ControlWindow_TitleCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        Section optAttr = createPage.getPageSection("Control Window");
        assertThat(optAttr.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_ControlWindow_LabelsCorrect() {
        String sectionName = "Control Window";
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getUseWindowOne().selectValue("Yes");
        createPage.getUseWindowTwo().selectValue("Yes");

        List<String> expectedLabels = new ArrayList<>(List.of("Use Window 1:", "Start Time:", "Stop Time:", "Use Window 2:", "Start Time:", "Stop Time:"));

        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Type_ValuesCorrect() {
        List<String> expectedDropDownValues = new ArrayList<>(List.of("Select", "LM ecobee Program", "LM Honeywell Program", "LM Itron Program", "LM Direct Program", "LM SEP Program", "LM Meter Disconnect Program"));
        List<String> actualDropDownValues = createPage.getType().getOptionValues();

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_OperationalState_ValuesCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        List<String> expectedDropDownValues = new ArrayList<>(List.of("Automatic", "Manual Only", "Timed"));
        List<String> actualDropDownValues = createPage.getOperationalState().getOptionValues();

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_OperationalState_DefaultValueCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String expectedDropDownDefaultValue = "Automatic";
        String actualDropDownDefaultValue = createPage.getOperationalState().getSelectedValue();

        assertThat(actualDropDownDefaultValue).contains(expectedDropDownDefaultValue);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Constraint_DefaultValueCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();
        String expectedDropDownDefaultValue = "Default Constraint";
        String actualDropDownDefaultValue = createPage.getConstraint().getSelectedValue();

        assertThat(actualDropDownDefaultValue).contains(expectedDropDownDefaultValue);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_TriggerOffset_InvalidValueValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getTriggerOffset().setInputValue("test/");
        createPage.getSaveBtn().click();

        assertThat(createPage.getTriggerOffset().getValidationError()).isEqualTo("Not a valid value.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_TriggerOffset_MaxLengthValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getTriggerOffset().setInputValue("1001000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getTriggerOffset().getValidationError()).isEqualTo("Must be between 0 and 100,000.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_TriggerOffset_MinLengthValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getTriggerOffset().setInputValue("-100");
        createPage.getSaveBtn().click();

        assertThat(createPage.getTriggerOffset().getValidationError()).isEqualTo("Must be between 0 and 100,000.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_RestoreOffset_InvalidValueValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getRestoreOffset().setInputValue("test/");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRestoreOffset().getValidationError()).isEqualTo("Not a valid value.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_RestoreOffset_MaxLengthValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getRestoreOffset().setInputValue("1001000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRestoreOffset().getValidationError()).isEqualTo("Must be between -10,000 and 100,000.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_RestoreOffset_MinLengthValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getRestoreOffset().setInputValue("-111111100");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRestoreOffset().getValidationError()).isEqualTo("Must be between -10,000 and 100,000.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Tab_TitlesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        List<String> titles = createPage.getTabElement().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Load Groups");
        softly.assertThat(titles.get(1)).isEqualTo("Notification");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_LdGrpsTab_TitlesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String ldGrpsTabTitle = "Load Groups";

        createPage.getTabElement().clickTabAndWait(ldGrpsTabTitle);

        SelectBoxElement selectBoxElement = createPage.getLoadGroupsSelectBox();

        softly.assertThat(selectBoxElement.getColumnHeading("Available")).contains("Available");
        softly.assertThat(selectBoxElement.getColumnHeading("Assigned")).contains("Assigned");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_LdGrpsSearch_TitleCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String ldGrpsTabTitle = "Load Groups";

        createPage.getTabElement().clickTabAndWait(ldGrpsTabTitle);

        SelectBoxElement selectBoxElement = createPage.getLoadGroupsSelectBox();

        waitUntilTableVisiable(Optional.of(selectBoxElement.getColumnByColumnName("Available")));

        assertThat(selectBoxElement.getColumnSearchLabel("Available")).contains("Search:");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_LdGrpsSelectAll_LabelCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String ldGrpsTabTitle = "Load Groups";

        createPage.getTabElement().clickTabAndWait(ldGrpsTabTitle);

        SelectBoxElement selectBoxElement = createPage.getLoadGroupsSelectBox();

        waitUntilTableVisiable(Optional.of(selectBoxElement.getColumnByColumnName("Available")));

        assertThat(selectBoxElement.getColumnSelectAllLabel("Available")).contains("Select All");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_LdGrpsColumnHeaders_Correct() {
        SoftAssertions softly = new SoftAssertions();
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String ldGrpsTabTitle = "Load Groups";

        createPage.getTabElement().clickTabAndWait(ldGrpsTabTitle);

        SelectBoxElement selectBoxElement = createPage.getLoadGroupsSelectBox();

        waitUntilTableVisiable(Optional.of(selectBoxElement.getColumnByColumnName("Available")));

        softly.assertThat(selectBoxElement.getColumnTableHeaders("Available").size()).isEqualTo(2);
        softly.assertThat(selectBoxElement.getColumnTableHeaders("Available").get(0)).isEqualTo("Name");
        softly.assertThat(selectBoxElement.getColumnTableHeaders("Available").get(1)).isEqualTo("Type");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_LdGrpSearchByValidName_ReturnsCorrectResults() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String ldGrpsTabTitle = "Load Groups";

        createPage.getTabElement().clickTabAndWait(ldGrpsTabTitle);

        SelectBoxElement selectBoxElement = createPage.getLoadGroupsSelectBox();

        waitUntilTableVisiable(Optional.of(selectBoxElement.getColumnByColumnName("Available")));

        assertThat(selectBoxElement.searchByValidName(ldGrpName)).contains(ldGrpName);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_LdGrpsSearchByInvalidName_ReturnsNoResults() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String ldGrpsTabTitle = "Load Groups";

        createPage.getTabElement().clickTabAndWait(ldGrpsTabTitle);

        SelectBoxElement selectBoxElement = createPage.getLoadGroupsSelectBox();

        waitUntilTableVisiable(Optional.of(selectBoxElement.getColumnByColumnName("Available")));

        assertThat(selectBoxElement.searchByInvalidName(ldGrpName + "TestWrongName")).contains("No results found");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_ControlWindowSection_LabelsCorrect() {
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Itron Program " + timeStamp;
        String sectionName = "Control Window";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(List.of("Use Window 1:", "", "", "Use Window 2:", "", ""));

        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_OperationalStateTimed_UseWindowRequiredValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String EXPECTED_MSG = "A timed program requires a non zero control time to be specified under the control window tab one.";

        createPage.getOperationalState().selectItemByValue("Timed");
        createPage.getSaveBtn().click();

        String userMsg = createPage.getUserMessage();

        assertThat(userMsg).contains(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_WindowOneStartTime_RequiredValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String EXPECTED_MSG = "Start Time is required.";

        createPage.getUseWindowOne().selectValue("Yes");
        createPage.getStartTimeWindowOne().clearValue();
        createPage.getSaveBtn().click();

        String userMsg = createPage.getStartTimeWindowOne().getValidationError();

        assertThat(userMsg).contains(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_WindowOneStopTime_RequiredValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String EXPECTED_MSG = "Stop Time is required.";

        createPage.getUseWindowOne().selectValue("Yes");
        createPage.getStopTimeWindowOne().clearValue();
        createPage.getSaveBtn().click();

        String userMsg = createPage.getStopTimeWindowOne().getValidationError();

        assertThat(userMsg).contains(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_WindowTwoStartTime_RequiredValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String EXPECTED_MSG = "Start Time is required.";
        createPage.getUseWindowTwo().selectValue("Yes");
        createPage.getStartTimeWindowTwo().clearValue();
        createPage.getSaveBtn().click();

        String userMsg = createPage.getStartTimeWindowTwo().getValidationError();

        assertThat(userMsg).contains(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_WindowTwoStopTime_RequiredValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String EXPECTED_MSG = "Stop Time is required.";
        createPage.getUseWindowTwo().selectValue("Yes");
        createPage.getStopTimeWindowTwo().clearValue();
        createPage.getSaveBtn().click();

        String userMsg = createPage.getStopTimeWindowTwo().getValidationError();

        assertThat(userMsg).contains(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_Cancel_NavigatesToCorrectUrl() {
        createPage.getCancelBtn().click();
        String EXPECTED_TITLE = "Setup";
        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_NotificationField_LabelsCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String sectionName = "Notification";
        List<String> expectedLabels = new ArrayList<>(List.of("Program Start ", "Program Stop ", "Notify On Adjustment", "Notify On Schedule"));

        createPage.getTabElement().clickTabAndWait(sectionName);

        SelectBoxElement selectBoxElement = createPage.getLoadGroupsSelectBox();
        List<String> actualLabels = selectBoxElement.getSectionLabelsNotification();

        assertThat(actualLabels).containsAll(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_NotificationPrgmStart_InvalidValueValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();
        String EXPECTED_MSG = "Must be a valid integer value.";
        String sectionName = "Notification";

        createPage.getTabElement().clickTabAndWait(sectionName);

        createPage.getProgramStart().setValue(true);
        createPage.getProgramStartMinutes().setInputValue("abcd");

        createPage.getSaveBtn().click();

        createPage.getTabElement().clickTabAndWait(sectionName);
        createPage.getProgramStart().setValue(true);

        String userMsg = createPage.getProgramStartMinutes().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_NotificationPrgmStop_InvalidValueValidation() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();
        String EXPECTED_MSG = "Must be a valid integer value.";
        String sectionName = "Notification";

        createPage.getTabElement().clickTabAndWait(sectionName);

        createPage.getProgramStop().setValue(true);
        createPage.getProgramStopMinutes().setInputValue("abcd");

        createPage.getSaveBtn().click();

        createPage.getTabElement().clickTabAndWait(sectionName);
        createPage.getProgramStop().setValue(true);

        String userMsg = createPage.getProgramStopMinutes().getValidationError();

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_NotificationPrgmStart_MaxLength5Chars() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String sectionName = "Notification";

        createPage.getTabElement().clickTabAndWait(sectionName);
        createPage.getProgramStart().setValue(true);
        String maxLength = createPage.getProgramStartMinutes().getMaxLength();

        assertThat(maxLength).isEqualTo("5");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmCreate_NotificationPrgmStop_MaxLength5Chars() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        String sectionName = "Notification";

        createPage.getTabElement().clickTabAndWait(sectionName);
        createPage.getProgramStop().setValue(true);
        String maxLength = createPage.getProgramStopMinutes().getMaxLength();

        assertThat(maxLength).isEqualTo("5");
    }

}