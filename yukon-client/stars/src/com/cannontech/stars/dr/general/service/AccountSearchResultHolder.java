package com.cannontech.stars.dr.general.service;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.stars.dr.general.service.impl.AccountSearchResult;

public class AccountSearchResultHolder extends SearchResult<AccountSearchResult> {

	int searchByDefinitionId;
	String searchValue;
	SearchResult<AccountSearchResult> accountSearchResults = SearchResult.emptyResult();
	String error = null;
	
	public AccountSearchResultHolder(int searchByDefinitionId, String searchValue, SearchResult<AccountSearchResult> accountSearchResults, String error) {
		
		this.searchByDefinitionId = searchByDefinitionId;
		this.searchValue = searchValue;
		this.accountSearchResults = accountSearchResults;
		this.error = error;
	}
	
	public static AccountSearchResultHolder emptyAccountSearchResultHolder() {
		
		SearchResult<AccountSearchResult> emptyResult = SearchResult.emptyResult();
		AccountSearchResultHolder accountSearchResultHolder = new AccountSearchResultHolder(OperatorAccountSearchBy.ACCOUNT_NUMBER.getDefinitionId(), "", emptyResult, null);
		
		return accountSearchResultHolder;
	}
	
	public int getSearchByDefinitionId() {
		return searchByDefinitionId;
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
