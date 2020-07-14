package com.cannontech.common.pao.attribute.dao.impl;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class AttributeDaoImpl implements AttributeDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private final Cache<Pair<Integer, PaoType>, PointIdentifier> attributeToPoint = CacheBuilder.newBuilder().build();
    private final Cache<Integer, CustomAttribute> idToAttribute = CacheBuilder.newBuilder().build();

    private YukonRowMapper<CustomAttribute> customAttributeMapper = rs -> {
        CustomAttribute row = new CustomAttribute();
        row.setId(rs.getInt("AttributeId"));
        row.setName(rs.getStringSafe("AttributeName"));
        return row;
    };

    private YukonRowMapper<AttributeAssignment> attributeAssignmentMapper = rs -> {
        AttributeAssignment row = new AttributeAssignment();
        row.setId(rs.getInt("AttributeAssignmentId"));
        row.setAttributeId(rs.getInt("AttributeId"));
        row.setPaoType(rs.getEnum("PaoType", PaoType.class));
        row.setPointType(rs.getEnum("PointType", PointType.class));
        row.setPointOffset(rs.getInt("PointOffset"));
        return row;
    };

    @Override
    public void saveAttributeAssignment(AttributeAssignment assignment) {        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId");
        sql.append("FROM AttributeAssignment");
        sql.append("WHERE AttributeAssignmentId").eq(assignment.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            
            clearCache(assignment);
            
            SqlParameterSink params = updateCreateSql.update("AttributeAssignment");
            addAssignmentParameters(params, assignment);
            updateCreateSql.append("WHERE AttributeAssignmentId").eq(assignment.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("AttributeAssignment");
            params.addValue("AttributeAssignmentId", nextValueHelper.getNextValue("AttributeAssignment"));
            addAssignmentParameters(params, assignment);
        }
        jdbcTemplate.update(updateCreateSql);
    }

    private void clearCache(AttributeAssignment assignment) {
        Pair<Integer, PaoType> pair = Pair.of(assignment.getAttributeId(), assignment.getPaoType());
        attributeToPoint.invalidate(pair);
    }
    
    private void clearCache(Integer attributeId) {
        idToAttribute.invalidate(attributeId);
    }

    private void addAssignmentParameters(SqlParameterSink params, AttributeAssignment assignment) {
        params.addValue("AttributeId", assignment.getAttributeId());
        params.addValue("PaoType", assignment.getPaoType());
        params.addValue("PointType", assignment.getPointType());
        params.addValue("PointOffset", assignment.getPointOffset());
    }

    @Override
    public void deleteAttributeAssignment(int attributeAssignmentId) {
        clearCache(getAssignmentById(attributeAssignmentId));
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM AttributeAssignment");
        sql.append("WHERE AttributeAssignmentId").eq(attributeAssignmentId);
        jdbcTemplate.update(sql);
    }

    @Override
    public AttributeAssignment getAssignmentById(int attributeAssignmentId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId, AttributeId, PaoType, PointType, PointOffset");
        sql.append("FROM AttributeAssignment");
        sql.append("WHERE AttributeAssignmentId").eq(attributeAssignmentId);
        return jdbcTemplate.queryForObject(sql, attributeAssignmentMapper);
    }
    
    private List<AttributeAssignment> getAssignmentsByAttributeId(int attributId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId, AttributeId, PaoType, PointType, PointOffset");
        sql.append("FROM AttributeAssignment");
        sql.append("WHERE AttributeId").eq(attributId);
        return jdbcTemplate.query(sql, attributeAssignmentMapper);
    }
    
    @Override
    public PointIdentifier getPointIdentifier(int attributeId, PaoType paoType) {
        Pair<Integer, PaoType> pair = Pair.of(attributeId, paoType);
        PointIdentifier point = attributeToPoint.getIfPresent(pair);
        if (point == null) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT AttributeAssignmentId, AttributeId, PaoType, PointType, PointOffset");
            sql.append("FROM AttributeAssignment");
            sql.append("WHERE AttributeId").eq(attributeId);
            sql.append("AND PaoType").eq_k(paoType);
            try {
                AttributeAssignment assignment = jdbcTemplate.queryForObject(sql, attributeAssignmentMapper);
                point = new PointIdentifier(assignment.getPointType(), assignment.getPointOffset());
                attributeToPoint.put(pair, point);
            } catch (EmptyResultDataAccessException e) {
                throw new IllegalUseOfAttribute("No Custom Attribute exists for attributeId " + attributeId + " and " + paoType);
            }
        }
        return point;
    }

    @Override
    public void saveCustomAttribute(CustomAttribute attribute) {         
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeId");
        sql.append("FROM CustomAttribute");
        sql.append("WHERE AttributeId").eq(attribute.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            
            clearCache(attribute.getId());
            
            SqlParameterSink params = updateCreateSql.update("CustomAttribute");
            params.addValue("AttributeName", attribute.getName());
            updateCreateSql.append("WHERE AttributeId").eq(attribute.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("CustomAttribute");
            params.addValue("AttributeId", nextValueHelper.getNextValue("CustomAttribute"));
            params.addValue("AttributeName", attribute.getName());
        }
        jdbcTemplate.update(updateCreateSql);
    }

    @Override
    public void deleteCustomAttribute(int attributeId) throws DataDependencyException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CustomAttribute");
        sql.append("WHERE AttributeId").eq(attributeId);
        /* if(true) {
         *      throw new DataDependencyException("--------DataDependencyException-------");
         * }
         */
        jdbcTemplate.update(sql);
        getAssignmentsByAttributeId(attributeId).forEach(assignment -> clearCache(assignment));
        clearCache(attributeId);
    }

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
        CustomAttribute attribute = idToAttribute.getIfPresent(attributeId);
        if (attribute == null) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT AttributeId, AttributeName");
            sql.append("FROM CustomAttribute");
            sql.append("WHERE AttributeId").eq(attributeId);
            attribute = jdbcTemplate.queryForObject(sql, customAttributeMapper);
            idToAttribute.put(attributeId, attribute);
        }
        return attribute;
    }
}
