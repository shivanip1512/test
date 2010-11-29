package com.cannontech.web.updater.deviceReconfig.handler;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceReconfig.DeviceReconfigMonitorUpdaterType;

public class DeviceReconfigProgressUpdaterHandler implements DeviceReconfigUpdaterHandler {

    private InventoryConfigTaskDao inventoryConfigTaskDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String handle(int taskId, YukonUserContext userContext) {

        DecimalFormat format = new DecimalFormat("#0%");
            
        InventoryConfigTask inventoryConfigTask = inventoryConfigTaskDao.getById(taskId);
        int itemsProcessed = inventoryConfigTask.getNumberOfItemsProcessed();
        
        if( itemsProcessed == inventoryConfigTask.getNumberOfItems()) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String complete = messageSourceAccessor.getMessage("yukon.web.widgets.deviceReconfigMonitorsWidget.complete");
            return complete;
        }
            
        double percentComplete = (new Double(itemsProcessed).doubleValue() / new Double(inventoryConfigTask.getNumberOfItems()).doubleValue()) * 100;
        
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
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}