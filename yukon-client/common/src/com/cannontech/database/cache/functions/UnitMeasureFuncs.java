package com.cannontech.database.cache.functions;

import java.util.Iterator;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteUnitMeasure;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class UnitMeasureFuncs {
/**
 * UnitMeasureFuncs constructor comment.
 */
private UnitMeasureFuncs() {
	super();
}

/**
 * Returns the unit of measure information for a given point id.
 * @param pointID
 * @return LiteUnitMeasure
 */
public static LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID) {
	//find the lite point unit for this pointid then
	//use it to find the actual lite unit of measure
						   
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized(cache) { 
		Iterator pUnitIter = cache.getAllPointsUnits().iterator();
		
		while(pUnitIter.hasNext()) {
			LitePointUnit lpu = (LitePointUnit) pUnitIter.next();
			
			if( lpu.getPointID() == pointID ) {
				return getLiteUnitMeasure(lpu.getUomID());
			}
		}
	}
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
public static LiteUnitMeasure getLiteUnitMeasure(int uomid) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List unitMeasures = cache.getAllUnitMeasures();

		for(int i=0;i<unitMeasures.size();i++)
		{
			if( uomid == ((com.cannontech.database.data.lite.LiteUnitMeasure)unitMeasures.get(i)).getUomID() )
				return (com.cannontech.database.data.lite.LiteUnitMeasure)unitMeasures.get(i);
		}
	}

	return null;
}
}
