package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsOperatorLogin;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: OperatorLoginAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 6, 2002 11:38:50 AM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class OperatorLoginAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
	        StarsOperatorLogin login = new StarsOperatorLogin();
	        login.setUsername( req.getParameter("USERNAME") );
	        login.setPassword( req.getParameter("PASSWORD") );
	        login.setDbAlias( req.getParameter("DATABASEALIAS") );
	        
	        StarsOperation operation = new StarsOperation();
	        operation.setStarsOperatorLogin( login );
	        
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
            
            StarsOperatorLogin login = reqOper.getStarsOperatorLogin();
            StarsOperator operator = authenticateOperator( login.getUsername(), login.getPassword(), login.getDbAlias() );
            
            if (operator == null) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
                failure.setDescription("Login failed, please check your username and password");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            session.setAttribute( "OPERATOR", operator );
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Login successful" );
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

	private StarsOperator authenticateOperator(String username, String password, String dbAlias)
	{	
		StarsOperator retVal = null;
		
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
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
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			} catch( Exception e ) { }
		}
			
		return retVal;
	}
	
}
