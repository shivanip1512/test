package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.SearchBoxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.GlobalSearchPage;

public class LoadGroupGlobalSearchTest extends SeleniumTestSetup{
	 private DriverExtensions driverExt;
	    private GlobalSearchPage globalSearchPage;

	    @BeforeClass(alwaysRun = true)
	    public void beforeClass() {
	        WebDriver driver = getDriver();
	        driverExt = getDriverExt();

	        driver.get(getBaseUrl() + Urls.HOME);
	        
	        globalSearchPage = new GlobalSearchPage(driverExt);
	    }

	    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	    public void loadGroupGlobalSearch_SearchLoadGroups_AndEnterNavigatesToSearchPage() {
	        globalSearchPage.getSearchBoxElement().setSearchValueAndEnter("Load Groups");
	        
	        boolean pageLoaded = waitForUrlToLoad(Urls.SEARCH + Urls.SEARCH_PARAM + "Load+Groups", Optional.empty());
	        
	        assertThat(pageLoaded).isTrue();
	    }

	    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	    public void loadGroupGlobalSearch_SearchLoadGroups_ResultNavigatesToLoadGroupsListPage() {
	        globalSearchPage.getSearchBoxElement().setSearchValueAndClickResult("Load Groups");
	        
	        boolean pageLoaded = waitForUrlToLoad(Urls.Assets.COMM_CHANNELS_LIST, Optional.empty());
	        
	        assertThat(pageLoaded).isTrue();       
	    }

	    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	    public void loadGroupGlobalSearch_SearchLoadGroups_ResultsContainLoadGroups() {
	        SearchBoxElement searchBox = globalSearchPage.getSearchBoxElement();
	        
	        searchBox.setSearchValue("Load Grou");
	        List<String> results = searchBox.getSearchResults();
	        
	        assertThat(results).contains("Load Groups");
	    }        

}
