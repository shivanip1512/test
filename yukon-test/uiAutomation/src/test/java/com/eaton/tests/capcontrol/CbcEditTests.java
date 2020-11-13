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
import com.eaton.pages.capcontrol.CbcDetailPage;
import com.eaton.pages.capcontrol.CbcEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class CbcEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private CbcEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Integer cbcEditId = TestDbDataType.VoltVarData.CBC_ID.getId();
        
        navigate(Urls.CapControl.CBC_EDIT + cbcEditId + Urls.EDIT);

        editPage = new CbcEditPage(driverExt, cbcEditId);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void cbcEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit CBC: AT CBC";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void cbcEdit_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "CBC was successfully saved.";
        String cbcEditId = TestDbDataType.VoltVarData.CBC_EDIT_ID.getId().toString();

        navigate(Urls.CapControl.CBC_EDIT + cbcEditId + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited CBC " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name, Optional.empty());

        CbcDetailPage detailPage = new CbcDetailPage(driverExt, Integer.parseInt(cbcEditId));

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void cbcEdit_Delete_Success() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Deleted CBC";
        String cbcDeleteId = TestDbDataType.VoltVarData.CBC_DELETE_ID.getId().toString();

        navigate(Urls.CapControl.CBC_EDIT + cbcDeleteId + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
