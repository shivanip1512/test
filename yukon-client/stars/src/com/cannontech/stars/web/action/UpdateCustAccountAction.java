package com.cannontech.stars.web.action;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.xml.soap.SOAPMessage;
import com.cannontech.stars.xml.util.*;
import com.cannontech.database.Transaction;
import com.cannontech.stars.xml.serialize.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class UpdateCustAccountAction extends ActionBase {

    public UpdateCustAccountAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
            StarsCustAccountInfo accountInfo = (StarsCustAccountInfo) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
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

            StarsSiteInformation siteInfo = new StarsSiteInformation();
            siteInfo.setSubstationName( req.getParameter("Substation") );
            siteInfo.setFeeder( req.getParameter("Feeder") );
            siteInfo.setPole( req.getParameter("Pole") );
            siteInfo.setTransformerSize( req.getParameter("TranSize") );
            siteInfo.setServiceVoltage( req.getParameter("ServVolt") );

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
            primContact.setHomePhone( req.getParameter("HomePhone") );
            primContact.setWorkPhone( req.getParameter("WorkPhone") );
            account.setPrimaryContact( primContact );

            StarsUpdateCustomerAccount updateAccount = (StarsUpdateCustomerAccount)
                    StarsCustAccountFactory.newStarsCustAccount(account, StarsUpdateCustomerAccount.class );

            StarsOperation operation = new StarsOperation();
            operation.setStarsUpdateCustomerAccount( updateAccount );

            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();

            StarsUpdateCustomerAccount starsAccount = reqOper.getStarsUpdateCustomerAccount();

            com.cannontech.database.data.starscustomer.CustomerAccount account =
                    (com.cannontech.database.data.starscustomer.CustomerAccount) session.getAttribute("CUSTOMER_ACCOUNT");
            if (account == null) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("Session invalidated, please login again");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            com.cannontech.database.data.starscustomer.CustomerBase customer = account.getCustomerBase();

			/*
			 * Delete the primary contact here, and it will be added later when CustomerBase is being updated
			 * We need to do this because CustomerBase.update will always add the primary contact, not update
			 * it, see CustomerContact.update for the reason
			 */
            com.cannontech.database.db.customer.CustomerContact primContact = customer.getPrimaryContact();
            Transaction transaction = Transaction.createTransaction( Transaction.DELETE, primContact );
            transaction.execute();
            
            StarsCustomerContactFactory.setCustomerContact(primContact, starsAccount.getPrimaryContact());

            Vector contactVct = new Vector();
            for (int i = 0; i < starsAccount.getAdditionalContactCount(); i++) {
                com.cannontech.database.db.customer.CustomerContact contact = new com.cannontech.database.db.customer.CustomerContact();
                StarsCustomerContactFactory.setCustomerContact(contact, starsAccount.getAdditionalContact(i));
                contactVct.addElement( contact );
            }
            customer.setCustomerContactVector( contactVct );
            
            transaction = Transaction.createTransaction( Transaction.UPDATE, customer );
            transaction.execute();
            
            com.cannontech.database.db.starscustomer.CustomerAccount accountDB = account.getCustomerAccount();
            accountDB.setAccountNumber( starsAccount.getAccountNumber() );
            accountDB.setAccountNotes( starsAccount.getAccountNotes() );
            
            com.cannontech.database.db.customer.CustomerAddress billAddr = account.getBillingAddress();
            StarsCustomerAddressFactory.setCustomerAddress( billAddr, starsAccount.getBillingAddress() );
            
            com.cannontech.database.data.starscustomer.AccountSite acctSite = account.getAccountSite();
            com.cannontech.database.db.starscustomer.AccountSite acctSiteDB = acctSite.getAccountSite();
            acctSiteDB.setSiteNumber( starsAccount.getPropertyNumber() );
            acctSiteDB.setPropertyNotes( starsAccount.getPropertyNotes() );
            
            com.cannontech.database.db.customer.CustomerAddress propAddr = acctSite.getStreetAddress();
            StarsCustomerAddressFactory.setCustomerAddress( propAddr, starsAccount.getStreetAddress() );
            
            StarsSiteInformation starsSiteInfo = starsAccount.getStarsSiteInformation();
            com.cannontech.database.data.starscustomer.SiteInformation siteInfo = acctSite.getSiteInformation();
            
            com.cannontech.database.db.starscustomer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
            siteInfoDB.setFeeder( starsSiteInfo.getFeeder() );
            siteInfoDB.setPole( starsSiteInfo.getPole() );
            siteInfoDB.setTransformerSize( starsSiteInfo.getTransformerSize() );
            siteInfoDB.setServiceVoltage( starsSiteInfo.getServiceVoltage() );
            
            com.cannontech.database.db.starscustomer.Substation substation = siteInfo.getSubstation();
            substation.setSubstationName( starsSiteInfo.getSubstationName() );
            
            transaction = Transaction.createTransaction( Transaction.UPDATE, account );
            transaction.execute();

            respOper.setStarsSuccess( null );

            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int parse(SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) return failure.getStatusCode();
			
            if (operation.getStarsSuccess() == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}