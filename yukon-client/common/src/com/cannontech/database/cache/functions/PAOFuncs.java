package com.cannontech.database.cache.functions;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
import com.cannontech.database.data.lite.LitePoint;

public final class PAOFuncs 
{

/**
 * PointFuncs constructor comment.
 */
private PAOFuncs() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
/* This method returns a HashTable that has a LiteYukonPAObject as the key and */
/*   an ArrayList of LitePoints as its values */
public static java.util.Hashtable getAllLitePAOWithPoints()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.Hashtable paoTable = null;

	synchronized (cache)
	{
		java.util.List paos = cache.getAllYukonPAObjects();
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort(paos, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		com.cannontech.database.data.lite.LiteYukonPAObject litePAO = null;

		paoTable = new java.util.Hashtable( paos.size() );
		
		for (int i = 0; i < paos.size(); i++)
		{
			litePAO = (com.cannontech.database.data.lite.LiteYukonPAObject) paos.get(i);

			java.util.ArrayList pointList = new java.util.ArrayList( points.size() );
			
			for (int j = 0; j < points.size(); j++)
			{				
				litePoint = (com.cannontech.database.data.lite.LitePoint) points.get(j);				
				if (litePoint.getPaobjectID() == litePAO.getYukonID())
					pointList.add( litePoint );
			}

			//add the liteDevice along with its litePoints
			paoTable.put( litePAO, pointList );

		}
	}

	return paoTable;
}
/**
 * This method was created in VisualAge.
 * @return int[][]
 */
// the format returned is :   
//			int[X][0] == id
//			int[X][1] == lite type
public static int[][] getAllPointIDsAndTypesForPAObject( int deviceid )
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized(cache)
	{
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort(points);
		
		int[][] ids = new int[points.size()][2];

		int pointCount = 0;
		for(int i=0;i<points.size();i++)
		{
			if( deviceid == ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPaobjectID() )
			{
				ids[pointCount][0] = ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPointID();
				ids[pointCount][1] = ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPointType();
				pointCount++;
			}
			
		}		

		int[][] realPts = new int[pointCount][2];
		System.arraycopy( ids, 0, realPts, 0, realPts.length );
		return realPts;
	}

}
/**
 * This method was created in VisualAge.
 * @return String
 */
public static com.cannontech.database.data.lite.LitePoint[] getLitePointsForPAObject( int paoID )
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized(cache)
	{
		java.util.List points = cache.getAllPoints();

		//a mutable lite point used for comparisons
		final LitePoint dummyLitePoint = 
						new LitePoint(Integer.MIN_VALUE,
							"**DUMMY**", 0, paoID, 0, 0 );
		
		//a Vector only needed to store temporary things
		java.util.List destList = new java.util.Vector(10);

		//binarySearchRepetition will sort and search the list
		com.cannontech.common.util.CtiUtilities.binarySearchRepetition(
					points,
					dummyLitePoint,
					com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator,
					destList );

		LitePoint[] litePoints = new LitePoint[0];
		return (LitePoint[])destList.toArray( litePoints );
	}

}
/**
 * This method was created in VisualAge.
 * @return String
 */
public static com.cannontech.database.data.lite.LiteYukonPAObject getLiteYukonPAO( int paoID )
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized(cache)
	{
		java.util.List paos = cache.getAllYukonPAObjects();
		java.util.Collections.sort(paos);
		String name = null;
		
		for(int i = 0; i < paos.size(); i++)
		{
			if( paoID == ((com.cannontech.database.data.lite.LiteYukonPAObject)paos.get(i)).getYukonID() )
			{
				return (com.cannontech.database.data.lite.LiteYukonPAObject)paos.get(i);
			}
			
		}

		return null;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return int
 */
public static int getMaxPAOid()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List paobjects = cache.getAllYukonPAObjects();
		java.util.Collections.sort( paobjects, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectIDComparator );

		return ((com.cannontech.database.data.lite.LiteYukonPAObject)paobjects.get(paobjects.size() - 1)).getYukonID();
	}

}
/**
 * This method was created in VisualAge.
 * @return String
 */
public static String getYukonPAOName( int paoID )
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized(cache)
	{
		java.util.List paos = cache.getAllYukonPAObjects();
		java.util.Collections.sort(paos);
		String name = null;
		
		for(int i = 0; i < paos.size(); i++)
		{
			if( paoID == ((com.cannontech.database.data.lite.LiteYukonPAObject)paos.get(i)).getYukonID() )
			{
				name = ((com.cannontech.database.data.lite.LiteYukonPAObject)paos.get(i)).getPaoName();
				break;
			}
			
		}

		return name;
	}

}
}
