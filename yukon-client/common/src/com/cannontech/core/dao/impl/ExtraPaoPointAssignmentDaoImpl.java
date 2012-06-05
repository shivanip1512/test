package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.enums.RegulatorPointMapping;

public class ExtraPaoPointAssignmentDaoImpl implements ExtraPaoPointAssignmentDao {
    
    @Autowired private PointDao pointDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    private YukonRowMapper<ExtraPaoPointMapping> extraPaoPointMappingRowMapper = new YukonRowMapper<ExtraPaoPointMapping>() {
        @Override
        public ExtraPaoPointMapping mapRow(YukonResultSet rs)  throws SQLException {
            ExtraPaoPointMapping mapping = new ExtraPaoPointMapping();
            mapping.setPointId(rs.getInt("PointId"));
            mapping.setRegulatorPointMapping(rs.getEnum("Attribute", RegulatorPointMapping.class));
            return mapping;
        }
    };
    
    private ParameterizedRowMapper<PaoPointIdentifier> paoPointIdentifierRowMapper = new ParameterizedRowMapper<PaoPointIdentifier>() {

        @Override
        public PaoPointIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
            int paoId = rs.getInt("paobjectId");
            String paoType = rs.getString("paoType");
            String pointType = rs.getString("pointType");
            int offset = rs.getInt("pointOffset");
            PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.getForDbString(paoType));
            PointType type = PointType.getForString(pointType);
            PointIdentifier pointIdentifier = new PointIdentifier(type, offset);
            return new PaoPointIdentifier(paoIdentifier, pointIdentifier);
        }
        
    };

    @Override
    public PaoPointIdentifier getPaoPointIdentifier(YukonPao pao, RegulatorPointMapping regulatorPointMapping) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.paobjectId paobjectId, ypo.type paoType, point.PointType pointType, point.PointOffset pointOffset");
        sql.append("from Point point");
        sql.append("join ExtraPaoPointAssignment eppa on point.PointId = eppa.PointId");
        sql.append("join YukonPaobject ypo on ypo.paobjectid = point.paobjectId");
        sql.append("where eppa.PAObjectId = ").appendArgument(pao.getPaoIdentifier().getPaoId());
        sql.append("and eppa.Attribute = ").appendArgument(regulatorPointMapping);
        try {
            return yukonJdbcTemplate.queryForObject(sql, paoPointIdentifierRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Point not does not exist for pao: " + pao + " and attribute: " + regulatorPointMapping.getDescription(), e);
        }
    }
    
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
            throw new NotFoundException("Point not does not exist for pao: " + pao + " and attribute: " + regulatorPointMapping.getDescription(), e);
        }
    }

    @Override
    public void saveAssignments(YukonPao pao, List<ExtraPaoPointMapping> pointMappings) {
        removeAssignments(pao);
        for( ExtraPaoPointMapping mapping : pointMappings ) {
            if(mapping.getPointId() > 0) {
                SqlStatementBuilder sql = new SqlStatementBuilder("insert into ExtraPaoPointAssignment");
                sql.append("values (").appendArgument(pao.getPaoIdentifier().getPaoId()).append(", ").appendArgument(mapping.getPointId());
                sql.append(", ").appendArgument(mapping.getRegulatorPointMapping()).append(")");
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
    public List<ExtraPaoPointMapping> getAssignments(PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointId, Attribute");
        sql.append("FROM ExtraPaoPointAssignment");
        sql.append("WHERE PAObjectId").eq(paoIdentifier.getPaoId());
        
        List<ExtraPaoPointMapping> mappings = yukonJdbcTemplate.query(sql, extraPaoPointMappingRowMapper);
        return mappings;
    }
    
    @Override
    public void addAssignment(YukonPao pao, int pointId, RegulatorPointMapping regulatorMapping, boolean overwriteExistingPoint) {
        //get existing mappings for this device
        List<ExtraPaoPointMapping> eppMappings = getAssignments(pao.getPaoIdentifier());
        
        //check for existing point on this mapping
        ExtraPaoPointMapping existingMapping = null;
        for(ExtraPaoPointMapping mapping : eppMappings) {
            if(mapping.getRegulatorPointMapping() == regulatorMapping) {
                existingMapping = mapping;
                break;
            }
        }
        if(!overwriteExistingPoint && existingMapping != null) {
            String message = "Illegal overwrite of existing mapping. Pao: " + pao.getPaoIdentifier().getPaoId() 
                             + ", Existing Point: " + existingMapping.getPointId() 
                             + ", New Point: " + pointId;
            throw new IllegalStateException(message);
        }
        
        //add new mapping
        eppMappings.remove(existingMapping);
        ExtraPaoPointMapping eppMapping = new ExtraPaoPointMapping();
        eppMapping.setRegulatorPointMapping(regulatorMapping);
        eppMapping.setPointId(pointId);
        eppMappings.add(eppMapping);
        
        //save
        saveAssignments(pao, eppMappings);
    }
    
    @Override
    public LitePoint getLitePoint(YukonPao regulator, RegulatorPointMapping regulatorMapping) {
        return pointDao.getLitePoint(getPointId(regulator, regulatorMapping));
    }

}