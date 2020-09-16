package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadProgramModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;

public class LoadProgramDetailsTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void loadPrgoramDetails_PageTitle_Correct() {
        final String EXPECTED_TITLE = "Load Program: AT Load Program";

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "665");

        LoadProgramDetailPage detailPage = new LoadProgramDetailPage(driverExt, 665);

        String actualPageTitle = detailPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void loadPrgoramDetails_Copy_Success() {

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "604");

        LoadProgramDetailPage detailPage = new LoadProgramDetailPage(driverExt, 604);

        CopyLoadProgramModal modal = detailPage.showCopyLoadProgramModal();

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Copied Program " + timeStamp;
        final String EXPECTED_MSG = name + " copied successfully.";

        modal.getName().setInputValue(name);

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Program: " + name, Optional.of(8));

        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt, 604);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(enabled = true, groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void loadPrgoramDetails_Delete_Success() {
        final String EXPECTED_MSG = "AT Delete Direct Program deleted successfully.";

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + "605");

        LoadProgramDetailPage detailPage = new LoadProgramDetailPage(driverExt, 605);

        ConfirmModal modal = detailPage.showDeleteLoadProgramModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());

        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_PROGRAM);

        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
