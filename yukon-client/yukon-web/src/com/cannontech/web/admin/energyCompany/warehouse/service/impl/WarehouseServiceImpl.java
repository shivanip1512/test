package com.cannontech.web.admin.energyCompany.warehouse.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.web.admin.energyCompany.warehouse.model.WarehouseDto;
import com.cannontech.web.admin.energyCompany.warehouse.service.WarehouseService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class WarehouseServiceImpl implements WarehouseService {
    
    private AddressDao addressDao;
    private DBPersistentDao dbPersistentDao;
    private WarehouseDao warehouseDao;
    private StarsEventLogService starsEventLogService;
    
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
    @Transactional
    public void createWarehouse(WarehouseDto warehouseDto) {
        addressDao.add(warehouseDto.getAddress());
        warehouseDto.getWarehouse().setAddressID(warehouseDto.getAddress().getAddressID());
        warehouseDao.create(warehouseDto.getWarehouse());

        dbPersistentDao.processDatabaseChange(DbChangeType.ADD, DbChangeCategory.WAREHOUSE, warehouseDto.getWarehouse().getWarehouseID());
        
        starsEventLogService.addWarehouse(warehouseDto.getWarehouse().getWarehouseName());
    }

    @Override
    @Transactional
    public void updateWarehouse(WarehouseDto warehouseDto) {
        //update warehouse & address
        warehouseDao.update(warehouseDto.getWarehouse());
        addressDao.update(warehouseDto.getAddress());

        dbPersistentDao.processDatabaseChange(DbChangeType.UPDATE, DbChangeCategory.WAREHOUSE, warehouseDto.getWarehouse().getWarehouseID());
        
        starsEventLogService.updateWarehouse(warehouseDto.getWarehouse().getWarehouseName());
    }

    @Override
    @Transactional
    public void deleteWarehouse(WarehouseDto warehouseDto) {
        warehouseDao.delete(warehouseDto.getWarehouse());
        addressDao.remove(warehouseDto.getAddress());

        dbPersistentDao.processDatabaseChange(DbChangeType.DELETE, DbChangeCategory.WAREHOUSE, warehouseDto.getWarehouse().getWarehouseID());
        
        starsEventLogService.deleteWarehouse(warehouseDto.getWarehouse().getWarehouseName());
    }

    @Override
    @Transactional
    public void deleteWarehouse(int warehouseId) {
        WarehouseDto warehouseDto = getWarehouse(warehouseId);
        deleteWarehouse(warehouseDto);
    }
    
    // DI Setters
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Autowired
    public void setStarsEventLogService(StarsEventLogService starsEventLogService) {
        this.starsEventLogService = starsEventLogService;
    }

    @Autowired
    public void setWarehouseDao(WarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }
}