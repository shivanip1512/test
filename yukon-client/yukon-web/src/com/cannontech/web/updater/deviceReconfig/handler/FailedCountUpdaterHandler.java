package com.cannontech.web.updater.deviceReconfig.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem.Status;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceReconfig.DeviceReconfigMonitorUpdaterType;

public class FailedCountUpdaterHandler implements DeviceReconfigUpdaterHandler {

    private InventoryConfigTaskDao inventoryConfigTaskDao;

    @Override
    public String handle(int taskId, YukonUserContext userContext) {
        return Integer.toString(inventoryConfigTaskDao.getTaskItemCount(taskId, Status.FAIL));
    }

    @Override
    public DeviceReconfigMonitorUpdaterType getUpdaterType() {
        return DeviceReconfigMonitorUpdaterType.FAILED_COUNT;
    }
    
    @Autowired
    public void setInventoryConfigTaskDao(InventoryConfigTaskDao inventoryConfigTaskDao) {
        this.inventoryConfigTaskDao = inventoryConfigTaskDao;
    }
    
}