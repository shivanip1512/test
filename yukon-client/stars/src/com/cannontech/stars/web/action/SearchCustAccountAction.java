package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteCustomerContact;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.customer.AccountSite;
import com.cannontech.database.db.stars.customer.CustomerAccount;
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
            if (searchAccount.getSearchValue().length() == 0) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The search value cannot be empty") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            LiteStarsCustAccountInformation liteAcctInfo = null;
            CustomerAccount[] accounts = null;
            		
            if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO).getEntryID()) {
            	/* Search by account number */
            	liteAcctInfo = energyCompany.searchByAccountNumber( searchAccount.getSearchValue() );
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_PHONE_NO).getEntryID()) {
            	/* Search by phone number */
            	accounts = CustomerAccount.searchByPhoneNumber( new Integer(energyCompanyID), ServletUtils.formatPhoneNumber(searchAccount.getSearchValue()) );
            	if (accounts != null && accounts.length == 1)
            		liteAcctInfo = energyCompany.getCustAccountInformation( accounts[0].getAccountID().intValue(), true );
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_LAST_NAME).getEntryID()) {
            	/* Search by last name */
            	accounts = CustomerAccount.searchByLastName( new Integer(energyCompanyID), searchAccount.getSearchValue() );
            	if (accounts != null && accounts.length == 1)
            		liteAcctInfo = energyCompany.getCustAccountInformation( accounts[0].getAccountID().intValue(), true );
            }
            else if (searchAccount.getSearchBy().getEntryID() == energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_SERIAL_NO).getEntryID()) {
            	/* Search by hardware serial number */
            	accounts = CustomerAccount.searchBySerialNumber( new Integer(energyCompanyID), searchAccount.getSearchValue() );
            	if (accounts != null && accounts.length == 1)
            		liteAcctInfo = energyCompany.getCustAccountInformation( accounts[0].getAccountID().intValue(), true );
            }

			StarsSearchCustomerAccountResponse resp = new StarsSearchCustomerAccountResponse();
			
            if (liteAcctInfo != null) {
	            user.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
	            
	            StarsCustAccountInformation starsAcctInfo = null;
	            if (SOAPServer.isClientLocal()) {
	            	starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
	            	user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
	            }
	            else	
	            	starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, energyCompanyID, true );
				resp.setStarsCustAccountInformation( starsAcctInfo );
            }
            else if (accounts != null && accounts.length > 0) {
				for (int i = 0; i < accounts.length; i++) {
					StarsCustAccountBrief acctBrief = new StarsCustAccountBrief();
					
					LiteCustomerContact contact = null;
					LiteAddress addr = null;
					LiteStarsCustAccountInformation acctInfo = energyCompany.getCustAccountInformation( accounts[i].getAccountID().intValue(), false );
							
					if (acctInfo != null) {
						acctBrief.setAccountID( acctInfo.getCustomerAccount().getAccountID() );
						acctBrief.setAccountNumber( acctInfo.getCustomerAccount().getAccountNumber() );
						contact = energyCompany.getCustomerContact( acctInfo.getCustomer().getPrimaryContactID() );
						addr = energyCompany.getAddress( acctInfo.getAccountSite().getStreetAddressID() );
					}
					else {
						acctBrief.setAccountID( accounts[i].getAccountID().intValue() );
						acctBrief.setAccountNumber( accounts[i].getAccountNumber() );
						
						com.cannontech.database.db.customer.Customer customer = new com.cannontech.database.db.customer.Customer();
						customer.setCustomerID( accounts[i].getCustomerID() );
						customer = (com.cannontech.database.db.customer.Customer) Transaction.createTransaction( Transaction.RETRIEVE, customer ).execute();
						contact = energyCompany.getCustomerContact( customer.getPrimaryContactID().intValue() );
						
						AccountSite site = new AccountSite();
						site.setAccountSiteID( accounts[i].getAccountSiteID() );
						site = (AccountSite) Transaction.createTransaction( Transaction.RETRIEVE, site ).execute();
						addr = energyCompany.getAddress( site.getStreetAddressID().intValue() );
					}
					
					acctBrief.setContactName( contact.getLastName() + ", " + contact.getFirstName() );
					StringBuffer phoneNo = new StringBuffer( contact.getHomePhone() );
					if (contact.getWorkPhone().length() > 0)
						phoneNo.append( ", " ).append( contact.getWorkPhone() );
					acctBrief.setContPhoneNumber( phoneNo.toString() );
					
					StringBuffer address = new StringBuffer( addr.getLocationAddress1() );
					if (addr.getLocationAddress2().length() > 0)
						address.append( ", " ).append( addr.getLocationAddress2() );
					address.append( ", " ).append( addr.getCityName() )
						   .append( ", " ).append( addr.getStateCode() )
						   .append( " " ).append( addr.getZipCode() );
					acctBrief.setStreetAddress( address.toString() );
					
					resp.addStarsCustAccountBrief( acctBrief );
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
            	/* The return value is a list of results */
            	user.setAttribute( ServletUtils.ATT_ACCOUNT_SEARCH_RESULTS, resp );
            	session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/SearchResults.jsp" );
            }
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}