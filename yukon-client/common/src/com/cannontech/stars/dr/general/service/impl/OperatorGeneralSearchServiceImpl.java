package com.cannontech.stars.dr.general.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.general.dao.OperatorAccountSearchDao;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.stars.dr.general.service.AccountSearchResultHolder;
import com.cannontech.stars.dr.general.service.OperatorGeneralSearchService;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class OperatorGeneralSearchServiceImpl implements OperatorGeneralSearchService {

	private RolePropertyDao rolePropertyDao;
	private OperatorAccountSearchDao operatorAccountSearchDao;
	@Autowired private PhoneNumberFormattingService phoneNumberFormattingService;
	
	@Override
	public AccountSearchResultHolder customerAccountSearch(OperatorAccountSearchBy searchBy, 
														   String searchValue,
														   int startIndex,
														   int pageCount,
														   LiteStarsEnergyCompany energyCompany, 
														   YukonUserContext userContext) {
		
		MessageSourceResolvable error = null;

		// basic validation
        if (!(searchBy == OperatorAccountSearchBy.ACCOUNT_NUMBER) && searchValue.trim().length() < 2) {
        	error = new YukonMessageSourceResolvable("yukon.web.modules.operator.operatorGeneralSearchService.error.needTwoCharacters");
        }
        if (StringUtils.isBlank(searchValue)) {
        	error = new YukonMessageSourceResolvable("yukon.web.modules.operator.operatorGeneralSearchService.error.emptySearchValue");
        }
        
        // accountIds
        List<Integer> accountIds = Lists.newArrayList();
        
        if (error == null) {
       
	        boolean adminManageMembers = 
	            rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, 
	                                          userContext.getYukonUser());
			boolean searchMembers = adminManageMembers && energyCompany.hasChildEnergyCompanies();
			
			// searchEnergyCompanyIds
			Set<Integer> searchEnergyCompanyIds = Sets.newHashSet(energyCompany.getLiteID());
			if (searchMembers) {
				 for (LiteStarsEnergyCompany company : energyCompany.getChildren()) {
		            searchEnergyCompanyIds.addAll(company.getAllEnergyCompaniesDownward());
		        }
			}
	        
	        // by acct number
	        if (searchBy == OperatorAccountSearchBy.ACCOUNT_NUMBER) {
	        	accountIds = operatorAccountSearchDao.getAccountIdsByAccountNumber(searchValue, searchEnergyCompanyIds);
	        }
	        
	        // by phone
	        else if (searchBy == OperatorAccountSearchBy.PHONE_NUMBER) {
	            if (phoneNumberFormattingService.isHasInvalidCharacters(searchValue)) {
	                error = new YukonMessageSourceResolvable("yukon.web.modules.operator.operatorGeneralSearchService.error.invalidPhoneNumber");
	            }else{
	                accountIds = operatorAccountSearchDao.getAccountIdsByPhoneNumber(searchValue, searchEnergyCompanyIds);
	            }
	        }
	        
	        // by last name
	        else if (searchBy == OperatorAccountSearchBy.LAST_NAME) {
	        	accountIds = operatorAccountSearchDao.getAccountIdsByLastName(searchValue, searchEnergyCompanyIds);
	        }
	        
	        // by hardware serial number
	        else if (searchBy == OperatorAccountSearchBy.SERIAL_NUMBER) {
	        	accountIds = operatorAccountSearchDao.getAccountIdsBySerialNumber(searchValue, searchEnergyCompanyIds);
	        }
	        
	        // by map number
	        else if (searchBy == OperatorAccountSearchBy.MAP_NUMBER) {
	        	accountIds = operatorAccountSearchDao.getAccountIdsByMapNumber(searchValue, searchEnergyCompanyIds);
	        }
	        
	        // by address
	        else if (searchBy == OperatorAccountSearchBy.ADDRESS) {
	        	accountIds = operatorAccountSearchDao.getAccountIdsByAddress(searchValue, searchEnergyCompanyIds);
	        }
	        
	        // by alternate tracking number
	        else if (searchBy == OperatorAccountSearchBy.ALT_TRACKING_NUMBER) {
	        	accountIds = operatorAccountSearchDao.getAccountIdsByAltTrackingNumber(searchValue, searchEnergyCompanyIds);
	        }
	        
	        // by company name
			else if (searchBy == OperatorAccountSearchBy.COMPANY) {
				accountIds = operatorAccountSearchDao.getAccountIdsByCompanyName(searchValue, searchEnergyCompanyIds);
			}            
        }
        
        // trim results for paging
        List<Integer> keeperAccountIds = accountIds.subList(startIndex, startIndex + pageCount > accountIds.size() ? accountIds.size() : startIndex + pageCount);
        
        List<AccountSearchResult> accountSearchResultsList = operatorAccountSearchDao.getAccountSearchResultsForAccountIds(keeperAccountIds);
        
        SearchResults<AccountSearchResult> searchResult = new SearchResults<AccountSearchResult>();
        searchResult.setBounds(startIndex, pageCount, accountIds.size());
        searchResult.setResultList((List<AccountSearchResult>) accountSearchResultsList);
        
        if (error == null && accountSearchResultsList.size() == 0) {
        	error = new YukonMessageSourceResolvable("yukon.web.modules.operator.operatorGeneralSearchService.error.noResultsFound");
        }
        
        AccountSearchResultHolder accountSearchResultHolder = new AccountSearchResultHolder(searchBy, searchValue, searchResult, error);
        return accountSearchResultHolder;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setOperatorAccountSearchDao(OperatorAccountSearchDao operatorAccountSearchDao) {
		this.operatorAccountSearchDao = operatorAccountSearchDao;
	}
}
