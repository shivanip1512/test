package com.cannontech.stars.web.action;

import java.util.Calendar;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsCustomerAddressFactory;
import com.cannontech.stars.xml.serialize.StarsCustomerContactFactory;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: NewCustAccountAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: 07/26/2002 11:10:58 AM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */

public class NewCustAccountAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsNewCustomerAccount newAccount = new StarsNewCustomerAccount();
			StarsCustomerAccount account = new StarsCustomerAccount();
			
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
            
	        for (int i = 2; i <= 4; i++) {
	            String lastName = req.getParameter("LastName" + i);
	            String firstName = req.getParameter("FirstName" + i);
	
	            if (lastName != null && lastName.trim().length() > 0
	                && firstName != null && firstName.trim().length() > 0) {
	                AdditionalContact contact = new AdditionalContact();
	                contact.setLastName( lastName );
	                contact.setFirstName( firstName );
	                contact.setHomePhone( req.getParameter("HomePhone" + i) );
	                contact.setWorkPhone( req.getParameter("WorkPhone" + i) );
	                account.addAdditionalContact( contact );
	            }
	        }

			newAccount.setStarsCustomerAccount( account );
			newAccount.setStarsLMPrograms( new StarsLMPrograms() );

            StarsOperation operation = new StarsOperation();
            operation.setStarsNewCustomerAccount( newAccount );

            return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();

            /* This part is for consumer side, must be removed later */
            Integer energyCompanyID = (Integer) session.getAttribute("ENERGY_COMPANY_ID");
            com.cannontech.database.data.web.Operator operator = null;

            if (energyCompanyID == null) {
                operator = (com.cannontech.database.data.web.Operator) session.getAttribute("OPERATOR");
                if (operator == null) {
                    StarsFailure failure = new StarsFailure();
                    failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                    failure.setDescription("Session invalidated, please login again");
                    respOper.setStarsFailure( failure );
                    return SOAPUtil.buildSOAPMessage( respOper );
                }

                energyCompanyID = new Integer( (int)operator.getEnergyCompanyID() );
            }

            StarsNewCustomerAccount newAccount = reqOper.getStarsNewCustomerAccount();
            StarsCustomerAccount starsAccount = newAccount.getStarsCustomerAccount();

            com.cannontech.database.data.starscustomer.CustomerAccount account =
                    new com.cannontech.database.data.starscustomer.CustomerAccount();
            com.cannontech.database.db.starscustomer.CustomerAccount accountDB = account.getCustomerAccount();
            
            com.cannontech.database.data.starscustomer.CustomerBase customer =
            		new com.cannontech.database.data.starscustomer.CustomerBase();
            com.cannontech.database.db.starscustomer.CustomerBase customerDB = customer.getCustomerBase();
            
            com.cannontech.database.db.customer.CustomerContact primContact = customer.getPrimaryContact();
            primContact.setContactID( primContact.getNextContactID() );
            StarsCustomerContactFactory.setCustomerContact( primContact, starsAccount.getPrimaryContact() );
            
            Vector contactVct = customer.getCustomerContactVector();
            for (int i = 0; i < starsAccount.getAdditionalContactCount(); i++) {
            	com.cannontech.database.db.customer.CustomerContact contact =
            			new com.cannontech.database.db.customer.CustomerContact();
	            StarsCustomerContactFactory.setCustomerContact( contact, starsAccount.getAdditionalContact(i) );
	            contactVct.addElement( contact );
            }
            
            customer.getEnergyCompanyBase().getEnergyCompany().setEnergyCompanyID( energyCompanyID );
            customerDB.setCustomerID( customerDB.getNextCustomerID() );
            customerDB.setPrimaryContactID( primContact.getContactID() );
            customerDB.setCustomerType( "Residential" );
            customerDB.setTimeZone( Calendar.getInstance().getTimeZone().getID() );
            customerDB.setPaoID( new Integer(0) );

            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, customer );
            transaction.execute();
            
            com.cannontech.database.db.customer.CustomerAddress billAddr = account.getBillingAddress();
            billAddr.setAddressID( billAddr.getNextAddressID() );
            StarsCustomerAddressFactory.setCustomerAddress( billAddr, starsAccount.getBillingAddress() );
            
            com.cannontech.database.data.starscustomer.AccountSite acctSite = account.getAccountSite();
            com.cannontech.database.db.starscustomer.AccountSite acctSiteDB = acctSite.getAccountSite();
            
            com.cannontech.database.db.customer.CustomerAddress propAddr = acctSite.getStreetAddress();
            propAddr.setAddressID( propAddr.getNextAddressID() );
            StarsCustomerAddressFactory.setCustomerAddress( propAddr, starsAccount.getStreetAddress() );

			com.cannontech.database.data.starscustomer.SiteInformation siteInfo = acctSite.getSiteInformation();            
            com.cannontech.database.db.starscustomer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
            StarsSiteInformation starsSiteInfo = starsAccount.getStarsSiteInformation();
            
            siteInfoDB.setSiteID( siteInfoDB.getNextSiteID() );
            siteInfoDB.setSubstationID( Integer.valueOf(starsSiteInfo.getSubstationName()) );
            siteInfoDB.setFeeder( starsSiteInfo.getFeeder() );
            siteInfoDB.setPole( starsSiteInfo.getPole() );
            siteInfoDB.setTransformerSize( starsSiteInfo.getTransformerSize() );
            siteInfoDB.setServiceVoltage( starsSiteInfo.getServiceVoltage() );
            
            transaction = Transaction.createTransaction( Transaction.INSERT, siteInfo );
            transaction.execute();
            
            acctSiteDB.setAccountSiteID( acctSiteDB.getNextAccountSiteID() );
            acctSiteDB.setSiteInformationID( siteInfoDB.getSiteID() );
            acctSiteDB.setSiteNumber( starsAccount.getPropertyNumber() );
            acctSiteDB.setStreetAddressID( propAddr.getAddressID() );
            acctSiteDB.setPropertyNotes( starsAccount.getPropertyNotes() );
            
            acctSite.setStreetAddress( propAddr );
            
            transaction = Transaction.createTransaction( Transaction.INSERT, acctSite );
            transaction.execute();
            
            com.cannontech.database.data.company.EnergyCompanyBase company =
            		new com.cannontech.database.data.company.EnergyCompanyBase();
            company.setEnergyCompanyID( energyCompanyID );
            
            transaction = Transaction.createTransaction( Transaction.RETRIEVE, company );
            transaction.execute();
            
            accountDB.setAccountID( accountDB.getNextAccountID() );
            accountDB.setAccountSiteID( acctSiteDB.getAccountSiteID() );
            accountDB.setAccountNumber( starsAccount.getAccountNumber() );
            accountDB.setCustomerID( customerDB.getCustomerID() );
            accountDB.setBillingAddressID( billAddr.getAddressID() );
            accountDB.setAccountNotes( starsAccount.getAccountNotes() );
            
            account.setBillingAddress( billAddr );
            account.setCustomerBase( customer );
            account.setEnergyCompanyBase( company );
            
            transaction = Transaction.createTransaction( Transaction.INSERT, account );
            transaction.execute();

            StarsSuccess success = new StarsSuccess();
            respOper.setStarsSuccess( success );

            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
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
