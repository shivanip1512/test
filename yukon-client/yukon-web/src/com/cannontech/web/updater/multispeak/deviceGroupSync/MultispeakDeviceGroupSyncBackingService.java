package com.cannontech.web.updater.multispeak.deviceGroupSync;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncService;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessorType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.multispeak.deviceGroupSync.handler.MultispeakDeviceGroupSyncUpdaterHandler;

public class MultispeakDeviceGroupSyncBackingService implements UpdateBackingService, InitializingBean {
    
    private Map<MultispeakDeviceGroupSyncUpdaterTypeEnum, MultispeakDeviceGroupSyncUpdaterHandler> handlersMap;
    private ObjectFormattingService objectFormattingService;
    private MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncService;
    private MeterDao meterDao;
    
    @Override
    public String getLatestValue(String updaterTypeStr, long afterDate, YukonUserContext userContext) {

        MultispeakDeviceGroupSyncProgress progress = multispeakDeviceGroupSyncService.getProgress();
        
        // should only occur during an updater last gasp just after a done-action, this basically just avoids seeing a null exception error in the log
        if (progress == null) {
        	return "";
        }
        
        MultispeakDeviceGroupSyncUpdaterTypeEnum updaterType = MultispeakDeviceGroupSyncUpdaterTypeEnum.valueOf(updaterTypeStr);
        MultispeakDeviceGroupSyncUpdaterHandler handler = handlersMap.get(updaterType);
        
        Object object = handler.handle(progress, userContext);
        return objectFormattingService.formatObjectAsString(object, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        
        handlersMap = new HashMap<MultispeakDeviceGroupSyncUpdaterTypeEnum, MultispeakDeviceGroupSyncUpdaterHandler>();
        
        // METERS_PROCESSED_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.METERS_PROCESSED_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {

        		int meterCount = meterDao.getMeterCount();
        		int processedCount = progress.getMetersProcessedCount();
        		
        		if (progress.isFinished()) {
        			
        			return meterCount;
        			
        		} else {
        			
        			if (processedCount < meterCount) {
        				
        				return progress.getMetersProcessedCount();
        				
        			} else {
        				
        				// processed count has reached the max number of meters yukon knows about, the cis vendor may have more
        				// progress is not yet complete, "hang" the count at 99%
        				return Math.floor(meterCount * 0.99);
        			}
        		}
        	}
        });
        
        // SUBSTATION_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.SUBSTATION_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress.getChangeCount(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION);
        	}
        });
        
        // SUBSTATION_NO_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.SUBSTATION_NO_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress.getNoChangeCount(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION);
        	}
        });
        
        // BILLING_CYCLE_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.BILLING_CYCLE_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress.getChangeCount(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE);
        	}
        });
        
        // BILLING_CYCLE_NO_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.BILLING_CYCLE_NO_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress.getNoChangeCount(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE);
        	}
        });
        
        // IS_RUNNING
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.IS_RUNNING, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress.isRunning();
        	}
        });
        
        // IS_NOT_RUNNING
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.IS_NOT_RUNNING, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return !progress.isRunning();
        	}
        });
        
        // IS_ABORTED
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.IS_ABORTED, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress.isHasException() || progress.isCanceled();
        	}
        });
        
        // STATUS_TEXT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.STATUS_TEXT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress.getStatus();
        	}
        });
        
        // STATUS_CLASS
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.STATUS_CLASS, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return new YukonMessageSourceResolvable(progress.getStatus().getFormatKey() + ".statusClass");
        	}
        });
    }
    
    @Autowired
    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
    	this.objectFormattingService = objectFormattingService;
    }

    @Autowired
    public void setMultispeakDeviceGroupSyncService(MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncService) {
		this.multispeakDeviceGroupSyncService = multispeakDeviceGroupSyncService;
	}
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
}
