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
import com.eaton.pages.demandresponse.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;

public class LoadProgramSEPCreateTests extends SeleniumTestSetup {

    private LoadProgramCreatePage createPage;
    private DriverExtensions driverExt;
    private String ldGrpName;
    String timeStamp;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        // Create Load Group to be assigned
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        ldGrpName = "SepLdGroup " + timeStamp;
        LoadGroupDigiSepCreateBuilder.buildLoadGroup().withName(ldGrpName)
                .create();

        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
        createPage = new LoadProgramCreatePage(driverExt);
        createPage.getType().selectItemByValue("LM_SEP_PROGRAM");
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if (getRefreshPage()) {
            refreshPage(createPage);
            createPage.getType().selectItemByValue("LM_SEP_PROGRAM");
        }
        setRefreshPage(false);

    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldPrgmSepCreate_RequiredFieldsOnly_Success() {
        String name = "AT SepProgram " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";
        setRefreshPage(true);

        createPage.getName().setInputValue(name);
        CreateSepPrgmGearModal gearModal = createPage.showCreateSepPrgmGearModal();
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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldPrgmSepCreate_AllFields_Success() {
        String name = "AT SepProgram2 " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";
        setRefreshPage(true);

        createPage.getName().setInputValue(name);
        CreateSepPrgmGearModal gearModal = createPage.showCreateSepPrgmGearModal();
        gearModal.getGearName().setInputValue("sepGear");
        gearModal.getGearType().selectItemByValue("SepCycle");
        waitForLoadingSpinner();
        gearModal.clickOkAndWaitForModalCloseDisplayNone();
        createPage.getTriggerOffset().setInputValue("22");
        createPage.getRestoreOffset().setInputValue("25");
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
    public void ldPrgmSepCreate_GearType_ValuesCorrect() {
        setRefreshPage(true);
        List<String> expectedGearTypes = new ArrayList<String>();
        expectedGearTypes.add("SEP Cycle");
        expectedGearTypes.add("SEP Temperature Offset");
        expectedGearTypes.add("No Control");

        CreateSepPrgmGearModal gearModal = createPage.showCreateSepPrgmGearModal();
        List<String> gearTypes = gearModal.getGearType().getOptionValues();
        assertThat(gearTypes).containsAll(expectedGearTypes);
    }
}
