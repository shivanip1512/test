package com.cannontech.common.pao.attribute.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignmentRequest;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
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

    @PostConstruct
    public void init() {
        cacheAttributes();
    }
    
    /**
     * Caches custom attributes
     */
    private void cacheAttributes() {
        attributeToPoint.invalidateAll();
        idToAttribute.invalidateAll();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeId, AttributeName");
        sql.append("FROM CustomAttribute");
        List<CustomAttribute> attributes = jdbcTemplate.query(sql, customAttributeMapper);
        if(attributes.isEmpty()) {
            return;
        }
        attributes.forEach(attribute -> idToAttribute.put(attribute.getId(), attribute));
        sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId, aa.AttributeId, AttributeName, PaoType, PointType, PointOffset");
        sql.append("FROM AttributeAssignment aa");
        sql.append("JOIN CustomAttribute ca ON aa.AttributeId = ca.AttributeId");
        List<AttributeAssignment> assignments = jdbcTemplate.query(sql, attributeAssignmentMapper);
        assignments.forEach(assignment -> {
            Pair<Integer, PaoType> pair = Pair.of(assignment.getCustomAttribute().getId(), assignment.getPaoType());
            PointIdentifier point = new PointIdentifier(assignment.getPointType(), assignment.getPointOffset());
            attributeToPoint.put(pair, point);
        });
    }
    
    private YukonRowMapper<CustomAttribute> customAttributeMapper = rs -> {
        return new CustomAttribute(rs.getInt("AttributeId"), rs.getStringSafe("AttributeName"));
    };
    
    public static YukonRowMapper<AttributeAssignment> attributeAssignmentMapper = rs -> {
        AttributeAssignment row = new AttributeAssignment();
        row.setId(rs.getInt("AttributeAssignmentId"));
        row.setCustomAttribute(new CustomAttribute(rs.getInt("AttributeId"), rs.getStringSafe("AttributeName")));
        row.setPaoType(rs.getEnum("PaoType", PaoType.class));
        row.setPointType(rs.getEnum("PointType", PointType.class));
        row.setPointOffset(rs.getInt("PointOffset"));
        return row;
    };
    
    @Override
    public void saveAttributeAssignment(AttributeAssignmentRequest assignment) {        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId");
        sql.append("FROM AttributeAssignment");
        sql.append("WHERE AttributeAssignmentId").eq(assignment.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("AttributeAssignment");
            addAssignmentParameters(params, assignment);
            updateCreateSql.append("WHERE AttributeAssignmentId").eq(assignment.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("AttributeAssignment");
            params.addValue("AttributeAssignmentId", nextValueHelper.getNextValue("AttributeAssignment"));
            addAssignmentParameters(params, assignment);
        }
        jdbcTemplate.update(updateCreateSql);
        cacheAttributes();
    }

    private void addAssignmentParameters(SqlParameterSink params, AttributeAssignmentRequest assignment) {
        params.addValue("AttributeId", assignment.getAttributeId());
        params.addValue("PaoType", assignment.getPaoType());
        params.addValue("PointType", assignment.getPointType());
        params.addValue("PointOffset", assignment.getPointOffset());
    }

    @Override
    public void deleteAttributeAssignment(int attributeAssignmentId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM AttributeAssignment");
        sql.append("WHERE AttributeAssignmentId").eq(attributeAssignmentId);
        jdbcTemplate.update(sql);
        cacheAttributes();
    }

    @Override
    public AttributeAssignment getAssignmentById(int attributeAssignmentId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId, aa.AttributeId, AttributeName, PaoType, PointType, PointOffset");
        sql.append("FROM AttributeAssignment aa");
        sql.append("JOIN CustomAttribute ca ON aa.AttributeId = ca.AttributeId");
        sql.append("WHERE AttributeAssignmentId").eq(attributeAssignmentId);
        return jdbcTemplate.queryForObject(sql, attributeAssignmentMapper);
    }
    
    @Override
    public PointIdentifier getPointIdentifier(int attributeId, PaoType paoType) {
        Pair<Integer, PaoType> pair = Pair.of(attributeId, paoType);
        PointIdentifier point = attributeToPoint.getIfPresent(pair);
        if(point == null) {
            throw new IllegalUseOfAttribute("No Custom Attribute exists for attributeId " + attributeId + " and " + paoType);
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
            SqlParameterSink params = updateCreateSql.update("CustomAttribute");
            params.addValue("AttributeName", attribute.getName());
            updateCreateSql.append("WHERE AttributeId").eq(attribute.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("CustomAttribute");
            params.addValue("AttributeId", nextValueHelper.getNextValue("CustomAttribute"));
            params.addValue("AttributeName", attribute.getName());
        }
        jdbcTemplate.update(updateCreateSql);
        cacheAttributes();
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
        cacheAttributes();
    }

    @Override
    public List<CustomAttribute> getCustomAttributes() {
        return idToAttribute.asMap().values().stream()
                .sorted((e1, e2) -> e1.getName().compareTo(e2.getName()))
                .collect(Collectors.toList()); 
    }
    
    @Override
    public CustomAttribute getCustomAttribute(int attributeId) {
        return idToAttribute.getIfPresent(attributeId);
    }
    
    @Override
    public PaoType getPaoTypeByAttributeId(int attributeId) {
        return attributeToPoint.asMap().keySet()
            .stream()
            .filter(cachedAttribute -> attributeId == cachedAttribute.getKey())
            .map(pair -> pair.getValue())
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Attribute id:"+ attributeId +"is not in cache"));
    }
}
