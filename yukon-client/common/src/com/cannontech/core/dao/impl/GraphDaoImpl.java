package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Graph related data retrieval functions
 * @author alauinger
 */
public final class GraphDaoImpl implements GraphDao {
    
    private PaoDao paoDao;
    private IDatabaseCache databaseCache;
    
	public GraphDaoImpl() { }

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.GraphDao#getLiteGraphDefinition(int)
     */
	public LiteGraphDefinition getLiteGraphDefinition(int id) {
		synchronized( databaseCache )	{
			Iterator iter = databaseCache.getAllGraphDefinitions().iterator();
			while( iter.hasNext() ) {
				LiteGraphDefinition lGDef = (LiteGraphDefinition) iter.next();
				if( lGDef.getLiteID() == id ) {
					return lGDef;
				}
			}
		}	

		return null;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.GraphDao#getLiteYukonPaobjects(int)
     */
	public java.util.List getLiteYukonPaobjects(int gDefID)
	{
		GraphDataSeries[] allSeries = GraphDataSeries.getAllGraphDataSeries(new Integer(gDefID));
		java.util.List liteYukonPaobjectsVector = new java.util.Vector(allSeries.length);
		
        PointDao pointDao = DaoFactory.getPointDao();
        PaoDao paoDao = DaoFactory.getPaoDao();
        
        for (int i = 0; i < allSeries.length; i++) {
            int pointId = allSeries[i].getPointID();
            LitePoint point = pointDao.getLitePoint(pointId);
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(point.getPaobjectID());
            liteYukonPaobjectsVector.add(pao);
        }

		return liteYukonPaobjectsVector;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.GraphDao#getAllGraphDataSeries()
     */
	public java.util.List getAllGraphDataSeries()
	{
		List graphDataSeriesVector = new ArrayList();
		
		synchronized(databaseCache)
		{
			Iterator iter = databaseCache.getAllGraphDefinitions().iterator();
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
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.GraphDao#getAllGraphDataSeries(int)
     */
	public java.util.List getAllGraphDataSeries(int type_)
	{
		java.util.List graphDataSeriesVector = new java.util.Vector(10);
		
		synchronized(databaseCache)
		{
			Iterator iter = databaseCache.getAllGraphDefinitions().iterator();
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

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
}
