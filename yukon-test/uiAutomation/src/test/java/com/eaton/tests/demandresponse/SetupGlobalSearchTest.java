package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.testng.SkipException;
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

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE, TestConstants.DemandResponse.SETUP })
    public void setupGlobalSearch_SearchSetupAndEnter_NavigatesToSearchPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndEnter("Setup");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.SEARCH + Urls.SEARCH_PARAM + "Setup", Optional.empty());
        
        assertThat(pageLoaded).isTrue();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE, TestConstants.DemandResponse.SETUP })
    public void setupGlobalSearch_SearchAndSelectSetup_ResultNavigatesToSetupListPage() {
        throw new SkipException("QA task created: QA-6229");
//        globalSearchPage.getSearchBoxElement().setSearchValueAndClickResult("Setup");
//        
//        boolean pageLoaded = waitForUrlToLoad(Urls.DemandResponse.SETUP, Optional.empty());
//        
//        assertThat(pageLoaded).isTrue();       
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE, TestConstants.DemandResponse.SETUP })
    public void setupGlobalSearch_SearchSetupPartialText_ResultsContainSetup() {
        throw new SkipException("QA task created: QA-6229");
//        SearchBoxElement searchBox = globalSearchPage.getSearchBoxElement();
//        
//        searchBox.setSearchValue("Set");
//        List<String> results = searchBox.getSearchResults();
//        
//        assertThat(results).contains("Setup");
    }        
}
