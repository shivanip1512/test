package com.cannontech.stars.web.action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPClient;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.SearchBy;
import com.cannontech.stars.xml.serialize.StarsCustAccountBrief;
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
            ServletUtils.removeTransientAttributes( user );

            StarsSearchCustomerAccount searchAccount = new StarsSearchCustomerAccount();
            SearchBy searchBy = new SearchBy();
            searchBy.setEntryID( Integer.parseInt(req.getParameter("SearchBy")) );
            searchAccount.setSearchBy( searchBy );
            searchAccount.setSearchValue( req.getParameter("SearchValue") );
            
            // Remember the last search option
            user.setAttribute( ServletUtils.ATT_LAST_SEARCH_OPTION, new Integer(searchBy.getEntryID()) );

            StarsOperation operation = new StarsOperation();
            operation.setStarsSearchCustomerAccount( searchAccount );

            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
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
            
            int energyCompanyID = user.getEnergyCompanyID();
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );

            StarsSearchCustomerAccount searchAccount = reqOper.getStarsSearchCustomerAccount();
            if (searchAccount.getSearchValue().trim().length() == 0) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The search value cannot be empty") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            LiteStarsCustAccountInformation liteAcctInfo = null;
            int[] accountIDs = null;
            		
            if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO).getEntryID()) {
            	/* Search by account number */
            	if (searchAccount.getSearchValue().indexOf( '*' ) == -1) {
	            	liteAcctInfo = energyCompany.searchByAccountNumber( searchAccount.getSearchValue() );
            	}
	            else {
	            	accountIDs = CustomerAccount.searchByAccountNumber( new Integer(energyCompanyID), searchAccount.getSearchValue().replace('*','%') );
	            	if (accountIDs != null && accountIDs.length == 1)
	            		liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[0], true );
	            }
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_PHONE_NO).getEntryID()) {
            	/* Search by phone number */
            	String phoneNo = null;
            	try {
            		phoneNo = ServletUtils.formatPhoneNumber( searchAccount.getSearchValue() );
            	}
            	catch (ServletException se) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, se.getMessage()) );
					return SOAPUtil.buildSOAPMessage( respOper );
            	}
            	
				accountIDs = CustomerAccount.searchByPhoneNumber( new Integer(energyCompanyID), phoneNo );
            	if (accountIDs != null && accountIDs.length == 1)
            		liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[0], true );
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_LAST_NAME).getEntryID()) {
            	/* Search by last name */
            	accountIDs = CustomerAccount.searchByLastName( new Integer(energyCompanyID), searchAccount.getSearchValue() );
            	if (accountIDs != null && accountIDs.length == 1)
            		liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[0], true );
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_SERIAL_NO).getEntryID()) {
            	/* Search by hardware serial number */
            	accountIDs = CustomerAccount.searchBySerialNumber( searchAccount.getSearchValue(), energyCompanyID );
            	if (accountIDs != null && accountIDs.length == 1)
            		liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[0], true );
            }

			StarsSearchCustomerAccountResponse resp = new StarsSearchCustomerAccountResponse();
			
            if (liteAcctInfo != null) {
	            liteAcctInfo.setLastLoginTime( System.currentTimeMillis() );	// Update the last login time
	            
	            if (liteAcctInfo.hasTwoWayThermostat(energyCompany)) {
					// Get up-to-date thermostat settings and register the account
					energyCompany.updateThermostatSettings( liteAcctInfo );
					
		            java.util.ArrayList accountList = energyCompany.getAccountsWithGatewayEndDevice();
		            synchronized (accountList) {
		            	if (!accountList.contains( liteAcctInfo )) accountList.add( liteAcctInfo );
		            }
	            }
	            
	            user.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
	            
	            StarsCustAccountInformation starsAcctInfo = null;
	            if (SOAPServer.isClientLocal()) {
	            	starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
					ServletUtils.removeTransientAttributes( user );
	            	user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
	            }
	            else	
	            	starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, energyCompany, true );
				resp.setStarsCustAccountInformation( starsAcctInfo );
            }
            else if (accountIDs != null && accountIDs.length > 0) {
				for (int i = 0; i < accountIDs.length; i++) {
					LiteStarsCustAccountInformation acctInfo = energyCompany.getBriefCustAccountInfo( accountIDs[i], true );
					if (acctInfo != null) {
						LiteContact contact = energyCompany.getContact( acctInfo.getCustomer().getPrimaryContactID(), acctInfo );
						LiteAddress addr = energyCompany.getAddress( acctInfo.getAccountSite().getStreetAddressID() );
						
						StarsCustAccountBrief acctBrief = new StarsCustAccountBrief();
						acctBrief.setAccountID( accountIDs[i] );
						acctBrief.setAccountNumber( acctInfo.getCustomerAccount().getAccountNumber() );
						acctBrief.setContactName( contact.getContLastName() + ", " + contact.getContFirstName() );
						
						String homePhone = ServerUtils.getNotification(
								ContactFuncs.getContactNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE) );
						String workPhone = ServerUtils.getNotification(
								ContactFuncs.getContactNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE) );
						
						StringBuffer phoneNo = new StringBuffer();
						if (homePhone.length() > 0)
							phoneNo.append( homePhone ).append( "(H)" );
						if (workPhone.length() > 0) {
							if (phoneNo.length() > 0) phoneNo.append( ", " );
							phoneNo.append( workPhone ).append( "(W)" );
						}
						if (phoneNo.length() == 0) phoneNo.append( "(none)" );
						acctBrief.setContPhoneNumber( phoneNo.toString() );
						
						StringBuffer address = new StringBuffer();
						if (addr.getLocationAddress1().trim().length() > 0)
							address.append( addr.getLocationAddress1() );
						if (addr.getLocationAddress2().trim().length() > 0) {
							if (address.length() > 0) address.append( ", " );
							address.append( addr.getLocationAddress2() );
						}
						if (addr.getCityName().trim().length() > 0) {
							if (address.length() > 0) address.append( ", " );
							address.append( addr.getCityName() );
						}
						if (addr.getStateCode().trim().length() > 0) {
							if (address.length() > 0) address.append( ", " );
							address.append( addr.getStateCode() ).append( " " ).append( addr.getZipCode() );
						}
						if (address.length() == 0) address.append( "(none)" );
						acctBrief.setStreetAddress( address.toString() );
						
						resp.addStarsCustAccountBrief( acctBrief );
					}
				}
            }
            else {
            	StarsFailure failure = StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "No customer account matching the search criteria" );
            	resp.setStarsFailure( failure );
            }
            
            respOper.setStarsSearchCustomerAccountResponse( resp );
            
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot complete the search for customer account") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
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

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (resp.getStarsCustAccountInformation() != null) {
				if (!SOAPClient.isServerLocal())
            		user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, resp.getStarsCustAccountInformation());
			}
            else {
            	/* No customer account, or more than one account found */
            	user.setAttribute( ServletUtils.ATT_ACCOUNT_SEARCH_RESULTS, resp );
            	return StarsConstants.FAILURE_CODE_OPERATION_FAILED;
            }
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}