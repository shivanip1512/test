package com.cannontech.stars.dr.event.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.database.data.event.EventWorkOrder;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.dr.event.dao.EventWorkOrderDao;
import com.cannontech.stars.dr.event.model.EventBase;

public class EventWorkOrderDaoImpl implements EventWorkOrderDao {
    private static final RowMapper<EventWorkOrder> rowMapper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    
    private final SqlStatementBuilder selectSql = new SqlStatementBuilder();
    {
        selectSql.append("SELECT EB.EventId, EB.UserId, EB.SystemCategoryId, EB.ActionId, EB.EventTimestamp, OrderId");
        selectSql.append("FROM EventBase EB");
        selectSql.append("JOIN EventWorkOrder EWO ON EWO.EventId = EB.EventId");
        selectSql.append("JOIN ECToWorkOrderMapping MAP ON MAP.WorkOrderId = EWO.OrderId");
    }
    
    static {
        rowMapper = oldEventWorkOrderRowMapper();
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add(com.cannontech.stars.dr.event.model.EventWorkOrder eventWorkOrder) {
        
        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        insertSql.append("INSERT INTO EventWorkOrder");
        insertSql.values(eventWorkOrder.getEventId(), eventWorkOrder.getWorkOrderId());

        jdbcTemplate.update(insertSql);
    
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
            jdbcTemplate.query(sql, new RowCallbackHandler() {
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
    public List<EventBase> getByWorkOrderId(final int workOrderId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectSql);
        sql.append("WHERE MAP.WorkOrderId").eq(workOrderId);
        sql.append("ORDER BY EB.EventTimestamp DESC");
        
        return jdbcTemplate.query(sql, new EventBaseRowMapper());
    }
    
    @Override
    public void deleteEventWorkOrders(List<Integer> workOrderIds) {
        if(!workOrderIds.isEmpty()) {
            chunkyJdbcTemplate.update(new EventWorkOrderDeleteSqlGenerator(), workOrderIds);
        }
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
    
    /**
     * Sql generator for deleting events from EventWorkOrder, useful for bulk deleteing
     * with chunking sql template.
     */
    private class EventWorkOrderDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> workOrderIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM EventWorkOrder WHERE OrderId IN (", workOrderIds, ")");
            return sql.toString();
        }
    }
    
    @Override
    public List<Integer> getEventIdsForWorkOrder(Integer workOrderId){
        String sql = "SELECT EventId FROM EventWorkOrder WHERE OrderId = ?";
        List<Integer> eventIds = new ArrayList<Integer>();
        eventIds = jdbcTemplate.query(sql, new IntegerRowMapper(), workOrderId);
        
        return eventIds;
    }
    
    private static RowMapper<EventWorkOrder> oldEventWorkOrderRowMapper() {
        final RowMapper<EventWorkOrder> mapper = new RowMapper<EventWorkOrder>() {
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

    private static class EventBaseRowMapper implements
            YukonRowMapper<com.cannontech.stars.dr.event.model.EventBase> {

        @Override
        public com.cannontech.stars.dr.event.model.EventBase mapRow(YukonResultSet rs) throws SQLException {
            com.cannontech.stars.dr.event.model.EventBase eventBase = 
                new com.cannontech.stars.dr.event.model.EventBase();

            eventBase.setEventId(rs.getInt("EventId"));
            eventBase.setUserId(rs.getInt("UserId"));
            eventBase.setSystemCategoryId(rs.getInt("SystemCategoryId"));
            eventBase.setActionId(rs.getInt("ActionId"));
            eventBase.setEventTimestamp(rs.getInstant("EventTimestamp"));

            return eventBase;
        }
    }
        
    @PostConstruct
    public void init() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(jdbcTemplate);
    }
}
