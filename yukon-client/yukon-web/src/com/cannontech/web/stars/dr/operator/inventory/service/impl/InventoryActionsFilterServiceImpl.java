package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.web.stars.dr.operator.inventory.model.FilterMode;
import com.cannontech.web.stars.dr.operator.inventory.model.FilterModel;
import com.cannontech.web.stars.dr.operator.inventory.model.RuleModel;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsFilterService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class InventoryActionsFilterServiceImpl implements InventoryActionsFilterService {
    
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    
    @Override
    public Set<InventoryIdentifier> getInventory(FilterModel filter, DateTimeZone timeZone, LiteYukonUser user) {
        
        YukonEnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        SqlFragmentCollection where;
        
        if (filter.getFilterMode() == FilterMode.AND) {
            where = SqlFragmentCollection.newAndCollection();
        } else {
            where = SqlFragmentCollection.newOrCollection();
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ib.inventoryId InventoryId, yle.YukonDefinitionId YukonDefinitionId");
        sql.append("FROM InventoryBase IB");
        sql.append(  "JOIN LMHardwareBase lmhb ON lmhb.InventoryId = ib.InventoryId");
        sql.append(  "LEFT JOIN LmHardwareControlGroup lmhcg ON lmhcg.InventoryId = ib.InventoryId");
        sql.append(  "JOIN YukonListEntry yle ON yle.EntryID = lmhb.LMHardwareTypeID");
        sql.append(  "JOIN ECToInventoryMapping ecim ON ecim.InventoryId = ib.InventoryId");
        
        for (RuleModel rule : filter.getFilterRules()) {
            where.add(getFragmentForRule(rule, timeZone, user));
        }
        
        /* Only retrieve inventory for this energy company */
        sql.append("WHERE ecim.EnergyCompanyId").eq(ec.getEnergyCompanyId());
        sql.append("AND").append(where);
        
        Set<InventoryIdentifier> result = Sets.newHashSet();
        
        /* Catch BadSqlGrammarException for oracle and DataIntegrityViolationException for sql server
         * when casting serial numbers as numeric when they have letters in them. */
        try {
            jdbcTemplate.queryInto(sql, new LmHardwareInventoryIdentifierMapper(), result);
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
    
    private SqlFragmentSource getFragmentForRule(RuleModel rule, DateTimeZone timeZone, LiteYukonUser user) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        switch (rule.getRuleType()) {
        
        case APPLIANCE_TYPE:
            
            sql.append("IB.InventoryId").in(new SqlStatementBuilder()
                .append("SELECT IB.InventoryId ")
                .append("FROM LMHardwareConfiguration LMHC")
                .append("  JOIN ApplianceBase AB ON AB.ApplianceId = LMHC.ApplianceId")
                .append("WHERE AB.ApplianceCategoryId").eq(rule.getApplianceType()));
            break;
            
        case ASSIGNED:
            
            sql.append("IB.AccountId").neq_k(0);
            break;
            
        case CUSTOMER_TYPE:
            
            SqlStatementBuilder customerTypeSql = new SqlStatementBuilder();
            customerTypeSql.append("SELECT IB.InventoryId");
            customerTypeSql.append("FROM Customer C");
            customerTypeSql.append("  JOIN CustomerAccount CA ON CA.CustomerId = C.CustomerId");
            customerTypeSql.append("  JOIN InventoryBase IB ON IB.AccountId = CA.AccountId");

            // The customer type is residential
            if (rule.isResidentialCustomerType()) {
                customerTypeSql.append("WHERE C.CustomerTypeId").eq_k(CustomerTypes.CUSTOMER_RESIDENTIAL);
            // The customer type is a commercial type
            } else {
                customerTypeSql.append("  JOIN CICustomerBase CICB ON CICB.CustomerId = C.CustomerId");
                customerTypeSql.append("WHERE C.CustomerTypeId").eq_k(CustomerTypes.CUSTOMER_CI);
                customerTypeSql.append("  AND CICB.CiCustType").eq(rule.getCiCustomerTypeId());
            }
            
            sql.append("IB.InventoryId").in(customerTypeSql);
            break;
        
        case DEVICE_STATUS:
            
            sql.append("IB.CurrentStateId").eq(rule.getDeviceStatusId());
            break;
            
        case DEVICE_STATUS_DATE_RANGE:
            
            DateTime from = rule.getDeviceStateDateFrom().toDateTimeAtStartOfDay(timeZone);
            DateTime to = rule.getDeviceStateDateTo().plusDays(1).toDateTimeAtStartOfDay(timeZone);
            sql.append("IB.InventoryId").in(new SqlStatementBuilder()
                .append("SELECT DISTINCT InventoryId")
                .append("FROM EventBase EB")
                .append("  JOIN EventInventory EI ON EI.EventId = EB.EventId")
                .append("WHERE EB.EventTimestamp").gte(from)
                .append("  AND EB.EventTimestamp").lt(to));
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
            
        case POSTAL_CODE:
            
            sql.append("IB.AccountId").in(new SqlStatementBuilder()
                .append("SELECT Distinct CA.AccountId")
                .append("FROM CustomerAccount CA")
                .append("  JOIN Customer Cust ON Cust.CustomerId = CA.CustomerId")
                .append("  JOIN Contact Cont ON Cont.ContactId = Cust.PrimaryContactId")
                .append("  JOIN Address A ON A.AddressId = Cont.AddressId")
                .append("  JOIN Address A2 ON CA.BillingAddressId = A2.AddressId")
                .append("WHERE A.ZipCode").eq(rule.getPostalCode())
                .append("OR A2.ZipCode").eq(rule.getPostalCode()));
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
            
            long fromSn = rule.getSerialNumberFrom();
            long toSn = rule.getSerialNumberTo();
            
            VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
            SqlBuilder oracleSql = builder.buildForAllOracleDatabases();
            
            oracleSql.append("(CAST (lmhb.ManufacturerSerialNumber AS NUMBER(19))").gte(fromSn).append("AND");
            oracleSql.append("CAST (lmhb.ManufacturerSerialNumber AS NUMBER(19))").lte(toSn).append(")");
            
            SqlBuilder otherSql =  builder.buildOther();
            otherSql.append("(CAST (lmhb.ManufacturerSerialNumber AS BIGINT)").gte(fromSn).append("AND");
            otherSql.append("CAST (lmhb.ManufacturerSerialNumber AS BIGINT)").lte(toSn).append(")");
            
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
            
            boolean multiple = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MULTI_WAREHOUSE, user);
            if (multiple) {
                sql.append("IB.InventoryId").in(new SqlStatementBuilder()
                    .append("SELECT ITWM.InventoryId")
                    .append("FROM InventoryToWarehouseMapping ITWM")
                    .append("WHERE ITWM.WarehouseId").eq(rule.getWarehouseId()));
            } else {
                sql.append("IB.AccountId").eq_k(0);
            }
            break;
            
        default:
            break;
        }
        
        return sql;
    }

}