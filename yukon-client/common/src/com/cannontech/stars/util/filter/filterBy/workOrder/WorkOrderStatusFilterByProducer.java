package com.cannontech.stars.util.filter.filterBy.workOrder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.stars.util.DateRangeFilterWrapper;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterBy.FilterBy;
import com.cannontech.stars.util.filter.filterBy.FilterByChunkingSqlGenerator;

public class WorkOrderStatusFilterByProducer extends AbstractWorkOrderFilterByProducer {
    private static final RowMapper<Integer> rowMapper = new IntegerRowMapper();
    
    @Override
    public Collection<? extends FilterBy> createFilterBys(FilterWrapper filter) {
        DateRangeFilterWrapper dateRangeFilterWrapper =
            (DateRangeFilterWrapper) filter;
        
        return Arrays.asList(createWorkOrderStatus(dateRangeFilterWrapper.getFilterID(),
                                                   dateRangeFilterWrapper.getStartDate(),
                                                   dateRangeFilterWrapper.getStopDate()));
    }

    @Override
    public int getFilterType() {
        return YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS;
    }
    
    private FilterBy createWorkOrderStatus(final String filterValue,
            final Date startDate, final Date stopDate) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return JoinTable.EMPTY_JOINTABLES;
            }
            @Override
            public List<Object> getParameterValues() {
                return Collections.emptyList();
            }
            @Override
            public String getSql() {
                SqlStatementBuilder sb1 = new SqlStatementBuilder();
                sb1.append("select y.OrderID ");
                sb1.append("from (");
                sb1.append("    select ewo.orderid, max(eb.eventtimestamp) as maxStamp "); 
                sb1.append("    from eventbase eb join eventworkorder ewo on eb.eventid = ewo.eventid "); 
                sb1.append("    where eb.eventtimestamp <= ? ");
                sb1.append("    and eb.eventtimestamp >= ? ");
                sb1.append("    group by ewo.orderid");
                sb1.append(") x ");
                sb1.append("inner join (");
                sb1.append("    select eb1.eventtimestamp, eb1.actionid, ewo1.orderid ");
                sb1.append("    from eventbase eb1 join eventworkorder ewo1 on eb1.eventid = ewo1.eventid");
                sb1.append(") y on x.maxStamp = y.eventtimestamp ");
                sb1.append("and x.orderid = y.orderid ");
                sb1.append("where y.actionid = ?");
                String sql1 = sb1.toString();
                
                List<Integer> list = jdbcTemplate.query(sql1,
                                                              rowMapper,
                                                              stopDate,
                                                              startDate,
                                                              filterValue);
                
                if (list.isEmpty()) {
                    list.add(Integer.MIN_VALUE);
                }
                
                FilterByChunkingSqlGenerator sqlGenerator = new FilterByChunkingSqlGenerator();
                String sql = sqlGenerator.generate(new SqlGenerator<Integer>() {
                    @Override
                    public String generate(List<Integer> subList) {
                        SqlStatementBuilder sb = new SqlStatementBuilder();
                        sb.append("wob.OrderID IN (");
                        sb.append(subList);
                        sb.append(")");
                        return sb.toString();
                    }
                }, list);

                return sql;
            }
        };
    }

}
