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
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class AttributeDaoImpl implements AttributeDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private final Cache<Pair<Integer, PaoType>, PointIdentifier> attributeToPoint = CacheBuilder.newBuilder().build();
    private final SetMultimap<PaoTypePointIdentifier, CustomAttribute> paoAndPointToAttribute = HashMultimap.create();

    @PostConstruct
    public void init() {
        cacheAttributes();
    }
    
    /**
     * Caches custom attributes
     */
    private void cacheAttributes() {
        attributeToPoint.invalidateAll();
        paoAndPointToAttribute.clear();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId, aa.AttributeId, AttributeName, PaoType, PointType, PointOffset");
        sql.append("FROM AttributeAssignment aa");
        sql.append("JOIN CustomAttribute ca ON aa.AttributeId = ca.AttributeId");
        List<AttributeAssignment> assignments = jdbcTemplate.query(sql, attributeAssignmentMapper);
        assignments.forEach(assignment -> {
            PointIdentifier pointIdent = new PointIdentifier(assignment.getPointType(), assignment.getOffset());
            Pair<Integer, PaoType> pair = Pair.of(assignment.getCustomAttribute().getCustomAttributeId(),
                    assignment.getPaoType());
            attributeToPoint.put(pair, pointIdent);
            paoAndPointToAttribute.put(PaoTypePointIdentifier.of(assignment.getPaoType(), pointIdent),
                    assignment.getCustomAttribute());
        });
    }

    public static YukonRowMapper<AttributeAssignment> attributeAssignmentMapper = rs -> {
        AttributeAssignment row = new AttributeAssignment();
        row.setAttributeAssignmentId(rs.getInt("AttributeAssignmentId"));
        row.setCustomAttribute(new CustomAttribute(rs.getInt("AttributeId"), rs.getStringSafe("AttributeName")));
        row.setPaoType(rs.getEnum("PaoType", PaoType.class));
        row.setOffset(rs.getInt("PointOffset"));
        row.setPointType(rs.getEnum("PointType", PointType.class));
        return row;
    };
    
    @Override
    public void saveAttributeAssignment(Assignment assignment) {        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId");
        sql.append("FROM AttributeAssignment");
        sql.append("WHERE AttributeAssignmentId").eq(assignment.getAttributeAssignmentId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("AttributeAssignment");
            addAssignmentParameters(params, assignment);
            updateCreateSql.append("WHERE AttributeAssignmentId").eq(assignment.getAttributeAssignmentId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("AttributeAssignment");
            params.addValue("AttributeAssignmentId", nextValueHelper.getNextValue("AttributeAssignment"));
            addAssignmentParameters(params, assignment);
        }
        jdbcTemplate.update(updateCreateSql);
        cacheAttributes();
    }

    private void addAssignmentParameters(SqlParameterSink params, Assignment assignment) {
        params.addValue("AttributeId", assignment.getAttributeId());
        params.addValue("PaoType", assignment.getPaoType());
        params.addValue("PointType", assignment.getPointType());
        params.addValue("PointOffset", assignment.getOffset());
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
        sql.append("WHERE AttributeId").eq(attribute.getCustomAttributeId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("CustomAttribute");
            params.addValue("AttributeName", attribute.getName());
            updateCreateSql.append("WHERE AttributeId").eq(attribute.getCustomAttributeId());
        } catch (EmptyResultDataAccessException e) {
            attribute.setId(nextValueHelper.getNextValue("CustomAttribute"));
            SqlParameterSink params = updateCreateSql.insertInto("CustomAttribute");
            params.addValue("AttributeId", attribute.getId());
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
        return paoAndPointToAttribute.values().stream()
                .distinct()
                .sorted((e1, e2) -> e1.getName().compareTo(e2.getName()))
                .collect(Collectors.toList()); 
    }
    
    @Override
    public CustomAttribute getCustomAttribute(int attributeId) {
        return paoAndPointToAttribute.values()
                .stream()
                .distinct()
                .filter(cachedAttribute -> attributeId == cachedAttribute.getCustomAttributeId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Attribute id:"+ attributeId +"is not in cache"));
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
    
    @Override
    public Attribute findCustomAttributeForPaoTypeAndPoint(PaoTypePointIdentifier paoTypePointIdentifier) {
        return paoAndPointToAttribute.get(paoTypePointIdentifier).isEmpty() ? null : paoAndPointToAttribute
                .get(paoTypePointIdentifier).iterator().next();
    }
}
