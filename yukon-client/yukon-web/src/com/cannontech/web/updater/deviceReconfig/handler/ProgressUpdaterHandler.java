package com.cannontech.web.updater.deviceReconfig.handler;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceReconfig.DeviceReconfigMonitorUpdaterType;

public class ProgressUpdaterHandler implements DeviceReconfigUpdaterHandler {

    private InventoryConfigTaskDao inventoryConfigTaskDao;

    @Override
    public String handle(int taskId, YukonUserContext userContext) {

        DecimalFormat format = new DecimalFormat("##0.#%");
            
        InventoryConfigTask inventoryConfigTask = inventoryConfigTaskDao.getById(taskId);
        
        int itemsProcessed = inventoryConfigTask.getNumberOfItemsProcessed();
        double percentComplete = ((double)itemsProcessed / (double) inventoryConfigTask.getNumberOfItems());
        
        if (percentComplete == 1.0) return "";
        
        return format.format(percentComplete);
    }

    @Override
    public DeviceReconfigMonitorUpdaterType getUpdaterType() {
        return DeviceReconfigMonitorUpdaterType.PROGRESS;
    }
    
    @Autowired
    public void setInventoryConfigTaskDao(InventoryConfigTaskDao inventoryConfigTaskDao) {
        this.inventoryConfigTaskDao = inventoryConfigTaskDao;
    }
    
}