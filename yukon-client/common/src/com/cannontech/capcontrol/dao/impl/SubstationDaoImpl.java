package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;

public class SubstationDaoImpl implements SubstationDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private IDatabaseCache dbCache;
    
    /** SELECT QUERY */
    private final static String select = 
        "SELECT ypo.PAObjectId, ypo.PAOName, ypo.Type, ypo.Description, ypo.DisableFlag, "
            + "s.MapLocationId, s.voltReductionPointId " + 
        "FROM YukonPAObject ypo " + 
            "JOIN CapControlSubstation s ON s.SubstationID = ypo.PAObjectID";
    
    private static final YukonRowMapper<Substation> mapper = new YukonRowMapper<Substation>() {
        @Override
        public Substation mapRow(YukonResultSet rs) throws SQLException {
            
            Substation sub = new Substation(PaoIdentifier.of(rs.getInt("PAObjectId"), rs.getEnum("Type", PaoType.class)));
            sub.setName(rs.getString("PaoName"));
            sub.setDisabled(rs.getBoolean("DisableFlag"));
            sub.setDescription(rs.getString("Description"));
            sub.setMapLocationId(rs.getString("MapLocationId"));
            sub.setVoltReductionPointId(rs.getInt("VoltReductionPointId"));
            
            return sub;
        }
    };
    
    @Override
    public Substation findSubstationById(final int id) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(select);
        sql.append("WHERE PAObjectID").eq(id);
        
        return jdbcTemplate.queryForObject(sql, mapper);
    }
    
    /**
     * This method returns the Area ID that owns the given substation ID.
     */
    @Override
    public Integer getParentAreaID( int id ) throws EmptyResultDataAccessException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT AreaID");
        sql.append("FROM CCSubAreaAssignment");
        sql.append("WHERE SubstationBusId").eq(id);
        
        Integer parentId = null;
        try {
            parentId = jdbcTemplate.queryForObject(sql, TypeRowMapper.INTEGER);
        } catch (EmptyResultDataAccessException e) {
            //check for special area
            sql = new SqlStatementBuilder();
            
            sql.append("SELECT AreaID");
            sql.append("FROM CCSubSpecialAreaAssignment");
            sql.append("WHERE SubstationBusId").eq(id);
            
            try {
                List<Integer> parentIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
                if (parentIds.size() > 0)
                    parentId = parentIds.get(0);
            } catch (EmptyResultDataAccessException er) {
                parentId = null;
            }

        }
        return parentId;
    }
    
    @Override
    public List<Substation> getSubstationsByArea(final int areaId) {
        
        YukonPao area = dbCache.getAllPaosMap().get(areaId);
        String tableName = area.getPaoIdentifier().getPaoType() == PaoType.CAP_CONTROL_AREA ? 
                "CCSubAreaAssignment" : "CCSubSpecialAreaAssignment";
        
        SqlStatementBuilder sql = new SqlStatementBuilder(select);
        sql.append("JOIN " + tableName + " a ON a.SubstationBusId = ypo.PAObjectID");
        sql.append("WHERE a.AreaId").eq(areaId);
        
        List<Substation> stations = jdbcTemplate.query(sql, mapper);
        
        return stations;
    }
    
    @Override
    public boolean assignSubstation(YukonPao area, YukonPao substation) {
        int areaId = area.getPaoIdentifier().getPaoId();

        String tableName = area.getPaoIdentifier().getPaoType() == PaoType.CAP_CONTROL_AREA ? "CCSubAreaAssignment"
                : "CCSubSpecialAreaAssignment";

        SqlStatementBuilder displaySql = new SqlStatementBuilder();

        displaySql.append("SELECT MAX(DisplayOrder)");
        displaySql.append("FROM " + tableName);
        displaySql.append("WHERE AreaId").eq(areaId);

        int displayOrder = jdbcTemplate.queryForInt(displaySql);

        // Remove any existing assignment.
        unassignSubstation(substation);

        SqlStatementBuilder assignSql = new SqlStatementBuilder();

        SqlParameterSink params = assignSql.insertInto(tableName);
        params.addValue("AreaID", areaId);
        params.addValue("SubstationBusID", substation.getPaoIdentifier().getPaoId());
        params.addValue("DisplayOrder", ++displayOrder);

        int rowsAffected = jdbcTemplate.update(assignSql);

        boolean result = (rowsAffected == 1);

        if (result) {
            dbChangeManager.processPaoDbChange(substation, DbChangeType.UPDATE);
            dbChangeManager.processPaoDbChange(area, DbChangeType.UPDATE);
        }

        return result;
    }
    
    @Override
    public boolean assignSubstation(int areaId, int substationId) {
        YukonPao area = dbCache.getAllPaosMap().get(areaId);
        YukonPao substation = dbCache.getAllPaosMap().get(substationId);
        return assignSubstation(area, substation);
    }
    
    @Override
    public boolean unassignSubstations(int areaId) {

        YukonPao area = dbCache.getAllPaosMap().get(areaId);
        String tableName = area.getPaoIdentifier().getPaoType() == PaoType.CAP_CONTROL_AREA ? "CCSubAreaAssignment"
                : "CCSubSpecialAreaAssignment";

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + tableName);
        sql.append("WHERE AreaId").eq(areaId);

        int rowsAffected = jdbcTemplate.update(sql);

        boolean result = rowsAffected == 1;

        if (result) {
            dbChangeManager.processPaoDbChange(area, DbChangeType.UPDATE);
        }

        return result;
    }

    @Override
    @Transactional
    public void updateSubAssignments(int areaId, List<Integer> subs) {
        
        SqlStatementBuilder sql;
        SqlParameterSink values;
        
        YukonPao area = dbCache.getAllPaosMap().get(areaId);
        String tableName = area.getPaoIdentifier().getPaoType() == PaoType.CAP_CONTROL_AREA ? 
                "CCSubAreaAssignment" : "CCSubSpecialAreaAssignment";
        
        // Remove all sub assignments on the area.
        unassignSubstations(areaId);
        
        // Add sub assignments to area
        for (int i = 0; i < subs.size(); i++) {
            
            sql = new SqlStatementBuilder();
            values = sql.insertInto(tableName);
            values.addValue("AreaId", areaId);
            values.addValue("SubstationBusId", subs.get(i));
            values.addValue("DisplayOrder", i + 1);
            
            jdbcTemplate.update(sql);
        }
        
        dbChangeManager.processPaoDbChange(area, DbChangeType.UPDATE);
    }
    
    @Override
    public boolean unassignSubstation(YukonPao substation) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CCSubAreaAssignment");
        sql.append("WHERE SubstationBusID").eq(substation.getPaoIdentifier().getPaoId());

        int rowsAffected = jdbcTemplate.update(sql);

        boolean result = (rowsAffected == 1);

        return result;
    }
    
    @Override
    public List<Integer> getAllUnassignedSubstationIds() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append(   "AND PAObjectID NOT IN");
        sql.append(      "(SELECT SubstationBusID");
        sql.append(      " FROM CCSubAreaAssignment)");
        sql.append("ORDER BY PAObjectID");
        
        List<Integer> listmap = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        return listmap;
    }
    
    @Override
    public List<LiteCapControlObject> getOrphans() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append("AND PAObjectID NOT IN (SELECT SubstationBusID FROM CCSubAreaAssignment)");
        sql.append("ORDER BY PAOName");
        
        List<LiteCapControlObject> orphans = jdbcTemplate.query(sql, CapbankControllerDao.LITE_ORPHAN_MAPPER);
        
        return orphans;
    }
    
    @Override
    public List<LiteCapControlObject> getSubstationsNotInSpecialArea(int areaId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description, saa.AreaId ParentId");
        sql.append("FROM YukonPAObject ypo");
        sql.append("LEFT JOIN CCSubAreaAssignment saa on saa.SubstationBusId = ypo.PAObjectId");
        sql.append("WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append("AND PAObjectID NOT IN (SELECT SubstationBusID FROM CCSubSpecialAreaAssignment");
        sql.append(" WHERE AreaID").eq(areaId);
        sql.append(") ORDER BY PAOName");
        
        List<LiteCapControlObject> subs = jdbcTemplate.query(sql, new YukonRowMapper<LiteCapControlObject>() {
            @Override
            public LiteCapControlObject mapRow(YukonResultSet rs) throws SQLException {
                
                LiteCapControlObject sub = new LiteCapControlObject();
                sub.setId(rs.getInt("PAObjectID"));
                sub.setType(rs.getString("TYPE"));
                sub.setDescription(rs.getString("Description"));
                sub.setName(rs.getString("PAOName"));
                sub.setParentId(rs.getInt("ParentId"));
                
                return sub;
            }
        });
        
        return subs;
    }
    
    @Override
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds (int areaId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").eq(PaoType.CAP_CONTROL_SUBSTATION);
        sql.append(   "AND PAObjectID NOT IN");
        sql.append(      "(SELECT SubstationBusID");
        sql.append(      " FROM CCSubSpecialAreaAssignment");
        sql.append(      " WHERE AreaID").eq(areaId);
        sql.append(      ")");
        
        List<Integer> subs = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        return subs;
    }
    
    @Override
    public Map<Integer, Double> getSubOrders(int areaId) {
        
        // Sub Id to Display Order
        Map<Integer, Double> orders = new HashMap<>();
        
        YukonPao area = dbCache.getAllPaosMap().get(areaId);
        String tableName = area.getPaoIdentifier().getPaoType() == PaoType.CAP_CONTROL_AREA ? 
                "CCSubAreaAssignment" : "CCSubSpecialAreaAssignment";
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SubstationBusId, DisplayOrder");
        sql.append("FROM " + tableName);
        sql.append("WHERE AreaId").eq(areaId);
        
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                orders.put(rs.getInt("SubstationBusId"), rs.getDouble("DisplayOrder"));
            }
        });
        
        return orders;
    }
    
}