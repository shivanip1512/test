package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.SubstationBusDetailPage;
import com.eaton.pages.capcontrol.SubstationBusEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationBusEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void substationBusEdit_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Bus: AT Substation Bus";

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "667" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, 667);

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void substationBusEdit_updateNameOnlySuccess() {
        final String EXPECTED_MSG = "Bus was saved successfully.";

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "430" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, 430);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Bus " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Bus: " + name, Optional.empty());

        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driverExt, 430);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void substationBusEdit_deleteSubstationBusSuccess() {
        final String EXPECTED_MSG = "Bus AT Delete Bus deleted successfully.";

        navigate(Urls.CapControl.SUBSTATION_BUS_EDIT + "574" + Urls.EDIT);

        SubstationBusEditPage editPage = new SubstationBusEditPage(driverExt, 574);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWait();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
