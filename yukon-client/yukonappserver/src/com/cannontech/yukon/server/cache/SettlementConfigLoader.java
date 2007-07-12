/*
 * Created on Dec 19, 2003
 */
package com.cannontech.yukon.server.cache;

import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.StopWatch;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.db.company.SettlementConfig;

/**
 * Loads up all the LiteTags
 * @author snebben
 */
public class SettlementConfigLoader implements Runnable {
	
	private static final String SQL = "select ConfigID, FieldName, FieldValue, YukonDefID, Description, EntryID, RefEntryID from " + SettlementConfig.TABLE_NAME + " order by YukonDefID, FieldName"; 
	
	private List _allSettlementConfigs;
	private Map _allSettlementConfigsMap;
	private String _dbAlias;
	
	public SettlementConfigLoader(List allSettlementConfigs_, Map allSettlementConfigsMap_, String databaseAlias) {
		_allSettlementConfigs = allSettlementConfigs_;
		_allSettlementConfigsMap = allSettlementConfigsMap_;
		_dbAlias = databaseAlias;
	}
	
	public void run() {
		StopWatch sw = new StopWatch().start();

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;

		try {
			conn = PoolManager.getInstance().getConnection(_dbAlias);
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SQL);

			while (rset.next()) {
				int configID = rset.getInt(1);
				String fieldName = rset.getString(2);
				String fieldValue = rset.getString(3);
				int yukonDefID = rset.getInt(4);
				String desc = rset.getString(5);
				int entryID = rset.getInt(6);
				int refEntryID = rset.getInt(7);

				LiteSettlementConfig lsc = new LiteSettlementConfig(configID, fieldName, fieldValue, yukonDefID, desc, entryID, refEntryID);
				_allSettlementConfigs.add(lsc);
				_allSettlementConfigsMap.put( new Integer(configID), lsc);
			}
		} catch (java.sql.SQLException e) {
			CTILogger.error(e.getMessage(), e);
		} finally {
			SqlUtils.close(rset, stmt, conn );
		}
		CTILogger.info(sw.stop().getElapsedTime() * 0.001 + " Secs for SettlementConfigLoader (" + _allSettlementConfigs.size() + " loaded)");
	}
}
