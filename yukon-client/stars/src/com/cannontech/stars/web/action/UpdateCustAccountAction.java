package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteAccountSite;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteCustomerAccount;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.ContactNotification;
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

	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );

			StarsUpdateCustomerAccount updateAccount = (StarsUpdateCustomerAccount)
					StarsFactory.newStarsCustAccount( starsAcctInfo.getStarsCustomerAccount(), StarsUpdateCustomerAccount.class );

			updateAccount.setAccountNumber( req.getParameter("AcctNo") );
			updateAccount.setIsCommercial( starsAcctInfo.getStarsCustomerAccount().getIsCommercial() );
			if (req.getParameter("Company") != null)
				updateAccount.setCompany( req.getParameter("Company") );
			updateAccount.setAccountNotes( req.getParameter("AcctNotes").replaceAll(System.getProperty("line.separator"), "<br>") );

			updateAccount.setPropertyNumber( req.getParameter("PropNo") );
			updateAccount.setPropertyNotes( req.getParameter("PropNotes").replaceAll(System.getProperty("line.separator"), "<br>") );

			StreetAddress propAddr = new StreetAddress();
			propAddr.setStreetAddr1( req.getParameter("SAddr1") );
			propAddr.setStreetAddr2( req.getParameter("SAddr2") );
			propAddr.setCity( req.getParameter("SCity") );
			propAddr.setState( req.getParameter("SState") );
			propAddr.setZip( req.getParameter("SZip") );
			propAddr.setCounty( req.getParameter("SCounty") );
			updateAccount.setStreetAddress( propAddr );

			Substation starsSub = new Substation();
			starsSub.setEntryID( Integer.parseInt(req.getParameter("Substation")) );
			
			StarsSiteInformation siteInfo = new StarsSiteInformation();
			siteInfo.setSubstation( starsSub );
			siteInfo.setFeeder( req.getParameter("Feeder") );
			siteInfo.setPole( req.getParameter("Pole") );
			siteInfo.setTransformerSize( req.getParameter("TranSize") );
			siteInfo.setServiceVoltage( req.getParameter("ServVolt") );
			updateAccount.setStarsSiteInformation( siteInfo );

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
			updateAccount.setBillingAddress( billAddr );

			PrimaryContact primContact = new PrimaryContact();
			primContact.setLastName( req.getParameter("LastName") );
			primContact.setFirstName( req.getParameter("FirstName") );
			
			ContactNotification homePhone = ServletUtils.createContactNotification(
					req.getParameter("HomePhone"), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE );
			if (homePhone != null) primContact.addContactNotification( homePhone );
			
			ContactNotification workPhone = ServletUtils.createContactNotification(
					req.getParameter("WorkPhone"), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
			if (workPhone != null) primContact.addContactNotification( workPhone );
			
			ContactNotification email = ServletUtils.createContactNotification(
					req.getParameter("Email"), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
			if (email != null) {
				email.setDisabled( req.getParameter("NotifyControl") == null );
				primContact.addContactNotification( email );
			} 
            
			updateAccount.setPrimaryContact( primContact );
			updateAccount.setAdditionalContact( starsAcctInfo.getStarsCustomerAccount().getAdditionalContact() );

			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateCustomerAccount( updateAccount );

			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException we) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, we.getMessage() );
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
			StarsUpdateCustomerAccount updateAccount = reqOper.getStarsUpdateCustomerAccount();
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            
			try {
				updateCustomerAccount( updateAccount, liteAcctInfo, energyCompany );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Customer account updated successfully" );
            
			respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to update the customer account information") );
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
			
			if (operation.getStarsSuccess() == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsUpdateCustomerAccount updateAccount = SOAPUtil.parseSOAPMsgForOperation(reqMsg).getStarsUpdateCustomerAccount();
			accountInfo.setStarsCustomerAccount( (StarsCustomerAccount)
					StarsFactory.newStarsCustAccount(updateAccount, StarsCustomerAccount.class) );

			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
    
	public static void updateCustomerAccount(StarsUpdateCustomerAccount updateAccount, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		java.sql.Connection conn = null;
		boolean autoCommit = true;
    	
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit( false );
    		
			/* Update customer account */
			LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
        	
			if (!liteAccount.getAccountNumber().equalsIgnoreCase( updateAccount.getAccountNumber() )) {
				// Check to see if the account number has duplicates
				if (energyCompany.searchAccountByAccountNo(updateAccount.getAccountNumber()) != null)
					throw new WebClientException( "Account number already exists" );
			}
        	
			LiteAddress liteBillAddr = energyCompany.getAddress( liteAccount.getBillingAddressID() );
			BillingAddress starsBillAddr = updateAccount.getBillingAddress();
        	
			if (!StarsLiteFactory.isIdenticalCustomerAddress( liteBillAddr, starsBillAddr )) {
				com.cannontech.database.db.customer.Address billAddr =
						(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteBillAddr );
				StarsFactory.setCustomerAddress( billAddr, starsBillAddr );
        		
				billAddr.setDbConnection( conn );
				billAddr.update();
				StarsLiteFactory.setLiteAddress( liteBillAddr, billAddr );
			}
        	
			if (!StarsLiteFactory.isIdenticalCustomerAccount( liteAccount, updateAccount )) {
				com.cannontech.database.db.stars.customer.CustomerAccount account =
						(com.cannontech.database.db.stars.customer.CustomerAccount) StarsLiteFactory.createDBPersistent( liteAccount );
				StarsFactory.setCustomerAccount( account, updateAccount );
        		
				account.setDbConnection( conn );
				account.update();
				StarsLiteFactory.setLiteCustomerAccount( liteAccount, account );
			}
			
			/* Update customer */
			LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
			com.cannontech.database.db.customer.Customer customerDB = new com.cannontech.database.db.customer.Customer();
			StarsLiteFactory.setCustomer( customerDB, liteCustomer );
        	
			LiteContact litePrimContact = ContactFuncs.getContact( liteCustomer.getPrimaryContactID() );
			PrimaryContact starsPrimContact = updateAccount.getPrimaryContact();
			
			boolean primContChanged = false;
			boolean ciCustChanged = false;
        	
			if (!StarsLiteFactory.isIdenticalCustomerContact( litePrimContact, starsPrimContact )) {
				com.cannontech.database.data.customer.Contact primContact = new com.cannontech.database.data.customer.Contact();
				StarsLiteFactory.setContact( primContact, litePrimContact, energyCompany );
				StarsFactory.setCustomerContact( primContact, starsPrimContact );
        		
				primContact.setDbConnection( conn );
				primContact.update();
				
				StarsLiteFactory.setLiteContact( litePrimContact, primContact );
				primContChanged = true;
			}
        	
			if (liteCustomer instanceof LiteCICustomer) {
				LiteCICustomer liteCICust = (LiteCICustomer) liteCustomer;
				
				if (!liteCICust.getCompanyName().equals( updateAccount.getCompany() )) {
					// Company name of a CI customer is changed
					com.cannontech.database.db.customer.CICustomerBase ciDB = new com.cannontech.database.db.customer.CICustomerBase();
					ciDB.setCustomerID( customerDB.getCustomerID() );
					ciDB.setDbConnection( conn );
					ciDB.retrieve();
        			
					ciDB.setCompanyName( updateAccount.getCompany() );
					ciDB.setDbConnection( conn );
					ciDB.update();
        			
					liteCICust.setCompanyName( ciDB.getCompanyName() );
					ciCustChanged = true;
				}
			}
        	
			/* Update account site */
			LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
        	
			LiteAddress liteStAddr = energyCompany.getAddress( liteAcctSite.getStreetAddressID() );
			StreetAddress starsStAddr = updateAccount.getStreetAddress();
        	
			if (!StarsLiteFactory.isIdenticalCustomerAddress( liteStAddr, starsStAddr )) {
				com.cannontech.database.db.customer.Address stAddr =
						(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteStAddr );
				StarsFactory.setCustomerAddress( stAddr, starsStAddr );
        		
				stAddr.setDbConnection( conn );
				stAddr.update();
				StarsLiteFactory.setLiteAddress( liteStAddr, stAddr );
			}
        	
			if (!StarsLiteFactory.isIdenticalAccountSite( liteAcctSite, updateAccount )) {
				com.cannontech.database.db.stars.customer.AccountSite acctSite =
						(com.cannontech.database.db.stars.customer.AccountSite) StarsLiteFactory.createDBPersistent( liteAcctSite );
				StarsFactory.setAccountSite( acctSite, updateAccount );
        		
				acctSite.setDbConnection( conn );
				acctSite.update();
				StarsLiteFactory.setLiteAccountSite( liteAcctSite, acctSite );
			}
        	
			/* Update site information */
			LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
			StarsSiteInformation starsSiteInfo = updateAccount.getStarsSiteInformation();
        	
			if (!StarsLiteFactory.isIdenticalSiteInformation(liteSiteInfo, starsSiteInfo)) {
				com.cannontech.database.db.stars.customer.SiteInformation siteInfo =
						(com.cannontech.database.db.stars.customer.SiteInformation) StarsLiteFactory.createDBPersistent( liteSiteInfo );
				StarsFactory.setSiteInformation( siteInfo, updateAccount );
        		
				siteInfo.setDbConnection( conn );
				siteInfo.update();
				StarsLiteFactory.setLiteSiteInformation( liteSiteInfo, siteInfo );
			}
			
			conn.commit();
			
			if (primContChanged) ServerUtils.handleDBChange( litePrimContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
			if (ciCustChanged) ServerUtils.handleDBChange( liteCustomer, DBChangeMsg.CHANGE_TYPE_UPDATE );
			//ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_UPDATE );
		}
		catch (java.sql.SQLException e) {
			try {
				if (conn != null) conn.rollback();
			}
			catch (java.sql.SQLException e2) {}
    		
			throw new WebClientException( "Failed to update the customer account information", e );
		}
		finally {
			try {
				if (conn != null) {
					conn.setAutoCommit( autoCommit );
					conn.close();
				}
			}
			catch (java.sql.SQLException e) {}
		}
	}
}