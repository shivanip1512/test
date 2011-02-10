package com.cannontech.web.stars.dr.operator.warehouse.service;

import java.util.List;

import com.cannontech.web.stars.dr.operator.warehouse.model.WarehouseDto;

public interface WarehouseService {

    public List<WarehouseDto> getWarehousesForEnergyCompany(int energyCompanyId);
    
    public WarehouseDto getWarehouse(int warehouseId);
    
    public void createWarehouse(WarehouseDto warehouseDto);
    
    public void updateWarehouse(WarehouseDto warehouseDto);
    
    public void deleteWarehouse(WarehouseDto warehouseDto);
    
    public void deleteWarehouse(int warehouseId);
}
