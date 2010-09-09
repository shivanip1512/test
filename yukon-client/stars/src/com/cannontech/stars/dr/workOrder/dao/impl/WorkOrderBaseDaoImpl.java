package com.cannontech.stars.dr.workOrder.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.event.dao.EventBaseDao;
import com.cannontech.stars.dr.event.dao.EventWorkOrderDao;
import com.cannontech.stars.dr.event.model.EventBase;
import com.cannontech.stars.dr.workOrder.dao.WorkOrderBaseDao;
import com.cannontech.stars.dr.workOrder.model.WorkOrderBase;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class WorkOrderBaseDaoImpl implements WorkOrderBaseDao, InitializingBean {

    private NextValueHelper nextValueHelper;
    private EventBaseDao eventBaseDao;
    private EventWorkOrderDao eventWorkOrderDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    private SimpleTableAccessTemplate<WorkOrderBase> workOrderBaseTemplate;

    private SqlStatementBuilder selectBase = new SqlStatementBuilder();
    {    
        selectBase.append("SELECT *");
        selectBase.append("FROM WorkOrderBase WOB");
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add(WorkOrderBase workOrderBase) {
        // Create work order base entry
        int workOrderId = nextValueHelper.getNextValue("WorkOrderBase");
        workOrderBase.setOrderId(workOrderId);
        
        workOrderBaseTemplate.insert(workOrderBase);
    }
    
    @Override
    @Transactional(readOnly = false)
    public void delete(int workOrderId) {
        
        // Get event ids for deletion
        List<EventBase> eventBases = eventWorkOrderDao.getByWorkOrderId(workOrderId);
        List<Integer> eventIds = 
            Lists.transform(eventBases, new Function<EventBase, Integer>() {
                @Override
                public Integer apply(EventBase eventBase) {
                    return eventBase.getEventId();
                }
            });
        
        // Remove mapping entry
        eventWorkOrderDao.deleteEventWorkOrders(Collections.singletonList(workOrderId));

        // Remove events
        eventBaseDao.deleteEvents(eventIds);
        
        // Remove work order base
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM WorkOrderBase");
        sql.append("WHERE OrderId").eq(workOrderId);
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void update(WorkOrderBase workOrderBase) {
        workOrderBaseTemplate.update(workOrderBase);
    }
    
    @Override
    public WorkOrderBase getById(int workOrderId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectBase);
        sql.append("WHERE orderId").eq(workOrderId);
        
        return yukonJdbcTemplate.queryForObject(sql, new WorkOrderBaseRowMapper());
    }

    @Override
    public List<WorkOrderBase> getByAccountId(int accountId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectBase);
        sql.append("WHERE accountId").eq(accountId);
        
        return yukonJdbcTemplate.query(sql, new WorkOrderBaseRowMapper());
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        workOrderBaseTemplate = new SimpleTableAccessTemplate<WorkOrderBase>(yukonJdbcTemplate, nextValueHelper);
        workOrderBaseTemplate.withTableName("WorkOrderBase");
        workOrderBaseTemplate.withPrimaryKeyField("OrderId");
        workOrderBaseTemplate.withFieldMapper(workOrderBaseFieldMapper);
        workOrderBaseTemplate.withPrimaryKeyValidOver(0);
    }
    
    private FieldMapper<WorkOrderBase> workOrderBaseFieldMapper = new FieldMapper<WorkOrderBase>() {

        @Override
        public void extractValues(MapSqlParameterSource p, WorkOrderBase workOrderBase) {
            p.addValue("OrderNumber", workOrderBase.getOrderNumber());           
            p.addValue("WorkTypeId", workOrderBase.getWorkTypeId());
            p.addValue("CurrentStateId", workOrderBase.getCurrentStateId());        
            p.addValue("ServiceCompanyId", workOrderBase.getServiceCompanyId());
            p.addValue("DateReported", workOrderBase.getDateReported());
            p.addValue("OrderedBy", workOrderBase.getOrderedBy());
            p.addValue("Description", workOrderBase.getDescription());
            p.addValue("DateScheduled", workOrderBase.getDateScheduled());
            p.addValue("DateCompleted", workOrderBase.getDateCompleted());
            p.addValue("ActionTaken", workOrderBase.getActionTaken());
            p.addValue("AccountId", workOrderBase.getAccountId());
            p.addValue("AdditionalOrderNumber", workOrderBase.getAdditionalOrderNumber());
        }

        @Override
        public Number getPrimaryKey(WorkOrderBase object) {
            return object.getOrderId();
        }

        @Override
        public void setPrimaryKey(WorkOrderBase object, int value) {
            object.setOrderId(value);
        }
    };
    
    // Row Mappers
    private static class WorkOrderBaseRowMapper implements YukonRowMapper<WorkOrderBase> {

        @Override
        public WorkOrderBase mapRow(YukonResultSet rs) throws SQLException {
            WorkOrderBase workOrderBase = new WorkOrderBase();

            workOrderBase.setOrderId(rs.getInt("OrderId"));
            workOrderBase.setOrderNumber(rs.getString("OrderNumber"));
            workOrderBase.setWorkTypeId(rs.getInt("WorkTypeId"));
            workOrderBase.setCurrentStateId(rs.getInt("CurrentStateId"));
            workOrderBase.setServiceCompanyId(rs.getInt("ServiceCompanyId"));
            workOrderBase.setDateReported(rs.getInstant("DateReported"));
            workOrderBase.setOrderedBy(rs.getString("OrderedBy"));
            workOrderBase.setDescription(rs.getString("Description"));
            workOrderBase.setDateScheduled(rs.getInstant("DateScheduled"));
            workOrderBase.setDateCompleted(rs.getInstant("DateCompleted"));
            workOrderBase.setActionTaken(rs.getString("ActionTaken"));
            workOrderBase.setAccountId(rs.getInt("AccountId"));
            workOrderBase.setAdditionalOrderNumber(rs.getString("AdditionalOrderNumber"));
            
            return workOrderBase;
        }

    }
    
    // DI Setters
    @Autowired
    public void setEventBaseDao(EventBaseDao eventBaseDao) {
        this.eventBaseDao = eventBaseDao;
    }
    
    @Autowired
    public void setEventWorkOrderDao(EventWorkOrderDao eventWorkOrderDao) {
        this.eventWorkOrderDao = eventWorkOrderDao;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}