package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.StarsUser;
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
	        login.setDbAlias( req.getParameter("DATABASEALIAS") );
	        
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
            
            StarsOperator operator = null;
            StarsUser user = null;
            StarsLogin login = reqOper.getStarsLogin();
            
            operator = authenticateOperator( login.getUsername(), login.getPassword(), login.getDbAlias() );
            if (operator == null)
            	user = authenticateUser( login.getUsername(), login.getPassword(), login.getDbAlias() );
            
            if (operator == null && user == null) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
                failure.setDescription("Login failed, please check your username and password");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            Integer energyCompanyID = null;
            if (operator != null)
            	energyCompanyID = new Integer( (int)operator.getEnergyCompanyID() );
            else
            	energyCompanyID = new Integer( user.getEnergyCompanyID() );
        	if (energyCompanyID.intValue() == 0) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
                failure.setDescription("No account information has been found for the current login");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
            // Get all selection lists
            com.cannontech.database.db.stars.CustomerSelectionList[] selectionLists =
            		com.cannontech.database.data.stars.CustomerSelectionList.getAllSelectionLists( energyCompanyID );
            java.util.Hashtable selectionListTable = new java.util.Hashtable();
            
            for (int i = 0; i < selectionLists.length; i++) {
            	com.cannontech.database.db.stars.CustomerListEntry[] entries =
            			com.cannontech.database.data.stars.CustomerListEntry.getAllListEntries( selectionLists[i].getListID() );
            	StarsCustSelectionList starsList = new StarsCustSelectionList();
            	starsList.setListID( selectionLists[i].getListID().intValue() );
            	
            	for (int j = 0; j < entries.length; j++) {
            		StarsSelectionListEntry starsEntry = new StarsSelectionListEntry();
            		starsEntry.setEntryID( entries[j].getEntryID().intValue() );
            		starsEntry.setContent( entries[j].getEntryText() );
            		starsEntry.setYukonDefinition( entries[j].getYukonDefinition() );
            		starsList.addStarsSelectionListEntry( starsEntry );
            	}
            	
            	selectionListTable.put( selectionLists[i].getListName(), starsList );
            }
            
            // Get substation list
            com.cannontech.database.db.stars.Substation[] subs =
            		com.cannontech.database.data.stars.Substation.getAllSubstations( energyCompanyID );
            StarsCustSelectionList starsList = new StarsCustSelectionList();
            
            for (int i = 0; i < subs.length; i++) {
            	StarsSelectionListEntry starsEntry = new StarsSelectionListEntry();
            	starsEntry.setEntryID( subs[i].getSubstationID().intValue() );
            	starsEntry.setContent( subs[i].getSubstationName() );
            	starsList.addStarsSelectionListEntry( starsEntry );
            }
            selectionListTable.put( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION, starsList );
            
            if (operator != null) {
            	session.setAttribute( "OPERATOR", operator );
	            operator.setAttribute( "CUSTOMER_SELECTION_LIST", selectionListTable );
            }
            else {
            	session.setAttribute( "USER", user );
	            user.setAttribute( "CUSTOMER_SELECTION_LIST", selectionListTable );
	            user.setDatabaseAlias( login.getDbAlias() );
            }
            
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

	private StarsUser authenticateUser(String username, String password, String dbAlias) {
		StarsUser retVal = null;
		
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
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
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			} catch( Exception e ) { }
		}
			
		return retVal;
	}
}
