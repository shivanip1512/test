package com.eaton.tests.demandresponse.loadprogram;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;
import com.eaton.elements.modals.gears.CreateEcobeePrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;

public class LoadProgramEcobeeCreateTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private LoadProgramCreatePage loadProgramCreatePage;
    private String loadGroupName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
        loadProgramCreatePage = new LoadProgramCreatePage(driverExt);
        // Create Load Group 'ECOBEE GROUP' for LoadProgram creation , we will use it while creating Load program in this class
        loadGroupName = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty()).create().getValue1().getString("name");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(loadProgramCreatePage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmEcobeeCreate_RequiredFieldsOnly_Success() {
        // generate dynamic name for Ecobee Program name
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String loadProgramName = "AT LM Ecobee Program " + timeStamp;
        // Defined expected message on load program save
        final String EXPECTED_MSG = loadProgramName + " saved successfully.";
        // Enter load program name and select its type and wait for section to load
        loadProgramCreatePage.getType().selectItemByValue("LM_ECOBEE_PROGRAM");
        loadProgramCreatePage.getName().setInputValue(loadProgramName);
        waitForLoadingSpinner();
        // Click 'Create' and create a gear
        CreateEcobeePrgmGearModal createEcobeePrgmGearModal = loadProgramCreatePage.showCreateEcobeePrgmGearModal();
        createEcobeePrgmGearModal.getGearName().setInputValue("GearEcobeeCycle " + timeStamp);
        createEcobeePrgmGearModal.getGearType().selectItemByValue("EcobeeCycle");
        waitForLoadingSpinner();
        createEcobeePrgmGearModal.clickOkAndWaitForModalCloseDisplayNone();
        // Assign the load group
        LoadGroupsTab groupsTab = loadProgramCreatePage.getLoadGroupTab();
        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(loadGroupName);
        loadProgramCreatePage.getSaveBtn().click();
        waitForPageToLoad("Load Program: " + loadProgramName, Optional.empty());
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        // Validate Success message
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmEcobeeCreate_AllFields_Success() {
        // generate dynamic name for Ecobee Program name
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String loadProgramName = "AT LM Ecobee Program " + timeStamp;

        final String EXPECTED_MSG = loadProgramName + " saved successfully.";
        // Enter load program name and select type
        loadProgramCreatePage.getName().setInputValue(loadProgramName);
        loadProgramCreatePage.getType().selectItemByValue("LM_ECOBEE_PROGRAM");
        waitForLoadingSpinner();
        // Add Gear
        CreateEcobeePrgmGearModal createEcobeePrgmGearModal = loadProgramCreatePage.showCreateEcobeePrgmGearModal();
        createEcobeePrgmGearModal.getGearName().setInputValue("GearEcobeeCycle " + timeStamp);
        createEcobeePrgmGearModal.getGearType().selectItemByValue("EcobeeSetpoint");
        waitForLoadingSpinner();

        // Set Control Parameters Mandatory : Yes
        createEcobeePrgmGearModal.getMandatory().selectValue("Yes");
        // Control Parameters Mode: Heat
        createEcobeePrgmGearModal.getMode().selectByValue("Heat");
        // Save the Gear
        createEcobeePrgmGearModal.clickOkAndWaitForModalCloseDisplayNone();

        // Select Operational State: Automatic
        loadProgramCreatePage.getOperationalState().selectItemByValue("Automatic");
        // Select Constraint :Default Constraint
        loadProgramCreatePage.getConstraint().selectItemByIndex(0);
        // Set Trigger Threshold Settings | Trigger Offset: & Restore Offset:
        loadProgramCreatePage.getTriggerOffset().setInputValue("58");
        loadProgramCreatePage.getRestoreOffset().setInputValue("1111");

        // Set Control Window | Use Window 1:
        loadProgramCreatePage.getUseWindowOne().selectValue("Yes");
        loadProgramCreatePage.getStartTimeWindowOne().setValue("12:57");
        loadProgramCreatePage.getStopTimeWindowOne().setValue("20:59");

        // Add load group
        LoadGroupsTab groupsTab = loadProgramCreatePage.getLoadGroupTab();
        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(loadGroupName);

        // Add the Notification , this part is Pending as we get to add the notification in the database first
        groupsTab.clickTabAndWait("Notification");

        // Save load program
        loadProgramCreatePage.getSaveBtn().click();
        waitForPageToLoad("Load Program: " + loadProgramName, Optional.empty());

        // Validate success message
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmEcobeeCreate_GearType_ValuesCorrect() {

        loadProgramCreatePage.getType().selectItemByValue("LM_ECOBEE_PROGRAM");
        waitForLoadingSpinner();
        CreateEcobeePrgmGearModal createEcobeePrgmGearModal = loadProgramCreatePage.showCreateEcobeePrgmGearModal();
        waitForLoadingSpinner();
        List<String> actualDropDownValues = createEcobeePrgmGearModal.getGearType().getOptionValues();

        List<String> expectedDropDownValues = new ArrayList<>(List.of("Select", "ecobee Cycle", "ecobee Setpoint"));
        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmEcobeeCreate_WithMultipleGears_Success() {
        setRefreshPage(true);
        // generate dynamic name for Ecobee Program name
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String loadProgramName = "AT LM Ecobee Program " + timeStamp;
        // Defined expected message on load program save
        final String EXPECTED_MSG = loadProgramName + " saved successfully.";
        // Enter load program name and select its type and wait for section to load
        loadProgramCreatePage.getType().selectItemByValue("LM_ECOBEE_PROGRAM");
        loadProgramCreatePage.getName().setInputValue(loadProgramName);
        waitForLoadingSpinner();
        // Add gear 1
        CreateEcobeePrgmGearModal createEcobeePrgmGearModal = loadProgramCreatePage.showCreateEcobeePrgmGearModal();
        createEcobeePrgmGearModal.getGearName().setInputValue("GearEcobeeCycle " + timeStamp);
        createEcobeePrgmGearModal.getGearType().selectItemByValue("EcobeeCycle");
        waitForLoadingSpinner();
        createEcobeePrgmGearModal.clickOkAndWaitForModalCloseDisplayNone();
        // Add gear 2
        createEcobeePrgmGearModal = loadProgramCreatePage.showCreateEcobeePrgmGearModal();
        createEcobeePrgmGearModal.getGearName().setInputValue("GearEcobeeCycle " + timeStamp);
        createEcobeePrgmGearModal.getGearType().selectItemByValue("EcobeeSetpoint");
        waitForLoadingSpinner();
        createEcobeePrgmGearModal.clickOkAndWaitForModalCloseDisplayNone();

        // Assign the load group
        LoadGroupsTab groupsTab = loadProgramCreatePage.getLoadGroupTab();
        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(loadGroupName);
        loadProgramCreatePage.getSaveBtn().click();
        waitForPageToLoad("Load Program: " + loadProgramName, Optional.empty());
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        // Validate Success message
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPgmEcobeeCreate_SetpointGearControlParamSection_LabelsCorrect() {
        String sectionName = "Control Parameters";
        loadProgramCreatePage.getType().selectItemByValue("LM_ECOBEE_PROGRAM");
        waitForLoadingSpinner();
        CreateEcobeePrgmGearModal createEcobeePrgmGearModal = loadProgramCreatePage.showCreateEcobeePrgmGearModal();
        createEcobeePrgmGearModal.getGearType().selectItemByValue("EcobeeSetpoint");
        waitForLoadingSpinner();
        List<String> actualLabels = createEcobeePrgmGearModal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(List.of("Mandatory:", "Setpoint Offset:", "Mode:", "How To Stop Control:"));
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPgmEcobeeCreate_CycleGearControlParamSection_LabelsCorrect() {
        String sectionName = "Control Parameters";
        loadProgramCreatePage.getType().selectItemByValue("LM_ECOBEE_PROGRAM");
        waitForLoadingSpinner();
        CreateEcobeePrgmGearModal createEcobeePrgmGearModal = loadProgramCreatePage.showCreateEcobeePrgmGearModal();
        createEcobeePrgmGearModal.getGearType().selectItemByValue("EcobeeCycle");
        waitForLoadingSpinner();
        List<String> actualLabels = createEcobeePrgmGearModal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(List.of("Mandatory:", "Control Percent:", "How To Stop Control:"));
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPgmEcobeeCreate_CycleGearRampInRampOutSection_LabelsCorrect() {
        String sectionName = "Ramp In / Ramp Out";
        loadProgramCreatePage.getType().selectItemByValue("LM_ECOBEE_PROGRAM");
        waitForLoadingSpinner();
        CreateEcobeePrgmGearModal createEcobeePrgmGearModal = loadProgramCreatePage.showCreateEcobeePrgmGearModal();
        createEcobeePrgmGearModal.getGearType().selectItemByValue("EcobeeCycle");
        waitForLoadingSpinner();
        List<String> actualLabels = createEcobeePrgmGearModal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(List.of("Ramp In:", "Ramp Out:"));
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
}
