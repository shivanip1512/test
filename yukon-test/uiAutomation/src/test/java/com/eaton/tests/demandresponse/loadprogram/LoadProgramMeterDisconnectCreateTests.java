package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupMeterDisconnectCreateBuilder;
import com.eaton.elements.modals.gears.CreateMeterDisconnectPrgmModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;
import com.github.javafaker.Faker;

public class LoadProgramMeterDisconnectCreateTests extends SeleniumTestSetup {

    private LoadProgramCreatePage createPage;
    private DriverExtensions driverExt;
    private Faker faker;
    String ldGrpName;
    String timeStamp;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
        setRefreshPage(false);
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        ldGrpName = "MeterLoadGroup" + timeStamp;

        new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.of(ldGrpName)).create();
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
        createPage = new LoadProgramCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmMeterDisconnectCreate_RequiredFields_Success() {
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String ldPrgmName = "Meter Disconnect LoadProgram" + timeStamp;
        String gearName = "Meter Disconnect Gear";

        final String EXPECTED_MSG = ldPrgmName + " saved successfully.";

        createPage.getType().selectItemByValue("LM_METER_DISCONNECT_PROGRAM");
        waitForLoadingSpinner();
        createPage.getName().setInputValue(ldPrgmName);

        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

        CreateMeterDisconnectPrgmModal createGearModal = createPage.showCreateMeterDiconnectPrgmModal(Optional.empty());
        createGearModal.getGearName().setInputValue(gearName);
        createGearModal.getGearType().selectItemByValue("MeterDisconnect");
        waitForLoadingSpinner();
        createGearModal.clickOkAndWaitForSpinner();

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Program: " + ldPrgmName, Optional.empty());

        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);

    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmMeterDisconnectCreate_AllFields_Success() {
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String ldPrgmName = "Meter Disconnect LoadProgram" + timeStamp;
        String gearName = "Meter Disconnect Gear";

        final String EXPECTED_MSG = ldPrgmName + " saved successfully.";

        Integer triggerOffset = faker.number().numberBetween(0, 99999);
        Integer restoreOffset = faker.number().numberBetween(0, 99999);

        createPage.getType().selectItemByValue("LM_METER_DISCONNECT_PROGRAM");
        waitForLoadingSpinner();
        createPage.getName().setInputValue(ldPrgmName);

        createPage.getTriggerOffset().setInputValue(String.valueOf(triggerOffset));
        createPage.getRestoreOffset().setInputValue(String.valueOf(restoreOffset));

        createPage.getUseWindowOne().selectValue("Yes");
        createPage.getStartTimeWindowOne().setValue("12:34");
        createPage.getStopTimeWindowOne().setValue("20:45");

        createPage.getUseWindowTwo().selectValue("Yes");
        createPage.getStartTimeWindowTwo().setValue("09:12");
        createPage.getStopTimeWindowTwo().setValue("22:34");

        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

        CreateMeterDisconnectPrgmModal createGearModal = createPage.showCreateMeterDiconnectPrgmModal(Optional.empty());
        createGearModal.getGearName().setInputValue(gearName);
        createGearModal.getGearType().selectItemByValue("MeterDisconnect");
        waitForLoadingSpinner();
        createGearModal.clickOkAndWaitForSpinner();

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Program: " + ldPrgmName, Optional.empty());

        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPgmMeterDisconnectCreate_GearType_ValuesCorrect() {
        List<String> expectedGearsList = new ArrayList<>(List.of("Select", "Meter Disconnect"));

        createPage.getType().selectItemByValue("LM_METER_DISCONNECT_PROGRAM");
        waitForLoadingSpinner();

        CreateMeterDisconnectPrgmModal createGearModal = createPage.showCreateMeterDiconnectPrgmModal(Optional.empty());
        waitForLoadingSpinner();

        List<String> actualGearsList = createGearModal.getGearType().getOptionValues();

        assertThat(actualGearsList).containsExactlyElementsOf(expectedGearsList);
    }
}
