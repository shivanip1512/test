package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.stars.xml.serialize.*;
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
			session.removeAttribute("STARS_USER");
			
	        StarsLogin login = new StarsLogin();
	        login.setUsername( req.getParameter("USERNAME") );
	        login.setPassword( req.getParameter("PASSWORD") );
	        
	        StarsOperation operation = new StarsOperation();
	        operation.setStarsLogin( login );
	        
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
            
            StarsLogin login = reqOper.getStarsLogin();
            LiteYukonUser liteUser = AuthFuncs.login( login.getUsername(), login.getPassword() );
            
            if (liteUser == null) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
                failure.setDescription("Login failed, please check your username and password");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	com.cannontech.stars.web.StarsUser user = new com.cannontech.stars.web.StarsUser(liteUser);
        	initSession(user, session);
            
            StarsUser starsUser = StarsLiteFactory.createStarsUser( liteUser );
            StarsLoginResponse resp = new StarsLoginResponse();
            resp.setStarsUser( starsUser );
            
            respOper.setStarsLoginResponse( resp );
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
			
			StarsLoginResponse resp = operation.getStarsLoginResponse();
            com.cannontech.stars.web.StarsUser user = new com.cannontech.stars.web.StarsUser( resp.getStarsUser() );
            session.setAttribute( "STARS_USER", user );
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	private void initSession(com.cannontech.stars.web.StarsUser user, HttpSession session) throws TransactionException  {
	
		YukonUser dbUser = (YukonUser) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(user);
		dbUser = (YukonUser) Transaction.createTransaction(Transaction.RETRIEVE, dbUser).execute();
		
		//update user stats
		dbUser.setLoginCount(new Integer(dbUser.getLoginCount().intValue()+1));
		dbUser.setLastLogin(new java.util.Date());
			
		Transaction.createTransaction(Transaction.UPDATE, dbUser).execute();
		
		session.setAttribute("YUKON_USER", user);
	}
}
