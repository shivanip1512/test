package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;

public class ExtraPaoPointAssignmentDaoImpl implements ExtraPaoPointAssignmentDao {
    
    @Autowired private PointDao pointDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    /**
     * Class to bundle the point id mapped to the regulator point mapping (previously these were mapped attributes)
     */
    private class ExtraPaoPointMapping {

        private final RegulatorPointMapping regulatorPointMapping;
        private final int pointId;

        public ExtraPaoPointMapping(RegulatorPointMapping regulatorPointMapping, int pointId) {
            this.regulatorPointMapping = regulatorPointMapping;
            this.pointId = pointId;
        }

        public RegulatorPointMapping getRegulatorPointMapping() {
            return regulatorPointMapping;
        }

        public int getPointId() {
            return pointId;
        }
    }

    private YukonRowMapper<ExtraPaoPointMapping> extraPaoPointMappingRowMapper = new YukonRowMapper<ExtraPaoPointMapping>() {
        @Override
        public ExtraPaoPointMapping mapRow(YukonResultSet rs)  throws SQLException {
            int pointId = rs.getInt("PointId");
            RegulatorPointMapping attribute = rs.getEnum("Attribute", RegulatorPointMapping.class);
            ExtraPaoPointMapping mapping = new ExtraPaoPointMapping(attribute, pointId);
            return mapping;
        }
    };

    @Override
    public int getPointId(YukonPao pao, RegulatorPointMapping regulatorPointMapping) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT point.PointId");
        sql.append("FROM Point point");
        sql.append(    "JOIN ExtraPaoPointAssignment eppa ON point.PointId = eppa.PointId");
        sql.append("WHERE eppa.PAObjectId").eq(pao.getPaoIdentifier().getPaoId());
        sql.append(    "AND eppa.Attribute").eq(regulatorPointMapping);
        try {
            return yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Point not does not exist for pao: " + pao + " and attribute: " + regulatorPointMapping.name(), e);
        }
    }

    @Override
    public void saveAssignments(YukonPao pao, Map<RegulatorPointMapping, Integer> pointMappings) {

        removeAssignments(pao);

        for (Entry<RegulatorPointMapping, Integer> mapping : pointMappings.entrySet()) {
            if (mapping.getValue() > 0) {
                SqlStatementBuilder sql = new SqlStatementBuilder("insert into ExtraPaoPointAssignment");
                sql.append("values (").appendArgument(pao.getPaoIdentifier().getPaoId()).append(", ").appendArgument(mapping.getValue());
                sql.append(", ").appendArgument(mapping.getKey()).append(")");
                yukonJdbcTemplate.update(sql);
            }
        }
    }
    
    @Override
    public void removeAssignments(YukonPao pao) {
        SqlStatementBuilder sql = new SqlStatementBuilder("delete from ExtraPaoPointAssignment where paobjectId = ");
        sql.appendArgument(pao.getPaoIdentifier().getPaoId());
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public boolean removeAssignment(PaoIdentifier paoIdentifier, RegulatorPointMapping regulatorMapping) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ExtraPaoPointAssignment");
        sql.append("WHERE PAObjectId").eq(paoIdentifier.getPaoId());
        sql.append("AND Attribute").eq_k(regulatorMapping);
        
        int rowsModified = yukonJdbcTemplate.update(sql);
        return rowsModified == 1;
    }
    
    @Override
    public Map<RegulatorPointMapping, Integer> getAssignments(PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointId, Attribute");
        sql.append("FROM ExtraPaoPointAssignment");
        sql.append("WHERE PAObjectId").eq(paoIdentifier.getPaoId());
        
        Map<RegulatorPointMapping, Integer> assignments = new HashMap<>();
        List<ExtraPaoPointMapping> mappings = yukonJdbcTemplate.query(sql, extraPaoPointMappingRowMapper);
        for (ExtraPaoPointMapping mapping : mappings) {
            assignments.put(mapping.getRegulatorPointMapping(), mapping.getPointId());
        }

        return assignments;
    }
    
    @Override
    public void addAssignment(YukonPao pao, int pointId, RegulatorPointMapping regulatorMapping, boolean overwriteExistingPoint) {
        //get existing mappings for this device
        Map<RegulatorPointMapping, Integer> eppMappings = getAssignments(pao.getPaoIdentifier());

        if (!overwriteExistingPoint && eppMappings.containsKey(regulatorMapping)) {
            String message = "Illegal overwrite of existing mapping. Pao: " + pao.getPaoIdentifier().getPaoId() 
                             + ", Existing Point: " + eppMappings.get(regulatorMapping)
                             + ", New Point: " + pointId;
            throw new IllegalStateException(message);
        }
        
        //add new mapping
        eppMappings.put(regulatorMapping, pointId);
        
        //save
        saveAssignments(pao, eppMappings);
    }
    
    @Override
    public LitePoint getLitePoint(YukonPao regulator, RegulatorPointMapping regulatorMapping) {
        return pointDao.getLitePoint(getPointId(regulator, regulatorMapping));
    }

}
