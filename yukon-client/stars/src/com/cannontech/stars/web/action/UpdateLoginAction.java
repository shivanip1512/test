package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsMsgUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.stars.xml.serialize.StarsUser;
import com.cannontech.stars.xml.serialize.types.StarsLoginStatus;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.tools.email.EmailMessage;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateLoginAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsUpdateLogin updateLogin = new StarsUpdateLogin();
			updateLogin.setUsername( req.getParameter("Username").trim() );
			updateLogin.setPassword( req.getParameter("Password").trim() );
			if (req.getParameter("Status") != null)
				updateLogin.setStatus( StarsLoginStatus.valueOf(req.getParameter("Status")) );
			else
				updateLogin.setStatus( StarsLoginStatus.DISABLED );
			if (req.getParameter("CustomerGroup") != null && req.getParameter("CustomerGroup").length() > 0)
				updateLogin.setGroupID( Integer.parseInt(req.getParameter("CustomerGroup")) );
            
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateLogin( updateLogin );
            
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
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
			StarsUpdateLogin updateLogin = reqOper.getStarsUpdateLogin();
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
					session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            
			try {
				updateLogin( updateLogin, liteAcctInfo, energyCompany );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			StarsSuccess success = new StarsSuccess();
			if (updateLogin.getUsername().equals(""))
				success.setDescription( "User login deleted successfully" );
			else
				success.setDescription( "User login updated successfully" );
            
			respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create/update login for the customer") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSuccess success = operation.getStarsSuccess();
			if (success == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsUpdateLogin updateLogin = reqOper.getStarsUpdateLogin();
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			if (updateLogin.getUsername().equals("")) {
				accountInfo.setStarsUser( null );
			}
			else {
				StarsUser userLogin = (StarsUser) StarsFactory.newStarsUser(updateLogin, StarsUser.class);
				accountInfo.setStarsUser( userLogin );
			}
			
			if (reqOper.getStarsNewCustomerAccount() == null)	// If not from the new customer account page
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, success.getDescription() );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static LiteYukonUser createLogin(StarsUpdateLogin login, LiteContact liteContact, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.data.user.YukonUser dataUser = new com.cannontech.database.data.user.YukonUser();
		com.cannontech.database.db.user.YukonUser dbUser = dataUser.getYukonUser();
        
		if (login.hasGroupID()) {
			com.cannontech.database.db.user.YukonGroup dbGroup = new com.cannontech.database.db.user.YukonGroup();
			dbGroup.setGroupID( new Integer(login.getGroupID()) );
			dataUser.getYukonGroups().addElement( dbGroup );
		}
        
		dbUser.setUsername( login.getUsername() );
		dbUser.setPassword( login.getPassword() );
		if (login.getStatus() != null)
			dbUser.setStatus( StarsMsgUtils.getUserStatus(login.getStatus()) );
		else
			dbUser.setStatus( com.cannontech.user.UserUtils.STATUS_ENABLED );
        
		dataUser = (com.cannontech.database.data.user.YukonUser)
				Transaction.createTransaction( Transaction.INSERT, dataUser ).execute();
		LiteYukonUser liteUser = new LiteYukonUser(
				dbUser.getUserID().intValue(),
				dbUser.getUsername(),
				dbUser.getPassword(),
				dbUser.getStatus()
				);
		ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD );
        
		if (liteContact != null) {
			liteContact.setLoginID( liteUser.getUserID() );
			com.cannontech.database.data.customer.Contact contact =
					(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
			Transaction.createTransaction(Transaction.UPDATE, contact.getContact()).execute();
			ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
		}
        
		return liteUser;
	}
	
	public static void deleteLogin(int userID) throws TransactionException {
		LiteContact liteContact = YukonUserFuncs.getLiteContact( userID );
		if (liteContact != null) {
			liteContact.setLoginID( com.cannontech.user.UserUtils.USER_DEFAULT_ID );
			com.cannontech.database.data.customer.Contact contact =
					(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
			Transaction.createTransaction(Transaction.UPDATE, contact.getContact()).execute();
			ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
		}
        
		com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
		yukonUser.setUserID( new Integer(userID) );
		Transaction.createTransaction(Transaction.DELETE, yukonUser).execute();
		
		StarsDatabaseCache.getInstance().deleteStarsYukonUser( userID );
		ServerUtils.handleDBChange( YukonUserFuncs.getLiteYukonUser(userID), DBChangeMsg.CHANGE_TYPE_DELETE );
	}
	
	public static void updateLogin(StarsUpdateLogin updateLogin, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		LiteContact liteContact = ContactFuncs.getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		int userID = liteContact.getLoginID();
	    
		String username = updateLogin.getUsername();
		String password = updateLogin.getPassword();
	    
		if (userID == com.cannontech.user.UserUtils.USER_ADMIN_ID ||
			userID == com.cannontech.user.UserUtils.USER_DEFAULT_ID)
		{
			// Create new customer login
			if (!updateLogin.hasGroupID())
				throw new WebClientException( "Cannot create login without a customer group" );
		    
			if (username.length() == 0 || password.length() == 0)
				throw new WebClientException( "Username and password cannot be empty" );
		    
			if (YukonUserFuncs.getLiteYukonUser(username) != null)
				throw new WebClientException( "Username '" + username + "' already exists" );
			
			LiteYukonUser liteUser = createLogin( updateLogin, liteContact, energyCompany );
		}
		else if (username.length() == 0) {
			// Remove customer login
			if (password.length() > 0)
				throw new WebClientException( "Username is empty. To remove the login, clear both username and password." );
		    
			deleteLogin( userID );
		}
		else {
			// Update customer login
			String status = null;
			if (updateLogin.getStatus() != null)
				status = StarsMsgUtils.getUserStatus( updateLogin.getStatus() );
			
			LiteYukonGroup loginGroup = null;
			if (updateLogin.hasGroupID())
				loginGroup = AuthFuncs.getGroup( updateLogin.getGroupID() );
			
			LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( userID );
			StarsAdminUtil.updateLogin( liteUser, username, password, status, loginGroup, energyCompany );
		}
	}
	
	public static void sendNotificationEmail(String to, StarsUpdateLogin updateLogin, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		String NEW_LINE = System.getProperty( "line.separator" );
		String msg = "Your login information has been changed, here is the new information:" + NEW_LINE
				+ NEW_LINE
				+ "User Name:\t" + updateLogin.getUsername() + NEW_LINE
				+ "Password:\t" + updateLogin.getPassword() + NEW_LINE;
		
		EmailMessage emailMsg = new EmailMessage( to, "Login Change Notification", msg );
		emailMsg.setFrom( energyCompany.getAdminEmailAddress() );
		emailMsg.send();
	}
	
	public static void generatePassword(HttpServletRequest req, HttpSession session) throws WebClientException {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
					session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			if (accountInfo.getStarsUser() == null)
				throw new WebClientException("Cannot generate new password. User login doesn't exist yet.");
			
			StarsUpdateLogin updateLogin = (StarsUpdateLogin) StarsFactory.newStarsUser( accountInfo.getStarsUser(), StarsUpdateLogin.class );
            
			// Generate a random password of length 6, consists of letters and digits
			char[] password = new char[6];
			for (int i = 0; i < 6; i++) {
				int rand = (int)(Math.random() * 62);
				if (rand < 10)
					password[i] = (char)(48 + rand);		// 48 is ascii for '0'
				else if (rand < 36)
					password[i] = (char)(65 + rand - 10);	// 65 is ascii for 'A'
				else
					password[i] = (char)(97 + rand - 36);	// 36 is ascii for 'a'
			}
			updateLogin.setPassword( new String(password) );
            
			updateLogin( updateLogin, liteAcctInfo, energyCompany );
			String confirmMsg = "User login has been updated successfully. The new password is \"" + updateLogin.getPassword() + "\".";
			
			// Try to send new password to customer by email
//			ContactNotification email = ServletUtils.getContactNotification( accountInfo.getStarsCustomerAccount().getPrimaryContact(), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
//			if (email != null) {
//				try {
//					sendNotificationEmail( email.getNotification(), updateLogin, energyCompany );
//					confirmMsg += "<br>A notification email has been sent to the customer successfully.";
//				}
//				catch (Exception e) {
//					CTILogger.error( e.getMessage(), e );
//					confirmMsg += "<br><font color='red'>Failed to send notification email to the customer, please contact the customer in other means.</font>";
//				}
//			}
//			else {
//				confirmMsg += "<br><font color='red'>The email address of the customer is not specified, please contact the customer in other means.</font>";
//			}
			
			StarsUser userLogin = (StarsUser) StarsFactory.newStarsUser(updateLogin, StarsUser.class);
			accountInfo.setStarsUser( userLogin );
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, confirmMsg);
		}
		catch (Exception e) {
			if (e instanceof WebClientException)
				throw (WebClientException)e;
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException( "Failed to generate new password for the customer" );
		}
	}

}
