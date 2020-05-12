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
import com.eaton.pages.capcontrol.CapBankDetailPage;
import com.eaton.pages.capcontrol.CapBankEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class CapBankEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit CapBank: AT Cap Bank";
        
        navigate(Urls.CapControl.CAP_BANK_EDIT + "669" + Urls.EDIT);

        CapBankEditPage editPage = new CapBankEditPage(driverExt, 669);

        String actualPageTitle = editPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_04_EditCCObjects"})
    public void editCapBankUpdateNameOnlySuccess() {
        final String EXPECTED_MSG = "CapBank was saved successfully.";
        
        navigate(Urls.CapControl.CAP_BANK_EDIT + "459" + Urls.EDIT);

        CapBankEditPage editPage = new CapBankEditPage(driverExt, 459);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Edited CapBank " + timeStamp;
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("CapBank: " + name, Optional.empty());
        
        CapBankDetailPage detailsPage = new CapBankDetailPage(driverExt, 459);
        
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }    
    
    @Test(enabled = true, groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_05_DeleteCCOjects"})
    public void deleteCapBankSuccess() {
        final String EXPECTED_MSG = "CapBank AT Delete CapBank deleted successfully.";
        
        navigate(Urls.CapControl.CAP_BANK_EDIT + "576" + Urls.EDIT);

        CapBankEditPage editPage = new CapBankEditPage(driverExt, 576);
        
        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();
        
        waitForPageToLoad("Orphans", Optional.empty());
        
        OrphansPage detailsPage = new OrphansPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
