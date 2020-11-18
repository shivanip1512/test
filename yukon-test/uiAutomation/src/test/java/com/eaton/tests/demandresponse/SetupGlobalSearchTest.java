package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.SearchBoxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.GlobalSearchPage;

public class SetupGlobalSearchTest extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private GlobalSearchPage globalSearchPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.HOME);
        globalSearchPage = new GlobalSearchPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE, TestConstants.Features.SETUP })
    public void setupGlobalSearch_SearchSetupAndEnter_NavigatesToSearchPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndEnter("Setup");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.SEARCH + Urls.SEARCH_PARAM + "Setup", Optional.empty());
        
        assertThat(pageLoaded).as("Expected URL: " + Urls.SEARCH + Urls.SEARCH_PARAM + "Setup | Actual Url: " + SeleniumTestSetup.getCurrentUrl()).isTrue();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE, TestConstants.Features.SETUP })
    public void setupGlobalSearch_SearchAndSelectSetupResult_NavigatesToSetupListPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndClickResult("Setup");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.DemandResponse.SETUP, Optional.empty());
        
        assertThat(pageLoaded).as("Expected URL: " + Urls.DemandResponse.SETUP + " | Actual Url: " + SeleniumTestSetup.getCurrentUrl()).isTrue();      
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE, TestConstants.Features.SETUP })
    public void setupGlobalSearch_SearchSetupPartialTextResult_ContainsSetup() {
        SearchBoxElement searchBox = globalSearchPage.getSearchBoxElement();
        
        searchBox.setSearchValue("Set");
        List<String> results = searchBox.getSearchResults();
        
        assertThat(results).contains("Setup");
    }        
}
