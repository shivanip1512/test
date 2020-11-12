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
import com.eaton.pages.capcontrol.FeederDetailPage;
import com.eaton.pages.capcontrol.FeederEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class FeederEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private FeederEditPage editPage;    

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        String feederId = TestDbDataType.VoltVarData.FEEDER_ID.getId().toString();
        
        navigate(Urls.CapControl.FEEDER_EDIT + feederId + Urls.EDIT);
        editPage = new FeederEditPage(driverExt, Integer.parseInt(feederId));
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void feederEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Feeder: AT Feader";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void feederEdit_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Feeder was saved successfully.";
        String feederEditId = TestDbDataType.VoltVarData.FEEDER_EDIT_ID.getId().toString();

        navigate(Urls.CapControl.FEEDER_EDIT + feederEditId + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Feeder " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Feeder: " + name, Optional.empty());

        FeederDetailPage detailsPage = new FeederDetailPage(driverExt, Integer.parseInt(feederEditId));

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void feederEdit_Delete_Success() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Feeder AT Delete Feeder deleted successfully.";
        String feederDeleteId = TestDbDataType.VoltVarData.FEEDER_DELETE_ID.getId().toString();

        navigate(Urls.CapControl.FEEDER_EDIT + feederDeleteId + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
