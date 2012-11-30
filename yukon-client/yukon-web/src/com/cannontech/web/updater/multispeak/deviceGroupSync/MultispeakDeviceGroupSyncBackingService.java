package com.cannontech.web.updater.multispeak.deviceGroupSync;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgressStatus;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncService;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessorType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.multispeak.deviceGroupSync.handler.MultispeakDeviceGroupSyncUpdaterHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class MultispeakDeviceGroupSyncBackingService implements UpdateBackingService, InitializingBean {
    
    private Map<MultispeakDeviceGroupSyncUpdaterTypeEnum, MultispeakDeviceGroupSyncUpdaterHandler> handlersMap;
    private ObjectFormattingService objectFormattingService;
    private MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncService;
    private MeterDao meterDao;
    private DateFormattingService dateFormattingService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private ImmutableMap<MultispeakDeviceGroupSyncProgressStatus, String> statusStyleClassNameMap;
    
    @PostConstruct
    public void init() {
    	
    	Builder<MultispeakDeviceGroupSyncProgressStatus, String> builder = ImmutableMap.builder();
    	builder.put(MultispeakDeviceGroupSyncProgressStatus.RUNNING, "");
    	builder.put(MultispeakDeviceGroupSyncProgressStatus.FAILED, "errorMessage");
    	builder.put(MultispeakDeviceGroupSyncProgressStatus.CANCELED, "errorMessage");
    	builder.put(MultispeakDeviceGroupSyncProgressStatus.FINISHED, "success");
    	statusStyleClassNameMap = builder.build();
    }
    
    @Override
    public String getLatestValue(String updaterTypeStr, long afterDate, YukonUserContext userContext) {

        MultispeakDeviceGroupSyncProgress progress = multispeakDeviceGroupSyncService.getProgress();
        
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
        		return progress != null ? progress.getChangeCount(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION) : 0;
        	}
        });
        
        // SUBSTATION_NO_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.SUBSTATION_NO_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.getNoChangeCount(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION) : 0;
        	}
        });
        
        // BILLING_CYCLE_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.BILLING_CYCLE_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.getChangeCount(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE) : 0;
        	}
        });
        
        // BILLING_CYCLE_NO_CHANGE_COUNT
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.BILLING_CYCLE_NO_CHANGE_COUNT, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return progress != null ? progress.getNoChangeCount(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE) : 0;
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
        		return getStatusTextObj(progress, MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION, userContext);
        	}
        });

        // STATUS_TEXT_OR_LAST_SYNC_BILLING_CYCLE
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.STATUS_TEXT_OR_LAST_SYNC_BILLING_CYCLE, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return getStatusTextObj(progress, MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE, userContext);
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
        		
        		if (progress != null && progress.getType().getProcessorTypes().contains(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION)) {
        			return true;
        		}
        		return false;
        	}
        });
        
        // NO_LINKABLE_PROGRESS_SUBSTATION
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.NO_LINKABLE_PROGRESS_SUBSTATION, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		
        		if (progress == null || !progress.getType().getProcessorTypes().contains(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION)) {
        			return true;
        		}
        		return false;
        	}
        });
        
        // HAS_LINKABLE_PROGRESS_BILLING_CYCLE
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.HAS_LINKABLE_PROGRESS_BILLING_CYCLE, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		
        		if (progress != null && progress.getType().getProcessorTypes().contains(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE)) {
        			return true;
        		}
        		return false;
        	}
        });
        
        // NO_LINKABLE_PROGRESS_BILLING_CYCLE
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.NO_LINKABLE_PROGRESS_BILLING_CYCLE, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		
        		if (progress == null || !progress.getType().getProcessorTypes().contains(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE)) {
        			return true;
        		}
        		return false;
        	}
        });
        
        // LAST_COMPLETED_SYNC_SUBSTATION
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.LAST_COMPLETED_SYNC_SUBSTATION, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return getLastCompletedSyncDateStr(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION, userContext);
        	}
        });

        // LAST_COMPLETED_SYNC_BILLING_CYCLE
        handlersMap.put(MultispeakDeviceGroupSyncUpdaterTypeEnum.LAST_COMPLETED_SYNC_BILLING_CYCLE, new MultispeakDeviceGroupSyncUpdaterHandler() {
        	@Override
        	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
        		return getLastCompletedSyncDateStr(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE, userContext);
        	}
        });
    }
    
    private Object getStatusTextObj(MultispeakDeviceGroupSyncProgress progress, MultispeakDeviceGroupSyncTypeProcessorType type, YukonUserContext userContext) {
    	
    	if (progress == null) {
			return null;
		}
		
		MultispeakDeviceGroupSyncProgressStatus status = progress.getStatus();
		if (status != MultispeakDeviceGroupSyncProgressStatus.FINISHED) {
			return status;
		}
		
		return getLastCompletedSyncDateStr(type, userContext);
    }
    
    private String getLastCompletedSyncDateStr(MultispeakDeviceGroupSyncTypeProcessorType type, YukonUserContext userContext) {
    	
    	Map<MultispeakDeviceGroupSyncTypeProcessorType, Instant> lastSyncInstants = multispeakDeviceGroupSyncService.getLastSyncInstants();
		Instant instant = lastSyncInstants.get(type);
		
		if (instant == null) {
			MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
			return messageSourceAccessor.getMessage("yukon.web.defaults.na");
		}
		
		String dateStr = dateFormattingService.format(instant, DateFormatEnum.FULL, userContext);
		return dateStr;
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
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
}
