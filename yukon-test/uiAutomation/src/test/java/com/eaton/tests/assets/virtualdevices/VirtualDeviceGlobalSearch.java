package com.eaton.tests.assets.virtualdevices;

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

public class VirtualDeviceGlobalSearch extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private GlobalSearchPage globalSearchPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.HOME);
        globalSearchPage = new GlobalSearchPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES })
    public void attributesGlobalSearch_SearchAttributesAndEnter_NavigatesToSearchPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndEnter("Virtual Devices");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.SEARCH + Urls.SEARCH_PARAM + "Virtual+Devices", Optional.empty());
        
        assertThat(pageLoaded).isTrue();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES })
    public void attributesGlobalSearch_SearchAndSelectAttributResult_NavigatesToAttributesListPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndClickResult("Virtual Devices");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.Assets.VIRTUAL_DEVICES, Optional.empty());
        
        assertThat(pageLoaded).isTrue();       
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES })
    public void attributesGlobalSearch_SearchCommResults_ContainsCommChannel() {
        SearchBoxElement searchBox = globalSearchPage.getSearchBoxElement();
        
        searchBox.setSearchValue("Virtual Devices");
        List<String> results = searchBox.getSearchResults();
        
        assertThat(results).contains("Virtual Devices");
    }        
}