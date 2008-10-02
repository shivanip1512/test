package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;

public class StarsWorkOrderBaseDaoImpl implements StarsWorkOrderBaseDao {
    private static final String selectSql;
    private static final ParameterizedRowMapper<LiteWorkOrderBase> rowMapper = createRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;

    static {

        selectSql = "SELECT OrderID,OrderNumber,WorkTypeID,CurrentStateID,ServiceCompanyID," +
                    "   DateReported,OrderedBy,Description,DateScheduled,DateCompleted," +
                    "   ActionTaken,AdditionalOrderNumber,EnergyCompanyID " +
                    "FROM WorkOrderBase wb " +
                    "JOIN ECToWorkOrderMapping ectwm ON ectwm.WorkOrderID = wb.OrderID";
        
    }
    
    @Override
    @Transactional(readOnly = true)
    public LiteWorkOrderBase getById(int workOrderId) {
        String sql = selectSql + " WHERE OrderID = ?";
        
        LiteWorkOrderBase workOrder = simpleJdbcTemplate.queryForObject(sql,
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
        
        List<LiteWorkOrderBase> list = simpleJdbcTemplate.query(sql, rowMapper);
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
        
        List<LiteWorkOrderBase> list = simpleJdbcTemplate.query(sql,
                                                                rowMapper,
                                                                energyCompanyId);
        return list;
    }

    private static ParameterizedRowMapper<LiteWorkOrderBase> createRowMapper() {
        return new ParameterizedRowMapper<LiteWorkOrderBase>() {
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
                workOrder.setDateScheduled(rs.getTimestamp("DateScheduled").getTime());
                workOrder.setDateCompleted(rs.getTimestamp("DateCompleted").getTime());
                workOrder.setActionTaken(rs.getString("ActionTaken"));
                workOrder.setAdditionalOrderNumber(rs.getString("AdditionalOrderNumber"));
                workOrder.setEnergyCompanyID(rs.getInt("EnergyCompanyID"));
                return workOrder;
            }
        };
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
