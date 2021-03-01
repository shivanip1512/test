package com.eaton.tests.capcontrol;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.FeederListPage;

public class FeederListTests extends SeleniumTestSetup {

    private FeederListPage listPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.CapControl.FEEDER_LIST);
        listPage = new FeederListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VOLT_VAR })
    public void feederList_ColumnHeaders_Correct() {
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
