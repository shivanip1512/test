package com.cannontech.stars.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.cannontech.database.data.web.Operator;

/**
 * <p>Title: StarsOperator.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 6, 2002 10:43:19 AM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class StarsOperator extends Operator {
	
	private Hashtable attributes = new Hashtable();
	
	public Object getAttribute(Object name) {
		return attributes.get( name );
	}
	
	public void setAttribute(Object name, Object value) {
		attributes.put( name, value );
	}
	
	public void removeAttribute(Object name) {
		attributes.remove( name );
	}
	
	public synchronized Integer getIncAttribute(Object name) {
		Integer value = (Integer) attributes.get( name );
		if (value != null) {
			Integer newValue = new Integer( value.intValue()+1 );
			attributes.put( name, newValue );
		}
		return value;
	}
	
	public void retrieve() throws SQLException {
		super.retrieve();
		
		String sql = null;
		Connection conn = getDbConnection();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		try {
			// Retrieve the next available call number from the database
			sql = "SELECT CallNumber FROM CallReportBase WHERE CallID = "
					   + "(SELECT MAX(CallReportID) FROM ECToCallReportMapping "
					   + "WHERE EnergyCompanyID = ?)";
					   
			pstmt = conn.prepareStatement( sql );
			pstmt.setLong( 1, getEnergyCompanyID() );
			rset = pstmt.executeQuery();			
			int callNo = 0;
			
			if (rset.next()) {
				String callNoStr = rset.getString( "CallNumber" );
				
				try {
					callNo = Integer.parseInt( callNoStr );
				}
				catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}
			
			setAttribute( "NEXT_CALL_NUMBER", new Integer(++callNo) );
			
			// Retrieve the next available order number from the database
			sql = "SELECT OrderNumber FROM WorkOrderBase WHERE OrderID = "
					   + "(SELECT MAX(WorkOrderID) FROM ECToWorkOrderMapping "
					   + "WHERE EnergyCompanyID = ?)";
					   
			pstmt = conn.prepareStatement( sql );
			pstmt.setLong( 1, getEnergyCompanyID() );
			rset = pstmt.executeQuery();
			int orderNo = 0;
			
			if (rset.next()) {
				String orderNoStr = rset.getString( "OrderNumber" );
				
				try {
					orderNo = Integer.parseInt( orderNoStr );
				}
				catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}
			
			setAttribute( "NEXT_ORDER_NUMBER", new Integer(++orderNo) );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (pstmt != null) pstmt.close();
				if (rset != null) rset.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
