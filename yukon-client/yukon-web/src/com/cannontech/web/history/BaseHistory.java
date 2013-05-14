package com.cannontech.web.history;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2001 3:34:59 PM)
 * @author: 
 */
public class BaseHistory {
	protected java.sql.Connection conn = null;
	protected java.sql.Statement stmt = null;
/**
 * EnergyExchangeHistory constructor comment.
 */
public BaseHistory() {
	super();
}
/**
 * EnergyExchangeHistory constructor comment.
 */
public BaseHistory(String dbAlias) {
	super();
	getConnection(dbAlias);
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 3:46:45 PM)
 */
public void gc() {
	try {
		if (conn != null) conn.close();
		if (stmt != null) stmt.close();

		com.cannontech.clientutils.CTILogger.info("Connection closed");
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 3:39:28 PM)
 */
public void getConnection(String dbAlias) {
	try {
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();

		com.cannontech.clientutils.CTILogger.info("Connected with the datebase");
	}
	catch (java.sql.SQLException se) {
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		try {
			if (conn != null) conn.close();
			if (stmt != null) stmt.close();
		}
		catch (java.sql.SQLException se2) {
			com.cannontech.clientutils.CTILogger.error( se2.getMessage(), se2 );
		}
	}
}
}
