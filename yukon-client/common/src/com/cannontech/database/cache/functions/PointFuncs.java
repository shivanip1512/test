package com.cannontech.database.cache.functions;

import java.util.Iterator;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;

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
	
}
