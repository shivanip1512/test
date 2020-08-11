package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.SelectPointModal;
import com.eaton.elements.modals.TrendAddPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendCreatePage;
import com.eaton.pages.tools.trends.TrendsListPage;

public class TrendCreateTests extends SeleniumTestSetup {

    private TrendCreatePage page;
    private DriverExtensions driverExt;
    String trendName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        navigate(Urls.Tools.TREND_CREATE);
        page = new TrendCreatePage(driverExt, Urls.Tools.TREND_CREATE);
    }

    @AfterMethod
    public void afterMethod() {
        refreshPage(page);
    }

    @Test
    public void trendCreate_WithPoint_Successfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "Trend " + timeStamp;
        page.getName().setInputValue(name);
        TrendAddPointModal addPointModal = page.showAndWaitAddPointModal();

        SelectPointModal pointModal = addPointModal.showAndWaitAddPointModal();
        pointModal.selectPoint("Month History", Optional.of("4999"));
        pointModal.clickOkAndWait();

        addPointModal.clickOkAndWaitForModalToClose();

        page.getSave().click();
        
        waitForPageToLoad(name, Optional.of(3));

        TrendsListPage listPage = new TrendsListPage(driverExt);

        String userMsg = listPage.getUserMessage();

        assertThat(name + " saved successfully.").isEqualTo(userMsg);
    }
}
