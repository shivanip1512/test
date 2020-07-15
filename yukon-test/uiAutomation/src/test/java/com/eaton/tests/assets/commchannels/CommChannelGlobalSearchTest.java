package com.eaton.tests.assets.commchannels;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.commchannels.CommChannelGlobalSearch;

public class CommChannelGlobalSearchTest extends SeleniumTestSetup {

	private DriverExtensions driverExt;
	private CommChannelGlobalSearch commChannel;
	private WebDriver driver;
	private String searchText="Comm Channels";
	
	@BeforeClass(alwaysRun=true)
    public void beforeClass() 
	{
		driverExt = getDriverExt();
		driver=getDriver();
		commChannel = new CommChannelGlobalSearch(driverExt);

	}  
	
	@BeforeMethod(alwaysRun = true)
	public void beforeMethod() 
	{
	 navigate(Urls.HOME);
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS })
    public void commChannelGlobalSearch_SearchCommChannelAndEnterNavigatesToSearchPage() {
        commChannel.searchDirectlyWithText();
        String expectedUrl= Urls.SEARCH + "Comm+Channels";
        assertThat(driver.getCurrentUrl()).isEqualTo(expectedUrl);
    }
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS })
	public void commChannelGlobalSearch_ResultNavigatesToCommChannelListPage() {
			commChannel.clickOnSearchedText_InGlobalSearchResult();
			String expectedTitle = "Comm Channels";
			assertThat(driver.getTitle()).isEqualTo(expectedTitle);
	}
	
	@Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS })
	public void commChannelGlobalSearch_SearchCommResultsContainCommChannel() {
			List<String> list = commChannel.verifySearchResult_InSearchSuggestionList(this.searchText);
			assertThat(list.size() != 0);
	}		
}