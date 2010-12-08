package com.cannontech.web.stars.dr.operator.inventoryOperations.service.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.dr.hardware.dao.impl.InventoryDaoImpl.InventoryIdentifierMapper;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterMode;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.RuleModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.service.InventoryOperationsFilterService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class InventoryOperationsFilterServiceImpl implements InventoryOperationsFilterService {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public Set<InventoryIdentifier> getInventory(FilterMode filterMode, List<RuleModel> rules, DateTimeZone timeZone, YukonUserContext userContext) throws ParseException {
        SqlFragmentCollection whereClause;
        
        if (filterMode == FilterMode.INTERSECT) {
            whereClause = SqlFragmentCollection.newAndCollection();
        } else {
            whereClause = SqlFragmentCollection.newOrCollection();
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ib.inventoryId InventoryId, yle.YukonDefinitionId YukonDefinitionId");
        sql.append("FROM InventoryBase ib");
        sql.append(  "JOIN LMHardwareBase lmhb ON lmhb.inventoryId = ib.inventoryId");
        sql.append(  "LEFT JOIN LmHardwareControlGroup lmhcg ON lmhcg.InventoryId = ib.InventoryId");
        sql.append(  "JOIN YukonListEntry yle ON yle.EntryID = lmhb.LMHardwareTypeID");
        
        for (RuleModel rule : rules) {
            whereClause.add(getFragmentForRule(rule, timeZone, userContext));
        }
        
        sql.append("WHERE").append(whereClause);
        
        Set<InventoryIdentifier> result = Sets.newHashSet();
        yukonJdbcTemplate.queryInto(sql, new InventoryIdentifierMapper(), result);
        
        return result;
    }
    
    private SqlFragmentSource getFragmentForRule(RuleModel rule, DateTimeZone timeZone, YukonUserContext userContext) throws ParseException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        switch (rule.getRuleType()) {
        
        case DEVICE_TYPE:
            sql.append("yle.entryId").eq(rule.getDeviceType().getEntryID());
            break;
            
        case FIELD_INSTALL_DATE:
            
            LocalDate localDate = new LocalDate(rule.getFieldInstallDate(), timeZone);
            Interval interval = localDate.toInterval(timeZone);
            sql.append("(ib.InstallDate").gte(interval.getStart());
            sql.append("AND ib.InstallDate").lt(interval.getEnd()).append(")");
            break;
            
        case LOAD_GROUP:
            List<String> groupIds = Lists.newArrayList(StringUtils.split(rule.getGroupIds(), ","));
            sql.append("(lmhcg.LmGroupId").in(groupIds);
            sql.append("AND lmhcg.GroupEnrollStart IS NOT NULL");
            sql.append("AND lmhcg.GroupEnrollStop IS NULL");
            sql.append("AND lmhcg.Type").eq_k(LMHardwareControlGroup.ENROLLMENT_ENTRY).append(")");
            break;
            
        case PROGRAM:
            List<String> programIds = Lists.newArrayList(StringUtils.split(rule.getProgramIds(), ","));
            sql.append("(lmhcg.ProgramId").in(programIds);
            sql.append("AND lmhcg.GroupEnrollStart IS NOT NULL");
            sql.append("AND lmhcg.GroupEnrollStop IS NULL");
            sql.append("AND lmhcg.Type").eq_k(LMHardwareControlGroup.ENROLLMENT_ENTRY).append(")");
            break;
            
        case PROGRAM_SIGNUP_DATE:
            LocalDate programSignupDate = new LocalDate(rule.getProgramSignupDate(), timeZone);
            Interval programSignupInterval = programSignupDate.toInterval(timeZone);
            sql.append("(lmhcg.GroupEnrollStart").gte(programSignupInterval.getStart());
            sql.append("AND lmhcg.GroupEnrollStart").lt(programSignupInterval.getEnd());
            sql.append("AND lmhcg.Type").eq(1);
            sql.append(")");
            break;
            
        case SERIAL_NUMBER_RANGE:
            sql.append("(lmhb.ManufacturerSerialNumber").gte(rule.getSerialNumberFrom()).append("AND");
            sql.append("lmhb.ManufacturerSerialNumber").lte(rule.getSerialNumberTo()).append(")");
            break;
            
        case UNENROLLED:
            sql.append("ib.InventoryId NOT IN (");
            sql.append(  "SELECT InventoryId");
            sql.append(  "FROM LmHardwareControlGroup cg");
            sql.append(  "WHERE cg.GroupEnrollStart IS NOT NULL");
            sql.append(  "  AND cg.GroupEnrollStop IS NULL");
            sql.append(  "  AND cg.Type").eq(1);
            sql.append(")");
            break;

        default:
            break;
        }
        
        return sql;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
}