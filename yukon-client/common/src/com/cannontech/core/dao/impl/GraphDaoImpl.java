package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Graph related data retrieval functions
 * @author alauinger
 */
public final class GraphDaoImpl implements GraphDao {
    
    private PaoDao paoDao;
    private IDatabaseCache databaseCache;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
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
		
        PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
        PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
        
        for (int i = 0; i < allSeries.length; i++) {
            int pointId = allSeries[i].getPointID();
            LitePoint point = pointDao.getLitePoint(pointId);
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(point.getPaobjectID());
            liteYukonPaobjectsVector.add(pao);
        }

		return liteYukonPaobjectsVector;
	}
	
	/**
     * Returns a new List<GraphDataSeries>
     */
	public List<GraphDataSeries> getAllGraphDataSeries()
	{
		List<GraphDataSeries> graphDataSeriesList = new ArrayList<GraphDataSeries>();
		
		synchronized(databaseCache)
		{
			Iterator<LiteGraphDefinition> iter = databaseCache.getAllGraphDefinitions().iterator();
			while(iter.hasNext())
			{
				LiteGraphDefinition liteGdef = iter.next();
				GraphDataSeries [] allGDSSeries = GraphDataSeries.getAllGraphDataSeries(new Integer(liteGdef.getLiteID()));
				for(int i = 0; i < allGDSSeries.length; i++)
				{
					graphDataSeriesList.add(allGDSSeries[i]);
				}
			}
		}
		return graphDataSeriesList;
	}
	
	/**
	 * Returns a new List<GraphDataSeries>
	 */
	public List<GraphDataSeries> getAllGraphDataSeries(int type_)
	{
		List<GraphDataSeries> graphDataSeriesList = new ArrayList<GraphDataSeries>();
		
		synchronized(databaseCache)
		{
			Iterator<LiteGraphDefinition> iter = databaseCache.getAllGraphDefinitions().iterator();
			while(iter.hasNext())
			{
				LiteGraphDefinition liteGdef = iter.next();
				GraphDataSeries [] allGDSSeries = GraphDataSeries.getAllGraphDataSeries(new Integer(liteGdef.getLiteID()));
				for(int i = 0; i < allGDSSeries.length; i++)
				{
					if( (allGDSSeries[i].getType().intValue() & type_) == type_)
						graphDataSeriesList.add(allGDSSeries[i]);
				}
			}
		}
		return graphDataSeriesList;
	}

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Override
    public List<LiteGraphDefinition> getGraphDefinitionsForUser(int yukonUserId) {

        String sql = "SELECT gd.GraphDefinitionId, gd.name " + 
            "FROM GraphDefinition gd, GraphCustomerList gcl, Customer c, Contact co " + 
            "WHERE gd.GraphDefinitionId = gcl.GraphDefinitionId " + 
            "AND gcl.CustomerId = c.customerId " + 
            "AND c.primaryContactId = co.contactId " + 
            "AND co.loginId = ? " + 
            "ORDER BY gd.name";

        List<LiteGraphDefinition> liteGraph = simpleJdbcTemplate.query(
                       sql,
                       new ParameterizedRowMapper<LiteGraphDefinition>() {

                           @Override
                           public LiteGraphDefinition mapRow(
                                   ResultSet rs,
                                   int rowNum)
                                   throws SQLException {

                               int graphId = rs.getInt("GraphDefinitionId");
                               String name = rs.getString("name");

                               LiteGraphDefinition definition = new LiteGraphDefinition(graphId,
                                                                                        name);

                               return definition;
                           }
                       },
                       yukonUserId);

        return liteGraph;
    }
    
}
