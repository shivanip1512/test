package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;
import java.util.*;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsCustomerAddressFactory;
import com.cannontech.stars.xml.StarsCustomerContactFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.message.dispatch.message.DBChangeMsg;

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
            account.setTimeZone( ServletUtils.getTimeZoneStr(Calendar.getInstance().getTimeZone()) );

            StreetAddress propAddr = new StreetAddress();
            propAddr.setStreetAddr1( req.getParameter("SAddr1") );
            propAddr.setStreetAddr2( req.getParameter("SAddr2") );
            propAddr.setCity( req.getParameter("SCity") );
            propAddr.setState( req.getParameter("SState") );
            propAddr.setZip( req.getParameter("SZip") );
            propAddr.setCounty( req.getParameter("SCounty") );
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
            primContact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone")) );
            primContact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone")) );
            
            Email email = new Email();
            email.setNotification( req.getParameter("Email") );
            email.setEnabled( Boolean.valueOf(req.getParameter("NotifyControl")).booleanValue() );
            primContact.setEmail( email );
            account.setPrimaryContact( primContact );
            
	        for (int i = 2; i <= 4; i++) {
	            String lastName = req.getParameter("LastName" + i);
	            String firstName = req.getParameter("FirstName" + i);
	
	            if (lastName != null && lastName.trim().length() > 0
	                && firstName != null && firstName.trim().length() > 0) {
	                AdditionalContact contact = new AdditionalContact();
	                contact.setLastName( lastName );
	                contact.setFirstName( firstName );
	                contact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone" + i)) );
	                contact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone" + i)) );
	                account.addAdditionalContact( contact );
	            }
	        }

			newAccount.setStarsCustomerAccount( account );

            StarsOperation operation = new StarsOperation();
            operation.setStarsNewCustomerAccount( newAccount );

            return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			e.printStackTrace();
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );

            StarsNewCustomerAccount newAccount = reqOper.getStarsNewCustomerAccount();
            StarsCustomerAccount starsAccount = newAccount.getStarsCustomerAccount();

			MultiDBPersistent multiDB = new MultiDBPersistent();
			
			/* Begin create CustomerAccount */
            com.cannontech.database.data.stars.customer.CustomerAccount account =
                    new com.cannontech.database.data.stars.customer.CustomerAccount();
            com.cannontech.database.db.stars.customer.CustomerAccount accountDB = account.getCustomerAccount();
            
            /*- Begin create Customer -*/
            com.cannontech.database.data.customer.Contact primContact = new com.cannontech.database.data.customer.Contact();
            StarsCustomerContactFactory.setCustomerContact( primContact, starsAccount.getPrimaryContact() );
            multiDB.getDBPersistentVector().add( primContact );
            
            ArrayList addContacts = new ArrayList();
            for (int i = 0; i < starsAccount.getAdditionalContactCount(); i++) {
            	com.cannontech.database.data.customer.Contact contact =
            			new com.cannontech.database.data.customer.Contact();
	            StarsCustomerContactFactory.setCustomerContact( contact, starsAccount.getAdditionalContact(i) );
	            multiDB.getDBPersistentVector().add( contact );
	            addContacts.add( contact );
            }
            
            com.cannontech.database.data.customer.Customer customer =
            		new com.cannontech.database.data.customer.Customer();
            com.cannontech.database.db.customer.Customer customerDB = customer.getCustomer();
            
            customerDB.setCustomerTypeID( new Integer(CustomerTypes.CUSTOMER_RESIDENTIAL) );
            if (starsAccount.getTimeZone() != null)
            	customerDB.setTimeZone( starsAccount.getTimeZone() );
            else
            	customerDB.setTimeZone( "(none)" );		// Should use the default setting of the energy company
            /*- End create Customer -*/
            
            com.cannontech.database.db.customer.Address billAddr = account.getBillingAddress();
            StarsCustomerAddressFactory.setCustomerAddress( billAddr, starsAccount.getBillingAddress() );
            
            /*- Begin create AccountSite -*/
            com.cannontech.database.data.stars.customer.AccountSite acctSite = account.getAccountSite();
            com.cannontech.database.db.stars.customer.AccountSite acctSiteDB = acctSite.getAccountSite();
            
            com.cannontech.database.db.customer.Address propAddr = acctSite.getStreetAddress();
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

            accountDB.setAccountNumber( starsAccount.getAccountNumber() );
            accountDB.setAccountNotes( starsAccount.getAccountNotes() );
            account.setCustomer( customer );
            account.setEnergyCompanyID( new Integer(user.getEnergyCompanyID()) );
            /* End create CustomerAccount */
            
            multiDB.getDBPersistentVector().add( account );
    		Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
            
            // Add lite contact objects
            LiteCustomerContact liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( primContact );
            energyCompany.addCustomerContact( liteContact );
            for (int i = 0; i < addContacts.size(); i++) {
            	liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( (com.cannontech.database.data.customer.Contact) addContacts.get(i) );
            	energyCompany.addCustomerContact( liteContact );
            }
            	
			LiteStarsCustAccountInformation liteAcctInfo = energyCompany.addCustAccountInformation( account );
            user.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
            
            ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_ADD );

			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Customer account created successfully" );
			respOper.setStarsSuccess( success );
			
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the customer account") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
            }
        }

        return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation respOper = SOAPUtil.parseSOAPMsgForOperation( respMsg );
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsFailure failure = respOper.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSuccess success = respOper.getStarsSuccess();
			if (success == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
				
			StarsCustAccountInformation accountInfo = new StarsCustAccountInformation();
			accountInfo.setStarsCustomerAccount( reqOper.getStarsNewCustomerAccount().getStarsCustomerAccount() );
			
			accountInfo.setStarsLMPrograms( new StarsLMPrograms() );
			accountInfo.setStarsAppliances( new StarsAppliances() );
			accountInfo.setStarsInventories( new StarsInventories() );
			accountInfo.setStarsServiceCompanies( new StarsServiceCompanies() );
			accountInfo.setStarsCallReportHistory( new StarsCallReportHistory() );
			accountInfo.setStarsServiceRequestHistory( new StarsServiceRequestHistory() );
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, accountInfo );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
