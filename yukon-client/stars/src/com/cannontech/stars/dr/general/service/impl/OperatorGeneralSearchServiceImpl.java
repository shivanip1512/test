package com.cannontech.stars.dr.general.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.model.Address;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.stars.dr.general.service.AccountSearchResultHolder;
import com.cannontech.stars.dr.general.service.OperatorGeneralSearchService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.xml.serialize.StarsBriefCustAccountInfo;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class OperatorGeneralSearchServiceImpl implements OperatorGeneralSearchService {

	private RolePropertyDao rolePropertyDao;
	private StarsCustAccountInformationDao starsCustAccountInformationDao;
	private ContactDao contactDao;
	private AddressDao addressDao;
	private ContactNotificationDao contactNotificationDao;
	
	@Override
	@SuppressWarnings("unchecked")
	public AccountSearchResultHolder customerAccountSearch(OperatorAccountSearchBy searchBy, 
														   String searchValue,
														   int startIndex,
														   int count,
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
        
        List<Object> accountList = Lists.newArrayList();
        List<StarsBriefCustAccountInfo> briefAccountList = Lists.newArrayList();
        List<LiteStarsCustAccountInformation> accounts = Lists.newArrayList();
        
        if (error == null) {
       
	        boolean adminManageMembers = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, userContext.getYukonUser());
			boolean searchMembers = adminManageMembers && energyCompany.hasChildEnergyCompanies();
	        
	        // by acct number
	        if (searchBy == OperatorAccountSearchBy.ACCOUNT_NUMBER) {
				accountList = energyCompany.searchAccountByAccountNumber(searchValue, searchMembers, true);
	        }
	        
	        // by phone
	        else if (searchBy == OperatorAccountSearchBy.PHONE_NUMBER) {
	        	
	        	try {
		    		String phoneNo = ServletUtils.formatPhoneNumberForSearch(searchValue);
					accountList = energyCompany.searchAccountByPhoneNo(phoneNo, searchMembers);
	        	} catch (WebClientException e) {
	        		error = new YukonMessageSourceResolvable("yukon.web.modules.operator.operatorGeneralSearchService.error.invalidPhoneNumber");
	        	}
	        }
	        
	        // by last name
	        else if (searchBy == OperatorAccountSearchBy.LAST_NAME) {
				accountList = energyCompany.searchAccountByLastName(searchValue, searchMembers, true);
	        }
	        
	        // by hardware serial number
	        else if (searchBy == OperatorAccountSearchBy.SERIAL_NUMBER) {
				accountList = energyCompany.searchAccountBySerialNo(searchValue, searchMembers);
	        }
	        
	        // by map number
	        else if (searchBy == OperatorAccountSearchBy.MAP_NUMBER) {
	        	accountList = energyCompany.searchAccountByMapNo(searchValue, searchMembers);
	        }
	        
	        // by address
	        else if (searchBy == OperatorAccountSearchBy.ADDRESS) {
	        	accountList = energyCompany.searchAccountByAddress(searchValue, searchMembers, true);
	        }
	        
	        // by alternate tracking number
	        else if (searchBy == OperatorAccountSearchBy.ALT_TRACING_NUMBER) {
	        	accountList = energyCompany.searchAccountByAltTrackNo(searchValue, searchMembers);
	        }
	        
	        // by company name
			else if (searchBy == OperatorAccountSearchBy.COMPANY) {
				accountList = energyCompany.searchAccountByCompanyName(searchValue, searchMembers);
			}            
			            
			
	        if (accountList.size() > 0) {
	        	
	        	// find account id
	        	Integer accountId = null;
	        	if (accountList.size() == 1) {
	        		if (searchMembers) {
	        			
	        			LiteStarsEnergyCompany foundEnergyCompany = (LiteStarsEnergyCompany)((Pair<Object, Object>)accountList.get(0)).getSecond();
	        			if (foundEnergyCompany == energyCompany) {
	        				accountId = (Integer)((Pair<Object, Object>)accountList.get(0)).getFirst();
	        			}
	        		}
	        		else {
						accountId = (Integer)accountList.get(0);
	        		}
	        	}
	        	
	        	// liteAcctInfo will only be loaded if exactly 1 account was found in search.
	            if (accountId != null) {    
	        		
	            	LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getById(accountId.intValue(), energyCompany.getEnergyCompanyID());
	        		
	            	// Get up-to-date thermostat settings
					if (liteAcctInfo.hasTwoWayThermostat(energyCompany)) {
						energyCompany.updateThermostatSettings(liteAcctInfo);
					}
					
					accounts.add(liteAcctInfo);
	        	}
	            
	            // by last name
	            else if (searchBy == OperatorAccountSearchBy.LAST_NAME) {
	                
	            	// Don't sort, the sort by has already been handled in the CustomerAccount.searchByPrimaryContactLastName query.
	                for (int i = 0; i < accountList.size(); i++) {
	                    
	                	StarsBriefCustAccountInfo briefAcctInfo = new StarsBriefCustAccountInfo();
	                    Integer accountId2 = (Integer) (searchMembers ? ((Pair)accountList.get(i)).getFirst() : accountList.get(i));
	                    
	                    briefAcctInfo.setAccountID(accountId2);
	                    LiteStarsEnergyCompany company = energyCompany;
	                    if (searchMembers) company = (LiteStarsEnergyCompany) ((Pair)accountList.get(i)).getSecond();
	                    if (searchMembers) briefAcctInfo.setEnergyCompanyID(company.getLiteID());
	                 
	                    briefAccountList.add(briefAcctInfo);
	                }
	            }                
				else {
					
					// Order the search result by company name/search criteria
					// (last name if search by last name, address if search by address, account # otherwise)
					Map<String, LiteStarsEnergyCompany> companyNameMap = new TreeMap<String, LiteStarsEnergyCompany>();
					Map<LiteStarsEnergyCompany, List<Integer>> companyAcctTable = new Hashtable<LiteStarsEnergyCompany, List<Integer>>();
					
					for (int i = 0; i < accountList.size(); i++) {
						LiteStarsEnergyCompany company = energyCompany;
						if (searchMembers) company = (LiteStarsEnergyCompany) ((Pair)accountList.get(i)).getSecond();
						
						List<Integer> acctList = companyAcctTable.get(company);
						if (acctList == null) {
							acctList = new ArrayList<Integer>();
							companyAcctTable.put(company, acctList);
							
							String companyName = (company == energyCompany)? "" : company.getName();
							companyNameMap.put(companyName, company);
						}
	
						Integer accountId2 = (Integer) (searchMembers ? ((Pair) accountList.get(i)).getFirst() : accountList.get(i));
						acctList.add(accountId2);
					}
					
					// make briefAcctInfos
					Collection<LiteStarsEnergyCompany> energyCompanies = companyNameMap.values();
					for (LiteStarsEnergyCompany company : energyCompanies) {
						
						List<Integer> accountIdsList = companyAcctTable.get(company);
						
						Integer[] accountIds = new Integer[accountIdsList.size()];
						accountIdsList.toArray(accountIds);
						
						for (int i = 0; i < accountIds.length; i++) {
							
							StarsBriefCustAccountInfo briefAcctInfo = new StarsBriefCustAccountInfo();
							briefAcctInfo.setAccountID(accountIds[i]);
							if (searchMembers) {
								briefAcctInfo.setEnergyCompanyID(company.getLiteID());
							}
							
							briefAccountList.add(briefAcctInfo);
						}
					}
				}
	        }
        }
        
        // turn briefAcctInfos into liteAcctInfos
        for (StarsBriefCustAccountInfo briefAcctInfo : briefAccountList) {
        	
        	LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getById(briefAcctInfo.getAccountID(), energyCompany.getEnergyCompanyID());
        	if (liteAcctInfo != null) {
        		accounts.add(liteAcctInfo);
        	}
        }
        
        // build SearchResult<AccountSearchResult>
        List<AccountSearchResult> accountSearchResultsList = Lists.newArrayList();
        for (LiteStarsCustAccountInformation account : accounts) {
        	
        	AccountSearchResult accountSearchResult = createSearchResult(account);
        	accountSearchResultsList.add(accountSearchResult);
        }
        
        // trim results for paging
        List<AccountSearchResult> keeperAccountSearchResultsList = Lists.newArrayListWithExpectedSize(count);
        for (int i = 0; i < accountSearchResultsList.size(); i++) {
        	
        	if (i >= startIndex && keeperAccountSearchResultsList.size() < count) {
        		
        		keeperAccountSearchResultsList.add(accountSearchResultsList.get(i));
        	}
        }
        
        SearchResult<AccountSearchResult> searchResult = new SearchResult<AccountSearchResult>();
        searchResult.setBounds(startIndex, count, accountSearchResultsList.size());
        searchResult.setResultList((List<AccountSearchResult>) keeperAccountSearchResultsList);
        
        if (error == null && keeperAccountSearchResultsList.size() == 0) {
        	error = new YukonMessageSourceResolvable("yukon.web.modules.operator.operatorGeneralSearchService.error.noResultsFound");
        }
        
        AccountSearchResultHolder accountSearchResultHolder = new AccountSearchResultHolder(searchBy, searchValue, searchResult, error);
        return accountSearchResultHolder;
	}
	
	private AccountSearchResult createSearchResult(LiteStarsCustAccountInformation account) {
		
		// ids
    	int accountId = account.getAccountID();
    	int energyCompanyId = account.getCustomer().getEnergyCompanyID();
    	String accountNumber = account.getCustomerAccount().getAccountNumber();

    	// name
    	int primaryContactId = account.getCustomer().getPrimaryContactID();
    	LiteContact primaryContact = contactDao.getContact(primaryContactId);
    	String firstName = primaryContact.getContFirstName();
    	String lastName = primaryContact.getContLastName();
    	LiteCustomer customer = account.getCustomer();
    	boolean isCiCustomer = (customer instanceof LiteCICustomer) && customer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI;
    	String company = null;
    	if (isCiCustomer) {
    		company = ((LiteCICustomer)customer).getCompanyName();
    	}
    	
    	String name = "";
    	name += lastName + ", ";
    	name += firstName;
    	if (isCiCustomer) {
    		name += "(" + company + ")";
    	}
    	
    	// phone
    	LiteContactNotification homePhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
        LiteContactNotification workPhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
    	
    	// address
    	Address address = null;
    	int addressId = account.getAccountSite().getStreetAddressID();
    	LiteAddress liteAddress = addressDao.getByAddressId(addressId);
    	if (liteAddress != null) {
    		address = new Address(liteAddress);
    	}
    	
    	AccountSearchResult accountSearchResult = new AccountSearchResult(accountId, energyCompanyId, accountNumber, name, homePhoneNotif, workPhoneNotif, address);
    	
    	return accountSearchResult;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setStarsCustAccountInformationDao(StarsCustAccountInformationDao starsCustAccountInformationDao) {
		this.starsCustAccountInformationDao = starsCustAccountInformationDao;
	}
	
	@Autowired
	public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}
	
	@Autowired
	public void setAddressDao(AddressDao addressDao) {
		this.addressDao = addressDao;
	}
	
	@Autowired
	public void setContactNotificationDao(ContactNotificationDao contactNotificationDao) {
		this.contactNotificationDao = contactNotificationDao;
	}
	
}
