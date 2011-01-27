package com.cannontech.web.stars.dr.operator.warehouse.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.AddressDao;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.web.stars.dr.operator.warehouse.model.WarehouseDto;
import com.cannontech.web.stars.dr.operator.warehouse.service.WarehouseService;

public class WarehouseServiceImpl implements WarehouseService {
    
    private WarehouseDao    warehouseDao;
    private AddressDao      addressDao;

    @Override
    public List<WarehouseDto> getWarehousesForEnergyCompany(int energyCompanyId) {
        List<WarehouseDto> warehouseDtos = new ArrayList<WarehouseDto>();
        List<Warehouse> warehouses = warehouseDao.getAllWarehousesForEnergyCompanyId(energyCompanyId);
        
        for(Warehouse warehouse : warehouses) {
            warehouseDtos.add(new WarehouseDto(warehouse, addressDao.getByAddressId(warehouse.getAddressID())));
        }
        return warehouseDtos;
    }

    @Override
    public WarehouseDto getWarehouse(int warehouseId) {
        Warehouse warehouse = warehouseDao.getWarehouse(warehouseId);
        return new WarehouseDto(warehouse, addressDao.getByAddressId(warehouse.getAddressID()));
    }

    @Override
    public int createWarehouse(WarehouseDto warehouseDto) {
        addressDao.add(warehouseDto.getAddress());
        warehouseDto.getWarehouse().setAddressID(warehouseDto.getAddress().getAddressID());
        return warehouseDao.create(warehouseDto.getWarehouse());
    }

    @Override
    public boolean updateWarehouse(WarehouseDto warehouseDto) {
        //update warehouse & address
        return warehouseDao.update(warehouseDto.getWarehouse()) && addressDao.update(warehouseDto.getAddress());
    }

    @Override
    public boolean deleteWarehouse(WarehouseDto warehouseDto) {
        return warehouseDao.delete(warehouseDto.getWarehouse())  && addressDao.remove(warehouseDto.getAddress());
    }

    @Override
    public boolean deleteWarehouse(int warehouseId) {
        WarehouseDto warehouseDto = getWarehouse(warehouseId);
        return warehouseDao.delete(warehouseDto.getWarehouse()) && addressDao.remove(warehouseDto.getAddress());
    }

    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    @Autowired
    public void setWarehouseDao(WarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }
}
