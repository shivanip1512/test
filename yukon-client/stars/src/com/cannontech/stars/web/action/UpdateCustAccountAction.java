package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;
import java.util.*;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustAccountFactory;
import com.cannontech.stars.xml.StarsCustomerAddressFactory;
import com.cannontech.stars.xml.StarsCustomerContactFactory;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsUpdateCustomerAccountResponse;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
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

public class UpdateCustAccountAction implements ActionBase {

    public UpdateCustAccountAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
			StarsCustAccountInformation accountInfo = null;
			if (user != null)
				accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null) return null;

            StarsCustomerAccount account = accountInfo.getStarsCustomerAccount();

            account.setAccountNumber( req.getParameter("AcctNo") );
            account.setIsCommercial( Boolean.valueOf(req.getParameter("Commercial")).booleanValue() );
            account.setCompany( req.getParameter("Company") );
            account.setAccountNotes( req.getParameter("AcctNotes") );

            account.setPropertyNumber( req.getParameter("PropNo") );
            account.setPropertyNotes( req.getParameter("PropNotes") );

            StreetAddress propAddr = new StreetAddress();
            propAddr.setStreetAddr1( req.getParameter("SAddr1") );
            propAddr.setStreetAddr2( req.getParameter("SAddr2") );
            propAddr.setCity( req.getParameter("SCity") );
            propAddr.setState( req.getParameter("SState") );
            propAddr.setZip( req.getParameter("SZip") );
            account.setStreetAddress( propAddr );

			Substation starsSub = new Substation();
			starsSub.setEntryID( Integer.parseInt(req.getParameter("Substation")) );
			
            StarsSiteInformation siteInfo = new StarsSiteInformation();
            siteInfo.setSubstation( starsSub );
            siteInfo.setFeeder( req.getParameter("Feeder") );
            siteInfo.setPole( req.getParameter("Pole") );
            siteInfo.setTransformerSize( req.getParameter("TranSize") );
            siteInfo.setServiceVoltage( req.getParameter("ServVolt") );
            account.setStarsSiteInformation( siteInfo );

            BillingAddress billAddr = new BillingAddress();
            billAddr.setStreetAddr1( req.getParameter("BAddr1") );
            billAddr.setStreetAddr2( req.getParameter("BAddr2") );
            billAddr.setCity( req.getParameter("BCity") );
            billAddr.setState( req.getParameter("BState") );
            billAddr.setZip( req.getParameter("BZip") );
            account.setBillingAddress( billAddr );

            PrimaryContact primContact = new PrimaryContact();
            primContact.setLastName( req.getParameter("LastName") );
            primContact.setFirstName( req.getParameter("FirstName") );
            primContact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone")) );
            primContact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone")) );
            account.setPrimaryContact( primContact );
            
            account.setTimeZone( ServletUtils.getTimeZoneStr(TimeZone.getDefault()) );

            StarsUpdateCustomerAccount updateAccount = (StarsUpdateCustomerAccount)
                    StarsCustAccountFactory.newStarsCustAccount(account, StarsUpdateCustomerAccount.class );

            StarsOperation operation = new StarsOperation();
            operation.setStarsUpdateCustomerAccount( updateAccount );

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

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            int energyCompanyID = user.getEnergyCompanyID();
            StarsUpdateCustomerAccount updateAccount = reqOper.getStarsUpdateCustomerAccount();
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
            /* Update customer account */
            LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
            
            LiteCustomerAddress liteBillAddr = SOAPServer.getCustomerAddress( energyCompanyID, liteAccount.getBillingAddressID() );
            BillingAddress starsBillAddr = updateAccount.getBillingAddress();
            
            if (!StarsLiteFactory.isIdenticalCustomerAddress( liteBillAddr, starsBillAddr )) {
            	com.cannontech.database.db.customer.CustomerAddress billAddr =
            			(com.cannontech.database.db.customer.CustomerAddress) StarsLiteFactory.createDBPersistent( liteBillAddr );
            	StarsCustomerAddressFactory.setCustomerAddress( billAddr, starsBillAddr );
            	
            	billAddr = (com.cannontech.database.db.customer.CustomerAddress)
            			Transaction.createTransaction( Transaction.UPDATE, billAddr ).execute();
            	StarsLiteFactory.setLiteCustomerAddress( liteBillAddr, billAddr );
            }
            
            if (!StarsLiteFactory.isIdentical( liteAccount, updateAccount )) {
            	com.cannontech.database.db.stars.customer.CustomerAccount account =
            			(com.cannontech.database.db.stars.customer.CustomerAccount) StarsLiteFactory.createDBPersistent( liteAccount );
/*            	account = (com.cannontech.database.db.stars.customer.CustomerAccount)
            			Transaction.createTransaction( Transaction.RETRIEVE, account ).execute();*/
            			
            	StarsCustAccountFactory.setCustomerAccount( account, updateAccount );
            	account = (com.cannontech.database.db.stars.customer.CustomerAccount)
            			Transaction.createTransaction( Transaction.UPDATE, account ).execute();
            	
            	StarsLiteFactory.setLiteCustomerAccount( liteAccount, account );
            }
    		
    		/* Update customer */
            LiteCustomerBase liteCustomer = liteAcctInfo.getCustomerBase();
            com.cannontech.database.db.stars.customer.CustomerBase customer =
            		(com.cannontech.database.db.stars.customer.CustomerBase) StarsLiteFactory.createDBPersistent( liteCustomer );
            
            LiteCustomerContact litePrimContact = SOAPServer.getCustomerContact( energyCompanyID, liteCustomer.getPrimaryContactID() );
            PrimaryContact starsPrimContact = updateAccount.getPrimaryContact();
            
            if (!StarsLiteFactory.isIdenticalCustomerContact( litePrimContact, starsPrimContact )) {
            	com.cannontech.database.db.customer.CustomerContact primContact =
            			(com.cannontech.database.db.customer.CustomerContact) StarsLiteFactory.createDBPersistent( litePrimContact );
/*            	primContact = (com.cannontech.database.db.customer.CustomerContact)
            			Transaction.createTransaction( Transaction.RETRIEVE, primContact ).execute();*/
            	StarsCustomerContactFactory.setCustomerContact( primContact, starsPrimContact );
            	
            	com.cannontech.database.db.stars.CustomerContact contactWrapper = new com.cannontech.database.db.stars.CustomerContact(primContact);
            	contactWrapper = (com.cannontech.database.db.stars.CustomerContact)
            			Transaction.createTransaction( Transaction.UPDATE, contactWrapper ).execute();
            	primContact = contactWrapper.getCustomerContact();
            			
				StarsLiteFactory.setLiteCustomerContact( litePrimContact, primContact );
            	//SOAPServer.updateCustomerContact( litePrimContact );
            }

			ArrayList contactList = liteCustomer.getAdditionalContacts();
            ArrayList newContactList = new ArrayList();
            
            for (int i = 0; i < updateAccount.getAdditionalContactCount(); i++) {
            	AdditionalContact starsContact = updateAccount.getAdditionalContact(i);
            	LiteCustomerContact liteContact = null;
				
            	for (int j = 0; j < contactList.size(); j++) {
		        	Integer contactID = (Integer) contactList.get(j);
		        	if (contactID.intValue() == starsContact.getContactID()) {
		        		contactList.remove(j);
		        		liteContact = SOAPServer.getCustomerContact( energyCompanyID, contactID.intValue() );
		        		
		        		if (!StarsLiteFactory.isIdentical(liteContact, starsContact)) {
			        		// Update the customer contact
			        		com.cannontech.database.db.customer.CustomerContact contact =
			        				(com.cannontech.database.db.customer.CustomerContact) StarsLiteFactory.createDBPersistent( liteContact );
/*			        		contact = (com.cannontech.database.db.customer.CustomerContact)
	            					Transaction.createTransaction( Transaction.RETRIEVE, contact ).execute();*/
			            	StarsCustomerContactFactory.setCustomerContact( contact, starsContact );
			            	
			            	com.cannontech.database.db.stars.CustomerContact contactWrapper = new com.cannontech.database.db.stars.CustomerContact(contact);
			            	contactWrapper = (com.cannontech.database.db.stars.CustomerContact)
			            			Transaction.createTransaction( Transaction.UPDATE, contactWrapper ).execute();
			            	contact = contactWrapper.getCustomerContact();
	            			
							StarsLiteFactory.setLiteCustomerContact( liteContact, contact );
			        		//SOAPServer.updateCustomerContact( liteContact );
		        		}
		        		break;
		        	}
            	}
            	
            	if (liteContact == null) {
            		// Add the new customer contact
            		com.cannontech.database.db.customer.CustomerContact contact = new com.cannontech.database.db.customer.CustomerContact();
		            StarsCustomerContactFactory.setCustomerContact( contact, starsContact );
		            
		            com.cannontech.database.db.stars.CustomerContact contactWrapper = new com.cannontech.database.db.stars.CustomerContact( contact, customer );
		            contactWrapper = (com.cannontech.database.db.stars.CustomerContact)
		            		Transaction.createTransaction( Transaction.INSERT, contactWrapper ).execute();
		            contact = contactWrapper.getCustomerContact();
		            
		            liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( contact );
		            SOAPServer.addCustomerContact( energyCompanyID, liteContact );
            	}
            	
            	newContactList.add( new Integer(liteContact.getContactID()) );
            }
            
            // Remove customer contacts that are not in the update list
            for (int i = 0; i < contactList.size(); i++) {
            	LiteCustomerContact liteContact = SOAPServer.getCustomerContact( energyCompanyID, ((Integer) contactList.get(i)).intValue() );
            	com.cannontech.database.db.customer.CustomerContact contact =
            			(com.cannontech.database.db.customer.CustomerContact) StarsLiteFactory.createDBPersistent( liteContact );
	            
	            com.cannontech.database.db.stars.CustomerContact contactWrapper = new com.cannontech.database.db.stars.CustomerContact( contact, customer );
	            contactWrapper = (com.cannontech.database.db.stars.CustomerContact)
	            		Transaction.createTransaction( Transaction.DELETE, contactWrapper ).execute();
	            contact = contactWrapper.getCustomerContact();
            	
            	SOAPServer.deleteCustomerContact( energyCompanyID, liteContact );
            }
            
            liteCustomer.setAdditionalContacts( newContactList );
            
            Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
            LiteCustomerSelectionList custTypeList = (LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_CUSTOMERTYPE );
            
            int custTypeID = 0;
            if (updateAccount.getIsCommercial())
            	custTypeID = StarsCustListEntryFactory.getStarsCustListEntry(
            			custTypeList, com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_CUSTTYPE_COMM).getEntryID();
            else
            	custTypeID = StarsCustListEntryFactory.getStarsCustListEntry(
            			custTypeList, com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_CUSTTYPE_RES).getEntryID();
            			
            if (liteCustomer.getCustomerTypeID() != custTypeID) {
/*	           customer = (com.cannontech.database.db.stars.customer.CustomerBase)
	            		Transaction.createTransaction( Transaction.RETRIEVE, customer ).execute();*/
	            
	            customer.setCustomerTypeID( new Integer(custTypeID) );
	            customer = (com.cannontech.database.db.stars.customer.CustomerBase)
	            		Transaction.createTransaction( Transaction.UPDATE, customer ).execute();
	            
	            liteCustomer.setCustomerTypeID( customer.getCustomerTypeID().intValue() );
            }
            
            /* Update account site */
            LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
            
            LiteCustomerAddress liteStAddr = SOAPServer.getCustomerAddress( energyCompanyID, liteAcctSite.getStreetAddressID() );
            StreetAddress starsStAddr = updateAccount.getStreetAddress();
            
            if (!StarsLiteFactory.isIdenticalCustomerAddress( liteStAddr, starsStAddr )) {
            	com.cannontech.database.db.customer.CustomerAddress stAddr =
            			(com.cannontech.database.db.customer.CustomerAddress) StarsLiteFactory.createDBPersistent( liteStAddr );
            	StarsCustomerAddressFactory.setCustomerAddress( stAddr, starsStAddr );
            	
            	stAddr = (com.cannontech.database.db.customer.CustomerAddress)
            			Transaction.createTransaction( Transaction.UPDATE, stAddr ).execute();
            	StarsLiteFactory.setLiteCustomerAddress( liteStAddr, stAddr );
            }
            
            if (!StarsLiteFactory.isIdentical( liteAcctSite, updateAccount )) {
            	com.cannontech.database.db.stars.customer.AccountSite acctSite =
            			(com.cannontech.database.db.stars.customer.AccountSite) StarsLiteFactory.createDBPersistent( liteAcctSite );
/*            	acctSite = (com.cannontech.database.db.stars.customer.AccountSite)
            			Transaction.createTransaction( Transaction.RETRIEVE, acctSite ).execute();*/
            	
            	StarsCustAccountFactory.setAccountSite( acctSite, updateAccount );
            	acctSite = (com.cannontech.database.db.stars.customer.AccountSite)
            			Transaction.createTransaction( Transaction.UPDATE, acctSite ).execute();
            	
            	StarsLiteFactory.setLiteAccountSite( liteAcctSite, acctSite );
            }
            
            /* Update site information */
            LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
            StarsSiteInformation starsSiteInfo = updateAccount.getStarsSiteInformation();
            
            if (!StarsLiteFactory.isIdentical(liteSiteInfo, starsSiteInfo)) {
            	com.cannontech.database.db.stars.customer.SiteInformation siteInfo =
            			(com.cannontech.database.db.stars.customer.SiteInformation) StarsLiteFactory.createDBPersistent( liteSiteInfo );
/*            	siteInfo = (com.cannontech.database.db.stars.customer.SiteInformation)
            			Transaction.createTransaction( Transaction.RETRIEVE, siteInfo ).execute();*/
            	
            	StarsCustAccountFactory.setSiteInformation( siteInfo, updateAccount );
            	siteInfo = (com.cannontech.database.db.stars.customer.SiteInformation)
            			Transaction.createTransaction( Transaction.UPDATE, siteInfo ).execute();
            	
            	StarsLiteFactory.setLiteSiteInformation( liteSiteInfo, siteInfo );
            }

			StarsCustAccountInformation starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, energyCompanyID, true );
			StarsUpdateCustomerAccountResponse resp = new StarsUpdateCustomerAccountResponse();
			resp.setStarsCustAccountInformation( starsAcctInfo );

            respOper.setStarsUpdateCustomerAccountResponse( resp  );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the customer account information") );
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
			
			StarsUpdateCustomerAccountResponse resp = operation.getStarsUpdateCustomerAccountResponse();
			if (resp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
			user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, resp.getStarsCustAccountInformation());
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}