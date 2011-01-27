package com.cannontech.web.stars.dr.operator.warehouse.service;

import java.util.List;

import com.cannontech.web.stars.dr.operator.warehouse.model.WarehouseDto;

public interface WarehouseService {

    public List<WarehouseDto> getWarehousesForEnergyCompany(int energyCompanyId);
    
    public WarehouseDto getWarehouse(int warehouseId);
    
    public int createWarehouse(WarehouseDto warehouseDto);
    
    public boolean updateWarehouse(WarehouseDto warehouseDto);
    
    public boolean deleteWarehouse(WarehouseDto warehouseDto);
    
    public boolean deleteWarehouse(int warehouseId);
}
