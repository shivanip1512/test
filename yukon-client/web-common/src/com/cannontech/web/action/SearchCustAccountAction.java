package com.cannontech.web.action;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.SearchBy;
import com.cannontech.stars.xml.serialize.StarsBriefCustAccountInfo;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SearchCustAccountAction implements ActionBase {
    
    public SearchCustAccountAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) return null;
            
            // Remove the "transient"(account-related) attributes
            ServletUtils.removeTransientAttributes( session );
            
            // Remove the previous search result
			session.removeAttribute( ServletUtils.ATT_ACCOUNT_SEARCH_RESULTS );

            StarsSearchCustomerAccount searchAccount = new StarsSearchCustomerAccount();
            SearchBy searchBy = new SearchBy();
            searchBy.setEntryID( Integer.parseInt(req.getParameter("SearchBy")) );
            searchAccount.setSearchBy( searchBy );
            searchAccount.setSearchValue( req.getParameter("SearchValue") );
            searchAccount.setPartialMatch( (req.getParameter("ExactMatch") == null)?true:false);
            
            // Remember the last search option
			session.setAttribute( ServletUtils.ATT_LAST_ACCOUNT_SEARCH_OPTION, new Integer(searchBy.getEntryID()) );
            session.setAttribute( ServletUtils.ATT_LAST_ACCOUNT_SEARCH_VALUE, new String(searchAccount.getSearchValue()) );
            
            StarsOperation operation = new StarsOperation();
            operation.setStarsSearchCustomerAccount( searchAccount );

            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }

        return null;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        	
            StarsSearchCustomerAccount searchAccount = reqOper.getStarsSearchCustomerAccount();
            int searchByDefID = DaoFactory.getYukonListDao().getYukonListEntry( searchAccount.getSearchBy().getEntryID() ).getYukonDefID();

            //Require at least 2 characters for all searches except accountnumber.
            if (!(searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO) && searchAccount.getSearchValue().trim().length() < 2) {
               respOper.setStarsFailure( StarsFactory.newStarsFailure( StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Please enter at least the first 2 characters to search.") );
               return SOAPUtil.buildSOAPMessage( respOper );
            }
            //Require at least something for all other searches
            if (searchAccount.getSearchValue().trim().length() == 0) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The search value cannot be empty") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
           
			boolean searchMembers = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
					&& (energyCompany.hasChildEnergyCompanies());
            List<Object> accountList = null;
            
            if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO) {
            	/* Search by account number */
				accountList = energyCompany.searchAccountByAccountNumber( searchAccount.getSearchValue(), searchMembers, true );
            }
            else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_PHONE_NO) {
            	/* Search by phone number */
            	try {
            		String phoneNo = ServletUtils.formatPhoneNumberForSearch( searchAccount.getSearchValue() );
					accountList = energyCompany.searchAccountByPhoneNo( phoneNo, searchMembers );
            	}
            	catch (WebClientException e) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
					return SOAPUtil.buildSOAPMessage( respOper );
            	}
            }
            else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_LAST_NAME) {
            	/* Search by last name */
				accountList = energyCompany.searchAccountByLastName( searchAccount.getSearchValue(), searchMembers, searchAccount.getPartialMatch());
            }
            else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_SERIAL_NO) {
            	/* Search by hardware serial number */
				accountList = energyCompany.searchAccountBySerialNo( searchAccount.getSearchValue(), searchMembers );
            }
            else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_MAP_NO) {
            	/* Search by map number */
            	accountList = energyCompany.searchAccountByMapNo( searchAccount.getSearchValue(), searchMembers );
            }
            else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ADDRESS) {
            	/* Search by address */
            	accountList = energyCompany.searchAccountByAddress( searchAccount.getSearchValue(), searchMembers, true );
            }
            else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ALT_TRACK_NO) {
            	/* Search by alternate tracking number */
            	accountList = energyCompany.searchAccountByAltTrackNo( searchAccount.getSearchValue(), searchMembers );
            }
			else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_COMPANY_NAME) {
				/* Search by company name (commercial customers) */
				accountList = energyCompany.searchAccountByCompanyName( searchAccount.getSearchValue(), searchMembers );
			}            
			            
			StarsSearchCustomerAccountResponse resp = new StarsSearchCustomerAccountResponse();
			
            if (accountList == null || accountList.size() == 0) {
				StarsFailure failure = StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "No customer account matching the search criteria" );
				resp.setStarsFailure( failure );
            }
            else {
            	Integer accountId = null;
            	if (accountList.size() == 1) {
            		if (searchMembers) {
            			if (((Pair)accountList.get(0)).getSecond() == energyCompany)
            				accountId = (Integer) ((Pair)accountList.get(0)).getFirst();
            		}
            		else
						accountId = (Integer) accountList.get(0);
            	}
            	
                if (accountId != null) {    //liteAcctInfo will only be loaded if exactly 1 account was found in search.
            		LiteAccountInfo liteAcctInfo = energyCompany.getCustAccountInformation( accountId, true );
            		
					session.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
		            
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
					ServletUtils.removeTransientAttributes( session );
					session.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
					
					resp.setStarsCustAccountInformation( starsAcctInfo );
            	}
                else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_LAST_NAME) {
                    //Don't sort, the sort by has already been handled in the CustomerAccount.searchByPrimaryContactLastName query.
                    for (int i = 0; i < accountList.size(); i++) {
                        StarsBriefCustAccountInfo starsAcctInfo = new StarsBriefCustAccountInfo();
                        Integer accountId2 = (Integer) (searchMembers ? ((Pair)accountList.get(i)).getFirst() : accountList.get(i));
                        
                        starsAcctInfo.setAccountID( accountId2);
                        LiteStarsEnergyCompany company = energyCompany;
                        if (searchMembers) company = (LiteStarsEnergyCompany) ((Pair)accountList.get(i)).getSecond();
                        if (searchMembers) starsAcctInfo.setEnergyCompanyID( company.getLiteID() );
                        
                        resp.addStarsBriefCustAccountInfo( starsAcctInfo );
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
						
						List<Integer> acctList = companyAcctTable.get( company );
						if (acctList == null) {
							acctList = new ArrayList<Integer>();
							companyAcctTable.put( company, acctList );
							
							String companyName = (company == energyCompany)? "" : company.getName();
							companyNameMap.put( companyName, company );
						}

						Integer accountId2 = (Integer) (searchMembers ? ((Pair) accountList.get(i)).getFirst() : accountList.get(i));
						acctList.add( accountId2);
					}
					
					Iterator it = companyNameMap.values().iterator();
					while (it.hasNext()) {
						LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) it.next();
						List<Integer> acctList = companyAcctTable.get( company );
						
						Integer[] accounts = new Integer[acctList.size()];
						acctList.toArray( accounts );
						
						if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_LAST_NAME) {
						    //Do not sort the lastnames!  This is now handled in the LiteStarsEnergyCompany.searchByPrimaryContactLastName query!!!
//							Arrays.sort( accounts, LAST_NAME_CMP );    
						}
						else if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ADDRESS) {
                            //Do not sort the addresses!  This is now handled in the LiteStarsEnergyCompany.searchByStreetAddress query!!!
							/*Pair[] pairs = new Pair[ accounts.length ];
							for (int i = 0; i < accounts.length; i++)
								pairs[i] = new Pair( accounts[i], company );
							Arrays.sort( pairs, ADDRESS_CMP );
							for (int i = 0; i < accounts.length; i++)
								accounts[i] = (LiteStarsCustAccountInformation) pairs[i].getFirst();*/
						}
						
						for (int i = 0; i < accounts.length; i++) {
							StarsBriefCustAccountInfo starsAcctInfo = new StarsBriefCustAccountInfo();
							starsAcctInfo.setAccountID( accounts[i] );
							if (searchMembers) starsAcctInfo.setEnergyCompanyID( company.getLiteID() );
							
							resp.addStarsBriefCustAccountInfo( starsAcctInfo );
						}
					}
				}
            }
            
            respOper.setStarsSearchCustomerAccountResponse( resp );
            
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to search for customer account") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	CTILogger.error( e2.getMessage(), e2 );
            }
        }

        return null;
    }

    public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSearchCustomerAccountResponse resp = operation.getStarsSearchCustomerAccountResponse();
            if (resp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

            if (resp.getStarsCustAccountInformation() == null  || resp.getStarsBriefCustAccountInfoCount() > 1) {
            	/* No customer account, or more than one account found */
				session.setAttribute( ServletUtils.ATT_ACCOUNT_SEARCH_RESULTS, resp );
            	return StarsConstants.FAILURE_CODE_OPERATION_FAILED;
            }
            
            return 0;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}