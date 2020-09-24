package com.eaton.tests.capcontrol;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.SubstationBusListPage;

public class SubstationBusListTests extends SeleniumTestSetup {

    private SubstationBusListPage listPage;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.CapControl.SUBSTATION_BUS_LIST);
        listPage = new SubstationBusListPage(driverExt);
    }

    @Test(groups = {TestConstants.Priority.MEDIUM, TestConstants.Features.VOLT_VAR })
    public void substationBusList_ColumnHeaders_Correct() {
        SoftAssertions softly = new SoftAssertions();
        final int EXPECTED_COUNT = 3;

        List<String> headers = this.listPage.getTable().getListTableHeaders();
        
        int actualCount = headers.size();
        
        softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);
        softly.assertThat(headers).contains("Name");
        softly.assertThat(headers).contains("Item Type");
        softly.assertThat(headers).contains("Description");
        softly.assertAll();
    }
}
