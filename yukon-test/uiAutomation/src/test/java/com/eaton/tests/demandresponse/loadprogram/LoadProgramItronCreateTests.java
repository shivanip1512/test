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

import com.eaton.builders.drsetup.loadgroup.LoadGroupCreateService;
import com.eaton.elements.modals.gears.CreateItronPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;
import com.github.javafaker.Faker;

public class LoadProgramItronCreateTests extends SeleniumTestSetup {
    private Faker faker = new Faker();

    private static final String TYPE = "LM_ITRON_PROGRAM";

    private DriverExtensions driverExt;
    private String ldGrpName;
    private LoadProgramCreatePage createPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
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
    public void ldPrgmItronCreate_RequiredFieldsOnly_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Itron Required " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        CreateItronPrgmGearModal modal = createPage.showCreateItronPrgmGearModal(Optional.empty());

        modal.getGearName().setInputValue("IC " + timeStamp);
        modal.getGearType().selectItemByValue("ItronCycle");
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

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmItronCreate_AllFields_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Itron All Fields " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getOperationalState().selectItemByValue("ManualOnly");

        createPage.getTriggerOffset().setInputValue(String.valueOf(faker.number().numberBetween(0, 100000)));
        createPage.getRestoreOffset().setInputValue(String.valueOf(faker.number().numberBetween(-10000, 100000)));

        CreateItronPrgmGearModal modal = createPage.showCreateItronPrgmGearModal(Optional.empty());

        modal.getGearName().setInputValue("IC " + timeStamp);
        modal.getGearType().selectItemByValue("ItronCycle");
        waitForLoadingSpinner();
        modal.clickOkAndWaitForModalCloseDisplayNone();

        createPage.getUseWindowOne().selectValue("Yes");
        createPage.getStartTimeWindowOne().setValue("01:32");
        createPage.getStopTimeWindowOne().setValue("16:54");
        createPage.getUseWindowTwo().selectValue("Yes");
        createPage.getStartTimeWindowTwo().setValue("11:54");
        createPage.getStopTimeWindowTwo().setValue("23:59");

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
    public void ldPrgmItronCreate_GearType_ValuesCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        CreateItronPrgmGearModal modal = createPage.showCreateItronPrgmGearModal(Optional.empty());

        List<String> actualDropDownValues = modal.getGearType().getOptionValues();

        List<String> expectedDropDownValues = new ArrayList<>(List.of("Select", "Itron Cycle"));

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmItronCreate_WithMultipleGears_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Itron Multi Gears " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        // Adding 2 gears
        for (int i = 1; i <= 2; i++) {
            CreateItronPrgmGearModal modal = createPage.showCreateItronPrgmGearModal(Optional.of(i));
            waitForLoadingSpinner();
            modal.getGearName().setInputValue("IC Gear " + i);
            modal.getGearType().selectItemByValue("ItronCycle");
            waitForLoadingSpinner();
            modal.clickOkAndWaitForModalCloseDisplayNone();
        }

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
    public void ldPrgmItronCreate_ItronCycleGearControlParamSection_LabelsCorrect() {
        String sectionName = "Control Parameters";

        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        CreateItronPrgmGearModal modal = createPage.showCreateItronPrgmGearModal(Optional.empty());

        modal.getGearType().selectItemByValue("ItronCycle");
        waitForLoadingSpinner();

        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(
                List.of("Duty Cycle Type:", "Duty Cycle:", "Duty Cycle Period:", "Criticality:", "How To Stop Control:"));

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmItronCreate_ItronCycleGearRampInRampOutSection_LabelsCorrect() {
        String sectionName = "Ramp In / Ramp Out";

        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        CreateItronPrgmGearModal modal = createPage.showCreateItronPrgmGearModal(Optional.empty());

        modal.getGearType().selectItemByValue("ItronCycle");
        waitForLoadingSpinner();

        List<String> actualLabels = modal.getPageSection(sectionName).getSectionLabels();
        List<String> expectedLabels = new ArrayList<>(List.of("Ramp In:", "Ramp Out:"));

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
}
