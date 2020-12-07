package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupDigiSepCreateBuilder;
import com.eaton.elements.modals.gears.CreateSepPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;

public class LoadProgramSEPCreateTests extends SeleniumTestSetup {

    private LoadProgramCreatePage createPage;
    private DriverExtensions driverExt;
    private String ldGrpName;
    private String timeStamp;
    private static final String TYPE = "LM_SEP_PROGRAM";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        // Create Load Group to be assigned
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        ldGrpName = "SepLdGroup " + timeStamp;
        LoadGroupDigiSepCreateBuilder.buildLoadGroup().withName(ldGrpName)
                .create();

        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
        createPage = new LoadProgramCreatePage(driverExt);
        
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmSepCreate_RequiredFieldsOnly_Success() {
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT SepProgram " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByValue(TYPE);
        SeleniumTestSetup.waitForLoadingSpinner();
        createPage.getName().setInputValue(name);
        CreateSepPrgmGearModal gearModal = createPage.showCreateSepPrgmGearModal(Optional.empty());
        gearModal.getGearName().setInputValue("sepGear");
        gearModal.getGearType().selectItemByValue("SepCycle");
        waitForLoadingSpinner();
        gearModal.clickOkAndWaitForModalCloseDisplayNone();
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
    public void ldPrgmSepCreate_AllFields_Success() {
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT SepProgram " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByValue(TYPE);
        SeleniumTestSetup.waitForLoadingSpinner();
        
        createPage.getName().setInputValue(name);
        CreateSepPrgmGearModal gearModal = createPage.showCreateSepPrgmGearModal(Optional.empty());
        gearModal.getGearName().setInputValue("sepGear");
        gearModal.getGearType().selectItemByValue("SepCycle");
        waitForLoadingSpinner();
        gearModal.clickOkAndWaitForModalCloseDisplayNone();
        createPage.getTriggerOffset().setInputValue("22");
        createPage.getRestoreOffset().setInputValue("25");
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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmSepCreate_GearType_ValuesCorrect() {
        List<String> expectedGearTypes = new ArrayList<>(List.of("Select", "SEP Cycle", "SEP Temperature Offset", "No Control"));

        createPage.getType().selectItemByValue(TYPE);
        SeleniumTestSetup.waitForLoadingSpinner();
        CreateSepPrgmGearModal gearModal = createPage.showCreateSepPrgmGearModal(Optional.empty());
        List<String> gearTypes = gearModal.getGearType().getOptionValues();
        assertThat(gearTypes).containsAll(expectedGearTypes);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmSepCreate_WithMultipleGears_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT SepProgram " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByValue(TYPE);
        SeleniumTestSetup.waitForLoadingSpinner();
        createPage.getName().setInputValue(name);
        // Adding 2 gears
        for (int i = 1; i <= 2; i++) {
            CreateSepPrgmGearModal modal = createPage.showCreateSepPrgmGearModal(Optional.of(i));
            waitForLoadingSpinner();
            modal.getGearName().setInputValue("SEP Gear " + i);
            modal.getGearType().selectItemByValue("SepCycle");
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
