package com.cannontech.stars.dr.event.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.stars.event.EventWorkOrder;
import com.cannontech.stars.dr.event.dao.EventWorkOrderDao;

public class EventWorkOrderDaoImpl implements EventWorkOrderDao {
    private static final String selectSql;
    private static final String selecltByWorkOrderIdSql;
    private static final ParameterizedRowMapper<EventWorkOrder> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        
        selectSql = "SELECT EB.EVENTID,USERID,SYSTEMCATEGORYID,ACTIONID,EVENTTIMESTAMP,ORDERID " +
                    "FROM EventBase EB,EventWorkOrder EWO,ECToWorkOrderMapping map";
        
        selecltByWorkOrderIdSql = selectSql + " WHERE EB.EVENTID = EWO.EVENTID " +
                                               "AND MAP.WORKORDERID = EWO.ORDERID " +
                                               "AND EWO.ORDERID = ? " +
                                               "ORDER BY EB.EVENTID, EVENTTIMESTAMP";
        
        rowMapper = createRowMapper();
        
    }
    
    @Override
    public Map<Integer, List<EventWorkOrder>> getByWorkOrders(final List<LiteWorkOrderBase> workOrderList) {
        final List<Integer> idList = new ArrayList<Integer>(workOrderList.size());
        for (final LiteWorkOrderBase workOrder : workOrderList) {
            Integer id = workOrder.getLiteID();
            idList.add(id);
        }
        
        final List<String> queryList = new ArrayList<String>();
        int size = idList.size();
        int chunkSize = 1000;
        for (int start = 0; start < size; start += chunkSize ) {
            int nextToIndex = start + chunkSize;
            int toIndex = (size < nextToIndex) ? size : nextToIndex;
            List<Integer> subList = idList.subList(start, toIndex);
            String sql = builderSqlForWorkOrderId(subList);
            queryList.add(sql);
        }
        
        final Map<Integer,List<EventWorkOrder>> resultMap = new HashMap<Integer,List<EventWorkOrder>>(workOrderList.size());
        for (final String sql : queryList) {
            simpleJdbcTemplate.getJdbcOperations().query(sql, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    final Integer key = rs.getInt("ORDERID");
                    List<EventWorkOrder> value = resultMap.get(key);
                    if (value == null) {
                        value = new ArrayList<EventWorkOrder>();
                        resultMap.put(key, value);
                    }
                    EventWorkOrder event = rowMapper.mapRow(rs, rs.getRow());
                    value.add(event);
                }
            });
        }
        return resultMap;
    }
    
    @Override
    public List<EventWorkOrder> getByWorkOrderId(final int workOrderId) {
        List<EventWorkOrder> list = simpleJdbcTemplate.query(selecltByWorkOrderIdSql, rowMapper, workOrderId);
        return list;
    }
    
    private String builderSqlForWorkOrderId(List<Integer> workOrderIdList) {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(selectSql);
        sqlBuilder.append(" WHERE EB.EVENTID = EWO.EVENTID ");
        sqlBuilder.append("AND MAP.WORKORDERID = EWO.ORDERID ");
        sqlBuilder.append("AND EWO.ORDERID IN (");
        sqlBuilder.append(SqlStatementBuilder.convertToSqlLikeList(workOrderIdList));
        sqlBuilder.append(") ");
        sqlBuilder.append("ORDER BY EB.EVENTID, EVENTTIMESTAMP");
        String sql = sqlBuilder.toString();
        return sql;
    }
    
    private static ParameterizedRowMapper<EventWorkOrder> createRowMapper() {
        final ParameterizedRowMapper<EventWorkOrder> mapper = new ParameterizedRowMapper<EventWorkOrder>() {
            @Override
            public EventWorkOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
                final EventWorkOrder event = new EventWorkOrder();
                event.setEventID(rs.getInt("EVENTID"));
                event.getEventBase().setUserID(rs.getInt("USERID"));
                event.getEventBase().setSystemCategoryID(rs.getInt("SYSTEMCATEGORYID"));
                event.getEventBase().setActionID(rs.getInt("ACTIONID"));
                event.getEventBase().setEventTimestamp( new Date(rs.getTimestamp("EVENTTIMESTAMP").getTime() ));                
                event.getEventWorkOrder().setWorkOrderID(rs.getInt("ORDERID"));
                return event;
            }
        };
        return mapper;
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
