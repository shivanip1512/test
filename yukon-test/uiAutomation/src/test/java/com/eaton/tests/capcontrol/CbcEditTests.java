package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

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

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.VoltVar.VOLT_VAR })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit CBC: AT CBC";
        
        navigate(Urls.CapControl.CBC_EDIT + "670" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, 670);

        String actualPageTitle = editPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.VoltVar.VOLT_VAR })
    public void editCbcRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "CBC was successfully saved.";
        
        navigate(Urls.CapControl.CBC_EDIT + "563" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, 563);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited CBC " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name, Optional.empty());

        CbcDetailPage detailPage = new CbcDetailPage(driverExt, 563);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.VoltVar.VOLT_VAR })
    public void deleteCbcSuccess() {
        final String EXPECTED_MSG = "Deleted CBC";
        
        navigate(Urls.CapControl.CBC_EDIT + "577" + Urls.EDIT);

        CbcEditPage editPage = new CbcEditPage(driverExt, 577);
        
        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
