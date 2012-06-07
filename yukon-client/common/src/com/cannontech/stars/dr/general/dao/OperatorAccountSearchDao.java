package com.cannontech.stars.dr.general.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.stars.dr.general.service.impl.AccountSearchResult;

public interface OperatorAccountSearchDao {

	public List<Integer> getAccountIdsByAccountNumber(String accountNumber, Set<Integer> energyCompanyIds);
	public List<Integer> getAccountIdsByPhoneNumber(String phoneNumber, Set<Integer> energyCompanyIds);
	public List<Integer> getAccountIdsByLastName(String lastName, Set<Integer> energyCompanyIds);
	public List<Integer> getAccountIdsBySerialNumber(String serialNumber, Set<Integer> energyCompanyIds);
	public List<Integer> getAccountIdsByMapNumber(String mapNumber, Set<Integer> energyCompanyIds);
	public List<Integer> getAccountIdsByAddress(String address1, Set<Integer> energyCompanyIds);
	public List<Integer> getAccountIdsByAltTrackingNumber(String altTrackingNumber, Set<Integer> energyCompanyIds);
	public List<Integer> getAccountIdsByCompanyName(String companyName, Set<Integer> energyCompanyIds);
	
	public List<AccountSearchResult> getAccountSearchResultsForAccountIds(List<Integer> accountIds);
	public AccountSearchResult getAccountSearchResultForAccountId(int accountId);
}
