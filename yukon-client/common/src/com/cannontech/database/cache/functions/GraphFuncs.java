package com.cannontech.database.cache.functions;

import java.util.Iterator;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteGraphDefinition;

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
}
