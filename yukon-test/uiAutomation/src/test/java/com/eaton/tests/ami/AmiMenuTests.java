package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AmiMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int AMI_INDEX = 0;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Ami.AMI })
    public void amiMenu_dashboardUrlCorrect() {
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 0);

        assertThat(url).contains(Urls.Ami.DASHBOARD);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_billingUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 1);

        assertThat(url).contains(Urls.Ami.BILLING);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_bulkImportUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 2);

        assertThat(url).contains(Urls.Ami.BULK_IMPORT);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_bulkUpdateUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 3);

        assertThat(url).contains(Urls.Ami.BULK_UPDATE);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_legacyImporterUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 4);

        assertThat(url).contains(Urls.Ami.LEGACY_IMPORTER);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_pointImportUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 5);

        assertThat(url).contains(Urls.Ami.POINT_IMPORT);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 6);

        assertThat(url).contains(Urls.Ami.REPORTS);
    }
}
