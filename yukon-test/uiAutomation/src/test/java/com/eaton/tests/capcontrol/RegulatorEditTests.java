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
import com.eaton.pages.capcontrol.RegulatorDetailPage;
import com.eaton.pages.capcontrol.RegulatorEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class RegulatorEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private RegulatorEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        String regulatorId = TestDbDataType.VoltVarData.REGULATOR_ID.getId().toString();
        
        setRefreshPage(false);
        navigate(Urls.CapControl.REGULATOR_EDIT + regulatorId + Urls.EDIT);
        editPage = new RegulatorEditPage(driverExt, Integer.parseInt(regulatorId));
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void regulatorEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Regulator: AT Regulator";

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void regulatorEdit_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        String regulatorEditId = TestDbDataType.VoltVarData.REGULATOR_EDIT_ID.getId().toString();
        
        navigate(Urls.CapControl.REGULATOR_EDIT + regulatorEditId+ Urls.EDIT);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Regulator " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Regulator: " + name, Optional.empty());

        RegulatorDetailPage detailsPage = new RegulatorDetailPage(driverExt, Integer.parseInt(regulatorEditId));

        // The saved successfully message is missing why?
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "Regulator was saved successfully.");
        String actualPageTitle = detailsPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo("Regulator: " + name);
    }

    @Test(enabled = false, groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void regulatorEdit_Delete_Success() {
        setRefreshPage(true);
        
        String regulatorDeleteId = TestDbDataType.VoltVarData.REGULATOR_DELETE_ID.getId().toString();

        navigate(Urls.CapControl.REGULATOR_EDIT + regulatorDeleteId + Urls.EDIT);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        // TODO need to figure out what to assert since there is no message like the other volt/var objects that it has been
        // deleted

//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        assertThat(userMsg).isEqualTo("Feeder AT Delete Feeder deleted successfully.");
    }
}
