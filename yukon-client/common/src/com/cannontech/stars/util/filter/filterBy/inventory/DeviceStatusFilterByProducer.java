package com.cannontech.stars.util.filter.filterBy.inventory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterBy.FilterBy;
import com.cannontech.stars.util.filter.filterBy.FilterByChunkingSqlGenerator;

public class DeviceStatusFilterByProducer extends AbstractInventoryFilterByProducer {
    private static final ParameterizedRowMapper<Integer> rowMapper = new IntegerRowMapper();
    
    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        return Arrays.asList(NON_DUMMY_METER,
                             createDeviceStatus(filter.getFilterID()));
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS;
    }
    
    private FilterBy createDeviceStatus(final String filterValue) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return JoinTable.EMPTY_JOINTABLES;
            }
            @Override
            public String getSql() {
                
                SqlStatementBuilder sb1 = new SqlStatementBuilder();
                sb1.append("select y.InventoryId ");
                sb1.append("from ( ");
                sb1.append("    select lmhe.InventoryId, max(lmceb.EventDateTime) as maxTime "); 
                sb1.append("    from LMCustomerEventBase lmceb join LMHardwareEvent lmhe on lmceb.EventId = lmhe.EventId group by lmhe.InventoryId) x ");
                sb1.append("    inner join (select lmceb1.EventDateTime, lmceb1.ActionId, lmhe1.InventoryId from LMCustomerEventBase lmceb1 join LMHardwareEvent lmhe1 on lmceb1.eventid = lmhe1.eventid) y on x.maxTime = y.EventDateTime AND x.InventoryId = y.InventoryId ");
                sb1.append("where y.ActionId IN (select EntryId from YukonListEntry where YukonDefinitionId = ? ");
                sb1.append(")");
                String sql1 = sb1.toString();
                
                List<Integer> list = simpleJdbcTemplate.query(sql1, rowMapper, filterValue);
                
                if (list.isEmpty()) {
                    list.add(Integer.MIN_VALUE);
                }
                
                FilterByChunkingSqlGenerator sqlGenerator = new FilterByChunkingSqlGenerator();
                String chunkedSql = sqlGenerator.generate(new SqlGenerator<Integer>() {
                    @Override
                    public String generate(List<Integer> subList) {
                        SqlStatementBuilder sb = new SqlStatementBuilder();
                        sb.append("ib.InventoryID IN (");
                        sb.append(subList);
                        sb.append(")");
                        return sb.toString();
                    }
                }, list);
                
                SqlStatementBuilder sb2 = new SqlStatementBuilder();
                sb2.append("ib.CurrentStateID = ? OR ");
                sb2.append(chunkedSql);
                
                String sql2 = sb2.toString();
                return sql2;
            }
            @Override
            public List<Object> getParameterValues() {
                return Arrays.<Object>asList(filterValue);
            }
        };
    }

}
