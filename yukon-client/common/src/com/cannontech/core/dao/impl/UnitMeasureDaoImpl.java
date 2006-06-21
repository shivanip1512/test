package com.cannontech.core.dao.impl;

import java.util.Iterator;

import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class UnitMeasureDaoImpl implements UnitMeasureDao {
    private IDatabaseCache databaseCache;
    
/**
 * UnitMeasureFuncs constructor comment.
 */
public UnitMeasureDaoImpl() {
	super();
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.UnitMeasureDao#getLiteUnitMeasureByPointID(int)
 */
public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID) {
	//find the lite point unit for this pointid then
	//use it to find the actual lite unit of measure
	synchronized(databaseCache) { 
		Iterator pUnitIter = databaseCache.getAllPointsUnits().iterator();
		
		while(pUnitIter.hasNext()) {
			LitePointUnit lpu = (LitePointUnit) pUnitIter.next();
			
			if( lpu.getPointID() == pointID ) {
				return getLiteUnitMeasure(lpu.getUomID());
			}
		}
	}
	return null;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.UnitMeasureDao#getLiteUnitMeasure(int)
 */
public LiteUnitMeasure getLiteUnitMeasure(int uomid) 
{
	synchronized( databaseCache )
	{
		java.util.List unitMeasures = databaseCache.getAllUnitMeasures();

		for(int i=0;i<unitMeasures.size();i++)
		{
			if( uomid == ((com.cannontech.database.data.lite.LiteUnitMeasure)unitMeasures.get(i)).getUomID() )
				return (com.cannontech.database.data.lite.LiteUnitMeasure)unitMeasures.get(i);
		}
	}

	return null;
}

public void setDatabaseCache(IDatabaseCache databaseCache) {
    this.databaseCache = databaseCache;
}
}
