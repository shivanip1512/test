package com.cannontech.stars.dr.general.service;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.stars.dr.general.service.impl.AccountSearchResult;

public class AccountSearchResultHolder {

	private OperatorAccountSearchBy searchBy;
	private String searchValue;
	private SearchResult<AccountSearchResult> accountSearchResults = SearchResult.emptyResult();
	private String error = null;
	
	public AccountSearchResultHolder(OperatorAccountSearchBy searchBy, String searchValue, SearchResult<AccountSearchResult> accountSearchResults, String error) {
		
		this.searchBy = searchBy;
		this.searchValue = searchValue;
		this.accountSearchResults = accountSearchResults;
		this.error = error;
	}
	
	public static AccountSearchResultHolder emptyAccountSearchResultHolder() {
		
		SearchResult<AccountSearchResult> emptyResult = SearchResult.emptyResult();
		AccountSearchResultHolder accountSearchResultHolder = new AccountSearchResultHolder(OperatorAccountSearchBy.ACCOUNT_NUMBER, "", emptyResult, null);
		
		return accountSearchResultHolder;
	}
	
	public OperatorAccountSearchBy getSearchBy() {
		return searchBy;
	}
	
	public String getSearchValue() {
		return searchValue;
	}
	
	public SearchResult<AccountSearchResult> getAccountSearchResults() {
		return accountSearchResults;
	}
	public String getError() {
		return error;
	}
	
	public boolean isHasError() {
		return !StringUtils.isBlank(error);
	}
	public boolean isSingleResult() {
		return accountSearchResults.getHitCount() == 1;
	}
}
