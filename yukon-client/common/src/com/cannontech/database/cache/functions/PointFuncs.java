package com.cannontech.database.cache.functions;

import com.cannontech.database.data.lite.LitePoint;
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
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort( points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator );
		
		for( int j = 0; j < points.size(); j++ )
		{
			if( pointID == ((LitePoint)points.get(j)).getPointID() )
				return (LitePoint)points.get(j);
		}
	}

	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return int
 */
public static int getMaxPointID()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
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
   com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
   java.util.ArrayList pointList = new java.util.ArrayList(20);
   
   synchronized( cache )
   {
      java.util.List points = cache.getAllPoints();
      java.util.Collections.sort( points, com.cannontech.database.data.lite.LiteComparators.litePointIDComparator );
      
      try
      {
         com.cannontech.common.util.NativeIntVector ptIds = 
            com.cannontech.database.db.point.PointUnit.getAllPointIDsByUOMID( uomIDs );
      
         for( int i = 0; i < ptIds.size(); i++ )
         {      
            for( int j = 0; j < points.size(); j++ )
            {
               LitePoint litePoint = (LitePoint)points.get(j);
                  
               if( ptIds.elementAt(i) == litePoint.getPointID() )
                  pointList.add( litePoint );
            }
         }
      }
      catch( java.sql.SQLException sq )
      {         
         sq.printStackTrace( System.out );
      }
      
   }

   LitePoint retVal[] = new LitePoint[ pointList.size() ];
   pointList.toArray( retVal );
   
   return retVal;
}

}
