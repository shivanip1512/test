package com.eaton.tests.ami;

import static org.assertj.core.api.Assertions.*;
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
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.HOME);
        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Ami.AMI })
    public void amiMenu_DashboardUrl_Correct() {
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 0);

        assertThat(url).contains(Urls.Ami.DASHBOARD);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_BillingUrl_Correct() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 1);

        assertThat(url).contains(Urls.Ami.BILLING);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_MeterProgrammingUrl_Correct() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 2);

        assertThat(url).contains(Urls.Ami.METER_PROGRAMMING);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_BulkImportUrl_Correct() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 3);

        assertThat(url).contains(Urls.Ami.BULK_IMPORT);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_BulkUpdateUrl_Correct() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 4);

        assertThat(url).contains(Urls.Ami.BULK_UPDATE);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_LegacyImporterUrl_Correct() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 5);

        assertThat(url).contains(Urls.Ami.LEGACY_IMPORTER);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_PointImportUrl_Correct() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 6);

        assertThat(url).contains(Urls.Ami.POINT_IMPORT);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Ami.AMI })
    public void amiMenu_ReportsUrl_Correct() {

        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 7);

        assertThat(url).contains(Urls.Ami.REPORTS);
    }
}
