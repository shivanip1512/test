package com.eaton.tests.tools.trends;

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

public class TrendsGlobalSearchTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private GlobalSearchPage globalSearchPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.HOME);
        globalSearchPage = new GlobalSearchPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void attributesGlobalSearch_SearchAttributesAndEnter_NavigatesToSearchPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndEnter("Trends");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.SEARCH + Urls.SEARCH_PARAM + "Trends", Optional.empty());
        
        assertThat(pageLoaded).isTrue();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void attributesGlobalSearch_SearchAndSelectAttributResult_NavigatesToAttributesListPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndClickResult("Trends");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.Tools.TRENDS_LIST, Optional.empty());
        
        assertThat(pageLoaded).isTrue();       
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void attributesGlobalSearch_SearchCommResults_ContainsCommChannel() {
        SearchBoxElement searchBox = globalSearchPage.getSearchBoxElement();
        
        searchBox.setSearchValue("Trends");
        List<String> results = searchBox.getSearchResults();
        
        assertThat(results).contains("Trends");
    }        
}