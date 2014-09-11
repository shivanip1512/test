package com.cannontech.web.admin.energyCompany.warehouse.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.web.admin.energyCompany.warehouse.model.WarehouseDto;
import com.cannontech.web.admin.energyCompany.warehouse.service.WarehouseService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class WarehouseServiceImpl implements WarehouseService {

    @Autowired private AddressDao addressDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private StarsEventLogService starsEventLogService;

    private final Function<Warehouse, WarehouseDto> warehouseToDtoFunction = new Function<Warehouse, WarehouseDto>() {
        @Override
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
    @Transactional
    public void createWarehouse(WarehouseDto warehouseDto) {
        addressDao.add(warehouseDto.getAddress());
        warehouseDto.getWarehouse().setAddressID(warehouseDto.getAddress().getAddressID());
        warehouseDao.create(warehouseDto.getWarehouse());

        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.WAREHOUSE,
            warehouseDto.getWarehouse().getWarehouseID());

        starsEventLogService.addWarehouse(warehouseDto.getWarehouse().getWarehouseName());
    }

    @Override
    @Transactional
    public void updateWarehouse(WarehouseDto warehouseDto) {
        // update warehouse & address
        warehouseDao.update(warehouseDto.getWarehouse());
        addressDao.update(warehouseDto.getAddress());

        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.WAREHOUSE,
            warehouseDto.getWarehouse().getWarehouseID());

        starsEventLogService.updateWarehouse(warehouseDto.getWarehouse().getWarehouseName());
    }

    @Override
    @Transactional
    public void deleteWarehouse(WarehouseDto warehouseDto) {
        warehouseDao.delete(warehouseDto.getWarehouse());
        addressDao.remove(warehouseDto.getAddress());

        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.WAREHOUSE,
            warehouseDto.getWarehouse().getWarehouseID());

        starsEventLogService.deleteWarehouse(warehouseDto.getWarehouse().getWarehouseName());
    }
}