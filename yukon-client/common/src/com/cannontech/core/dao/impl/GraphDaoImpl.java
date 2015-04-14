package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.yukon.IDatabaseCache;

public final class GraphDaoImpl implements GraphDao {

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;

    public static final YukonRowMapper<LiteGraphDefinition> liteGraphMapper = new YukonRowMapper<LiteGraphDefinition>() {
        @Override
        public LiteGraphDefinition mapRow(YukonResultSet rs) throws SQLException {

            int graphId = rs.getInt("GraphDefinitionId");
            String name = rs.getString("Name");
            LiteGraphDefinition definition = new LiteGraphDefinition(graphId, name);

            return definition;
        }
    };

    @Override
    public LiteGraphDefinition getLiteGraphDefinition(int graphDefinitionId) {

        synchronized (databaseCache) {
            Iterator<LiteGraphDefinition> iter = databaseCache.getAllGraphDefinitions().iterator();
            while(iter.hasNext()) {
                LiteGraphDefinition liteGraph = iter.next();
                if (liteGraph.getLiteID() == graphDefinitionId) {
                    return liteGraph;
                }
            }
        }

        return null;
    }

    @Override
    public LiteGraphDefinition retrieveLiteGraphDefinition(int graphDefinitionId) throws NotFoundException{
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT GraphDefinitionId, Name");
            sql.append("FROM GraphDefinition WHERE GraphDefinitionId").eq(graphDefinitionId);
            return jdbcTemplate.queryForObject(sql, liteGraphMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A GraphDefinition with id " + graphDefinitionId + " cannot be found.");
        }
    }
    
    @Override
    public List<LiteYukonPAObject> getLiteYukonPaobjects(int graphDefinitionId) {

        List<GraphDataSeries> allSeries = getGraphDataSeries(graphDefinitionId);
        List<LiteYukonPAObject> paos = new ArrayList<>(allSeries.size());

        for (GraphDataSeries series: allSeries) {
            int pointId = series.getPointID();
            LitePoint point = pointDao.getLitePoint(pointId);
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(point.getPaobjectID());
            paos.add(pao);
        }

        return paos;
    }

    @Override
    public List<GraphDataSeries> getAllGraphDataSeriesByType(int type) {

        List<GraphDataSeries> graphDataSeries = new ArrayList<>();

        synchronized (databaseCache) {
            Iterator<LiteGraphDefinition> iter = databaseCache.getAllGraphDefinitions().iterator();
            while (iter.hasNext()) {
                LiteGraphDefinition liteGdef = iter.next();
                List<GraphDataSeries> allSeries = getGraphDataSeries(liteGdef.getLiteID());
                for (GraphDataSeries series : allSeries) {
                    if ((series.getType() & type) == type) {
                        graphDataSeries.add(series);
                    }
                }
            }
        }
        return graphDataSeries;
    }

    @Override
    public List<LiteGraphDefinition> getGraphDefinitionsForUser(int userId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select gd.GraphDefinitionId, gd.Name");
        sql.append("from GraphDefinition gd, GraphCustomerList gcl, Customer c, Contact co");
        sql.append("where gd.GraphDefinitionId = gcl.GraphDefinitionId");
        sql.append("and gcl.CustomerId = c.CustomerId");
        sql.append("and c.PrimaryContactId = co.ContactId");
        sql.append("and co.LoginId").eq(userId);
        sql.append("order by gd.Name");

        List<LiteGraphDefinition> graphs = jdbcTemplate.query(sql, liteGraphMapper);

        return graphs;
    }

    @Override
    public List<LiteGraphDefinition> getGraphDefinitions() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select GraphDefinitionId, Name");
        sql.append("from GraphDefinition");
        sql.append("order by Name");

        List<LiteGraphDefinition> graphs = jdbcTemplate.query(sql, liteGraphMapper);

        return graphs;
    }

    @Override
    public List<GraphDataSeries> getGraphDataSeries(final int graphDefinitionId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select gds.GraphDataSeriesId, gds.Type, gds.PointId, gds.Label, gds.Axis, gds.Color, ypo.PaoName, gds.Multiplier, gds.Renderer, gds.MoreData");
        sql.append("from GraphDataSeries gds");
        sql.append("join Point p on p.PointId = gds.PointId");
        sql.append("join YukonPAObject ypo on ypo.PAObjectId = p.PAObjectId");
        sql.append("where gds.GraphDefinitionId").eq(graphDefinitionId);
        sql.append("order by p.PointOffset");

        List<GraphDataSeries> series = jdbcTemplate.query(sql, new YukonRowMapper<GraphDataSeries>() {
            @Override
            public GraphDataSeries mapRow(YukonResultSet rs) throws SQLException {

                GraphDataSeries dataSeries = new GraphDataSeries();

                dataSeries.setGraphDataSeriesID(rs.getInt("GraphDataSeriesId"));
                dataSeries.setGraphDefinitionID(graphDefinitionId);
                dataSeries.setType(rs.getInt("Type"));
                dataSeries.setPointID(rs.getInt("PointId"));
                dataSeries.setLabel(rs.getString("Label"));
                dataSeries.setAxis(rs.getString("Axis").charAt(0));
                dataSeries.setColor(rs.getInt("Color"));
                dataSeries.setDeviceName(rs.getString("PaoName"));
                dataSeries.setMultiplier(rs.getDouble("Multiplier"));
                dataSeries.setRenderer(rs.getInt("Renderer"));
                dataSeries.setMoreData(rs.getString("MoreData"));

                return dataSeries;
            }
        });

        return series;
    }

}