package com.eaton.tests.assets.commchannels;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.SkipException;
import org.testng.annotations.*;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.GlobalSearchPage;

public class CommChannelGlobalSearchTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private GlobalSearchPage globalSearchPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.HOME);
        globalSearchPage = new GlobalSearchPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void commChannelGlobalSearch_SearchCommChannelAndEnter_NavigatesToSearchPage() {
        globalSearchPage.getSearchBoxElement().setSearchValueAndEnter("Comm Channels");
        
        boolean pageLoaded = waitForUrlToLoad(Urls.SEARCH + Urls.SEARCH_PARAM + "Comm+Channels", Optional.empty());
        
        assertThat(pageLoaded).isTrue();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void commChannelGlobalSearch_SearchAndSelectCommChannelsResult_NavigatesToCommChannelListPage() {
        throw new SkipException("QA task created: QA-6229");
        
//        globalSearchPage.getSearchBoxElement().setSearchValueAndClickResult("Comm Channels");
//        
//        boolean pageLoaded = waitForUrlToLoad(Urls.Features.COMM_CHANNELS_LIST, Optional.empty());
//        
//        assertThat(pageLoaded).isTrue();       
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.COMM_CHANNELS })
    public void commChannelGlobalSearch_SearchCommResults_ContainsCommChannel() {
        throw new SkipException("QA task created: QA-6229");
        
//        SearchBoxElement searchBox = globalSearchPage.getSearchBoxElement();
//        
//        searchBox.setSearchValue("Comm Chann");
//        List<String> results = searchBox.getSearchResults();
//        
//        assertThat(results).contains("Comm Channels");
    }        
}