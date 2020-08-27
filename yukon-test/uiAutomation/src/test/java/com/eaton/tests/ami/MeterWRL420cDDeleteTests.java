package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.MeterConstants;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;
import com.eaton.pages.ami.MeterDetailsPage;

public class MeterWRL420cDDeleteTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(enabled = true, groups = { TestConstants.Priority.LOW, TestConstants.Ami.AMI })
    public void meterWRL420cDDetail_deleteSuccess() {
        final int DEVICEID = 1233;
    	final String EXPECTED_MSG = "Meter AT Delete " + MeterConstants.WRL420CD.getMeterType() + " deleted successfully.";

        navigate(Urls.Ami.METER_DETAIL + DEVICEID);

        MeterDetailsPage meterDetailsPage = new MeterDetailsPage(driverExt, DEVICEID);

        ConfirmModal modal = meterDetailsPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWait();

        waitForUrlToLoad(Urls.Ami.AMI_DASHBOARD, Optional.of(10));

        AmiDashboardPage dashboardPage = new AmiDashboardPage(driverExt);

        String userMsg = dashboardPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
