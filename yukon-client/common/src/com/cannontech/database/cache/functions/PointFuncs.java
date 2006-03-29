package com.cannontech.database.cache.functions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.db.point.RawPointHistory;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class PointFuncs {
/**
 * PointFuncs constructor comment.
 */
private PointFuncs() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
public static LitePoint getLitePoint(int pointID) 
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		return (LitePoint) 
			cache.getAllPointsMap().get( new Integer(pointID) );
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return int
 */
public static int getMaxPointID()
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort( points, com.cannontech.database.data.lite.LiteComparators.litePointIDComparator );

		return ((LitePoint)points.get(points.size() - 1)).getPointID();
	}

}

/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint[]
 * @param uomID int
 */
public static LitePoint[] getLitePointsByUOMID(int[] uomIDs) 
{
   DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
   java.util.ArrayList pointList = new java.util.ArrayList(32);
   
   synchronized( cache )
   {
      java.util.List points = cache.getAllPoints();
      //java.util.Collections.sort( points, com.cannontech.database.data.lite.LiteComparators.litePointIDComparator );
      
      for( int i = 0; i < points.size(); i++ )
      {      
			LitePoint litePoint = (LitePoint)points.get(i);
			
			for( int j = 0; j < uomIDs.length; j++ )
            if( litePoint.getUofmID() != uomIDs[j] )
            {
               pointList.add( litePoint );
               break;
            }
            
      }

   }

   LitePoint retVal[] = new LitePoint[ pointList.size() ];
   pointList.toArray( retVal );
   
   return retVal;
}

	/**
	 * Returns the name of the point with a given id.
	 * @param id
	 * @return String
	 */
	public static String getPointName(int id) 
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) 
		{
			LitePoint lp =
				(LitePoint)cache.getAllPointsMap().get( new Integer(id) );
				
			if( lp != null )
				return lp.getPointName();
			else
				return null;
		}
	}

	
	/**
	 * Finds the lite point limit given a point id
	 * @param pointID
	 * @return LitePointLimit
	 */
	public static LitePointLimit getPointLimit(int pointID) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) {
			Iterator iter = cache.getAllPointLimits().iterator();
			while(iter.hasNext()) {
				LitePointLimit lpl = (LitePointLimit) iter.next();
				if( lpl.getPointID() == pointID ) {
					return lpl;
				}
			}
		}	
		
		return null;
	}

	public static LitePointUnit getPointUnit(int pointID) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized(cache) {
			Iterator iter = cache.getAllPointsUnits().iterator();
			while(iter.hasNext()) {
				LitePointUnit lpu = (LitePointUnit) iter.next();
				if( lpu.getPointID() == pointID ) {
					return lpu;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the LiteStateGroup for a certain point by a StateGroupID
	 * @param id
	 * @return LiteStateGroup
	 */
	public static LiteStateGroup getStateGroup( int stateGroupID ) 
	{
		return (LiteStateGroup)
			DefaultDatabaseCache.getInstance().getAllStateGroupMap().get( new Integer(stateGroupID) );
	}

	/**
	 * Returns a pointID (int), where deviceID is used to gain a collection of LitePoints.
	 * PointOffset and PointType is used to select one of the LitePoints.
	 */
	public static int getPointIDByDeviceID_Offset_PointType(int deviceID, int pointOffset, int pointType)
	{
		LitePoint [] litePoints = PAOFuncs.getLitePointsForPAObject(deviceID);
		for (int i = 0; i < litePoints.length; i++)
		{
			LitePoint lp = litePoints[i];
			if( lp.getPointOffset() == pointOffset && pointType == lp.getPointType())
				return lp.getPointID();
		}
	
		return PointTypes.SYS_PID_SYSTEM; //not found
	}
	/**
	 * Queries Rawpointhistory for the most recent entry for pointID.
	 * Use this function in when PointChangeCache does not give you the most recent PointData.
	 * @param pointID
	 * @return
	 */
	public static Double retrieveCICustomerPointData(int pointID)
	{
		Double data = new Double(0);
		String sqlString = "SELECT TIMESTAMP, VALUE FROM " + RawPointHistory.TABLE_NAME +
			" WHERE POINTID = " + pointID + 
			" ORDER BY TIMESTAMP DESC ";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);

			while (rset.next())
			{
				Timestamp ts = rset.getTimestamp(1);
				double value = rset.getDouble(2);

				data = new Double(value);
				break;
			}
		}
		catch (java.sql.SQLException e)
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}
			catch (java.sql.SQLException e)
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
		return data;
	}
	public static List getCapBankMonitorPoints(CapBank capBank) {
		List monitorPointList = new ArrayList();
		for (Iterator iter = capBank.getCcMonitorBankList().iterator(); iter.hasNext();) {
			CCMonitorBankList point = (CCMonitorBankList) iter.next();			
			
			CapBankMonitorPointParams monitorPoint = new CapBankMonitorPointParams();
			monitorPoint.setCapBankId(point.getCapBankId().intValue());
			monitorPoint.setPointId(point.getPointId().intValue());
			monitorPoint.setDisplayOrder(point.getDisplayOrder().intValue());
			if (point.getScannable().charValue() == 'Y') 
				monitorPoint.setInitScan(true);
			else
				monitorPoint.setInitScan(false);
			monitorPoint.setNINAvg(point.getNINAvg().longValue());
			monitorPoint.setLowerBandwidth(point.getLowerBandwidth().floatValue());
			monitorPoint.setUpperBandwidth(point.getUpperBandwidth().floatValue());			
			LitePoint p = PointFuncs.getLitePoint(point.getPointId().intValue());
			monitorPoint.setPointName(p.getPointName());
			monitorPoint.setOverrideFdrLimits(false);
			monitorPointList.add(monitorPoint);
		}
			return monitorPointList;
	}
	
}
