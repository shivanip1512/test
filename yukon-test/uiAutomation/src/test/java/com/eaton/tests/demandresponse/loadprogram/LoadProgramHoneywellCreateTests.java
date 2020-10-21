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
        setRefreshPage(false);
        
        Pair<JSONObject, JSONObject> pair = new LoadGroupHoneywellCreateBuilder.Builder(Optional.empty())
                .withKwCapacity(Optional.empty())
                .create();
        
        JSONObject response = pair.getValue1();
        
        ldGrpName = response.getString("name");
        
        createPage = new LoadProgramCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Honeywell Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();

        modal.getGearName().setInputValue("TC " + timeStamp);
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
        setRefreshPage(true);
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Honeywell Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();

        modal.getGearName().setInputValue("TC " + timeStamp);
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
        setRefreshPage(true);
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Honeywell Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();

        modal.getGearName().setInputValue("TC " + timeStamp);
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
        setRefreshPage(true);
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);

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
        setRefreshPage(true);
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        modal.getGearType().selectItemByValue("HoneywellSetpoint");
        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(List.of("Mandatory:", "Mode:", "Setpoint Offset:", "How To Stop Control:"));
        
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldPrgmHoneywellCreate_CycleGearControlParamSection_LabelsCorrect() {
    	String sectionName = "Control Parameters";
        setRefreshPage(true);
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);

        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
        waitForLoadingSpinner();

        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
        modal.getGearType().selectItemByValue("HoneywellCycle");
        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(List.of("Mandatory:", "Control Percent:", "Cycle Period:", "How To Stop Control:"));
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
    
//    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
//    public void ldPrgmHoneywellCreate_SetpointGearSetpointOffset_RequiredValidation() {
//        setRefreshPage(true);
//        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
//
//        createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
//        waitForLoadingSpinner();
//
//        CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
//        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
//        modal.getGearName().setInputValue("TC " + timeStamp);
//        modal.getGearType().selectItemByValue("HoneywellSetpoint");
//        waitForLoadingSpinner();
//        modal.getSetpointOffset().clearInputValue();
//        modal.clickOkAndWaitForModalCloseDisplayNone();
//
//        createPage.getSaveBtn().click();
//        modal = createPage.showCreateHoneywellPrgmGearModal();
//        assertThat(modal.getSetpointOffset().getValidationError()).isEqualTo("Setpoint Offset is required.");
//    }

//    
//    ldPrgmHoneywellCreate_CycleGearControlPercent_RequiredValidation
//    ldPrgmHoneywellCreate_CycleGearControlPercent_MaxRangeValidation
//    ldPrgmHoneywellCreate_CycleGearControlPercent_MinRangeValidation
//    ldPrgmHoneywellCreate_InvalidGearNameErrorMessage_Displayed
//    ldPrgmHoneywellCreate_CycleGearRampInRampOutSection_TitleCorrect
//    ldPrgmHoneywellCreate_CycleGearRampInRampOutSection_LabelsCorrect
}
