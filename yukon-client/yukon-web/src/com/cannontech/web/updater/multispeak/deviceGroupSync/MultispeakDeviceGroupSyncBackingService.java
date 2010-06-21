package com.cannontech.web.updater.multispeak.deviceGroupSync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.multispeak.deviceGroupSync.handler.MultispeakDeviceGroupSyncUpdaterHandler;

public class MultispeakDeviceGroupSyncBackingService implements UpdateBackingService, InitializingBean {
    
    private List<MultispeakDeviceGroupSyncUpdaterHandler> handlers;
    private Map<MultispeakDeviceGroupSyncUpdaterTypeEnum, MultispeakDeviceGroupSyncUpdaterHandler> handlersMap;
    private MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncService;
    
    @Override
    public String getLatestValue(String updaterTypeStr, long afterDate, YukonUserContext userContext) {

        MultispeakDeviceGroupSyncProgress progress = multispeakDeviceGroupSyncService.getProgress();
        
        // should only occur during an updater last gasp just after a done-action, this basically just avoids seeing a null exception error in the log
        if (progress == null) {
        	return "";
        }
        
        MultispeakDeviceGroupSyncUpdaterTypeEnum updaterType = MultispeakDeviceGroupSyncUpdaterTypeEnum.valueOf(updaterTypeStr);
        MultispeakDeviceGroupSyncUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(progress, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        
        this.handlersMap = new HashMap<MultispeakDeviceGroupSyncUpdaterTypeEnum, MultispeakDeviceGroupSyncUpdaterHandler>();
        for (MultispeakDeviceGroupSyncUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
    
    @Autowired
    public void setHandlers(List<MultispeakDeviceGroupSyncUpdaterHandler> handlers) {
        this.handlers = handlers;
    }
    
    @Autowired
    public void setMultispeakDeviceGroupSyncService(MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncService) {
		this.multispeakDeviceGroupSyncService = multispeakDeviceGroupSyncService;
	}
}
