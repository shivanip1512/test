package com.cannontech.stars.web.action;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.db.stars.customer.*;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustAccountInformationFactory;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.SearchBy;
import com.cannontech.stars.xml.serialize.StarsCustAccountBrief;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
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
            StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) return null;
            
            // Remove the "transient"(account-related) attributes
            Enumeration enum = (operator == null) ? session.getAttributeNames() : operator.getAttributeNames();
            while (enum.hasMoreElements()) {
            	String attName = (String) enum.nextElement();
            	if (attName.startsWith( ServletUtils.TRANSIENT_ATT_LEADING ))
        			operator.removeAttribute(attName);
            }

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

            StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            int energyCompanyID = (int) operator.getEnergyCompanyID();
            Hashtable selectionLists = com.cannontech.stars.web.servlet.SOAPServer.getAllSelectionLists( energyCompanyID );

            StarsSearchCustomerAccount searchAccount = reqOper.getStarsSearchCustomerAccount();
            LiteStarsCustAccountInformation liteAcctInfo = null;
            CustomerAccount[] accounts = null;
            		
            if (searchAccount.getSearchBy().getEntryID() == StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SEARCHBY),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_SEARCHBY_ACCTNO ).getEntryID()) {
            	/* Search by account number */
            	liteAcctInfo = SOAPServer.searchByAccountNumber(
            			energyCompanyID, searchAccount.getSearchValue() );
            }
            else if (searchAccount.getSearchBy().getEntryID() == StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SEARCHBY),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_SEARCHBY_PHONENO ).getEntryID()) {
            	/* Search by phone number */
            	accounts = CustomerAccount.searchByPhoneNumber( new Integer(energyCompanyID), ServletUtils.formatPhoneNumber(searchAccount.getSearchValue()) );
            	if (accounts != null && accounts.length == 1)
            		liteAcctInfo = SOAPServer.getCustAccountInformation( energyCompanyID, accounts[0].getAccountID().intValue(), true );
            }
            else if (searchAccount.getSearchBy().getEntryID() == StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SEARCHBY),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_SEARCHBY_LASTNAME ).getEntryID()) {
            	/* Search by last name */
            	accounts = CustomerAccount.searchByLastName( new Integer(energyCompanyID), searchAccount.getSearchValue() );
            	if (accounts != null && accounts.length == 1)
            		liteAcctInfo = SOAPServer.getCustAccountInformation( energyCompanyID, accounts[0].getAccountID().intValue(), true );
            }
            else if (searchAccount.getSearchBy().getEntryID() == StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SEARCHBY),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_SEARCHBY_SERIALNO ).getEntryID()) {
            	/* Search by hardware serial number */
            	accounts = CustomerAccount.searchBySerialNumber( new Integer(energyCompanyID), searchAccount.getSearchValue() );
            	if (accounts != null && accounts.length == 1)
            		liteAcctInfo = SOAPServer.getCustAccountInformation( energyCompanyID, accounts[0].getAccountID().intValue(), true );
            }

			StarsSearchCustomerAccountResponse resp = new StarsSearchCustomerAccountResponse();
			
            if (liteAcctInfo != null) {
	            operator.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
	            StarsCustAccountInformation starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, energyCompanyID, true );
				resp.setStarsCustAccountInformation( starsAcctInfo );
            }
            else if (accounts != null && accounts.length > 0) {
				for (int i = 0; i < accounts.length; i++) {
					StarsCustAccountBrief acctBrief = new StarsCustAccountBrief();
					
					LiteCustomerContact contact = null;
					LiteCustomerAddress addr = null;
					LiteStarsCustAccountInformation acctInfo =
							SOAPServer.getCustAccountInformation( energyCompanyID, accounts[i].getAccountID().intValue(), false );
							
					if (acctInfo != null) {
						acctBrief.setAccountID( acctInfo.getCustomerAccount().getAccountID() );
						acctBrief.setAccountNumber( acctInfo.getCustomerAccount().getAccountNumber() );
						contact = SOAPServer.getCustomerContact( energyCompanyID, acctInfo.getCustomerBase().getPrimaryContactID() );
						addr = SOAPServer.getCustomerAddress( energyCompanyID, acctInfo.getAccountSite().getStreetAddressID() );
					}
					else {
						acctBrief.setAccountID( accounts[i].getAccountID().intValue() );
						acctBrief.setAccountNumber( accounts[i].getAccountNumber() );
						
						CustomerBase customer = new CustomerBase();
						customer.setCustomerID( accounts[i].getCustomerID() );
						customer = (CustomerBase) Transaction.createTransaction( Transaction.RETRIEVE, customer ).execute();
						contact = SOAPServer.getCustomerContact( energyCompanyID, customer.getPrimaryContactID().intValue() );
						
						AccountSite site = new AccountSite();
						site.setAccountSiteID( accounts[i].getAccountSiteID() );
						site = (AccountSite) Transaction.createTransaction( Transaction.RETRIEVE, site ).execute();
						addr = SOAPServer.getCustomerAddress( energyCompanyID, site.getStreetAddressID().intValue() );
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
            	StarsFailure failure = StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "No customer account matching the search criteria" );
            	resp.setStarsFailure( failure );
            }
            
            respOper.setStarsSearchCustomerAccountResponse( resp );
            
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			if (resp.getStarsCustAccountInformation() != null)
            	operator.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, resp.getStarsCustAccountInformation());
            else {
            	/* The return value is a list of results */
            	operator.setAttribute( ServletUtils.ATT_ACCOUNT_SEARCH_RESULTS, resp );
            	session.setAttribute( ServletUtils.ATT_REDIRECT, "/OperatorDemos/Consumer/SearchResults.jsp" );
            }
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}