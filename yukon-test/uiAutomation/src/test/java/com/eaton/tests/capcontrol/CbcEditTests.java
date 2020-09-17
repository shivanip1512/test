package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcDetailPage;
import com.eaton.pages.capcontrol.CbcEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class CbcEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private CbcEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        
        navigate(Urls.CapControl.CBC_EDIT + "670" + Urls.EDIT);

        editPage = new CbcEditPage(driverExt, 670);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(editPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void cbcEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit CBC: AT CBC";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void cbcEdit_RequiredFieldsOnly_Success() {
        final String EXPECTED_MSG = "CBC was successfully saved.";

        navigate(Urls.CapControl.CBC_EDIT + "563" + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited CBC " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name, Optional.empty());

        CbcDetailPage detailPage = new CbcDetailPage(driverExt, 563);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void cbcEdit_Delete_Success() {
        final String EXPECTED_MSG = "Deleted CBC";

        navigate(Urls.CapControl.CBC_EDIT + "577" + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
