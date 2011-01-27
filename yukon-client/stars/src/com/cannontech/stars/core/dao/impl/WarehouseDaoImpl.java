package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.WarehouseDao;

public class WarehouseDaoImpl implements WarehouseDao{
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static String TABLE_NAME = "Warehouse";

    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
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
    
    static {
        insertSql = "INSERT INTO " + TABLE_NAME + " (WarehouseID, EnergyCompanyID, AddressID, WarehouseName, Notes) " +
                    "VALUES (?,?,?,?,?)";
        removeSql = "DELETE FROM " + TABLE_NAME + " WHERE WarehouseID = ?";
        updateSql = "UPDATE " + TABLE_NAME + " SET WarehouseName = ?, Notes = ? WHERE WarehouseID = ?";
    }

    @Override
    public List<Warehouse> getAllWarehousesForEnergyCompanyId (int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM " + TABLE_NAME + " WHERE ENERGYCOMPANYID ").eq(energyCompanyId);
        
        List<Warehouse> warehouses = yukonJdbcTemplate.query(sql, warehouseRowMapper);
        
        return warehouses;
    }
    
    @Override
    public Warehouse getWarehouse(int warehouseId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM " + TABLE_NAME + " WHERE WAREHOUSEID ").eq(warehouseId);
        return yukonJdbcTemplate.queryForObject(sql, warehouseRowMapper);
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
        sql.append("select wh.* from " + TABLE_NAME + " wh");
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
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Override
    public int create(final Warehouse warehouse) {
        warehouse.setWarehouseID(nextValueHelper.getNextValue(TABLE_NAME));
        int rowsAffected = simpleJdbcTemplate.update(insertSql, warehouse.getWarehouseID(),
                                                     warehouse.getEnergyCompanyID(),
                                                     warehouse.getAddressID(),
                                                     SqlUtils.convertStringToDbValue(warehouse.getWarehouseName()),
                                                     SqlUtils.convertStringToDbValue(warehouse.getNotes()));
        if(rowsAffected == 1) {
            return warehouse.getWarehouseID();
        } else {
            return -1;
        }
    }

    @Override
    public boolean update(Warehouse warehouse) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, SqlUtils.convertStringToDbValue(warehouse.getWarehouseName()),
                                  SqlUtils.convertStringToDbValue(warehouse.getNotes()),
                                  warehouse.getWarehouseID());
        return (rowsAffected == 1);
    }

    @Override
    public boolean delete(Warehouse warehouse) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, warehouse.getWarehouseID());
        return (rowsAffected == 1);
    }
    
    @Override
    public boolean delete(int warehouseId) {
        return delete(getWarehouse(warehouseId));
    }
}