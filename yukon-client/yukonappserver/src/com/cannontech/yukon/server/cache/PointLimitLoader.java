package com.cannontech.yukon.server.cache;

import java.util.List;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LitePointLimit;

/**
 * Loader for Point Limits
 * @author alauinger
 */
public class PointLimitLoader implements Runnable {
	private static final String sql =
		"SELECT PointID,LimitNumber,HighLimit,LowLimit,LimitDuration FROM PointLimits";

	private List<LitePointLimit> allPointLimits = null;
	private String dbAlias = null;

	public PointLimitLoader(List<LitePointLimit> allPointLimits, String dbAlias) {
		this.allPointLimits = allPointLimits;
		this.dbAlias = dbAlias;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		long timerStart = System.currentTimeMillis();
		long timerStop;

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;

		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);

			while (rset.next()) {
				int pointID = rset.getInt(1);
				int limitNumber = rset.getInt(2);
				double highLimit = rset.getDouble(3);
				double lowLimit = rset.getDouble(4);
				int limitDuration = rset.getInt(5);

				LitePointLimit lpl =
					new LitePointLimit(pointID, limitNumber, highLimit, lowLimit, limitDuration);
				allPointLimits.add(lpl);
			}
		} catch (java.sql.SQLException e) {
			com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
		} finally {
			SqlUtils.close(rset, stmt, conn );

			timerStop = System.currentTimeMillis();
			com.cannontech.clientutils.CTILogger.info(
				(timerStop - timerStart) * .001 + " Secs for PointLimitsLoader");

		}
	}
}
