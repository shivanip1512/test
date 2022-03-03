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
import com.eaton.pages.capcontrol.AreaDetailPage;
import com.eaton.pages.capcontrol.AreaEditPage;
import com.eaton.pages.capcontrol.CapControlDashboardPage;

public class AreaEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private AreaEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        String areaId = TestDbDataType.VoltVarData.AREA_ID.getId().toString();
        
        navigate(Urls.CapControl.AREA_EDIT + areaId + Urls.EDIT);

        editPage = new AreaEditPage(driverExt, Integer.parseInt(areaId));
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(editPage);            
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void areaEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Area: AT Area";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void areaEdit_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        String editAreaId = TestDbDataType.VoltVarData.AREA_EDIT_ID.getId().toString();
        final String EXPECTED_MSG = "Area was saved successfully.";

        navigate(Urls.CapControl.AREA_EDIT + editAreaId + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Area " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Area: " + name, Optional.empty());

        AreaDetailPage detailsPage = new AreaDetailPage(driverExt, 449);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void areaEdit_Delete_Success() {
        setRefreshPage(true);
        String deleteAreaId = TestDbDataType.VoltVarData.AREA_DELETE_ID.getId().toString();
        
        final String EXPECTED_MSG = "Area AT Delete Area Deleted successfully.";

        navigate(Urls.CapControl.AREA_EDIT + deleteAreaId + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.CapControl.DASHBOARD, Optional.empty());

        CapControlDashboardPage detailsPage = new CapControlDashboardPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
