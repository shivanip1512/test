package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.stars.database.db.hardware.Warehouse;

public interface WarehouseDao {

    /**
     * Retrieves a list of Warehouses for the given energyCompany id.
     * @param energyCompanyId
     * @return List<Warehouse>
     */
    public List<Warehouse> getAllWarehousesForEnergyCompanyId(int energyCompanyId);
    
    /**
     * Retrieves a Warehouse for a given id.
     * @param warehouseId
     * @return
     */
    public Warehouse getWarehouse(int warehouseId);

    /**
     * Removes an inventory item from it's warehouse.
     * @param inventoryId
     */
    public void removeFromWarehouse(int inventoryId);
    
    /**
     * Removes an inventory item from it's current warehouse and if 
     * newWarehouseId is greater than 0, assigns it to the new warehouse.
     * @param inventoryId
     * @param newWarehouseId
     */
    public void moveInventoryToAnotherWarehouse(int inventoryId, int newWarehouseId);

    /** 
     * Retreives the warehouse for the given inventoryId or null if 
     * the inventory is not in a warehouse.
     * @param inventoryId
     * @return Warehouse
     */
    public Warehouse findWarehouseForInventoryId(int inventoryId);
    
    /**
     * Create a new Warehouse Object
     * @param   warehouse   Warehouse   Contains the new warehouse parameters
     */
    public void create(Warehouse warehouse);

    /**
     * Update an existing Warehouse with the parameters supplied by warehouse
     * @param   warehouse   Warehouse  Contains the updated warehouse parameters
     */
    public void update(Warehouse warehouse);
    
    /**
     * 
     * @param warehouse
     */
    public void delete(Warehouse warehouse);
    
    public void delete(int warehouseId);
}
