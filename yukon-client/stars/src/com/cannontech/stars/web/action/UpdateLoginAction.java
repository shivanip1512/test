package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;
import java.util.Iterator;

import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.*;
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

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            int energyCompanyID = user.getEnergyCompanyID();
            
            LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
            		user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            StarsUpdateLogin updateLogin = reqOper.getStarsUpdateLogin();
            if (updateLogin.getUsername().length() == 0 || updateLogin.getPassword().length() == 0) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Username and password cannot be empty") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            LiteYukonUser liteUser = liteAcctInfo.getYukonUser();
            
            if (liteUser == null) {
	            com.cannontech.database.data.user.YukonUser dataUser = new com.cannontech.database.data.user.YukonUser();
	            com.cannontech.database.db.user.YukonUser dbUser = dataUser.getYukonUser();
	            
	            dbUser.setUserID( com.cannontech.database.db.user.YukonUser.getNextUserID() );
            	dbUser.setUsername( updateLogin.getUsername() );
            	dbUser.setPassword( updateLogin.getPassword() );
            	dbUser.setStatus( "Enabled" );
	            
	            DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	            LiteYukonGroup liteGroup = null;
	            
	            synchronized (cache) {
	            	Iterator it = cache.getAllYukonGroups().iterator();
	            	while (it.hasNext()) {
	            		LiteYukonGroup g = (LiteYukonGroup) it.next();
	            		if (g.getGroupName().equalsIgnoreCase("STARS Consumers")) {
	            			liteGroup = g;
	            			break;
	            		}
	            	}
	            }
	            
	            com.cannontech.database.db.user.YukonGroup dbGroup = new com.cannontech.database.db.user.YukonGroup();
	            dbGroup.setGroupID( new Integer(liteGroup.getGroupID()) );
	            dataUser.getYukonGroups().addElement( dbGroup );
	            
            	dataUser = (com.cannontech.database.data.user.YukonUser)
	            		Transaction.createTransaction( Transaction.INSERT, dataUser ).execute();
	            
	            LiteCustomerContact litePrimContact = SOAPServer.getCustomerContact( energyCompanyID, liteAcctInfo.getCustomerBase().getPrimaryContactID() );
	            com.cannontech.database.db.customer.CustomerContact primContact =
	            		(com.cannontech.database.db.customer.CustomerContact) StarsLiteFactory.createDBPersistent( litePrimContact );
	            		
	            primContact.setLogInID( dbUser.getUserID() );
	            com.cannontech.database.db.stars.CustomerContact contactWrapper = new com.cannontech.database.db.stars.CustomerContact( primContact );
	            Transaction.createTransaction( Transaction.UPDATE, contactWrapper ).execute();
	            
	            liteUser = new LiteYukonUser( dbUser.getUserID().intValue(), dbUser.getUsername(), dbUser.getPassword() );
	            liteAcctInfo.setYukonUser( liteUser );
	            
	            ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD );
            }
            else {
		        com.cannontech.database.db.user.YukonUser dbUser = new com.cannontech.database.db.user.YukonUser();
	            dbUser.setUserID( new Integer(liteUser.getUserID()) );
	            dbUser = (com.cannontech.database.db.user.YukonUser)
	            		Transaction.createTransaction( Transaction.RETRIEVE, dbUser ).execute();
	            		
	            dbUser.setUsername( updateLogin.getUsername() );
	            dbUser.setPassword( updateLogin.getPassword() );
	            dbUser = (com.cannontech.database.db.user.YukonUser)
	            		Transaction.createTransaction( Transaction.UPDATE, dbUser ).execute();
	            
	            liteUser.setUsername( dbUser.getUsername() );
	            liteUser.setPassword( dbUser.getPassword() );
	            
	            ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
            }
            
            StarsUser starsUser = StarsLiteFactory.createStarsUser( liteUser );
            StarsUpdateLoginResponse resp = new StarsUpdateLoginResponse();
            resp.setStarsUser( starsUser );
            
            respOper.setStarsUpdateLoginResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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
			
			StarsUpdateLoginResponse resp = operation.getStarsUpdateLoginResponse();
			if (resp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			accountInfo.setStarsUser( resp.getStarsUser() );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
