package com.eaton.pages.assets.commchannels;

import java.util.List;

import com.eaton.elements.SearchBoxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CommChannelGlobalSearch extends PageBase {
	private String searchText="Comm Channels";
	private String parentElement = "toolbar";
		
	public CommChannelGlobalSearch(DriverExtensions driverExt, String searchText) {
	        super(driverExt);
			this.searchText=searchText;
				
			requiresLogin = true;
	        pageUrl = Urls.HOME ;
	    }
		    
		public CommChannelGlobalSearch(DriverExtensions driverExt) {
		        super(driverExt);
		    }
		
		//Object creation for Search box element class.
		public SearchBoxElement getSearchBoxElement() {	
				return new SearchBoxElement(this.driverExt, this.parentElement, this.searchText);	
			}
		
		//Function to set search text in global search box and hit enter button.
		public void searchDirectlyWithText(){
			getSearchBoxElement().setSearchValueWithEnter(this.searchText);
		}
		
		//To validate if searched text is present in global search box suggestion list.
		public List<String> verifySearchResult_InSearchSuggestionList(String searchText) {
			List<String> results=getSearchBoxElement().validateSearchTextPresentInSugestionList(searchText);
			
			return results;
			}
		
		public void clickOnSearchedText_InGlobalSearchResult() {
			getSearchBoxElement().setSearchValueAndClickResult(searchText);
		}
		
	}
