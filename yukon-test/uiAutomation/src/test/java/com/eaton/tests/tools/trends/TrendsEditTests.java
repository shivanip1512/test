package com.eaton.tests.tools.trends;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.MarkerModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendEditPage;

public class TrendsEditTests extends SeleniumTestSetup {
    
    private TrendEditPage editPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.Tools.TREND + 1 + Urls.EDIT);

        this.editPage = new TrendEditPage(driverExt);
    }

    @Test
    public void editTrend_WithMultipleMarkers_Success() {
        editPage.getTabElement().clickTabAndWait("Additional Options");
           
        MarkerModal modal = editPage.showAndWaitMarkerSetupAddModal();
        
        modal.getLabel().setInputValue("Test label");
        modal.clickOkAndWait();
        
        modal = editPage.showAndWaitMarkerSetupAddModal();
        
        modal.getLabel().setInputValue("Test label two");
        modal.clickOkAndWait();
        
        editPage.getSave().click();        
    }
}
