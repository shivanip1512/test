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

import com.eaton.builders.drsetup.loadgroup.LoadGroupEmetconCreateBuilder;
import com.eaton.elements.modals.gears.CreateDirectPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;
import com.github.javafaker.Faker;

public class LoadProgramDirectCreateTests extends SeleniumTestSetup{
    private Faker faker = new Faker();

    private static final String TYPE = "LM_DIRECT_PROGRAM";

    private DriverExtensions driverExt;
    private String ldGrpName;
    private LoadProgramCreatePage createPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        ldGrpName = "Before Class " + timeStamp;

        Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
                .create();

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
    public void ldPrgmDirectCreate_RequiredFieldsOnly_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Direct Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        CreateDirectPrgmGearModal modal = createPage.showCreateDirectPrgmGearsModal(Optional.empty());

        modal.getGearName().setInputValue("DirectGear " + timeStamp);
        modal.getGearType().selectItemByValue("TimeRefresh");
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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmDirectCreate_AllFields_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Direct Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        createPage.getOperationalState().selectItemByValue("ManualOnly");

        createPage.getTriggerOffset().setInputValue(String.valueOf(faker.number().numberBetween(0, 100000)));
        createPage.getRestoreOffset().setInputValue(String.valueOf(faker.number().numberBetween(-10000, 100000)));

        CreateDirectPrgmGearModal modal = createPage.showCreateDirectPrgmGearsModal(Optional.empty());

        modal.getGearName().setInputValue("DirectGear " + timeStamp);
        modal.getGearType().selectItemByValue("SmartCycle");
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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmDirectCreate_GearType_ValuesCorrect() {
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        CreateDirectPrgmGearModal modal = createPage.showCreateDirectPrgmGearsModal(Optional.empty());

        List<String> actualDropDownValues = modal.getGearType().getOptionValues();

        List<String> expectedDropDownValues = new ArrayList<>(List.of("Select", "Time Refresh", "Smart Cycle", "Master Cycle",
                "Rotation", "Latching", "True Cycle", "Magnitude Cycle", "Target Cycle", "Thermostat Ramping",
                "Simple Thermostat Ramping", "Beat The Peak", "No Control"));

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmDirectCreate_WithMultipleGears_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Direct Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue(TYPE);
        waitForLoadingSpinner();

        // Adding 2 gears
        for (int i = 1; i <= 2; i++) {
            CreateDirectPrgmGearModal modal = createPage.showCreateDirectPrgmGearsModal(Optional.of(i));
            waitForLoadingSpinner();
            modal.getGearName().setInputValue("Direct Gear " + i);
            modal.getGearType().selectItemByValue("TimeRefresh");
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
}
