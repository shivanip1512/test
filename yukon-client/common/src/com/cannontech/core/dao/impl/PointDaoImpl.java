package com.cannontech.core.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class PointDaoImpl implements PointDao {
    
    private PaoDao paoDao;
    private IDatabaseCache databaseCache;
    
/**
 * PointFuncs constructor comment.
 */
public PointDaoImpl() {
	super();
}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PointDao#getLitePoint(int)
 */
public LitePoint getLitePoint(int pointID) 
{
	synchronized( databaseCache )
	{
		return (LitePoint) 
			databaseCache.getAllPointsMap().get( new Integer(pointID) );
	}
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.PointDao#getMaxPointID()
 */
public int getMaxPointID()
{
	synchronized( databaseCache )
	{
		java.util.List points = databaseCache.getAllPoints();
		java.util.Collections.sort( points, com.cannontech.database.data.lite.LiteComparators.litePointIDComparator );

		return ((LitePoint)points.get(points.size() - 1)).getPointID();
	}

}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.PointDao#getLitePointsByUOMID(int[], int[])
 */
public List getLitePointsByUOMID(int[] uomIDs, int[] types) 
{
   java.util.ArrayList pointList = new java.util.ArrayList(32);
   
   synchronized( databaseCache ) {
      java.util.List points = databaseCache.getAllPoints(); 
      for( int i = 0; i < points.size(); i++ ) {      
		LitePoint litePoint = (LitePoint)points.get(i);
		for( int j = 0; j < uomIDs.length; j++ ) {
            if( litePoint.getUofmID() == uomIDs[j] ) {
            	for (int k=0; k < types.length; k ++) {
            	   if (litePoint.getLiteType() == types[k]) {
   						LiteYukonPAObject liteDevice = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
   						if (DeviceClasses.isCoreDeviceClass(liteDevice.getPaoClass())) {
   							pointList.add( litePoint );
   							break;	
   						}
            	   }
            	}   
            }
		}
      }
   }
   Collections.sort(pointList, LiteComparators.liteStringComparator);
   return pointList;
}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointName(int)
     */
	public String getPointName(int id) 
	{
		synchronized(databaseCache) 
		{
			LitePoint lp =
				(LitePoint)databaseCache.getAllPointsMap().get( new Integer(id) );
				
			if( lp != null )
				return lp.getPointName();
			else
				return null;
		}
	}

	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointLimit(int)
     */
	public LitePointLimit getPointLimit(int pointID) {
		synchronized(databaseCache) {
			Iterator iter = databaseCache.getAllPointLimits().iterator();
			while(iter.hasNext()) {
				LitePointLimit lpl = (LitePointLimit) iter.next();
				if( lpl.getPointID() == pointID ) {
					return lpl;
				}
			}
		}	
		
		return null;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointUnit(int)
     */
	public LitePointUnit getPointUnit(int pointID) {
		synchronized(databaseCache) {
			Iterator iter = databaseCache.getAllPointsUnits().iterator();
			while(iter.hasNext()) {
				LitePointUnit lpu = (LitePointUnit) iter.next();
				if( lpu.getPointID() == pointID ) {
					return lpu;
				}
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getStateGroup(int)
     */
	public LiteStateGroup getStateGroup( int stateGroupID ) 
	{
		return (LiteStateGroup)
			databaseCache.getAllStateGroupMap().get( new Integer(stateGroupID) );
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getPointIDByDeviceID_Offset_PointType(int, int, int)
     */
	public int getPointIDByDeviceID_Offset_PointType(int deviceID, int pointOffset, int pointType)
	{
		LitePoint [] litePoints = paoDao.getLitePointsForPAObject(deviceID);
		for (int i = 0; i < litePoints.length; i++)
		{
			LitePoint lp = litePoints[i];
			if( lp.getPointOffset() == pointOffset && pointType == lp.getPointType())
				return lp.getPointID();
		}
	
		return PointTypes.SYS_PID_SYSTEM; //not found
	}
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#retrieveCICustomerPointData(int)
     */
	public Double retrieveCICustomerPointData(int pointID)
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
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.PointDao#getCapBankMonitorPoints(com.cannontech.database.data.capcontrol.CapBank)
     */
	public List getCapBankMonitorPoints(CapBank capBank) {
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
			LitePoint p = getLitePoint(point.getPointId().intValue());
			monitorPoint.setPointName(p.getPointName());
			monitorPoint.setOverrideFdrLimits(false);
			monitorPointList.add(monitorPoint);
		}
			return monitorPointList;
	}
	public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
