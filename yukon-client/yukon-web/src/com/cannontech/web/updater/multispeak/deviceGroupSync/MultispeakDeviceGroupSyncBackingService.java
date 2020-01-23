package com.cannontech.web.updater.multispeak.deviceGroupSync;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakSyncProgressStatus;
import com.cannontech.multispeak.service.MultispeakSyncTypeProcessorType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.multispeak.MspHandler;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.multispeak.MultispeakSyncBackingServiceBase;
import com.cannontech.web.updater.multispeak.deviceGroupSync.handler.MultispeakDeviceGroupSyncUpdaterHandler;
import com.google.common.collect.ImmutableMap;

public class MultispeakDeviceGroupSyncBackingService extends MultispeakSyncBackingServiceBase implements UpdateBackingService {
    
    private Map<MultispeakDeviceGroupSyncUpdaterTypeEnum, MultispeakDeviceGroupSyncUpdaterHandler> handlersMap;
    private ObjectFormattingService objectFormattingService;
    private MeterDao meterDao;
    
    private ImmutableMap<MultispeakSyncProgressStatus, String> statusStyleClassNameMap;
    @Autowired private MspHandler mspHandler;
    @Autowired MultispeakDao multispeakDao;
    @Autowired MultispeakFuncs multispeakFuncs;

    @Override
    public String getLatestValue(String updaterTypeStr, long afterDate, YukonUserContext userContext) {
        int vendorId = multispeakFuncs.getPrimaryCIS();
        MultispeakDeviceGroupSyncProgress progress = null;

        if (vendorId > 0) {
            progress = mspHandler.getDeviceGroupSyncService().getProgress();
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
    
    @PostConstruct
    public void secondInit() throws Exception {
        
        handlersMap = new HashMap<MultispeakDeviceGroupSyncUpdaterTypeEnum, MultispeakDeviceGroupSyncUpdaterHandler>();
        
        // METERS_PROCESSED_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.METERS_PROCESSED_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {

        		if (progress == null) {
        			return 0;
        		}
        		
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
        		return progress != null ? progress.getChangeCount(MultispeakSyncTypeProcessorType.SUBSTATION) : 0;
        	}
        });
        
        // SUBSTATION_NO_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.SUBSTATION_NO_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.getNoChangeCount(MultispeakSyncTypeProcessorType.SUBSTATION) : 0;
        	}
        });
        
        // BILLING_CYCLE_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.BILLING_CYCLE_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.getChangeCount(MultispeakSyncTypeProcessorType.BILLING_CYCLE) : 0;
        	}
        });
        
        // BILLING_CYCLE_NO_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.BILLING_CYCLE_NO_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.getNoChangeCount(MultispeakSyncTypeProcessorType.BILLING_CYCLE) : 0;
        	}
        });
        
        // IS_RUNNING
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.IS_RUNNING, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.isRunning() : false;
        	}
        });
        
        // IS_NOT_RUNNING
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.IS_NOT_RUNNING, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? !progress.isRunning() : true;
        	}
        });
        
        // IS_ABORTED
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.IS_ABORTED, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.isHasException() || progress.isCanceled() : false;
        	}
        });
        
        // STATUS_TEXT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.STATUS_TEXT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.getStatus() : null; // status is a DisplayableEnum
        	}
        });
        
        // STATUS_TEXT_OR_LAST_SYNC_SUBSTATION
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.STATUS_TEXT_OR_LAST_SYNC_SUBSTATION, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return getStatusTextObj(progress, MultispeakSyncTypeProcessorType.SUBSTATION, userContext);
        	}
        });

        // STATUS_TEXT_OR_LAST_SYNC_BILLING_CYCLE
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.STATUS_TEXT_OR_LAST_SYNC_BILLING_CYCLE, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return getStatusTextObj(progress, MultispeakSyncTypeProcessorType.BILLING_CYCLE, userContext);
        	}
        });
        
        // STATUS_CLASS
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.STATUS_CLASS, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? statusStyleClassNameMap.get(progress.getStatus()) : null;
        	}
        });
        
        // HAS_LINKABLE_PROGRESS_SUBSTATION
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.HAS_LINKABLE_PROGRESS_SUBSTATION, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		
        		if (progress != null && progress.getType().getProcessorTypes().contains(MultispeakSyncTypeProcessorType.SUBSTATION)) {
        			return true;
        		}
        		return false;
        	}
        });
        
        // NO_LINKABLE_PROGRESS_SUBSTATION
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.NO_LINKABLE_PROGRESS_SUBSTATION, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		
        		if (progress == null || !progress.getType().getProcessorTypes().contains(MultispeakSyncTypeProcessorType.SUBSTATION)) {
        			return true;
        		}
        		return false;
        	}
        });
        
        // HAS_LINKABLE_PROGRESS_BILLING_CYCLE
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.HAS_LINKABLE_PROGRESS_BILLING_CYCLE, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		
        		if (progress != null && progress.getType().getProcessorTypes().contains(MultispeakSyncTypeProcessorType.BILLING_CYCLE)) {
        			return true;
        		}
        		return false;
        	}
        });
        
        // NO_LINKABLE_PROGRESS_BILLING_CYCLE
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.NO_LINKABLE_PROGRESS_BILLING_CYCLE, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		
        		if (progress == null || !progress.getType().getProcessorTypes().contains(MultispeakSyncTypeProcessorType.BILLING_CYCLE)) {
        			return true;
        		}
        		return false;
        	}
        });
        
        // LAST_COMPLETED_SYNC_SUBSTATION
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.LAST_COMPLETED_SYNC_SUBSTATION, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return getLastCompletedSyncDateStr(MultispeakSyncTypeProcessorType.SUBSTATION, userContext);
        	}
        });

        // LAST_COMPLETED_SYNC_BILLING_CYCLE
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.LAST_COMPLETED_SYNC_BILLING_CYCLE, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return getLastCompletedSyncDateStr(MultispeakSyncTypeProcessorType.BILLING_CYCLE, userContext);
        	}
        });
    }
    
    private Object getStatusTextObj(MultispeakDeviceGroupSyncProgress progress, MultispeakSyncTypeProcessorType type, YukonUserContext userContext) {
    	
    	if (progress == null) {
			return null;
		}
		
		MultispeakSyncProgressStatus status = progress.getStatus();
		if (status != MultispeakSyncProgressStatus.FINISHED) {
			return status;
		}
		
		return getLastCompletedSyncDateStr(type, userContext);
    }

    
    @Autowired
    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
    	this.objectFormattingService = objectFormattingService;
    }
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
}
