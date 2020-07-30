package com.cannontech.common.pao.attribute.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;

public class AttributeDaoImpl implements AttributeDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    public static YukonRowMapper<AttributeAssignment> attributeAssignmentMapper = rs -> {
        AttributeAssignment row = new AttributeAssignment();
        row.setAttributeAssignmentId(rs.getInt("AttributeAssignmentId"));
        CustomAttribute attribute = new CustomAttribute(rs.getInt("AttributeId"), rs.getStringSafe("AttributeName"));
        row.setCustomAttribute(attribute);
        row.setAttributeId(attribute.getCustomAttributeId());
        row.setPaoType(rs.getEnum("PaoType", PaoType.class));
        row.setOffset(rs.getInt("PointOffset"));
        row.setPointType(rs.getEnum("PointType", PointType.class));
        return row;
    };
    
    private YukonRowMapper<CustomAttribute> customAttributeMapper = rs -> {
        CustomAttribute row = new CustomAttribute();
        row.setCustomAttributeId(rs.getInt("AttributeId"));
        row.setName(rs.getStringSafe("AttributeName"));
        return row;
    };
    
    @Override
    public List<CustomAttribute> getCustomAttributes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeId, AttributeName");
        sql.append("FROM CustomAttribute");
        sql.append("ORDER BY AttributeName");
        return jdbcTemplate.query(sql, customAttributeMapper);
    }
    
    @Override
    public CustomAttribute getCustomAttribute(int attributeId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT AttributeId, AttributeName");
            sql.append("FROM CustomAttribute");
            sql.append("WHERE AttributeId").eq(attributeId);
            return jdbcTemplate.queryForObject(sql, customAttributeMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Attribute with id " + attributeId + " cannot be found.");
        }
    }
    
    @Override
    public AttributeAssignment getAssignmentById(int attributeAssignmentId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT AttributeAssignmentId, aa.AttributeId, AttributeName, PaoType, PointType, PointOffset");
            sql.append("FROM AttributeAssignment aa");
            sql.append("JOIN CustomAttribute ca ON aa.AttributeId = ca.AttributeId");
            sql.append("WHERE AttributeAssignmentId").eq(attributeAssignmentId);
            return jdbcTemplate.queryForObject(sql, attributeAssignmentMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Attribute Assignment with id " + attributeAssignmentId + " cannot be found.");
        }
    }
    
    @Override
    public List<AttributeAssignment> getAssignments() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId, aa.AttributeId, AttributeName, PaoType, PointType, PointOffset");
        sql.append("FROM AttributeAssignment aa");
        sql.append("JOIN CustomAttribute ca ON aa.AttributeId = ca.AttributeId");
        return jdbcTemplate.query(sql, attributeAssignmentMapper);
    }
}
