package com.cannontech.database.cache.functions;

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
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
public static com.cannontech.database.data.lite.LiteUnitMeasure getLiteUnitMeasure(int uomid) 
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
