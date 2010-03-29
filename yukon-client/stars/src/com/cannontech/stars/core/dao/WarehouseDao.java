package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.database.db.stars.hardware.Warehouse;

public interface WarehouseDao {

    /**
     * Retrieves a list of Warehouses for the given energyCompany id.
     * @param energyCompanyId
     * @return List<Warehouse>
     */
    public List<Warehouse> getAllWarehousesForEnergyCompanyId(int energyCompanyId);

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

}
