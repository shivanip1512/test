/*
 * Created on Dec 19, 2003
 */
package com.cannontech.yukon.server.cache;

import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StopWatch;
import com.cannontech.database.data.lite.LiteTag;

/**
 * Loads up all the LiteTags
 * @author aaron
 */
public class TagLoader implements Runnable {
	
	private static final String SQL = "select TagID,TagName,TagLevel,Inhibit,ColorID,ImageID from Tags order by TagLevel";
	
	private List _allTags;
	private String _dbAlias;
	
	public TagLoader(List allTags, String databaseAlias) {
		_allTags = allTags;
		_dbAlias = databaseAlias;
	}
	
	public void run() {
		StopWatch sw = new StopWatch().start();

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;

		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(_dbAlias);
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SQL);

			while (rset.next()) {
				int tagID = rset.getInt(1);
				String tagName = rset.getString(2);
				int tagLevel = rset.getInt(3);
				boolean inhibit = CtiUtilities.isTrue(new Character(rset.getString(4).charAt(0)));
				int colorID = rset.getInt(5);
				int imageID = rset.getInt(6);

				LiteTag lt = new LiteTag(tagID, tagName, tagLevel, inhibit, colorID, imageID);
				_allTags.add(lt);
			}
		} catch (java.sql.SQLException e) {
			com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (java.sql.SQLException e) {
				com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
			}
		}
		CTILogger.info(sw.stop().getElapsedTime() * 0.001 + " Secs for TagLoader (" + _allTags.size() + " loaded)");
	}
}
