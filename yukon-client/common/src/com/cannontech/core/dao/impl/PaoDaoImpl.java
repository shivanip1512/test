package com.cannontech.core.dao.impl;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
import java.util.List;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public final class PaoDaoImpl implements PaoDao 
{
    private IDatabaseCache databaseCache;
    
/**
 * PointFuncs constructor comment.
 */
public PaoDaoImpl() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return LitePoint
 * @param pointID int
 */
/* This method returns a HashTable that has a LiteYukonPAObject as the key and */
/*   an ArrayList of LitePoints as its values */
/*
public java.util.Hashtable getAllLitePAOWithPoints()
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	java.util.Hashtable paoTable = null;

	synchronized (cache)
	{
		java.util.List paos = cache.getAllYukonPAObjects();
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort(paos, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		LitePoint litePoint = null;
		LiteYukonPAObject litePAO = null;

		paoTable = new java.util.Hashtable( paos.size() );
		
		for (int i = 0; i < paos.size(); i++)
		{
			litePAO = (LiteYukonPAObject) paos.get(i);

			java.util.ArrayList pointList = new java.util.ArrayList( points.size() );
			
			for (int j = 0; j < points.size(); j++)
			{				
				litePoint = (LitePoint) points.get(j);				
				if (litePoint.getPaobjectID() == litePAO.getYukonID())
					pointList.add( litePoint );
			}

			//add the liteDevice along with its litePoints
			paoTable.put( litePAO, pointList );

		}
	}

	return paoTable;
}
*/
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getAllPointIDsAndTypesForPAObject(int)
 */
// the format returned is :   
//			int[X][0] == id
//			int[X][1] == lite type
public int[][] getAllPointIDsAndTypesForPAObject( int deviceid )
{
	synchronized(databaseCache)
	{
		java.util.List points = databaseCache.getAllPoints();
		java.util.Collections.sort(points);
		
		int[][] ids = new int[points.size()][2];

		int pointCount = 0;
		for(int i=0;i<points.size();i++)
		{
			if( deviceid == ((LitePoint)points.get(i)).getPaobjectID() )
			{
				ids[pointCount][0] = ((LitePoint)points.get(i)).getPointID();
				ids[pointCount][1] = ((LitePoint)points.get(i)).getPointType();
				pointCount++;
			}
			
		}		

		int[][] realPts = new int[pointCount][2];
		System.arraycopy( ids, 0, realPts, 0, realPts.length );
		return realPts;
	}

}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getLitePointsForPAObject(int)
 */
public LitePoint[] getLitePointsForPAObject( int paoID )
{
	synchronized(databaseCache)
	{
		java.util.List points = databaseCache.getAllPoints();

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
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getLiteYukonPAO(int)
 */
public LiteYukonPAObject getLiteYukonPAO( int paoID )
{
	synchronized( databaseCache )
	{
		LiteYukonPAObject liteYukonPAObject = (LiteYukonPAObject) 
			databaseCache.getAllPAOsMap().get( new Integer(paoID) );
		return liteYukonPAObject;
	}
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getAllCapControlSubBuses()
 */
public List getAllCapControlSubBuses() {
	List subBusList = null;
	synchronized (databaseCache)
	{
		subBusList = databaseCache.getAllCapControlSubBuses();
	}

	return subBusList;
}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getMaxPAOid()
 */
public int getMaxPAOid()
{
	synchronized( databaseCache )
	{
		java.util.List paobjects = databaseCache.getAllYukonPAObjects();
		java.util.Collections.sort( paobjects, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectIDComparator );

		return ((LiteYukonPAObject)paobjects.get(paobjects.size() - 1)).getYukonID();
	}

}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getYukonPAOName(int)
 */
public String getYukonPAOName( int paoID )
{
	LiteYukonPAObject pao = getLiteYukonPAO( paoID );
	if( pao != null )
		return pao.getPaoName();
	else
		return null;
}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getAllLiteRoutes()
 */
public LiteYukonPAObject[] getAllLiteRoutes()
{
	//Get an instance of the databaseCache.
	java.util.ArrayList routeList = new java.util.ArrayList(10);
	synchronized(databaseCache)
	{
		java.util.List routes = databaseCache.getAllRoutes();
		java.util.Collections.sort( routes, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		for (int i = 0; i < routes.size(); i++)
		{
			LiteYukonPAObject litePao = (LiteYukonPAObject)routes.get(i);
			routeList.add(litePao);	
		}
	}
	LiteYukonPAObject retVal[] = new LiteYukonPAObject[routeList.size()];
	routeList.toArray( retVal );
	return retVal;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getRoutesByType(int[])
 */
public LiteYukonPAObject[] getRoutesByType(int[] routeTypes) 
{   
	java.util.ArrayList routeList = new java.util.ArrayList(10);
	synchronized(databaseCache)
	{
		java.util.List routes = databaseCache.getAllRoutes();
		java.util.Collections.sort( routes, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
      
		for( int i = 0; i < routes.size(); i++ )
		{      
			LiteYukonPAObject litePao = (LiteYukonPAObject)routes.get(i);
			
			for( int j = 0; j < routeTypes.length; j++ )
				if( litePao.getType() != routeTypes[j] )
				{
					routeList.add( litePao);
					break;
				}
		}
	}

	LiteYukonPAObject retVal[] = new LiteYukonPAObject[ routeList.size() ];
	routeList.toArray( retVal );
   
	return retVal;
}


/* (non-Javadoc)
 * @see com.cannontech.core.dao.PaoDao#getAllUnusedCCPAOs(java.lang.Integer)
 */
public LiteYukonPAObject[] getAllUnusedCCPAOs( Integer ignoreID ) {
		
	synchronized( databaseCache ) {

		List lPaos = databaseCache.getAllUnusedCCDevices();
		
		LiteYukonPAObject retVal[] = (LiteYukonPAObject[])
			lPaos.toArray( new LiteYukonPAObject[ lPaos.size() + 1 ] );

		retVal[lPaos.size()] = getLiteYukonPAO( ignoreID.intValue() );   
		return retVal;
	}
}

public void setDatabaseCache(IDatabaseCache databaseCache) {
    this.databaseCache = databaseCache;
}
}
