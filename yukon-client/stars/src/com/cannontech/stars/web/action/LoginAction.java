package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsLogin;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.serialize.types.StarsLoginType;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: LoginAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 6, 2002 11:38:50 AM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class LoginAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			session.removeAttribute( ServletUtils.ATT_YUKON_USER );
			
	        StarsLogin login = new StarsLogin();
	        login.setUsername( req.getParameter("USERNAME") );
	        login.setPassword( req.getParameter("PASSWORD") );
	        if (req.getParameter("action").equalsIgnoreCase("OperatorLogin"))
	        	login.setLoginType( StarsLoginType.OPERATORLOGIN );
	        else
	        	login.setLoginType( StarsLoginType.CONSUMERLOGIN );
	        
	        StarsOperation operation = new StarsOperation();
	        operation.setStarsLogin( login );
	        
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
            
            StarsLogin login = reqOper.getStarsLogin();
            LiteYukonUser user = AuthFuncs.login( login.getUsername(), login.getPassword() );
            if (user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Login failed, please check your username and password") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            // check whether the login type matches the role
            boolean typeMatch = false;
            if (login.getLoginType().getType() == StarsLoginType.OPERATORLOGIN_TYPE)
        		typeMatch = ServerUtils.isOperator( user );
            else
        		typeMatch = ServerUtils.isConsumer( user );
            if (!typeMatch) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Login failed, please check your username and password") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            StarsYukonUser starsUser = SOAPServer.getStarsYukonUser( user.getUserID() );
            if (starsUser == null) {
            	starsUser = new StarsYukonUser( user );
            	SOAPServer.addStarsYukonUser( starsUser );
            }
            
            initSession( starsUser, session );
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Login successful" );
            respOper.setStarsSuccess( success );
            
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot login the customer") );
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
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
            if (operation.getStarsSuccess() == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	private void initSession(StarsYukonUser user, HttpSession session) throws TransactionException  {
	
		com.cannontech.database.data.user.YukonUser dbUser = 
			(com.cannontech.database.data.user.YukonUser) LiteFactory.createDBPersistent(user);
		
		Transaction trans = Transaction.createTransaction(Transaction.RETRIEVE,dbUser);
		trans.execute();
		
		//update user stats
		dbUser.setLoginCount(new Integer(dbUser.getLoginCount().intValue()+1));
		dbUser.setLastLogin(new java.util.Date());
			
		trans = Transaction.createTransaction(Transaction.UPDATE,dbUser);
		
		session.setAttribute(ServletUtils.ATT_YUKON_USER, user);
	}
}
