package com.eaton.tests.capcontrol;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapBankListPage;

public class CapBankListTests extends SeleniumTestSetup {

    private CapBankListPage listPage;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        softly = new SoftAssertions();

        driver.get(getBaseUrl() + Urls.CapControl.CAP_BANK_LIST);

        listPage = new CapBankListPage(driverExt);
    }

    @Test(groups = {TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.VoltVar.VOLT_VAR})
    public void columnHeadersCorrect() {
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
