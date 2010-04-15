package com.cannontech.web.menu.option.producer;

public class SearchType {

	private String searchTypeValue;
	private String displayKey;
	
	public SearchType(String searchTypeValue, String displayKey) {
		
		this.searchTypeValue = searchTypeValue;
		this.displayKey = displayKey;
	}
	
	public String getSearchTypeValue() {
		return searchTypeValue;
	}
	
	public String getDisplayKey() {
		return displayKey;
	}
}
