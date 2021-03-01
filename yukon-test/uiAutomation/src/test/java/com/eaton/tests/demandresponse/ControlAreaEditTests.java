package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaDetailPage;
import com.eaton.pages.demandresponse.ControlAreaEditPage;

public class ControlAreaEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void controlAreaEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Control Area: AT Control Area";
        
        String controlAreaId = TestDbDataType.DemandResponseData.CONTROLAREA_ID.getId().toString();

        navigate(Urls.DemandResponse.CONTROL_AREA_EDIT + controlAreaId + Urls.EDIT);

        ControlAreaEditPage editPage = new ControlAreaEditPage(driverExt, Integer.parseInt(controlAreaId));
        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void controlAreaEdit_RequiredFieldsOnly_Success() {
        String editAreaId = TestDbDataType.DemandResponseData.CONTROLAREA_EDIT_ID.getId().toString();
        
        navigate(Urls.DemandResponse.CONTROL_AREA_EDIT + editAreaId + Urls.EDIT);
        
        String controlAreaEditId = TestDbDataType.DemandResponseData.CONTROLAREA_EDIT_ID.getId().toString();

        ControlAreaEditPage editPage = new ControlAreaEditPage(driverExt, Integer.parseInt(controlAreaEditId));

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Control Area " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        editPage.getName().setInputValue(name);

        editPage.getSave().click();

        waitForPageToLoad("Control Area: " + name, Optional.empty());

        ControlAreaDetailPage detailsPage = new ControlAreaDetailPage(driverExt, Integer.parseInt(controlAreaEditId));

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
