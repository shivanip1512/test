package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.Pair;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPClient;
import com.cannontech.stars.web.servlet.SOAPServer;
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
            
            // Remember the last search option
			session.setAttribute( ServletUtils.ATT_LAST_SEARCH_OPTION, new Integer(searchBy.getEntryID()) );

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
            
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        	
            StarsSearchCustomerAccount searchAccount = reqOper.getStarsSearchCustomerAccount();
            if (searchAccount.getSearchValue().trim().length() == 0) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The search value cannot be empty") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
			boolean searchMembers = energyCompany.getChildren().size() > 0;
			ArrayList accountList = null;
            
            if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO).getEntryID()) {
            	/* Search by account number */
				accountList = energyCompany.searchAccountByAccountNo( searchAccount.getSearchValue(), searchMembers );
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_PHONE_NO).getEntryID()) {
            	/* Search by phone number */
            	try {
            		String phoneNo = ServletUtils.formatPhoneNumber( searchAccount.getSearchValue() );
					accountList = energyCompany.searchAccountByPhoneNo( phoneNo, searchMembers );
            	}
            	catch (WebClientException e) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
					return SOAPUtil.buildSOAPMessage( respOper );
            	}
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_LAST_NAME).getEntryID()) {
            	/* Search by last name */
				accountList = energyCompany.searchAccountByLastName( searchAccount.getSearchValue(), searchMembers );
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_SERIAL_NO).getEntryID()) {
            	/* Search by hardware serial number */
				accountList = energyCompany.searchAccountBySerialNo( searchAccount.getSearchValue(), searchMembers );
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_MAP_NO).getEntryID()) {
            	/* Search by map number */
            	Object obj = energyCompany.searchAccountByMapNo( searchAccount.getSearchValue(), searchMembers );
            	if (obj != null) {
            		accountList = new ArrayList();
            		accountList.add( obj );
            	} 
            }
            
			StarsSearchCustomerAccountResponse resp = new StarsSearchCustomerAccountResponse();
			
            if (accountList == null || accountList.size() == 0) {
				StarsFailure failure = StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "No customer account matching the search criteria" );
				resp.setStarsFailure( failure );
            }
            else {
				LiteStarsCustAccountInformation liteAcctInfo = null;
            	if (accountList.size() == 1) {
            		if (searchMembers) {
            			if (((Pair)accountList.get(0)).getSecond() == energyCompany)
            				liteAcctInfo = (LiteStarsCustAccountInformation) ((Pair)accountList.get(0)).getFirst();
            		}
            		else
						liteAcctInfo = (LiteStarsCustAccountInformation) accountList.get(0);
            	}
            	
            	if (liteAcctInfo != null) {
					if (liteAcctInfo.hasTwoWayThermostat(energyCompany)) {
						// Get up-to-date thermostat settings
						energyCompany.updateThermostatSettings( liteAcctInfo );
					}
		            
					session.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
		            
					StarsCustAccountInformation starsAcctInfo = null;
					if (SOAPServer.isClientLocal()) {
						starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
						ServletUtils.removeTransientAttributes( session );
						session.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
					}
					else	
						starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, energyCompany, true );
					resp.setStarsCustAccountInformation( starsAcctInfo );
            	}
				else {
					for (int i = 0; i < accountList.size(); i++) {
						StarsBriefCustAccountInfo starsAcctInfo = new StarsBriefCustAccountInfo();
						if (searchMembers) {
							starsAcctInfo.setAccountID( ((LiteStarsCustAccountInformation) ((Pair)accountList.get(i)).getFirst()).getAccountID() );
							starsAcctInfo.setEnergyCompanyID( ((LiteStarsEnergyCompany) ((Pair)accountList.get(i)).getSecond()).getLiteID() );
						}
						else
							starsAcctInfo.setAccountID( ((LiteStarsCustAccountInformation)accountList.get(i)).getAccountID() );
						
						resp.addStarsBriefCustAccountInfo( starsAcctInfo );
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

			if (resp.getStarsCustAccountInformation() != null) {
				if (!SOAPClient.isServerLocal())
					session.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, resp.getStarsCustAccountInformation());
			}
            else {
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