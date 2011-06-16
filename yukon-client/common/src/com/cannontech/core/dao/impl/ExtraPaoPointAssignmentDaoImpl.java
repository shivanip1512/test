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
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.point.PointType;

public class ExtraPaoPointAssignmentDaoImpl implements ExtraPaoPointAssignmentDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
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
    public PaoPointIdentifier getPaoPointIdentifier(YukonPao pao, Attribute attribute) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.paobjectId paobjectId, ypo.type paoType, point.PointType pointType, point.PointOffset pointOffset");
        sql.append("from Point point");
        sql.append("join ExtraPaoPointAssignment eppa on point.PointId = eppa.PointId");
        sql.append("join YukonPaobject ypo on ypo.paobjectid = point.paobjectId");
        sql.append("where eppa.PAObjectId = ").appendArgument(pao.getPaoIdentifier().getPaoId());
        sql.append("and eppa.Attribute = ").appendArgument(attribute);
        try {
            return yukonJdbcTemplate.queryForObject(sql, paoPointIdentifierRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Point not does not exist for pao: " + pao + " and attribute: " + attribute.getDescription(), e);
        }
    }
    
    @Override
    public int getPointId(YukonPao pao, Attribute attribute) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select point.pointId");
        sql.append("from Point point");
        sql.append("join ExtraPaoPointAssignment eppa on point.PointId = eppa.PointId");
        sql.append("where eppa.PAObjectId = ").appendArgument(pao.getPaoIdentifier().getPaoId());
        sql.append("and eppa.Attribute = ").appendArgument(attribute);
        try {
            return yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Point not does not exist for pao: " + pao + " and attribute: " + attribute.getDescription(), e);
        }
    }

    @Override
    public void saveAssignments(YukonPao pao, List<ExtraPaoPointMapping> pointMappings) {
        removeAssignments(pao);
        for( ExtraPaoPointMapping mapping : pointMappings ) {
            if(mapping.getPointId() > 0) {
                SqlStatementBuilder sql = new SqlStatementBuilder("insert into ExtraPaoPointAssignment");
                sql.append("values (").appendArgument(pao.getPaoIdentifier().getPaoId()).append(", ").appendArgument(mapping.getPointId());
                sql.append(", ").appendArgument(mapping.getAttribute()).append(")");
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
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
