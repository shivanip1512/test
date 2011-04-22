package com.cannontech.web.stars.dr.operator.inventoryOperations.service.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.LmHardwareInventoryIdentifierMapper;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterMode;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.RuleModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.service.InventoryOperationsFilterService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class InventoryOperationsFilterServiceImpl implements InventoryOperationsFilterService {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    
    @Override
    public Set<InventoryIdentifier> getInventory(FilterMode filterMode, List<RuleModel> rules, DateTimeZone timeZone, YukonUserContext userContext) throws ParseException {
        SqlFragmentCollection whereClause;
        
        if (filterMode == FilterMode.AND) {
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
        
        /* Catch BadSqlGrammarException for oracle and DataIntegrityViolationException for sql server
         * when casting serial numbers as numeric when they have letters in them. */
        try {
            yukonJdbcTemplate.queryInto(sql, new LmHardwareInventoryIdentifierMapper(), result);
        } catch (BadSqlGrammarException e) {
            if (e.getCause().getMessage().contains("ORA-01722: invalid number")) {
                throw new InvalidSerialNumberRangeDataException(e);
            } else {
                throw e;
            }
        } catch (DataIntegrityViolationException e) {
            throw new InvalidSerialNumberRangeDataException(e);
        }
        
        return result;
    }
    
    private SqlFragmentSource getFragmentForRule(RuleModel rule, DateTimeZone timeZone, YukonUserContext userContext) throws ParseException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        switch (rule.getRuleType()) {
        
        case APPLIANCE_TYPE:
            SqlStatementBuilder inventoryIdsByAppType = new SqlStatementBuilder();
            inventoryIdsByAppType.append("SELECT IB.InventoryId ");
            inventoryIdsByAppType.append("FROM LMHardwareConfiguration LMHC");
            inventoryIdsByAppType.append("  JOIN ApplianceBase AB ON AB.ApplianceId = LMHC.ApplianceId");
            inventoryIdsByAppType.append("WHERE AB.ApplianceCategoryId").eq(rule.getApplianceType());
            
            sql.append("IB.InventoryId").in(inventoryIdsByAppType);
            break;
            
        case CUSTOMER_TYPE:
            SqlStatementBuilder inventoryIdsFromCustomerTypeSql = new SqlStatementBuilder();
            inventoryIdsFromCustomerTypeSql.append("SELECT IB.InventoryId");
            inventoryIdsFromCustomerTypeSql.append("FROM Customer C");
            inventoryIdsFromCustomerTypeSql.append("  JOIN CustomerAccount CA ON CA.CustomerId = C.CustomerId");
            inventoryIdsFromCustomerTypeSql.append("  JOIN InventoryBase IB ON IB.AccountId = CA.AccountId");
            inventoryIdsFromCustomerTypeSql.append("  LEFT JOIN CICustomerBase CICB ON CICB.CustomerId = C.CustomerId");

            // The customer type is residential
            if (rule.isResidentialCustomerType()) {
                inventoryIdsFromCustomerTypeSql.append("WHERE CICB.CiCustType IS NULL");
            // The customer type is a commercial type
            } else {
                inventoryIdsFromCustomerTypeSql.append("WHERE CICB.CiCustType").eq(rule.getCiCustomerTypeId());
            }
            
            sql.append("IB.InventoryId").in(inventoryIdsFromCustomerTypeSql);
            break;
        
        case DEVICE_STATUS:
            sql.append("IB.CurrentStateId").eq(rule.getDeviceStatusId());
            break;
            
        case DEVICE_STATUS_DATE_RANGE:
            SqlStatementBuilder inventoryIdsFromDeviceStateDateRangeSql = new SqlStatementBuilder();
            inventoryIdsFromDeviceStateDateRangeSql.append("SELECT DISTINCT InventoryId");
            inventoryIdsFromDeviceStateDateRangeSql.append("FROM EventBase EB");
            inventoryIdsFromDeviceStateDateRangeSql.append("  JOIN EventInventory EI ON EI.EventId = EB.EventId");
            inventoryIdsFromDeviceStateDateRangeSql.append("WHERE EB.EventTimestamp").gte(rule.getDeviceStateDateFrom().toDateMidnight(timeZone));
            inventoryIdsFromDeviceStateDateRangeSql.append("  AND EB.EventTimestamp").lt(rule.getDeviceStateDateTo().minusDays(1).toDateMidnight(timeZone));
            
            sql.append("IB.InventoryId").in(inventoryIdsFromDeviceStateDateRangeSql);
            break;
            
        case DEVICE_TYPE:
            sql.append("yle.entryId").eq(rule.getDeviceType());
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
            
        case MEMBER:
            SqlStatementBuilder inventoryIdsFromECSql = new SqlStatementBuilder();
            inventoryIdsFromECSql.append("SELECT ECTIM.InventoryId");
            inventoryIdsFromECSql.append("FROM ECToInventoryMapping ECTIM");
            inventoryIdsFromECSql.append("WHERE ECTIM.EnergyCompanyId").eq(rule.getMemberOfEnergyCompanyId());
            
            sql.append("IB.InventoryId").in(inventoryIdsFromECSql);
            break;
            
        case POSTAL_CODE:
            SqlStatementBuilder accountIdsForServiceZipCodeSql = new SqlStatementBuilder();
            accountIdsForServiceZipCodeSql.append("SELECT Distinct CA.AccountId");
            accountIdsForServiceZipCodeSql.append("FROM CustomerAccount CA");
            accountIdsForServiceZipCodeSql.append("  JOIN Customer Cust ON Cust.CustomerId = CA.CustomerId");
            accountIdsForServiceZipCodeSql.append("  JOIN Contact Cont ON Cont.ContactId = Cust.PrimaryContactId");
            accountIdsForServiceZipCodeSql.append("  JOIN Address A ON A.AddressId = Cont.AddressId");
            accountIdsForServiceZipCodeSql.append("  JOIN Address A2 ON CA.BillingAddressId = A2.AddressId");
            accountIdsForServiceZipCodeSql.append("WHERE A.ZipCode").eq(rule.getPostalCode());
            accountIdsForServiceZipCodeSql.append("OR A2.ZipCode").eq(rule.getPostalCode());
            
            sql.append("IB.AccountId").in(accountIdsForServiceZipCodeSql);
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
            VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
            SqlBuilder oracleSql = builder.buildFor(DatabaseVendor.ORACLE11G, DatabaseVendor.ORACLE10G);
            oracleSql.append("(CAST (lmhb.ManufacturerSerialNumber AS NUMBER(19))").gte(rule.getSerialNumberFrom()).append("AND");
            oracleSql.append("CAST (lmhb.ManufacturerSerialNumber AS NUMBER(19))").lte(rule.getSerialNumberTo()).append(")");
            
            SqlBuilder otherSql =  builder.buildOther();
            otherSql.append("(CAST (lmhb.ManufacturerSerialNumber AS BIGINT)").gte(rule.getSerialNumberFrom()).append("AND");
            otherSql.append("CAST (lmhb.ManufacturerSerialNumber AS BIGINT)").lte(rule.getSerialNumberTo()).append(")");
            
            return builder;
            
        case SERVICE_COMPANY:
            sql.append("IB.InstallationCompanyId").eq(rule.getServiceCompanyId());
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

        case WAREHOUSE:
            SqlStatementBuilder inventoryIdsByWarehouseIdSql = new SqlStatementBuilder();
            inventoryIdsByWarehouseIdSql.append("SELECT ITWM.InventoryId");
            inventoryIdsByWarehouseIdSql.append("FROM InventoryToWarehouseMapping ITWM");
            inventoryIdsByWarehouseIdSql.append("WHERE ITWM.WarehouseId").eq(rule.getWarehouseId());
            
            sql.append("IB.InventoryId").in(inventoryIdsByWarehouseIdSql);
            break;
            
        default:
            break;
        }
        
        return sql;
    }

    // DI Setters
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setVendorSpecificSqlBuilderFactory(VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory) {
        this.vendorSpecificSqlBuilderFactory = vendorSpecificSqlBuilderFactory;
    }
}