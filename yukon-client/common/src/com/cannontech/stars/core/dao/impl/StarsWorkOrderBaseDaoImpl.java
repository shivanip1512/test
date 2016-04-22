package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.dr.event.dao.EventWorkOrderDao;

public class StarsWorkOrderBaseDaoImpl implements StarsWorkOrderBaseDao {
    private static final String selectSql;
    private static final RowMapper<LiteWorkOrderBase> rowMapper = createRowMapper();
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EventWorkOrderDao eventWorkOrderDao;
    private ChunkingSqlTemplate chunkyJdbcTemplate;

    static {

        selectSql = "SELECT OrderID,OrderNumber,WorkTypeID,CurrentStateID,ServiceCompanyID," +
                    "   DateReported,OrderedBy,Description,DateScheduled,DateCompleted," +
                    "   ActionTaken,AdditionalOrderNumber,EnergyCompanyID, AccountId " +
                    "FROM WorkOrderBase wb " +
                    "JOIN ECToWorkOrderMapping ectwm ON ectwm.WorkOrderID = wb.OrderID";
        
    }
    
    @Override
    @Transactional(readOnly = true)
    public LiteWorkOrderBase getById(int workOrderId) {
        String sql = selectSql + " WHERE OrderID = ?";
        
        LiteWorkOrderBase workOrder = jdbcTemplate.queryForObject(sql,
                                                                        rowMapper,
                                                                        workOrderId);
        return workOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiteWorkOrderBase> getByIds(Collection<Integer> ids) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append(selectSql);
        sqlBuilder.append(" WHERE OrderID IN (");
        sqlBuilder.append(ids);
        sqlBuilder.append(")");
        String sql = sqlBuilder.toString();
        
        List<LiteWorkOrderBase> list = jdbcTemplate.query(sql, rowMapper);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Integer, LiteWorkOrderBase> getByIdsMap(Collection<Integer> ids) {
        List<LiteWorkOrderBase> list = getByIds(ids);
        
        Map<Integer,LiteWorkOrderBase> map = new HashMap<Integer, LiteWorkOrderBase>(list.size());
        
        for (final LiteWorkOrderBase value : list) {
            Integer key = value.getOrderID();
            map.put(key, value);
        }
        
        return map;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteWorkOrderBase> getAll(int energyCompanyId) {
        String sql = selectSql + " WHERE ectwm.EnergyCompanyID = ?";
        
        List<LiteWorkOrderBase> list = jdbcTemplate.query(sql,
                                                                rowMapper,
                                                                energyCompanyId);
        return list;
    }

    private static RowMapper<LiteWorkOrderBase> createRowMapper() {
        return new RowMapper<LiteWorkOrderBase>() {
            @Override
            public LiteWorkOrderBase mapRow(ResultSet rs, int rowNum) throws SQLException {
                LiteWorkOrderBase workOrder = new LiteWorkOrderBase();
                workOrder.setOrderID(rs.getInt("OrderID"));
                workOrder.setOrderNumber(rs.getString("OrderNumber"));
                workOrder.setWorkTypeID(rs.getInt("WorkTypeID"));
                workOrder.setCurrentStateID(rs.getInt("CurrentStateID"));
                workOrder.setServiceCompanyID(rs.getInt("ServiceCompanyID"));
                workOrder.setDateReported(rs.getTimestamp("DateReported").getTime());
                workOrder.setOrderedBy(rs.getString("OrderedBy"));
                workOrder.setDescription(rs.getString("Description"));
                if (rs.getTimestamp("DateScheduled") != null)
                    workOrder.setDateScheduled(rs.getTimestamp("DateScheduled").getTime());
                if (rs.getTimestamp("DateCompleted") != null)
                    workOrder.setDateCompleted(rs.getTimestamp("DateCompleted").getTime());
                workOrder.setActionTaken(rs.getString("ActionTaken"));
                workOrder.setAdditionalOrderNumber(rs.getString("AdditionalOrderNumber"));
                workOrder.setEnergyCompanyID(rs.getInt("EnergyCompanyID"));
                workOrder.setAccountID(rs.getInt("AccountId"));
                return workOrder;
            }
        };
    }
    
    @Override
    @Transactional
    public void deleteByAccount(int accountId) {
        List<Integer> workOrderIds = getByAccount(accountId);
        ecMappingDao.deleteECToWorkOrderMapping(workOrderIds);
        eventWorkOrderDao.deleteEventWorkOrders(workOrderIds);
        if(!workOrderIds.isEmpty()) {
            chunkyJdbcTemplate.update(new WorkOrderSqlGenerator(), workOrderIds);
        }
    }
    
    /**
     * Sql generator for deleting work orders, useful for bulk deleteing
     * with chunking sql template.
     */
    private class WorkOrderSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> workOrderIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM WorkOrderBase WHERE OrderId IN (", workOrderIds, ")");
            return sql.toString();
        }
    }
    
    @Override
    public List<Integer> getByAccount(int accountId){
        String sql = "SELECT OrderId FROM WorkOrderBase WHERE AccountId = ?";
        List<Integer> workOrderIds = jdbcTemplate.query(sql, new IntegerRowMapper(), accountId);
        return workOrderIds;
    }
    
    @PostConstruct
    public void init() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(jdbcTemplate);
    }
}
