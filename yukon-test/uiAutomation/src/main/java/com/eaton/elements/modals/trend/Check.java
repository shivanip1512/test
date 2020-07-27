package com.eaton.elements.modals.trend;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.trend.TrendEditPage;

public class Check extends SeleniumTestSetup {
    
    private DriverExtensions driverExt;
    private TrendEditPage trendEditPage;
    private String trendId = "12";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        driverExt = getDriverExt();
    }
    
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Tools.TREND + trendId+Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt);
    }

    @Test
    public void test1() {
        trendEditPage.getName().setInputValue("trendEdit");
        //trendEditPage.get
        trendEditPage.getTabElement().clickTab("Additional Options");
        //waitForUrlToLoad(Urls.Tools.TREND + trendId+Urls.EDIT, Optional.empty());
        //trendEditPage.getCancel().click();
    }
}
