package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.core.dao.WarehouseDao;

public class WarehouseDaoImpl implements WarehouseDao{
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    public ParameterizedRowMapper<Warehouse> warehouseRowMapper = new ParameterizedRowMapper<Warehouse>() {
        public Warehouse mapRow(ResultSet rs, int rowNum) throws SQLException {
            Warehouse warehouse = new Warehouse();
            warehouse.setEnergyCompanyID(rs.getInt("EnergyCompanyId"));
            warehouse.setWarehouseID(rs.getInt("WarehouseId"));
            warehouse.setWarehouseName(SqlUtils.convertDbValueToString(rs.getString("WarehouseName")));
            warehouse.setAddressID(rs.getInt("AddressId"));
            warehouse.setNotes(SqlUtils.convertDbValueToString(rs.getString("Notes")));
            return warehouse;
        }
    };

    @Override
    public List<Warehouse> getAllWarehousesForEnergyCompanyId (int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM Warehouse WHERE ENERGYCOMPANYID ").eq(energyCompanyId);
        
        List<Warehouse> warehouses = yukonJdbcTemplate.query(sql, warehouseRowMapper);
        
        return warehouses;
    }
    
    @Override
    public void moveInventoryToAnotherWarehouse(int inventoryId, int newWarehouseId) {
        /* Remove it from it's current warehouse */
        removeFromWarehouse(inventoryId);
        
        if(newWarehouseId > 0){ /* Greater than zero means assign it to a warehouse. */
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("insert into InventoryToWarehouseMapping");
            sql.append(" values (").appendArgument(newWarehouseId).append(", ").appendArgument(inventoryId).append(")");
            yukonJdbcTemplate.update(sql);
        }
    }
    
    @Override
    public void removeFromWarehouse(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from InventoryToWarehouseMapping where inventoryId ").eq(inventoryId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public Warehouse findWarehouseForInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select wh.* from Warehouse wh");
        sql.append("join InventoryToWarehouseMapping inv on wh.WarehouseId = inv.WarehouseId");
        sql.append("where inv.InventoryId ").eq(inventoryId);
        try {
            return yukonJdbcTemplate.queryForObject(sql, warehouseRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}