package com.cannontech.stars.core.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.database.db.hardware.Warehouse;

public class WarehouseDaoImpl implements WarehouseDao, InitializingBean {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    private SimpleTableAccessTemplate<Warehouse> warehouseTemplate;
    
    private FieldMapper<Warehouse> warehouseFieldMapper = new FieldMapper<Warehouse>() {
        @Override
        public void extractValues(MapSqlParameterSource p, Warehouse warehouse) {
            p.addValue("AddressID", warehouse.getAddressID());
            p.addValue("EnergyCompanyID", warehouse.getEnergyCompanyID());
            p.addValue("WarehouseName", warehouse.getWarehouseName());
            p.addValue("Notes", warehouse.getNotes());
        }
        
        @Override
        public Number getPrimaryKey(Warehouse warehouse) {
            return warehouse.getWarehouseID();
        }
        
        @Override
        public void setPrimaryKey(Warehouse warehouse, int newID) {
            warehouse.setWarehouseID(newID);
        }
    };
    
    //Row Mappers
    private static class WarehouseRowMapper implements YukonRowMapper<Warehouse> {

        @Override
        public Warehouse mapRow(YukonResultSet rs) throws SQLException {
            Warehouse warehouse = new Warehouse();
            
            warehouse.setWarehouseID(rs.getInt("WarehouseID"));
            warehouse.setAddressID(rs.getInt("AddressID"));
            warehouse.setEnergyCompanyID(rs.getInt("EnergyCompanyID"));
            warehouse.setWarehouseName(rs.getStringSafe("WarehouseName"));
            warehouse.setNotes(rs.getStringSafe("Notes"));
            
            return warehouse;
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        warehouseTemplate = new SimpleTableAccessTemplate<Warehouse>(yukonJdbcTemplate, nextValueHelper);
        warehouseTemplate.setTableName("Warehouse");
        warehouseTemplate.setPrimaryKeyField("WarehouseID");
        warehouseTemplate.setFieldMapper(warehouseFieldMapper);
        warehouseTemplate.setPrimaryKeyValidOver(0);
    }

    @Override
    public void create(final Warehouse warehouse) {
        warehouseTemplate.save(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
        warehouseTemplate.save(warehouse);
    }

    @Override
    public void delete(Warehouse warehouse) {
        delete(warehouse.getWarehouseID());
    }

    @Override
    public void delete(int warehouseId) {
        // Remove warehouse
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Warehouse");
        sql.append("WHERE WarehouseID").eq(warehouseId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public List<Warehouse> getAllWarehousesForEnergyCompanyId (int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM Warehouse");
        sql.append("WHERE ENERGYCOMPANYID").eq(energyCompanyId);
        
        return yukonJdbcTemplate.query(sql, new WarehouseRowMapper());
    }
    
    @Override
    public Warehouse getWarehouse(int warehouseId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM Warehouse");
        sql.append("WHERE WAREHOUSEID").eq(warehouseId);
        return yukonJdbcTemplate.queryForObject(sql, new WarehouseRowMapper());
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
            return yukonJdbcTemplate.queryForObject(sql, new WarehouseRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    //DI
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}