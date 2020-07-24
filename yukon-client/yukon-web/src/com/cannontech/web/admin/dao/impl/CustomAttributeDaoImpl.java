package com.cannontech.web.admin.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.dao.impl.AttributeDaoImpl;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.dao.CustomAttributeDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;

public class CustomAttributeDaoImpl implements CustomAttributeDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private AttributeDao attributeDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private static String deletionFailed = "yukon.web.modules.adminSetup.config.attributes.deletion";
    private static String deletionFailedDataExport = "yukon.web.modules.adminSetup.config.attributes.deletion.export";
    //Device data monitor
    private static String deletionFailedDDM = "yukon.web.modules.adminSetup.config.attributes.deletion.ddm";
    
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
    public AttributeAssignment createAttributeAssignment(Assignment assignment) {
        assignment.setAttributeAssignmentId(nextValueHelper.getNextValue("AttributeAssignment"));
        SqlStatementBuilder createSql = new SqlStatementBuilder();
        SqlParameterSink params = createSql.insertInto("AttributeAssignment");
        params.addValue("AttributeAssignmentId", assignment.getAttributeAssignmentId());
        addAssignmentParameters(params, assignment);
        jdbcTemplate.update(createSql);
        attributeDao.cacheAttributes();
        return attributeDao.getAssignmentById(assignment.getAttributeAssignmentId());
    }
    
    @Override
    public AttributeAssignment updateAttributeAssignment(Assignment assignment) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        SqlParameterSink params = updateSql.update("AttributeAssignment");
        addAssignmentParameters(params, assignment);
        updateSql.append("WHERE AttributeAssignmentId").eq(assignment.getAttributeAssignmentId());
        jdbcTemplate.update(updateSql);
        attributeDao.cacheAttributes();
        return attributeDao.getAssignmentById(assignment.getAttributeAssignmentId());
    }
    
    @Override
    public CustomAttribute createCustomAttribute(CustomAttribute attribute) {
        SqlStatementBuilder createSql = new SqlStatementBuilder();
        attribute.setCustomAttributeId(nextValueHelper.getNextValue("CustomAttribute"));
        SqlParameterSink params = createSql.insertInto("CustomAttribute");
        params.addValue("AttributeId", attribute.getCustomAttributeId());
        params.addValue("AttributeName", attribute.getName());
        jdbcTemplate.update(createSql);
        attributeDao.cacheAttributes();
        return attribute;
    }
    
    @Override
    public CustomAttribute updateCustomAttribute(CustomAttribute attribute) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        SqlParameterSink params = updateSql.update("CustomAttribute");
        params.addValue("AttributeName", attribute.getName());
        updateSql.append("WHERE AttributeId").eq(attribute.getCustomAttributeId());
        jdbcTemplate.update(updateSql);
        attributeDao.cacheAttributes();
        return attribute;
    }

    @Override
    public void deleteCustomAttribute(int attributeId) throws DataDependencyException {
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        
        //Return message: Attribute {0} cannot be deleted, it is currently being used by: Device Data Monitor(s) alpha, beta. Data Export(s) myExportName
        List<String> dataExportsUsingTheAttribute = new ArrayList<>();
        SqlStatementBuilder sqlExport = new SqlStatementBuilder();
        sqlExport.append("SELECT FormatName FROM ArchiveValuesExportFormat format");
        sqlExport.append("JOIN ArchiveValuesExportAttribute att on att.FormatId = format.FormatId");
        sqlExport.append("WHERE att.AttributeName").eq(attributeId);
        dataExportsUsingTheAttribute.addAll(jdbcTemplate.query(sqlExport, TypeRowMapper.STRING));
        
        if(dataExportsUsingTheAttribute.isEmpty()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM CustomAttribute");
            sql.append("WHERE AttributeId").eq(attributeId);
            jdbcTemplate.update(sql);
            attributeDao.cacheAttributes();
        }
        
      /* if(true) {
            throw new DataDependencyException("--------DataDependencyException-------");
        }*/
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
 