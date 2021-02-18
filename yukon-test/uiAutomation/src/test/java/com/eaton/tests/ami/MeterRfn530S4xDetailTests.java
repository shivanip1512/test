package com.eaton.tests.ami;

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
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterRfn530S4xDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(enabled = true, groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.AMI })
    public void meterRfn530S4xDetail_Delete_Success() {
        String rfn530S4xDeleteId = TestDbDataType.MeterData.RFN_530S4X_DELETE_ID.getId().toString();
        
        final String EXPECTED_MSG = "Meter AT Delete RFN-530S4x deleted successfully.";

        navigate(Urls.Ami.METER_DETAIL + rfn530S4xDeleteId);

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, Integer.parseInt(rfn530S4xDeleteId));

        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWaitForModalToClose();

        waitForUrlToLoad(Urls.Ami.AMI_DASHBOARD, Optional.of(10));

        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt);

        String userMsg = dashboardPage.getUserMessage();

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }
}
