package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import com.eaton.elements.SearchBoxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.GlobalSearchPage;

public class CommChannelGlobalSearchTest extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private GlobalSearchPage globalSearchPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);
        
        globalSearchPage = new GlobalSearchPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelGlobalSearch_SearchCommChannelAndEnterNavigatesToSearchPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndEnter("Comm Channels");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.SEARCH + Urls.SEARCH_PARAM + "Comm+Channels", Optional.empty());
        
        assertThat(pageLoaded).isTrue();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelGlobalSearch_ResultNavigatesToCommChannelListPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndClickResult("Comm Channels");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.Assets.COMM_CHANNELS_LIST, Optional.empty());
        
        assertThat(pageLoaded).isTrue();       
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.COMM_CHANNELS })
    public void commChannelGlobalSearch_SearchCommResultsContainCommChannel() {
        SearchBoxElement searchBox = globalSearchPage.getSearchBoxElement();
        
        searchBox.setSearchValue("Comm Chann");
        List<String> results = searchBox.getSearchResults();
        
        assertThat(results).contains("Comm Channels");
    }        
}