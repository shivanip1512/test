package com.cannontech.esub.web;

import java.util.logging.Logger;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author alauinger
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Authenticator {

	public static SessionInfo login(String username, String password) {

		com.cannontech.database.data.web.User retVal = null;

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;

		try {
			conn =
				com
					.cannontech
					.database
					.PoolManager
					.getInstance()
					.getConnection(
					CtiUtilities.getDatabaseAlias());

			if (conn == null) {
				Logger.global.severe("Could not get database connection");
				return null;
			}

			stmt = conn.createStatement();
			rset =
				stmt.executeQuery(
					"SELECT LoginID FROM CustomerLogin WHERE Username='"
						+ username
						+ "' AND Password='"
						+ password
						+ "'");

			if (rset.next()) {
				retVal = new com.cannontech.database.data.web.User();
				retVal.setId(rset.getLong(1));

				stmt.close();

				if (retVal != null) {
					retVal.setDbConnection(conn);
					retVal.retrieve();
					retVal.setDbConnection(null);
				}

				SessionInfo info = new SessionInfo();
				info.setUser(retVal);
				return info;
			}
		} catch (java.sql.SQLException e) {
			Logger.global.severe(e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}

		return null;

	}
}
