package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsLogin;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
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
			session.removeAttribute("OPERATOR");
			session.removeAttribute("USER");
			
	        StarsLogin login = new StarsLogin();
	        login.setUsername( req.getParameter("USERNAME") );
	        login.setPassword( req.getParameter("PASSWORD") );
	        
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
            
            StarsOperator operator = null;
            StarsUser user = null;
            StarsLogin login = reqOper.getStarsLogin();
            
            operator = authenticateOperator( login.getUsername(), login.getPassword() );
            if (operator == null)
            	user = authenticateUser( login.getUsername(), login.getPassword() );
            
            if (operator == null && user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Login failed, please check your username and password") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            if (operator != null) {
            	session.setAttribute( "OPERATOR", operator );
            }
            else {
            	session.setAttribute( "USER", user );
	            user.setDatabaseAlias( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            }
            
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

	private StarsOperator authenticateOperator(String username, String password)
	{	
		StarsOperator retVal = null;
		
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(
					com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			stmt = conn.createStatement();
			rset = stmt.executeQuery("SELECT LoginID FROM OperatorLogin WHERE Username='" + username + "' AND Password='" + password + "'");
	
			if( rset.next() )
			{			
				retVal = new StarsOperator();
				retVal.setLoginID(rset.getLong(1));
			
				retVal.setDbConnection(conn);
				retVal.retrieve();
				retVal.setDbConnection(null);	
			}
	
			stmt.close();
	
			if( retVal != null )
			{	
				retVal.setDbConnection(conn);
				retVal.retrieve();
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
                if (rset != null) rset.close();
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			} catch( Exception e ) { }
		}
			
		return retVal;
	}

	private StarsUser authenticateUser(String username, String password) {
		StarsUser retVal = null;
		
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(
					com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			stmt = conn.createStatement();
			rset = stmt.executeQuery("SELECT LoginID FROM CustomerLogin WHERE Username='" + username + "' AND Password='" + password + "'");
	
			if( rset.next() )
			{			
				retVal = new StarsUser();
				retVal.setId(rset.getLong(1));								
			}
	
			stmt.close();
	
			if( retVal != null )
			{	
				retVal.setDbConnection(conn);
				retVal.retrieve();
				retVal.setDbConnection(null);
			}		
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
                if (rset != null) rset.close();
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			} catch( Exception e ) { }
		}
			
		return retVal;
	}
}
