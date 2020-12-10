package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.gears.CreateDirectPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramCreatePage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;

public class LoadProgramCreateTests extends SeleniumTestSetup {

    private LoadProgramCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
        createPage = new LoadProgramCreatePage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void loadProgramCreate_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Load Program";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void loadProgramCreate_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT LM Direct Program " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_DIRECT_PROGRAM");
        waitForLoadingSpinner();

        CreateDirectPrgmGearModal modal = createPage.showCreateDirectPrgmGearsModal(Optional.empty());

        modal.getGearName().setInputValue("TC " + timeStamp);
        modal.getGearType().selectItemByValue("TrueCycle");
        waitForLoadingSpinner();
        modal.clickOkAndWaitForModalCloseDisplayNone();

        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable("AT RFN Expresscom Ldgrp for Create Ldprgm");

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Program: " + name, Optional.empty());

        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
