package com.cannontech.stars.web.action;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsCustomerAddressFactory;
import com.cannontech.stars.xml.StarsCustomerContactFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccountResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.serialize.StarsLogin;
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

			com.cannontech.stars.xml.serialize.Substation starsSub = new com.cannontech.stars.xml.serialize.Substation();
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
/*			
			newAccount.setStarsLMProgramSignUps( new StarsLMProgramSignUps() );
			
	        String userName = req.getParameter("UserName");
	        if (userName != null && userName.length() > 0) {
		        StarsLogin login = new StarsLogin();
		        login.setUsername( req.getParameter("UserName") );
		        login.setPassword( req.getParameter("Password") );
				newAccount.setStarsLogin( login );
	        }
*/
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

            com.cannontech.stars.web.StarsOperator operator = (com.cannontech.stars.web.StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            Integer energyCompanyID = new Integer( (int)operator.getEnergyCompanyID() );
        	Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
            
            com.cannontech.database.data.company.EnergyCompanyBase energyCompany =
            		new com.cannontech.database.data.company.EnergyCompanyBase();
            energyCompany.setEnergyCompanyID( energyCompanyID );            
            
            energyCompany = (com.cannontech.database.data.company.EnergyCompanyBase)
            		Transaction.createTransaction( Transaction.RETRIEVE, energyCompany ).execute();

            StarsNewCustomerAccount newAccount = reqOper.getStarsNewCustomerAccount();
            StarsCustomerAccount starsAccount = newAccount.getStarsCustomerAccount();

			/* Begin create CustomerAccount */
            com.cannontech.database.data.stars.customer.CustomerAccount account =
                    new com.cannontech.database.data.stars.customer.CustomerAccount();
            com.cannontech.database.db.stars.customer.CustomerAccount accountDB = account.getCustomerAccount();
            
            /*- Begin create Customer -*/
            com.cannontech.database.data.stars.customer.CustomerBase customer =
            		new com.cannontech.database.data.stars.customer.CustomerBase();
            com.cannontech.database.db.stars.customer.CustomerBase customerDB = customer.getCustomerBase();
            
            com.cannontech.database.db.customer.CustomerContact primContact = customer.getPrimaryContact();
            StarsCustomerContactFactory.setCustomerContact( primContact, starsAccount.getPrimaryContact() );
            
            Vector contactVct = customer.getCustomerContactVector();
            for (int i = 0; i < starsAccount.getAdditionalContactCount(); i++) {
            	com.cannontech.database.db.customer.CustomerContact contact =
            			new com.cannontech.database.db.customer.CustomerContact();
	            StarsCustomerContactFactory.setCustomerContact( contact, starsAccount.getAdditionalContact(i) );
	            contactVct.addElement( contact );
            }
            
            int custTypeID = 0;
        	LiteCustomerSelectionList custTypeList = (LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_CUSTOMERTYPE);
        	if (starsAccount.getIsCommercial())
        		custTypeID = StarsCustListEntryFactory.getStarsCustListEntry(
        				custTypeList, com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_CUSTTYPE_COMM ).getEntryID();
        	else
        		custTypeID = StarsCustListEntryFactory.getStarsCustListEntry(
        				custTypeList, com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_CUSTTYPE_RES ).getEntryID();
            customerDB.setCustomerTypeID( new Integer(custTypeID) );
            
            customerDB.setTimeZone( "(none)" );	// How to set the proper time zone???
            customerDB.setPaoID( new Integer(0) );
            /*- End create Customer -*/
            
            com.cannontech.database.db.customer.CustomerAddress billAddr = account.getBillingAddress();
            StarsCustomerAddressFactory.setCustomerAddress( billAddr, starsAccount.getBillingAddress() );
            
            /*- Begin create AccountSite -*/
            com.cannontech.database.data.stars.customer.AccountSite acctSite = account.getAccountSite();
            com.cannontech.database.db.stars.customer.AccountSite acctSiteDB = acctSite.getAccountSite();
            
            com.cannontech.database.db.customer.CustomerAddress propAddr = acctSite.getStreetAddress();
            StarsCustomerAddressFactory.setCustomerAddress( propAddr, starsAccount.getStreetAddress() );

			/*-- Begin create SiteInformation --*/
			com.cannontech.database.data.stars.customer.SiteInformation siteInfo = acctSite.getSiteInformation();
            com.cannontech.database.db.stars.customer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
            StarsSiteInformation starsSiteInfo = starsAccount.getStarsSiteInformation();
            
            siteInfoDB.setSubstationID( new Integer(starsSiteInfo.getSubstation().getEntryID()) );
            siteInfoDB.setFeeder( starsSiteInfo.getFeeder() );
            siteInfoDB.setPole( starsSiteInfo.getPole() );
            siteInfoDB.setTransformerSize( starsSiteInfo.getTransformerSize() );
            siteInfoDB.setServiceVoltage( starsSiteInfo.getServiceVoltage() );
            /*-- End create SiteInformation --*/
            
            acctSiteDB.setSiteNumber( starsAccount.getPropertyNumber() );
            acctSiteDB.setPropertyNotes( starsAccount.getPropertyNotes() );
            /*- End create AccountSite -*/
/*            
            com.cannontech.database.db.customer.CustomerLogin login = account.getCustomerLogin();
            StarsLogin starsLogin = newAccount.getStarsLogin();
            if (starsLogin != null) {
	            login.setUserName( starsLogin.getUsername() );
	            login.setUserPassword( starsLogin.getPassword() );
	            login.setLoginType( "STARS" );		// Add "STARS" type to CustomerLogin db class later
            }
            else {
            	login.setUserName( starsAccount.getAccountNumber() );
            	login.setUserPassword( "" );
	            login.setLoginType( "STARS" );
            }
            
            accountDB.setAccountNumber( starsAccount.getAccountNumber() );
            accountDB.setAccountNotes( starsAccount.getAccountNotes() );
*/
            account.setCustomerBase( customer );
            account.setEnergyCompanyID( energyCompanyID );
            //account.setCustomerLogin( login );
            /* End create CustomerAccount */
            
            account = (com.cannontech.database.data.stars.customer.CustomerAccount)
            		Transaction.createTransaction( Transaction.INSERT, account ).execute();
            
			LiteStarsCustAccountInformation liteAcctInfo = SOAPServer.addCustAccountInformation( energyCompanyID, account );
            
            operator.setAttribute( "CUSTOMER_ACCOUNT_INFORMATION", liteAcctInfo );
            StarsCustAccountInformation starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, energyCompanyID );

            StarsNewCustomerAccountResponse resp = new StarsNewCustomerAccountResponse();
            resp.setStarsCustAccountInformation( starsAcctInfo );
            
            respOper.setStarsNewCustomerAccountResponse( resp );
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
			
			StarsNewCustomerAccountResponse resp = operation.getStarsNewCustomerAccountResponse();
			if (resp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			operator.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION", resp.getStarsCustAccountInformation() );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
