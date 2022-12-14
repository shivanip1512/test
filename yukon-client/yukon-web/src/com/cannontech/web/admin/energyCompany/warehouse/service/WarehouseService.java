package com.cannontech.web.admin.energyCompany.warehouse.service;

import java.util.List;

import com.cannontech.web.admin.energyCompany.warehouse.model.WarehouseDto;

public interface WarehouseService {

    public List<WarehouseDto> getWarehousesForEnergyCompany(int energyCompanyId);

    public WarehouseDto getWarehouse(int warehouseId);

    public void createWarehouse(WarehouseDto warehouseDto);

    public void updateWarehouse(WarehouseDto warehouseDto);

    public void deleteWarehouse(WarehouseDto warehouseDto);
}
