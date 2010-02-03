package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.capcontrol.AttributePointMapping;
import com.cannontech.database.data.point.PointType;

public class ExtraPaoPointAssignmentDaoImpl implements ExtraPaoPointAssignmentDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private ParameterizedRowMapper<PaoPointIdentifier> paoPointIdentifierRowMapper = new ParameterizedRowMapper<PaoPointIdentifier>() {

        @Override
        public PaoPointIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
            int paoId = rs.getInt("paobjectId");
            String pointType = rs.getString("pointType");
            int offset = rs.getInt("pointOffset");
            PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.LOAD_TAP_CHANGER);
            PointType type = PointType.getForString(pointType);
            PointIdentifier pointIdentifier = new PointIdentifier(type, offset);
            return new PaoPointIdentifier(paoIdentifier, pointIdentifier);
        }
        
    };

    @Override
    public PaoPointIdentifier getPaoPointIdentifier(YukonPao pao, Attribute attribute) {
        SqlStatementBuilder sql = new SqlStatementBuilder("select ypo.paobjectId paobjectId, point.PointType pointType, point.PointOffset pointOffset");
        sql.append("from Point point");
        sql.append("join ExtraPaoPointAssignment eppa on point.PointId = eppa.PointId");
        sql.append("join YukonPaobject ypo on ypo.paobjectid = point.paobjectId");
        sql.append("where eppa.PAObjectId = ").appendArgument(pao.getPaoIdentifier().getPaoId());
        sql.append("and eppa.Attribute = ").appendArgument(attribute);
        return yukonJdbcTemplate.queryForObject(sql, paoPointIdentifierRowMapper);
    }

    @Override
    public void saveAssignments(int paoId, List<AttributePointMapping> pointMappings) {
        removeAssignments(paoId);
        for( AttributePointMapping mapping : pointMappings ) {
            if(mapping.getPointId() > 0) {
                SqlStatementBuilder sql = new SqlStatementBuilder("insert into ExtraPaoPointAssignment");
                sql.append("values (").appendArgument(paoId).append(", ").appendArgument(mapping.getPointId());
                sql.append(", ").appendArgument(mapping.getAttribute()).append(")");
                yukonJdbcTemplate.update(sql);
            }
        }
    }
    
    public void removeAssignments(int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder("delete from ExtraPaoPointAssignment where paobjectId = ");
        sql.appendArgument(paoId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
