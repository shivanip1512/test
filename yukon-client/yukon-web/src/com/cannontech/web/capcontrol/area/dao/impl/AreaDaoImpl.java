package com.cannontech.web.capcontrol.area.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.area.dao.AreaDao;
import com.cannontech.web.capcontrol.area.model.Area;

@Repository
public class AreaDaoImpl implements AreaDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValue;
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private HolidayScheduleDao holidayScheduleDao;
    @Autowired private SeasonScheduleDao seasonScheduleDao;
    @Autowired private PaoPersistenceService paoPersistence;
    
    /** ROW MAPPER */
    private final static YukonRowMapper<Area> mapper = new YukonRowMapper<Area>() {
        @Override
        public Area mapRow(YukonResultSet rs) throws SQLException {
            
            Area area = new Area();
            area.setId(rs.getInt("AreaId"));
            area.setType(rs.getEnum("Type", PaoType.class));
            area.setName(rs.getString("PAOName"));
            area.setDescription(SqlUtils.convertDbValueToString(rs.getString("Description")));
            area.setDisabled(rs.getBoolean("DisableFlag"));
            int voltReduction = rs.getInt("VoltReductionPointId");
            area.setVoltReductionPoint(voltReduction == 0 ? null : voltReduction);
            
            return area;
        }
    };
    
    /** SELECT QUERY */
    private String selectQuery(PaoType type) {
        return "SELECT cca.AreaId, cca.VoltReductionPointId, " + 
                "ypo.PaoName, ypo.Type, ypo.Description, ypo.DisableFlag " + 
                "FROM " + (type == PaoType.CAP_CONTROL_AREA ? "CapControlArea" : "CapControlSpecialArea") + " cca " + 
                    "JOIN YukonPAOBject ypo on ypo.PAObjectId = cca.AreaId";
    }
    
    @Override
    public List<Area> getAreas(PaoType type) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder(selectQuery(type));
        List<Area> areas = jdbcTemplate.query(sql, mapper);
        
        return areas;
    }
    
    @Override
    public Area getArea(int areaId) {
        
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(areaId);
        SqlStatementBuilder sql = new SqlStatementBuilder(selectQuery(pao.getPaoType()));
        sql.append("WHERE cca.AreaId").eq(areaId);
        
        Area area = jdbcTemplate.queryForObject(sql, mapper);
        
        return area;
    }
    
    @Override
    @Transactional
    public int save(Area area) {
        
        String table = area.getType() == PaoType.CAP_CONTROL_AREA ? "CapControlArea" : "CapControlSpecialArea";
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        DbChangeType change = DbChangeType.UPDATE;
        
        // Volt Reduction Point of 0 means no volt reduction.
        int voltReductionPoint = area.getVoltReductionPoint() == null ? 0 : area.getVoltReductionPoint();
        
        if (area.getId() == null) {
            // INSERT
            area.setId(nextValue.getNextValue("YukonPAObject"));
            
            SqlParameterSink values = sql.insertInto("YukonPAObject");
            values.addValue("PAObjectId", area.getId());
            values.addValue("PAOStatistics", PaoUtils.DEFAULT_PAO_STATS);
            values.addValue("Category", PaoCategory.CAPCONTROL);
            values.addValue("PAOClass", PaoClass.CAPCONTROL);
            values.addValue("Type", area.getType());
            values.addValue("PAOName", area.getName());
            values.addValue("Description", SqlUtils.convertStringToDbValue(area.getDescription()));
            values.addValue("DisableFlag", YNBoolean.valueOf(area.isDisabled()));
            jdbcTemplate.update(sql);
            
            sql = new SqlStatementBuilder();
            values = sql.insertInto(table);
            values.addValue("AreaId", area.getId());
            values.addValue("VoltReductionPointId", voltReductionPoint);
            jdbcTemplate.update(sql);
            
            change = DbChangeType.ADD;
            
        } else {
            // UPDATE
            SqlParameterSink values = sql.update("YukonPAObject");
            values.addValue("PAOName", area.getName());
            values.addValue("Description", SqlUtils.convertStringToDbValue(area.getDescription()));
            values.addValue("DisableFlag", YNBoolean.valueOf(area.isDisabled()));
            sql.append("WHERE PAObjectId").eq(area.getId());
            jdbcTemplate.update(sql);
            
            sql = new SqlStatementBuilder();
            values = sql.update(table);
            values.addValue("VoltReductionPointId", voltReductionPoint);
            sql.append("WHERE AreaId").eq(area.getId());
            jdbcTemplate.update(sql);
            
        }
        
        dbChangeManager.processPaoDbChange(PaoIdentifier.of(area.getId(), area.getType()), change);
        
        return area.getId();
    }
    
    @Override
    @Transactional
    public void delete(PaoIdentifier area) {
        
        int areaId = area.getPaoId();
        String table;
        String assignmentTable;
        String dynamicTable;
        if (area.getPaoType() == PaoType.CAP_CONTROL_AREA) {
            table = "CapControlArea";
            assignmentTable = "CCSubAreaAssignment";
            dynamicTable = "DynamicCcArea";
        } else {
            table = "CapControlSpecialArea";
            assignmentTable = "CCSubSpecialAreaAssignment";
            dynamicTable = "DynamicCcSpecialArea";
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + assignmentTable);
        sql.append("WHERE AreaId").eq(areaId);
        jdbcTemplate.update(sql);
        
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + dynamicTable);
        sql.append("WHERE AreaId").eq(areaId);
        jdbcTemplate.update(sql);
        
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CapControlComment");
        sql.append("WHERE PaoId").eq(areaId);
        jdbcTemplate.update(sql);
        
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + table);
        sql.append("WHERE AreaId").eq(areaId);
        jdbcTemplate.update(sql);
        
        holidayScheduleDao.deleteStrategyAssigment(areaId);
        seasonScheduleDao.deleteStrategyAssigment(areaId);
        
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DynamicCCOperationStatistics");
        sql.append("WHERE PAObjectId").eq(areaId);
        jdbcTemplate.update(sql);
        
        // PaoPersistence#deletePao sends a db change message for us.
        paoPersistence.deletePao(area);
    }
}