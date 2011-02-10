package com.cannontech.web.stars.dr.operator.warehouse.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.AddressDao;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.web.stars.dr.operator.warehouse.model.WarehouseDto;
import com.cannontech.web.stars.dr.operator.warehouse.service.WarehouseService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class WarehouseServiceImpl implements WarehouseService {
    
    private WarehouseDao warehouseDao;
    private AddressDao addressDao;
    
    private Function<Warehouse, WarehouseDto> warehouseToDtoFunction = new Function<Warehouse, WarehouseDto>() {
        public WarehouseDto apply(Warehouse from) {
            return new WarehouseDto(from, addressDao.getByAddressId(from.getAddressID()));
        }
    };

    @Override
    public List<WarehouseDto> getWarehousesForEnergyCompany(int energyCompanyId) {
        List<Warehouse> warehouses = warehouseDao.getAllWarehousesForEnergyCompanyId(energyCompanyId);
        return Lists.transform(warehouses, warehouseToDtoFunction);
    }

    @Override
    public WarehouseDto getWarehouse(int warehouseId) {
        return warehouseToDtoFunction.apply(warehouseDao.getWarehouse(warehouseId));
    }

    @Override
    public void createWarehouse(WarehouseDto warehouseDto) {
        addressDao.add(warehouseDto.getAddress());
        warehouseDto.getWarehouse().setAddressID(warehouseDto.getAddress().getAddressID());
        warehouseDao.create(warehouseDto.getWarehouse());
    }

    @Override
    public void updateWarehouse(WarehouseDto warehouseDto) {
        //update warehouse & address
        warehouseDao.update(warehouseDto.getWarehouse());
        addressDao.update(warehouseDto.getAddress());
    }

    @Override
    public void deleteWarehouse(WarehouseDto warehouseDto) {
        warehouseDao.delete(warehouseDto.getWarehouse());
        addressDao.remove(warehouseDto.getAddress());
    }

    @Override
    public void deleteWarehouse(int warehouseId) {
        WarehouseDto warehouseDto = getWarehouse(warehouseId);
        warehouseDao.delete(warehouseDto.getWarehouse());
        addressDao.remove(warehouseDto.getAddress());
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
