/*
 * Created on Oct 1, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.cache.functions;

import java.sql.Connection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.point.RawPointHistory;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RPHFuncs
{
	/**
	 * Returns a hashMap of key (POINTID) and value of RawPointHistory
	 * The data retrieved is the most recent found in the database.
	 * @param deviceID
	 * @return
	 */
	public static HashMap getRecentRPHData(Integer deviceID)
	{
		Date timerStart = null;
		Date timerStop = null;
	
		timerStart = new Date();
		HashMap rphData = new HashMap(15);
	
		String sql = "SELECT RPH.CHANGEID, RPH.POINTID, RPH.TIMESTAMP, RPH.QUALITY, RPH.VALUE " +
						" FROM RAWPOINTHISTORY RPH ";
					
						if( deviceID != null)
						{
							sql += ", POINT P, YUKONPAOBJECT PAO "+
								" WHERE P.POINTID = RPH.POINTID " +
								" AND PAO.PAOBJECTID = " + deviceID.intValue();
						}
						sql += " ORDER BY RPH.POINTID, TIMESTAMP DESC";

		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		Connection conn = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());		
			if( conn == null )
			{
				throw new IllegalStateException("Database connection should not be (null).");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());			
				rset = pstmt.executeQuery();
	
				int prevPointId = -1;
				while( rset.next() )
				{
					Integer pointID = new Integer(rset.getInt(2));
					if( pointID.intValue() != prevPointId)
					{
						Integer changeID = new Integer(rset.getInt(1));
						long ts = rset.getTimestamp(3).getTime();
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(new Date(ts));
						Integer quality = new Integer(rset.getInt(4));
						Double value = new Double(rset.getDouble(5));
					
						RawPointHistory rph = new RawPointHistory(changeID, pointID, cal, quality, value);
						rphData.put(pointID, rph);
						prevPointId = pointID.intValue();
					}
				}
			}		
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}
			timerStop = new java.util.Date();
			String message = " Secs for Currrent RPH Data Retrieval for ";
			if( deviceID != null)
				message += "DeviceID: " + deviceID.intValue();
			else
				message += "all Devices."; 
		
			CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + message );

		}
		return rphData;
	}
}
