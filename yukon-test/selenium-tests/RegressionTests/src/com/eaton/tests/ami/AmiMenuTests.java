package com.eaton.tests.ami;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

@Test(groups = TestNgGroupConstants.AMI)
public class AmiMenuTests extends SeleniumTestSetup {
    
    private static final String EXPECTED = "Expected Url: ";
    private static final String ACTUAL = " Actual Url: ";
    private HomePage page;
    private static final String AMI =  "AMI";

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driver, getBaseUrl());
    }
    
    @Test(groups = { "smoketest", "SM03_02_NavigateToLinks" })
    public void dashboardUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI, "Dashboard");

        Assert.assertTrue(url.contains(Urls.Ami.DASHBOARD), EXPECTED + Urls.Assets.DASHBOARD + ACTUAL + url);
    }
    
    @Test
    public void billingUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI, "Billing");

        Assert.assertTrue(url.contains(Urls.Ami.BILLING), EXPECTED + Urls.Ami.BILLING + ACTUAL + url);
    }
    
    @Test
    public void bulkImportUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI, "Bulk Import");

        Assert.assertTrue(url.contains(Urls.Ami.BULK_IMPORT), EXPECTED + Urls.Ami.BULK_IMPORT + ACTUAL + url);
    }
    
    @Test
    public void bulkUpdateUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI, "Bulk Update");

        Assert.assertTrue(url.contains(Urls.Ami.BULK_UPDATE), EXPECTED + Urls.Ami.BULK_UPDATE + ACTUAL + url);
    }
    
    @Test
    public void legacyImporterUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI, "Legacy Importer");

        Assert.assertTrue(url.contains(Urls.Ami.LEGACY_IMPORTER), EXPECTED + Urls.Ami.LEGACY_IMPORTER + ACTUAL + url);
    }
    
    @Test
    public void reportsUrlCorrect() {
        
        String url = page.getMenu().getMenuOptionUrl(AMI, "Reports");

        Assert.assertTrue(url.contains(Urls.Ami.REPORTS), EXPECTED + Urls.Ami.REPORTS + ACTUAL + url);
    }
}
