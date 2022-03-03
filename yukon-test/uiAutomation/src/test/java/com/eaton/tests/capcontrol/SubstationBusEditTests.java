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
import com.eaton.pages.capcontrol.SubstationBusDetailPage;
import com.eaton.pages.capcontrol.SubstationBusEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationBusEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private SubstationBusEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        String subBusId = TestDbDataType.VoltVarData.SUBBUS_ID.getId().toString();
        
        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + subBusId + Urls.EDIT);

        editPage = new SubstationBusEditPage(driverExt, Integer.parseInt(subBusId));
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void substationBusEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Bus: AT Substation Bus";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void substationBusEdit_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Bus was saved successfully.";
        
        String subBusEditId = TestDbDataType.VoltVarData.SUBBUS_EDIT_ID.getId().toString();

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + subBusEditId + Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Bus " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Bus: " + name, Optional.empty());

        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driverExt, Integer.parseInt(subBusEditId));

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void substationBusEdit_Delete_Success() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Bus AT Delete Bus deleted successfully.";
        
        String subBusDeleteId = TestDbDataType.VoltVarData.SUBBUS_DELETE_ID.getId().toString();

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + subBusDeleteId + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
