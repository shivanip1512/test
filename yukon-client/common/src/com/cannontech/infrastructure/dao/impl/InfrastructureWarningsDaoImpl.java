package com.cannontech.infrastructure.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.google.common.collect.Lists;

public class InfrastructureWarningsDaoImpl implements InfrastructureWarningsDao {
    @Autowired YukonJdbcTemplate jdbcTemplate;
    
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
                                                                                      warning.getSeverity());
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
                 .columns("PaoId", "WarningType", "Severity", "Argument1", "Argument2", "Argument3")
                 .values(values);
        
        jdbcTemplate.yukonBatchUpdate(insertSql);
    }
    
    @Override
    public List<InfrastructureWarning> getWarnings() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaoId, Type, WarningType, Severity, Argument1, Argument2, Argument3");
        sql.append("FROM InfrastructureWarnings iw");
        sql.append("JOIN YukonPaObject ypo ON ypo.PaObjectId = iw.PaoId");
        
        return jdbcTemplate.query(sql, (YukonResultSet rs) -> {
                PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PaoId", "Type");
                InfrastructureWarningType warningType = rs.getEnum("WarningType", InfrastructureWarningType.class);
                InfrastructureWarningSeverity severity = rs.getEnum("Severity", InfrastructureWarningSeverity.class);
                Object[] arguments = getWarningArguments(rs);
                
                return new InfrastructureWarning(paoIdentifier, warningType, arguments, severity);
        });
    }
    
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
