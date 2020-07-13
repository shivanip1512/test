package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaDetailPage;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;

public class ControlAreaDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void controlAreaDetail_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Control Area: AT Control Area";

        navigate(Urls.DemandResponse.CONTROL_AREA_DETAILS + "662");

        ControlAreaDetailPage editPage = new ControlAreaDetailPage(driverExt, 662);

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void controlAreaDetail_deleteControlAreaSuccess() {
        final String EXPECTED_MSG = "AT Delete Control Area deleted successfully.";

        navigate(Urls.DemandResponse.CONTROL_AREA_DETAILS + "589");

        ControlAreaDetailPage detailPage = new ControlAreaDetailPage(driverExt, 589);

        ConfirmModal confirmModal = detailPage.showDeleteControlAreaModal();

        confirmModal.clickOkAndWait();

        waitForPageToLoad("Setup", Optional.empty());

        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.CONTROL_AREA);

        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
