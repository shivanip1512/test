package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.stars.LiteAccountSite;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteCustomerAccount;
import com.cannontech.database.data.lite.stars.LiteCustomerContact;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPClient;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.Email;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateCustomerAccount;
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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
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
            propAddr.setCounty( req.getParameter("SCounty") );
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
            if (req.getParameter("CopyAddress") != null) {
	            billAddr.setStreetAddr1( req.getParameter("SAddr1") );
	            billAddr.setStreetAddr2( req.getParameter("SAddr2") );
	            billAddr.setCity( req.getParameter("SCity") );
	            billAddr.setState( req.getParameter("SState") );
	            billAddr.setZip( req.getParameter("SZip") );
	            //billAddr.setCounty( req.getParameter("BCounty") );
            }
            else {
	            billAddr.setStreetAddr1( req.getParameter("BAddr1") );
	            billAddr.setStreetAddr2( req.getParameter("BAddr2") );
	            billAddr.setCity( req.getParameter("BCity") );
	            billAddr.setState( req.getParameter("BState") );
	            billAddr.setZip( req.getParameter("BZip") );
            }
            account.setBillingAddress( billAddr );

            PrimaryContact primContact = new PrimaryContact();
            primContact.setLastName( req.getParameter("LastName") );
            primContact.setFirstName( req.getParameter("FirstName") );
            primContact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone")) );
            primContact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone")) );
            
            Email email = new Email();
            email.setNotification( req.getParameter("Email") );
            email.setEnabled( Boolean.valueOf(req.getParameter("NotifyControl")).booleanValue() );
            primContact.setEmail( email );
            account.setPrimaryContact( primContact );
            
            account.setTimeZone( ServletUtils.getTimeZoneStr(Calendar.getInstance().getTimeZone()) );

            StarsUpdateCustomerAccount updateAccount = (StarsUpdateCustomerAccount)
                    StarsFactory.newStarsCustAccount(account, StarsUpdateCustomerAccount.class );

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

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
            int energyCompanyID = user.getEnergyCompanyID();
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            StarsUpdateCustomerAccount updateAccount = reqOper.getStarsUpdateCustomerAccount();
            
            /* Update customer account */
            LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
            
            LiteAddress liteBillAddr = energyCompany.getAddress( liteAccount.getBillingAddressID() );
            BillingAddress starsBillAddr = updateAccount.getBillingAddress();
            
            if (!StarsLiteFactory.isIdenticalCustomerAddress( liteBillAddr, starsBillAddr )) {
            	com.cannontech.database.db.customer.Address billAddr =
            			(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteBillAddr );
            	StarsFactory.setCustomerAddress( billAddr, starsBillAddr );
            	
            	billAddr = (com.cannontech.database.db.customer.Address)
            			Transaction.createTransaction( Transaction.UPDATE, billAddr ).execute();
            	StarsLiteFactory.setLiteAddress( liteBillAddr, billAddr );
            }
            
            if (!StarsLiteFactory.isIdenticalCustomerAccount( liteAccount, updateAccount )) {
            	com.cannontech.database.db.stars.customer.CustomerAccount account =
            			(com.cannontech.database.db.stars.customer.CustomerAccount) StarsLiteFactory.createDBPersistent( liteAccount );
/*            	account = (com.cannontech.database.db.stars.customer.CustomerAccount)
            			Transaction.createTransaction( Transaction.RETRIEVE, account ).execute();*/
            			
            	StarsFactory.setCustomerAccount( account, updateAccount );
            	account = (com.cannontech.database.db.stars.customer.CustomerAccount)
            			Transaction.createTransaction( Transaction.UPDATE, account ).execute();
            	
            	StarsLiteFactory.setLiteCustomerAccount( liteAccount, account );
            }
    		
    		/* Update customer */
            LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
            com.cannontech.database.db.customer.Customer customer =
            		(com.cannontech.database.db.customer.Customer) StarsLiteFactory.createDBPersistent( liteCustomer );
            
            LiteCustomerContact litePrimContact = energyCompany.getCustomerContact( liteCustomer.getPrimaryContactID() );
            PrimaryContact starsPrimContact = updateAccount.getPrimaryContact();
            
            if (!StarsLiteFactory.isIdenticalCustomerContact( litePrimContact, starsPrimContact )) {
            	com.cannontech.database.data.customer.Contact primContact =
            			(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( litePrimContact );
            	StarsFactory.setCustomerContact( primContact, starsPrimContact );
            	primContact = (com.cannontech.database.data.customer.Contact)
            			Transaction.createTransaction( Transaction.UPDATE, primContact ).execute();
            			
				StarsLiteFactory.setLiteCustomerContact( litePrimContact, primContact );
				//ServerUtils.handleDBChange( litePrimContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
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
		        		liteContact = energyCompany.getCustomerContact( contactID.intValue() );
		        		
		        		if (!StarsLiteFactory.isIdenticalCustomerContact(liteContact, starsContact)) {
			        		// Update the customer contact
			        		com.cannontech.database.data.customer.Contact contact =
			        				(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
			            	StarsFactory.setCustomerContact( contact, starsContact );
			            	contact = (com.cannontech.database.data.customer.Contact)
			            			Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
			            			
							StarsLiteFactory.setLiteCustomerContact( liteContact, contact );
							//ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
		        		}
		        		break;
		        	}
            	}
            	
            	if (liteContact == null) {
            		// Add the new customer contact
            		com.cannontech.database.data.customer.Contact contact = new com.cannontech.database.data.customer.Contact();
		            StarsFactory.setCustomerContact( contact, starsContact );
		            contact = (com.cannontech.database.data.customer.Contact)
		            		Transaction.createTransaction( Transaction.INSERT, contact ).execute();
		            
		            liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( contact );
		            energyCompany.addCustomerContact( liteContact );
            	}
            	
            	newContactList.add( new Integer(liteContact.getContactID()) );
            }
            
            // Remove customer contacts that are not in the update list
            for (int i = 0; i < contactList.size(); i++) {
            	LiteCustomerContact liteContact = energyCompany.getCustomerContact( ((Integer) contactList.get(i)).intValue() );
            	com.cannontech.database.data.customer.Contact contact =
            			(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
	            contact = (com.cannontech.database.data.customer.Contact)
	            		Transaction.createTransaction( Transaction.DELETE, contact ).execute();
            	
            	energyCompany.deleteCustomerContact( liteContact.getContactID() );
            }
            
            liteCustomer.setAdditionalContacts( newContactList );
	        
            if (!StarsLiteFactory.isIdenticalCustomer(liteCustomer, updateAccount)) {
	            int custTypeID = updateAccount.getIsCommercial() ? CustomerTypes.CUSTOMER_CI : CustomerTypes.CUSTOMER_RESIDENTIAL;
	            customer.setCustomerTypeID( new Integer(custTypeID) );
	            if (updateAccount.getTimeZone() != null)
	            	liteCustomer.setTimeZone( updateAccount.getTimeZone() );
	            customer = (com.cannontech.database.db.customer.Customer)
	            		Transaction.createTransaction( Transaction.UPDATE, customer ).execute();
	            
	            liteCustomer.setCustomerTypeID( custTypeID );
	            liteCustomer.setTimeZone( customer.getTimeZone() );
            }
            
            /* Update account site */
            LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
            
            LiteAddress liteStAddr = energyCompany.getAddress( liteAcctSite.getStreetAddressID() );
            StreetAddress starsStAddr = updateAccount.getStreetAddress();
            
            if (!StarsLiteFactory.isIdenticalCustomerAddress( liteStAddr, starsStAddr )) {
            	com.cannontech.database.db.customer.Address stAddr =
            			(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteStAddr );
            	StarsFactory.setCustomerAddress( stAddr, starsStAddr );
            	
            	stAddr = (com.cannontech.database.db.customer.Address)
            			Transaction.createTransaction( Transaction.UPDATE, stAddr ).execute();
            	StarsLiteFactory.setLiteAddress( liteStAddr, stAddr );
            }
            
            if (!StarsLiteFactory.isIdenticalAccountSite( liteAcctSite, updateAccount )) {
            	com.cannontech.database.db.stars.customer.AccountSite acctSite =
            			(com.cannontech.database.db.stars.customer.AccountSite) StarsLiteFactory.createDBPersistent( liteAcctSite );
/*            	acctSite = (com.cannontech.database.db.stars.customer.AccountSite)
            			Transaction.createTransaction( Transaction.RETRIEVE, acctSite ).execute();*/
            	
            	StarsFactory.setAccountSite( acctSite, updateAccount );
            	acctSite = (com.cannontech.database.db.stars.customer.AccountSite)
            			Transaction.createTransaction( Transaction.UPDATE, acctSite ).execute();
            	
            	StarsLiteFactory.setLiteAccountSite( liteAcctSite, acctSite );
            }
            
            /* Update site information */
            LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
            StarsSiteInformation starsSiteInfo = updateAccount.getStarsSiteInformation();
            
            if (!StarsLiteFactory.isIdenticalSiteInformation(liteSiteInfo, starsSiteInfo)) {
            	com.cannontech.database.db.stars.customer.SiteInformation siteInfo =
            			(com.cannontech.database.db.stars.customer.SiteInformation) StarsLiteFactory.createDBPersistent( liteSiteInfo );
/*            	siteInfo = (com.cannontech.database.db.stars.customer.SiteInformation)
            			Transaction.createTransaction( Transaction.RETRIEVE, siteInfo ).execute();*/
            	
            	StarsFactory.setSiteInformation( siteInfo, updateAccount );
            	siteInfo = (com.cannontech.database.db.stars.customer.SiteInformation)
            			Transaction.createTransaction( Transaction.UPDATE, siteInfo ).execute();
            	
            	StarsLiteFactory.setLiteSiteInformation( liteSiteInfo, siteInfo );
            }
            
            ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_UPDATE );
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Customer account updated successfully" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
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
			
			if (operation.getStarsSuccess() == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsUpdateCustomerAccount updateAccount = SOAPUtil.parseSOAPMsgForOperation(reqMsg).getStarsUpdateCustomerAccount();
			accountInfo.setStarsCustomerAccount( (StarsCustomerAccount)
					StarsFactory.newStarsCustAccount(updateAccount, StarsCustomerAccount.class) );

            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}