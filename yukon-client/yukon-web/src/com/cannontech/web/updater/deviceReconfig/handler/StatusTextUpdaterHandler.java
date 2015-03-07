package com.cannontech.web.updater.deviceReconfig.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceReconfig.DeviceReconfigMonitorUpdaterType;

public class StatusTextUpdaterHandler implements DeviceReconfigUpdaterHandler {

    private InventoryConfigTaskDao inventoryConfigTaskDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String handle(int taskId, YukonUserContext userContext) {

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        InventoryConfigTask inventoryConfigTask = inventoryConfigTaskDao.getById(taskId);
        int itemsProcessed = inventoryConfigTask.getNumberOfItemsProcessed();
        
        if( itemsProcessed == inventoryConfigTask.getNumberOfItems()) {
            return messageSourceAccessor.getMessage("yukon.web.modules.operator.inventory.config.schedule.status.complete");
        } else {
            return messageSourceAccessor.getMessage("yukon.web.modules.operator.inventory.config.schedule.status.inProgress");
        }
        
    }

    @Override
    public DeviceReconfigMonitorUpdaterType getUpdaterType() {
        return DeviceReconfigMonitorUpdaterType.STATUS_TEXT;
    }
    
    @Autowired
    public void setInventoryConfigTaskDao(InventoryConfigTaskDao inventoryConfigTaskDao) {
        this.inventoryConfigTaskDao = inventoryConfigTaskDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}