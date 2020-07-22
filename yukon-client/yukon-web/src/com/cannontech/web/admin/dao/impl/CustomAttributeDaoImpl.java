package com.cannontech.web.admin.dao.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.dao.impl.AttributeDaoImpl;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.web.admin.dao.CustomAttributeDao;

public class CustomAttributeDaoImpl implements CustomAttributeDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private AttributeDao attributeDao;
    @Autowired private NextValueHelper nextValueHelper;

    @Override
    public List<AttributeAssignment> getCustomAttributeDetails(List<Integer> attributeIds, List<PaoType> deviceTypes,
            SortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId, aa.AttributeId, AttributeName, PaoType, PointType, PointOffset");
        sql.append("FROM AttributeAssignment aa");
        sql.append("JOIN CustomAttribute ca ON aa.AttributeId = ca.AttributeId");

        if (CollectionUtils.isNotEmpty(attributeIds)) {
            sql.append("WHERE aa.AttributeId").in(attributeIds);
        }
        if (CollectionUtils.isNotEmpty(deviceTypes)) {
            if (CollectionUtils.isEmpty(attributeIds)) {
                sql.append("WHERE PaoType").in_k(deviceTypes);
            } else {
                sql.append("AND PaoType").in_k(deviceTypes);
            }
        }

        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            if (direction != null) {
                sql.append(direction);
            }
        }
        return jdbcTemplate.query(sql, AttributeDaoImpl.attributeAssignmentMapper);
    }
    
    @Override
    public AttributeAssignment saveAttributeAssignment(Assignment assignment) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        SqlParameterSink params = updateSql.update("AttributeAssignment");
        addAssignmentParameters(params, assignment);
        updateSql.append("WHERE AttributeAssignmentId").eq(assignment.getAttributeAssignmentId());
        int rowsUpdated = jdbcTemplate.update(updateSql);

        if (rowsUpdated < 1) {
            int pk = nextValueHelper.getNextValue("AttributeAssignment");
            assignment.setAttributeAssignmentId(pk);
            SqlStatementBuilder createSql = new SqlStatementBuilder();
            params = createSql.insertInto("AttributeAssignment");
            params.addValue("AttributeAssignmentId", assignment.getAttributeAssignmentId());
            addAssignmentParameters(params, assignment);
            jdbcTemplate.update(createSql);
        }
        attributeDao.cacheAttributes();
        return attributeDao.getAssignmentById(assignment.getAttributeAssignmentId());
    }
    
    @Override
    public CustomAttribute saveCustomAttribute(CustomAttribute attribute) {
        SqlStatementBuilder createSql = new SqlStatementBuilder();
        SqlParameterSink params = createSql.update("CustomAttribute");
        params.addValue("AttributeName", attribute.getName());
        createSql.append("WHERE AttributeId").eq(attribute.getCustomAttributeId());
        int rowsUpdated = jdbcTemplate.update(createSql);
        if (rowsUpdated < 1) {
            int pk = nextValueHelper.getNextValue("CustomAttribute");
            attribute.setCustomAttributeId(pk);
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            params = updateSql.insertInto("CustomAttribute");
            params.addValue("AttributeId", pk);
            params.addValue("AttributeName", attribute.getName());
            jdbcTemplate.update(updateSql);
        }
        attributeDao.cacheAttributes();
        return attribute;
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
        attributeDao.cacheAttributes();
    }

    @Override
    public void deleteAttributeAssignment(int attributeAssignmentId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM AttributeAssignment");
        sql.append("WHERE AttributeAssignmentId").eq(attributeAssignmentId);
        jdbcTemplate.update(sql);
        attributeDao.cacheAttributes();
    }
    
    private void addAssignmentParameters(SqlParameterSink params, Assignment assignment) {
        params.addValue("AttributeId", assignment.getAttributeId());
        params.addValue("PaoType", assignment.getPaoType());
        params.addValue("PointType", assignment.getPointType());
        params.addValue("PointOffset", assignment.getOffset());
    }
}
 