package com.cannontech.web.updater.deviceReconfig.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceReconfig.DeviceReconfigMonitorUpdaterType;

public class ItemsProcessedUpdaterHandler implements DeviceReconfigUpdaterHandler {

    private InventoryConfigTaskDao inventoryConfigTaskDao;

    @Override
    public String handle(int taskId, YukonUserContext userContext) {
        InventoryConfigTask task = inventoryConfigTaskDao.getById(taskId);
        return Integer.toString(task.getNumberOfItemsProcessed());
    }

    @Override
    public DeviceReconfigMonitorUpdaterType getUpdaterType() {
        return DeviceReconfigMonitorUpdaterType.ITEMS_PROCESSED;
    }
    
    @Autowired
    public void setInventoryConfigTaskDao(InventoryConfigTaskDao inventoryConfigTaskDao) {
        this.inventoryConfigTaskDao = inventoryConfigTaskDao;
    }
    
}