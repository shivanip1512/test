package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.graph.GraphDataSeries;

/**
 * Graph related data retrieval functions
 * @author alauinger
 */
public final class GraphFuncs {
	private GraphFuncs() { }
	
	public static LiteGraphDefinition getLiteGraphDefinition(int id) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )	{
			Iterator iter = cache.getAllGraphDefinitions().iterator();
			while( iter.hasNext() ) {
				LiteGraphDefinition lGDef = (LiteGraphDefinition) iter.next();
				if( lGDef.getLiteID() == id ) {
					return lGDef;
				}
			}
		}	

		return null;
	}
	
	public static java.util.List getLiteYukonPaobjects(int gDefID)
	{
		GraphDataSeries[] allSeries = GraphDataSeries.getAllGraphDataSeries(new Integer(gDefID));
		java.util.List liteYukonPaobjectsVector = new java.util.Vector(allSeries.length);
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			Iterator iter = cache.getAllPoints().iterator();
			while(iter.hasNext())
			{
				LitePoint litePoint = (LitePoint) iter.next();
				for(int i = 0; i < allSeries.length; i++)
				{
					if( ((GraphDataSeries)allSeries[i]).getPointID().intValue() == litePoint.getLiteID())
					{
						LiteYukonPAObject litePaobject = PAOFuncs.getLiteYukonPAO(litePoint.getPaobjectID());
						if (! liteYukonPaobjectsVector.contains(litePaobject))
							liteYukonPaobjectsVector.add(litePaobject);
					}
				}
			}
		}
		return liteYukonPaobjectsVector;
	}
	
	public static java.util.List getAllGraphDataSeries()
	{
		List graphDataSeriesVector = new ArrayList();
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			Iterator iter = cache.getAllGraphDefinitions().iterator();
			while(iter.hasNext())
			{
				LiteGraphDefinition liteGdef = (LiteGraphDefinition) iter.next();
				GraphDataSeries [] allGDSSeries = GraphDataSeries.getAllGraphDataSeries(new Integer(liteGdef.getLiteID()));
				for(int i = 0; i < allGDSSeries.length; i++)
				{
					graphDataSeriesVector.add(allGDSSeries[i]);
				}
			}
		}
		return graphDataSeriesVector;
	}
	
	public static java.util.List getAllGraphDataSeries(int type_)
	{
		java.util.List graphDataSeriesVector = new java.util.Vector(10);
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			Iterator iter = cache.getAllGraphDefinitions().iterator();
			while(iter.hasNext())
			{
				LiteGraphDefinition liteGdef = (LiteGraphDefinition) iter.next();
				GraphDataSeries [] allGDSSeries = GraphDataSeries.getAllGraphDataSeries(new Integer(liteGdef.getLiteID()));
				for(int i = 0; i < allGDSSeries.length; i++)
				{
					if( (allGDSSeries[i].getType().intValue() & type_) == type_)
						graphDataSeriesVector.add(allGDSSeries[i]);
				}
			}
		}
		return graphDataSeriesVector;
	}	
}
