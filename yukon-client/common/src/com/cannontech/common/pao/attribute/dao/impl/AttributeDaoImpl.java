package com.cannontech.common.pao.attribute.dao.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
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
    private final Cache<Pair<Integer, PaoType>, PointIdentifier> attributeToPoint = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS).build();

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
            clearCache(assignment);
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("AttributeAssignment");
            addAssignmentParameters(params, assignment);
            updateCreateSql.append("WHERE AttributeAssignmentId").eq(assignment.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("AttributeAssignment");
            params.addValue("AttributeAssignmentId", nextValueHelper.getNextValue("AttributeAssignment"));
            addAssignmentParameters(params, assignment);
        }
        try {
            jdbcTemplate.update(updateCreateSql);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Assignment " + assignment
                    + " has either same attribute assigned to multiple points on the same device or multiple entries with the exact same mapping",
                    e);
        }
    }

    private void clearCache(AttributeAssignment assignment) {
        Pair<Integer, PaoType> pair = Pair.of(assignment.getAttributeId(), assignment.getPaoType());
        attributeToPoint.invalidate(pair);
    }

    private void addAssignmentParameters(SqlParameterSink params, AttributeAssignment assignment) {
        params.addValue("AttributeId", assignment.getAttributeId());
        params.addValue("PaoType", assignment.getPaoType());
        params.addValue("PointType", assignment.getPointType());
        params.addValue("PointOffset", assignment.getPointOffset());
    }

    @Override
    public void deleteAttributeAssignment(int attributeAssignmentId) {
        // Check if an attribute assignment is being used elsewhere in Yukon (to provide an error to a user attempting to delete
        // it). This can just be stubbed out, for now. (Does this make sense to have for attribute assignments?)
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
                attributeToPoint.put(pair, new PointIdentifier(assignment.getPointType(), assignment.getPointOffset()));
            } catch (EmptyResultDataAccessException e) {
                throw new IllegalUseOfAttribute("No Custom Attribute exists for attributeId " + attributeId + " and " + paoType);
            }
        }
        return attributeToPoint.getIfPresent(pair);
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
        try {
            jdbcTemplate.update(updateCreateSql);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Attribute with name " + attribute.getName() + " already exist.", e);
        }
    }

    @Override
    public int deleteCustomAttribute(int attributeId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CustomAttribute");
        sql.append("WHERE AttributeId").eq(attributeId);
        //attribute doesn't have point information assigned
        sql.append("AND AttributeId NOT IN");
        sql.append("  (SELECT AttributeId FROM AttributeAssignment)");
        //attribute not used by data export
        sql.append("AND AttributeId NOT IN");
        sql.append("  (SELECT AttributeName FROM ArchiveValuesExportAttribute");
        return jdbcTemplate.update(sql);
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
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeId, AttributeName");
        sql.append("FROM CustomAttribute");
        sql.append("WHERE AttributeId").eq(attributeId);
        return jdbcTemplate.queryForObject(sql, customAttributeMapper);
    }
}
