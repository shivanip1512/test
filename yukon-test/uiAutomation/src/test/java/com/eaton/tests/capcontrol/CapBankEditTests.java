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
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapBankDetailPage;
import com.eaton.pages.capcontrol.CapBankEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class CapBankEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private CapBankEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        String capBankId = TestDbDataType.VoltVarData.CAPBANK_ID.getId().toString();
        
        navigate(Urls.CapControl.CAP_BANK_EDIT + capBankId + Urls.EDIT);

        editPage = new CapBankEditPage(driverExt, Integer.parseInt(capBankId));
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void capBankEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit CapBank: AT Cap Bank";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void capBankEdit_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        String capBankEditId = TestDbDataType.VoltVarData.CAPBANK_EDIT_ID.getId().toString();
        
        final String EXPECTED_MSG = "CapBank was saved successfully.";

        navigate(Urls.CapControl.CAP_BANK_EDIT + capBankEditId+ Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited CapBank " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("CapBank: " + name, Optional.empty());

        CapBankDetailPage detailsPage = new CapBankDetailPage(driverExt, Integer.parseInt(capBankEditId));

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void capBankEdit_Delete_Success() {
        setRefreshPage(true);
        String capBankDeleteId = TestDbDataType.VoltVarData.CAPBANK_DELETE_ID.getId().toString();
        
        final String EXPECTED_MSG = "CapBank AT Delete CapBank deleted successfully.";

        navigate(Urls.CapControl.CAP_BANK_EDIT + capBankDeleteId + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
