package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.google.common.collect.Lists;

public class VoltageRegulatorDaoImpl implements VoltageRegulatorDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public int getKeepAliveTimerForRegulator(int regulatorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT KeepAliveTimer");
        sql.append("FROM Regulator");
        sql.append("WHERE RegulatorId").eq(regulatorId);
        
        int result = yukonJdbcTemplate.queryForInt(sql);
        return result;
    }
    
    @Override
    public int getKeepAliveConfigForRegulator(int regulatorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT KeepAliveConfig");
        sql.append("FROM Regulator");
        sql.append("WHERE RegulatorId").eq(regulatorId);
        
        int result = yukonJdbcTemplate.queryForInt(sql);
        return result;
    }
    
    @Override
    public double getVoltChangePerTapForRegulator(int regulatorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT VoltChangePerTap");
        sql.append("FROM Regulator");
        sql.append("WHERE RegulatorId").eq(regulatorId);
        
        Double result = yukonJdbcTemplate.queryForObject(sql, RowMapper.DOUBLE);
        return result;
    }
    
    @Override
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
        ParameterizedRowMapper<LiteCapControlObject> rowMapper = new ParameterizedRowMapper<LiteCapControlObject>() {
            public LiteCapControlObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                LiteCapControlObject lco = new LiteCapControlObject();
                lco.setId(rs.getInt("PAObjectID"));
                lco.setType(rs.getString("Type"));
                lco.setDescription(rs.getString("Description"));
                lco.setName(rs.getString("PAOName"));
                lco.setParentId(0);
                return lco;
            }
        };
        
        List<PaoType> regulatorTypes = Lists.newArrayList();
        regulatorTypes.add(PaoType.GANG_OPERATED);
        regulatorTypes.add(PaoType.PHASE_OPERATED);
        regulatorTypes.add(PaoType.LOAD_TAP_CHANGER);
        
        /* Get the unordered total count */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.CAPCONTROL);
        sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
        sql.append("    AND PAObjectID not in (SELECT RegulatorId FROM RegulatorToZoneMapping)");
        sql.append("    AND Type").in(regulatorTypes);
        
        int orphanCount = yukonJdbcTemplate.queryForInt(sql);
        
        /* Get the paged subset of cc objects */
        sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.CAPCONTROL);
        sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
        sql.append("    AND PAObjectID not in (SELECT RegulatorId FROM RegulatorToZoneMapping)");
        sql.append("    AND Type").in(regulatorTypes);
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor<LiteCapControlObject> orphanExtractor = new PagingResultSetExtractor<LiteCapControlObject>(start, count, rowMapper);
        yukonJdbcTemplate.query(sql, orphanExtractor);
        
        List<LiteCapControlObject> unassignedLtcs = orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedLtcs);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
    }
    
    @Override
    public boolean isOrphan(int regulatorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Count(*)");
        sql.append("FROM RegulatorToZoneMapping");
        sql.append("WHERE RegulatorId").eq(regulatorId);
        
        int count = yukonJdbcTemplate.queryForInt(sql);
        if(count == 0) return true;
        return false;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}