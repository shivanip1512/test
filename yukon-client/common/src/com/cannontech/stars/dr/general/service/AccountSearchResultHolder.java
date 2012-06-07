package com.cannontech.stars.dr.general.service;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.stars.dr.general.service.impl.AccountSearchResult;

public class AccountSearchResultHolder {

	private OperatorAccountSearchBy searchBy;
	private String searchValue;
	private SearchResult<AccountSearchResult> accountSearchResults = SearchResult.emptyResult();
	private MessageSourceResolvable warning = null;
	
	public AccountSearchResultHolder(OperatorAccountSearchBy searchBy, String searchValue, SearchResult<AccountSearchResult> accountSearchResults, MessageSourceResolvable warning) {
		
		this.searchBy = searchBy;
		this.searchValue = searchValue;
		this.accountSearchResults = accountSearchResults;
		this.warning = warning;
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
	public MessageSourceResolvable getWarning() {
		return warning;
	}
	
	public boolean isHasWarning() {
		return warning != null;
	}
	public boolean isSingleResult() {
		return accountSearchResults.getHitCount() == 1;
	}
}
