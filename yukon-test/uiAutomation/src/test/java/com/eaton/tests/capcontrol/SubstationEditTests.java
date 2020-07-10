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
import com.eaton.pages.capcontrol.SubstationDetailPage;
import com.eaton.pages.capcontrol.SubstationEditPage;
import com.eaton.pages.capcontrol.orphans.OrphansPage;

public class SubstationEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Substation: AT Substation";

        navigate(Urls.CapControl.SUBSTATION_EDIT + "666" + Urls.EDIT);

        SubstationEditPage editPage = new SubstationEditPage(driverExt, 666);

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void editSubstationRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Substation was saved successfully.";

        navigate(Urls.CapControl.SUBSTATION_EDIT + "451" + Urls.EDIT);

        SubstationEditPage editPage = new SubstationEditPage(driverExt, 451);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());

        String name = "AT Edited Substation " + timeStamp;
        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Substation: " + name, Optional.empty());

        SubstationDetailPage detailsPage = new SubstationDetailPage(driverExt, 451);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void deleteSubstationSuccess() {
        final String EXPECTED_MSG = "Substation AT Delete Substation deleted successfully.";

        navigate(Urls.CapControl.SUBSTATION_EDIT + "573" + Urls.EDIT);

        SubstationEditPage editPage = new SubstationEditPage(driverExt, 573);

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();

        modal.clickOkAndWait();

        waitForPageToLoad("Orphans", Optional.empty());

        OrphansPage detailsPage = new OrphansPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
