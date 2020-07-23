package com.cannontech.common.pao.attribute.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class AttributeDaoImpl implements AttributeDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private final Cache<Pair<Integer, PaoType>, PointIdentifier> attributeToPoint = CacheBuilder.newBuilder().build();
    private final SetMultimap<PaoTypePointIdentifier, CustomAttribute> paoAndPointToAttribute = HashMultimap.create();

    @PostConstruct
    public void init() {
        cacheAttributes();
    }
    
    /**
     * Caches custom attributes
     */
    
    @Override
    public void cacheAttributes() {
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
