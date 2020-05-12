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

@Test(groups = TestConstants.AMI)
public class AmiMenuTests extends SeleniumTestSetup {
    
    private HomePage page;
    private static final int AMI_INDEX =  0;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_02_NavigateToLinks" })
    public void dashboardUrlCorrect() {        
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 0);

        assertThat(url).contains(Urls.Ami.DASHBOARD);
    }
    
    @Test
    public void billingUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 1);

        assertThat(url).contains(Urls.Ami.BILLING);
    }
    
    @Test
    public void bulkImportUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 2);

        assertThat(url).contains(Urls.Ami.BULK_IMPORT);
    }
    
    @Test
    public void bulkUpdateUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 3);

        assertThat(url).contains(Urls.Ami.BULK_UPDATE);
    }
    
    @Test
    public void legacyImporterUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 4);

        assertThat(url).contains(Urls.Ami.LEGACY_IMPORTER);
    }
    
    @Test
    public void pointImportUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 5);

        assertThat(url).contains(Urls.Ami.POINT_IMPORT);
    }
    
    @Test
    public void reportsUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI_INDEX, 6);

        assertThat(url).contains(Urls.Ami.REPORTS);
    }
}
