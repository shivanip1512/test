package com.cannontech.web.admin.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.exception.DataDependencyException.DependencyType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.dao.impl.AttributeDaoImpl;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.dao.CustomAttributeDao;

public class CustomAttributeDaoImpl implements CustomAttributeDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private AttributeDao attributeDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DatabaseVendorResolver databaseConnectionVendorResolver;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private static final Logger log = YukonLogManager.getLogger(CustomAttributeDaoImpl.class);
    
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
        try {
            assignment.setAttributeAssignmentId(nextValueHelper.getNextValue("AttributeAssignment"));
            SqlStatementBuilder createSql = new SqlStatementBuilder();
            SqlParameterSink params = createSql.insertInto("AttributeAssignment");
            params.addValue("AttributeAssignmentId", assignment.getAttributeAssignmentId());
            addAssignmentParameters(params, assignment);
            jdbcTemplate.update(createSql);
            return attributeDao.getAssignmentById(assignment.getAttributeAssignmentId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("An attribute assignment already exists for Device Type:" + assignment.getPaoType() + ", PointType:"
                    + assignment.getPointType() + ", Offset:" + assignment.getOffset() + ".", e);
        }
    }
    
    @Override
    public AttributeAssignment updateAttributeAssignment(Assignment assignment) {
        try {
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            SqlParameterSink params = updateSql.update("AttributeAssignment");
            addAssignmentParameters(params, assignment);
            updateSql.append("WHERE AttributeAssignmentId").eq(assignment.getAttributeAssignmentId());
            jdbcTemplate.update(updateSql);
            return attributeDao.getAssignmentById(assignment.getAttributeAssignmentId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException(
                    "An attribute assignment already exists for Device Type:" + assignment.getPaoType() + ", PointType:"
                            + assignment.getPointType() + ", Offset:" + assignment.getOffset() + ".",
                    e);
        }
    }

    @Override
    public CustomAttribute createCustomAttribute(CustomAttribute attribute) {
        try {
            SqlStatementBuilder createSql = new SqlStatementBuilder();
            attribute.setCustomAttributeId(nextValueHelper.getNextValue("CustomAttribute"));
            SqlParameterSink params = createSql.insertInto("CustomAttribute");
            params.addValue("AttributeId", attribute.getCustomAttributeId());
            params.addValue("AttributeName", attribute.getName());
            jdbcTemplate.update(createSql);
            return attribute;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to create Custom Attribute. An attribute with this name may already exist.", e);
        }
    }
    
    @Override
    public CustomAttribute updateCustomAttribute(CustomAttribute attribute) {
        try {
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            SqlParameterSink params = updateSql.update("CustomAttribute");
            params.addValue("AttributeName", attribute.getName());
            updateSql.append("WHERE AttributeId").eq(attribute.getCustomAttributeId());
            jdbcTemplate.update(updateSql);
            return attribute;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to update Custom Attribute. An attribute with this name may already exist.", e);
        }
    }

    @Override
    public void deleteCustomAttribute(int attributeId) throws DataDependencyException {
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();
        SqlStatementBuilder sqlExportFormat = new SqlStatementBuilder();
        sqlExportFormat.append("SELECT FormatName FROM ArchiveValuesExportFormat format");
        sqlExportFormat.append("JOIN ArchiveValuesExportAttribute att on att.FormatId = format.FormatId");
        sqlExportFormat.append("WHERE att.AttributeName").eq(String.valueOf(attributeId));
        SqlStatementBuilder sqlExportJob = new SqlStatementBuilder();
        sqlExportJob.append("WITH JobIds");
        sqlExportJob.append("   AS");
        sqlExportJob.append("     (");
        sqlExportJob.append("        SELECT jp.JobID FROM JobProperty jp ");
        sqlExportJob.append("           JOIN Job j on jp.JobID = j.JobID");
        sqlExportJob.append("           JOIN CustomAttribute ca ON jp.value");
        if(isOracle) {
            sqlExportJob.append("               LIKE ('%' || ca.AttributeId || '%')");
        } else {
            sqlExportJob.append("               LIKE CONCAT('%', ca.AttributeId, '%')");
        }
        sqlExportJob.append("                   WHERE jp.name='attributes'");
        sqlExportJob.append("                   AND BeanName = 'scheduledArchivedDataFileExportJobDefinition'");
        sqlExportJob.append("                   AND ca.AttributeId").eq(attributeId);
        sqlExportJob.append("                   AND j.Disabled").neq_k(JobDisabledStatus.D);
        sqlExportJob.append("     )");
        sqlExportJob.append("SELECT jpName.Value as Name, jpAttribute.Value as Value, j.JobID");
        sqlExportJob.append("FROM JobIds j JOIN JobProperty jpAttribute on jpAttribute.JobID = j.JobID");
        sqlExportJob.append("JOIN JobProperty jpName on jpName.JobID = j.JobID");
        sqlExportJob.append("WHERE jpAttribute.Name = 'attributes'");
        sqlExportJob.append("AND jpName.Name = 'exportFileName'");

        List<String> formatDetails = jdbcTemplate.query(sqlExportFormat, TypeRowMapper.STRING);
        List<String> exportDetails = new ArrayList<>();
        
        jdbcTemplate.query(sqlExportJob, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                String name = rs.getString("Name");
                String value = rs.getString("Value");
                if (Arrays.asList(value.split(",")).contains(String.valueOf(attributeId))) {
                    exportDetails.add(name);
                }
            }
        });
        
        if (formatDetails.isEmpty() && exportDetails.isEmpty()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM CustomAttribute");
            sql.append("WHERE AttributeId").eq(attributeId);
            jdbcTemplate.update(sql);
        } else {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
            DataDependencyException exception = new DataDependencyException(
                    messageSourceAccessor.getMessage(attributeDao.getCustomAttribute(attributeId)),
                    " Attribute " + attributeId + " cannot be deleted");
            log.debug("dependent object:{}", exception.getDependentObject());
            if (!formatDetails.isEmpty()) {
                exception.addDependency(DependencyType.EXPORT_FORMAT, formatDetails);
                log.debug("format names:{}", exception.getDependency(DependencyType.EXPORT_FORMAT));
            }
            if (!exportDetails.isEmpty()) {
                exception.addDependency(DependencyType.SCHEDULED_EXPORT, exportDetails);
                log.debug("export names:{}", exception.getDependency(DependencyType.SCHEDULED_EXPORT));
            }
            throw exception;
        }
    }

    @Override
    public void deleteAttributeAssignment(int attributeAssignmentId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM AttributeAssignment");
        sql.append("WHERE AttributeAssignmentId").eq(attributeAssignmentId);
        jdbcTemplate.update(sql);
    }
    
    private void addAssignmentParameters(SqlParameterSink params, Assignment assignment) {
        if (assignment.getAttributeId() != null) {
            params.addValue("AttributeId", assignment.getAttributeId());
        }
        if (assignment.getPaoType() != null) {
            params.addValue("PaoType", assignment.getPaoType());
        }
        if (assignment.getPointType() != null) {
            params.addValue("PointType", assignment.getPointType());
        }
        if (assignment.getOffset() != null) {
            params.addValue("PointOffset", assignment.getOffset());
        }
    }
}
 