package com.cannontech.web.updater.deviceReconfig.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceReconfig.DeviceReconfigMonitorUpdaterType;

public class NewOperationForFailedUpdaterHandler implements DeviceReconfigUpdaterHandler {

    private InventoryConfigTaskDao inventoryConfigTaskDao;

    @Override
    public String handle(int taskId, YukonUserContext userContext) {

        InventoryConfigTask inventoryConfigTask = inventoryConfigTaskDao.getById(taskId);
        int itemsProcessed = inventoryConfigTask.getNumberOfItemsProcessed();
        
        if (itemsProcessed == inventoryConfigTask.getNumberOfItems()) {
            if (inventoryConfigTaskDao.getFailedCount(taskId) > 0) {
                return "visible";
            }
            return "hidden";
        }
        
        return "hidden";
    }

    @Override
    public DeviceReconfigMonitorUpdaterType getUpdaterType() {
        return DeviceReconfigMonitorUpdaterType.NEW_OPERATION_FOR_FAILED;
    }
    
    @Autowired
    public void setInventoryConfigTaskDao(InventoryConfigTaskDao inventoryConfigTaskDao) {
        this.inventoryConfigTaskDao = inventoryConfigTaskDao;
    }
    
}