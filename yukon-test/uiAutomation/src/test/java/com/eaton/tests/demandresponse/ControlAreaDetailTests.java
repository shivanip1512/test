package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaDetailPage;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;

public class ControlAreaDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void controlAreaDetail_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Control Area: AT Control Area";
        
        String controlAreaId = TestDbDataType.DemandResponseData.CONTROLAREA_ID.getId().toString();

        navigate(Urls.DemandResponse.CONTROL_AREA_DETAILS + controlAreaId);

        ControlAreaDetailPage editPage = new ControlAreaDetailPage(driverExt, Integer.parseInt(controlAreaId));

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void controlAreaDetail_Delete_Success() {
        final String EXPECTED_MSG = "AT Delete Control Area deleted successfully.";
        
        String controlAreaDeleteId = TestDbDataType.DemandResponseData.CONTROLAREA_DELETE_ID.getId().toString();

        navigate(Urls.DemandResponse.CONTROL_AREA_DETAILS + controlAreaDeleteId);

        ControlAreaDetailPage detailPage = new ControlAreaDetailPage(driverExt, Integer.parseInt(controlAreaDeleteId));

        ConfirmModal confirmModal = detailPage.showDeleteControlAreaModal();

        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());

        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.CONTROL_AREA);

        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
