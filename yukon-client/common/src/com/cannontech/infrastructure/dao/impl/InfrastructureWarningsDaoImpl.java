package com.cannontech.infrastructure.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningSummary;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.google.common.collect.Lists;

public class InfrastructureWarningsDaoImpl implements InfrastructureWarningsDao{

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DatabaseVendorResolver dbVendorResolver;
        
    @Override
    @Transactional
    public void insert(Collection<InfrastructureWarning> warnings) {
        // First, delete the current contents of the table
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM InfrastructureWarnings");
        jdbcTemplate.update(deleteSql);
        
        // Next, build a map of the warnings data
        List<List<Object>> values = warnings.stream()
                                            .map(warning -> {
                                                List<Object> row = Lists.newArrayList(warning.getPaoIdentifier().getPaoId(),
                                                                                      warning.getWarningType(),
                                                                                      warning.getSeverity(),
                                                                                      warning.getTimestamp());
                                                for (int i = 0; i < 3; i++) {
                                                    if (i < warning.getArguments().length) {
                                                        row.add(warning.getArguments()[i].toString());
                                                    } else {
                                                        row.add(null);
                                                    }
                                                }
                                                return row;
                                            })
                                            .collect(Collectors.toList());
        
        // Finally, insert the new warnings
        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        insertSql.batchInsertInto("InfrastructureWarnings")
                 .columns("PaoId", "WarningType", "Severity", "Timestamp", "Argument1", "Argument2", "Argument3")
                 .values(values);
        
        jdbcTemplate.yukonBatchUpdate(insertSql);
    }
    
    @Override
    public synchronized List<InfrastructureWarning> getWarnings() {
        return jdbcTemplate.query(selectAllInfrastructureWarnings(), infrastructureWarningRowMapper);
    }

    @Override
    public synchronized List<InfrastructureWarning> getWarnings(int deviceId) {
        SqlStatementBuilder sql = selectAllInfrastructureWarnings();
        sql.append(" WHERE ypo.PAObjectID").eq(deviceId);
        List<InfrastructureWarning> warnings = jdbcTemplate.query(sql, infrastructureWarningRowMapper);
        return warnings;
    }

    @Override
    public synchronized InfrastructureWarningSummary getWarningsSummary() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT");

        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaObjectId)");
        sql.append("FROM YukonPaObject");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.GATEWAY.getPaoTypes());
        sql.append(") AS Gateways,");

        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaObjectId)");
        sql.append("FROM YukonPaObject");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.RELAY.getPaoTypes());
        sql.append(") AS Relays,");

        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaObjectId)");
        sql.append("FROM YukonPaObject");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.CCU.getPaoTypes());
        sql.append(") AS Ccus,");

        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaObjectId)");
        sql.append("FROM YukonPaObject");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.REPEATER.getPaoTypes());
        sql.append(") AS Repeaters,");
        
        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaObjectId)");
        sql.append("FROM YukonPaObject");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.METER.getPaoTypes());
        sql.append(") AS Meters,");

        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaoId)");
        sql.append("FROM InfrastructureWarnings iw");
        sql.append("JOIN YukonPaObject ypo ON iw.PaoId = ypo.PAObjectID");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.GATEWAY.getPaoTypes());
        sql.append(") AS WarningGateways,");

        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaoId)");
        sql.append("FROM InfrastructureWarnings iw");
        sql.append("JOIN YukonPaObject ypo ON iw.PaoId = ypo.PAObjectID");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.RELAY.getPaoTypes());
        sql.append(") AS WarningRelays,");

        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaoId)");
        sql.append("FROM InfrastructureWarnings iw");
        sql.append("JOIN YukonPaObject ypo ON iw.PaoId = ypo.PAObjectID");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.CCU.getPaoTypes());
        sql.append(") AS WarningCcus,");

        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaoId)");
        sql.append("FROM InfrastructureWarnings iw");
        sql.append("JOIN YukonPaObject ypo ON iw.PaoId = ypo.PAObjectID");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.REPEATER.getPaoTypes());
        sql.append(") AS WarningRepeaters,");
        
        sql.append("(");
        sql.append("SELECT COUNT(DISTINCT PaoId)");
        sql.append("FROM InfrastructureWarnings iw");
        sql.append("JOIN YukonPaObject ypo ON iw.PaoId = ypo.PAObjectID");
        sql.append("WHERE Type").in(InfrastructureWarningDeviceCategory.METER.getPaoTypes());
        sql.append(") AS WarningMeters");
        
        if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            sql.append("FROM dual");
        }

        return jdbcTemplate.queryForObject(sql, warningSummaryRowMapper);

    }

    @Override
    public List<InfrastructureWarning> getWarnings(Boolean highSeverityOnly, InfrastructureWarningDeviceCategory... categories) {
        // Get all PaoTypes included in these categories
        Set<PaoType> types = Arrays.stream(categories)
                                   .map(category -> category.getPaoTypes())
                                   .flatMap(Set::stream)
                                   .collect(Collectors.toSet());
        
        SqlStatementBuilder sql = selectAllInfrastructureWarnings();
        sql.append("WHERE Type").in_k(types);
        if (BooleanUtils.isTrue(highSeverityOnly)) {
            sql.append("AND Severity").eq("HIGH");
        }
        
        return jdbcTemplate.query(sql, infrastructureWarningRowMapper);
    }
    
    private static SqlStatementBuilder selectAllInfrastructureWarnings() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaoId, Type, WarningType, Severity, Timestamp, Argument1, Argument2, Argument3");
        sql.append("FROM InfrastructureWarnings iw");
        sql.append("JOIN YukonPaObject ypo ON ypo.PaObjectId = iw.PaoId");
        return sql;
    }
    
    private static final YukonRowMapper<InfrastructureWarningSummary> warningSummaryRowMapper = (YukonResultSet rs) -> {
        InfrastructureWarningSummary summary = new InfrastructureWarningSummary();
        summary.setTotalGateways(rs.getInt("Gateways"));
        summary.setTotalRelays(rs.getInt("Relays"));
        summary.setTotalCcus(rs.getInt("Ccus"));
        summary.setTotalRepeaters(rs.getInt("Repeaters"));
        summary.setTotalMeters(rs.getInt("Meters"));
        summary.setWarningGateways(rs.getInt("WarningGateways"));
        summary.setWarningRelays(rs.getInt("WarningRelays"));
        summary.setWarningCcus(rs.getInt("WarningCcus"));
        summary.setWarningRepeaters(rs.getInt("WarningRepeaters"));
        summary.setWarningMeters(rs.getInt("WarningMeters"));
        return summary;
    };
    
    private static final YukonRowMapper<InfrastructureWarning> infrastructureWarningRowMapper = (YukonResultSet rs) -> {
        PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PaoId", "Type");
        InfrastructureWarningType warningType = rs.getEnum("WarningType", InfrastructureWarningType.class);
        InfrastructureWarningSeverity severity = rs.getEnum("Severity", InfrastructureWarningSeverity.class);
        Instant timestamp = rs.getInstant("Timestamp");
        Object[] arguments = getWarningArguments(rs);
        
        return new InfrastructureWarning(paoIdentifier, warningType, severity, timestamp, arguments);
    };
    
    private static Object[] getWarningArguments(YukonResultSet rs) throws SQLException {
        List<String> arguments = new ArrayList<>();
        String argument1 = rs.getString("Argument1");
        if (argument1 != null) {
            arguments.add(argument1);
        }
        String argument2 = rs.getString("Argument2");
        if (argument2 != null) {
            arguments.add(argument2);
        }
        String argument3 = rs.getString("Argument3");
        if (argument3 != null) {
            arguments.add(argument3);
        }
        return arguments.toArray();
    }
}
