package com.cannontech.stars.web.action;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteCustomerAccount;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.stars.xml.serialize.StarsUser;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

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
            updateLogin.setUsername( req.getParameter("Username") );
            updateLogin.setPassword( req.getParameter("Password") );
            
            StarsOperation operation = new StarsOperation();
            operation.setStarsUpdateLogin( updateLogin );
            
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
                respOper.setStarsFailure( StarsFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            int energyCompanyID = user.getEnergyCompanyID();
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
            		user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
	        int userID = liteAcctInfo.getCustomerAccount().getLoginID();
	        LiteYukonUser liteUser = null;
	        if (userID != com.cannontech.user.UserUtils.USER_YUKON_ID)
	        	liteUser = YukonUserFuncs.getLiteYukonUser( userID );
	        
            StarsUpdateLogin updateLogin = reqOper.getStarsUpdateLogin();
            String username = updateLogin.getUsername();
            String password = updateLogin.getPassword();
	        	
            if (userID == com.cannontech.user.UserUtils.USER_YUKON_ID) {
            	// Create new customer login
		        if (username.trim().length() == 0 || password.trim().length() == 0) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Username and password cannot be empty") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
		        }
		        
		        if (!checkLogin( updateLogin )) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Username already exists, please enter a different one") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
		        }
		        
            	createLogin( liteAcctInfo.getCustomerAccount(), energyCompany, updateLogin );
            }
            else if (username.trim().length() == 0) {
            	// Remove customer login
		        if (password.trim().length() > 0) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Username is empty. To remove the login, clear both username and password") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
		        }
		        
		        deleteLogin( liteAcctInfo.getCustomerAccount(), userID );
            }
            else {
            	// Update customer login
		        if (!liteUser.getUsername().equalsIgnoreCase(username) && !checkLogin(updateLogin) ) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Username already exists, please enter a different one") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
		        }

		        com.cannontech.database.db.user.YukonUser dbUser = (com.cannontech.database.db.user.YukonUser)
		        		StarsLiteFactory.createDBPersistent( liteUser );
	            dbUser.setUsername( username );
	            dbUser.setPassword( password );
	            dbUser.setStatus( com.cannontech.user.UserUtils.STATUS_ENABLED );
	            dbUser = (com.cannontech.database.db.user.YukonUser)
	            		Transaction.createTransaction( Transaction.UPDATE, dbUser ).execute();
	            
	            ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
            }
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "User login updated successfully" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create/update login for the customer") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
					
			StarsUser starsUser = accountInfo.getStarsUser();
			if (starsUser == null) {
				starsUser = new StarsUser();
				accountInfo.setStarsUser( starsUser );
			}
			starsUser.setUsername( updateLogin.getUsername() );
			starsUser.setPassword( updateLogin.getPassword() );

			if (reqOper.getStarsNewCustomerAccount() == null)	// If not from the new customer account page
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, success.getDescription() );
				
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	/**
	 * Check to see if the username already exists
	 */
	public static boolean checkLogin(StarsUpdateLogin login) {
        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
        	Iterator it = cache.getAllYukonUsers().iterator();
        	while (it.hasNext()) {
        		LiteYukonUser lUser = (LiteYukonUser) it.next();
        		if (lUser.getUsername().equalsIgnoreCase( login.getUsername() ))
        			return false;
        	}
        }
        
        return true;
	}
	
	public static void createLogin(LiteCustomerAccount liteAccount, LiteStarsEnergyCompany energyCompany, StarsUpdateLogin login) throws Exception {
        com.cannontech.database.data.user.YukonUser dataUser = new com.cannontech.database.data.user.YukonUser();
        com.cannontech.database.db.user.YukonUser dbUser = dataUser.getYukonUser();
        
        String groupName = energyCompany.getEnergyCompanySetting(EnergyCompanyRole.CUSTOMER_GROUP_NAME);
        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
        LiteYukonGroup liteGroup = null;
        
        synchronized (cache) {
        	Iterator it = cache.getAllYukonGroups().iterator();
        	while (it.hasNext()) {
        		LiteYukonGroup g = (LiteYukonGroup) it.next();
        		if (g.getGroupName().equalsIgnoreCase( groupName )) {
        			liteGroup = g;
        			break;
        		}
        	}
        }
        
        com.cannontech.database.db.user.YukonGroup dbGroup = new com.cannontech.database.db.user.YukonGroup();
        dbGroup.setGroupID( new Integer(liteGroup.getGroupID()) );
        dataUser.getYukonGroups().addElement( dbGroup );
        
    	dbUser.setUsername( login.getUsername() );
    	dbUser.setPassword( login.getPassword() );
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
        
        liteAccount.setLoginID( dbUser.getUserID().intValue() );
        com.cannontech.database.db.stars.customer.CustomerAccount account =
        		(com.cannontech.database.db.stars.customer.CustomerAccount) StarsLiteFactory.createDBPersistent( liteAccount );
        Transaction.createTransaction(Transaction.UPDATE, account).execute();
	}
	
	public static void deleteLogin(LiteCustomerAccount liteAccount, int userID) throws Exception {
		if (liteAccount != null) {
	        liteAccount.setLoginID( com.cannontech.user.UserUtils.USER_YUKON_ID );
	        com.cannontech.database.db.stars.customer.CustomerAccount account =
	        		(com.cannontech.database.db.stars.customer.CustomerAccount) StarsLiteFactory.createDBPersistent( liteAccount );
	        Transaction.createTransaction(Transaction.UPDATE, account).execute();
		}
        
		com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
		yukonUser.setUserID( new Integer(userID) );
		Transaction.createTransaction(Transaction.DELETE, yukonUser).execute();
		
		SOAPServer.deleteStarsYukonUser( userID );
		ServerUtils.handleDBChange(
				YukonUserFuncs.getLiteYukonUser( userID ),
				com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE
				);
	}

}
