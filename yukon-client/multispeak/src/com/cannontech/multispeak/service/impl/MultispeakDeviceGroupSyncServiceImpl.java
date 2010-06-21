package com.cannontech.multispeak.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.dao.MultispeakGetAllServiceLocationsCallback;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncService;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

public class MultispeakDeviceGroupSyncServiceImpl implements MultispeakDeviceGroupSyncService {

	private ScheduledExecutor scheduledExecutor;
	private MspObjectDao mspObjectDao;
	private MultispeakFuncs multispeakFuncs;
	private MultispeakDao multispeakDao;
	private MeterDao meterDao;
	private MultispeakMeterService multispeakMeterService;
	private DeviceGroupEditorDao deviceGroupEditorDao;
	private PersistedSystemValueDao persistedSystemValueDao;
	
	private Logger log = YukonLogManager.getLogger(MultispeakDeviceGroupSyncServiceImpl.class);
	private static final String SUBSTATION_SYNC_LOG_STRING = "SubstationDeviceGroupSync";
    private static final String BILLING_CYCLE_LOG_STRING = "BillingCycleDeviceGroupSync";
	
	private MultispeakDeviceGroupSyncProgress progress;
	
	// INIT
	@PostConstruct
	public void init() {
		progress = null;
	}
	
	public MultispeakDeviceGroupSyncProgress getProgress() {
		return progress;
	};
	
	// IS SYNC IN PROGRESS
	public boolean isProgressAvailable() {
		return progress != null;
	}
	
	// CANCEL
	public void cancel() {
		progress.cancel();
		log.debug("Multispeak device group sync canceled.");
	}
	
	// START
	@Override
	public void startSyncForType(final MultispeakDeviceGroupSyncType type, final YukonUserContext userContext) {
		
		log.debug("Multispeak device group sync started. type =  " + type);
		
		
		// vendor and parent device groups
		final MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
		
		DeviceGroup substationNameDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.SUBSTATION_NAME);
        final StoredDeviceGroup substationDeviceGroupParent = deviceGroupEditorDao.getStoredGroup(substationNameDeviceGroup);
		
		DeviceGroup billingCycledeviceGroup = multispeakFuncs.getBillingCycleDeviceGroup();
        final StoredDeviceGroup billingCycleDeviceGroupParent = deviceGroupEditorDao.getStoredGroup(billingCycledeviceGroup);

    	// MultispeakGetAllServiceLocationsCallback
    	// processes the list of msp meters as they are retrieved from the cis vendor
    	final MultispeakGetAllServiceLocationsCallback callback = new MultispeakGetAllServiceLocationsCallback() {
    		
    		// FINISH
    		@Override
    		public void finish() {
    			
    			if (type.isSupportsSubstationSync()) {
    				persistedSystemValueDao.setValue(PersistedSystemValueKey.MSP_SUBSTATION_DEVICE_GROUP_SYNC_LAST_COMPLETED, new DateTime(userContext.getJodaTimeZone()));
    			}
    			if (type.isSupportsBillingCycleSync()) {
    				persistedSystemValueDao.setValue(PersistedSystemValueKey.MSP_BILLING_CYCLE_DEVICE_GROUP_SYNC_LAST_COMPLETED, new DateTime(userContext.getJodaTimeZone()));
    			}
    			
    			progress.finish();
    		}
    		
    		// IS CANCELED
    		@Override
    		public boolean isCanceled() {
    			return progress.isCanceled();
    		}

    		// PROCESS SERVICE LOCATIONS LIST
    		@Override
    		public void processServiceLocations(List<ServiceLocation> mspServiceLocations) {
    			
    			// kill before gathering meters if canceled
    			if (progress.isCanceled()) {
    				return;
    			}
    			
    			log.debug("Handling msp ServiceLocation list of size " + mspServiceLocations.size());
    			
    			// loop per service location
    			for (ServiceLocation mspServiceLocation : mspServiceLocations) {
    				
    				List<com.cannontech.multispeak.deploy.service.Meter> mspMeters = mspObjectDao.getMspMetersByServiceLocation(mspServiceLocation, mspVendor);
    				
    				// msp meter number map
        			log.debug("Handling msp meter list of size " + mspMeters.size() + " for Service Location: " + mspServiceLocation.getObjectID());
        			ImmutableListMultimap<String,com.cannontech.multispeak.deploy.service.Meter> mspMeterMap = Multimaps.index(mspMeters, new Function<com.cannontech.multispeak.deploy.service.Meter, String>() {
        	    		@Override
        	    		public String apply(com.cannontech.multispeak.deploy.service.Meter device) {
        	    			return device.getMeterNo();
        	    		}
        	    	});
        			
        			// yukon meters
        			List<String> meterNumberList = new ArrayList<String>(mspMeterMap.keySet());
        	    	List<Meter> meters = meterDao.getMetersForMeterNumbers(meterNumberList);
        	    	log.debug("Found " + meters.size() + " yukon meters for Service Location: " + mspServiceLocation.getObjectID());
    				
        	    	// loop per yukon meter
    				for (Meter meter : meters) {
        	    		
        	    		String meterNumber = meter.getMeterNumber();
        	    		ImmutableList<com.cannontech.multispeak.deploy.service.Meter> mspMeterList = mspMeterMap.get(meterNumber);

        	    		for (com.cannontech.multispeak.deploy.service.Meter mspMeter : mspMeterList) {
        	    			
        	    			// kill before processing another meter if canceled
        	    			if (progress.isCanceled()) {
        	    				return;
        	    			}
            	    		
        	    			// SUBSTATION SYNC
        	    			if (type.isSupportsSubstationSync()) {
        	    				
        	    				boolean added = false;
        	    				if (mspMeter.getUtilityInfo() != null) {
        	    					
        	    					String substationName = mspMeter.getUtilityInfo().getSubstationName();
        	    					added = multispeakMeterService.updateDeviceGroup(substationName, substationDeviceGroupParent, meterNumber, meter, SUBSTATION_SYNC_LOG_STRING, mspVendor);
        	    				}
        	    				
        	    				if (added) {
        	    					progress.incrementSubstationChangeCount();
        	    				} else {
        	    					progress.incrementSubstationNoChangeCount();
        	    				}
        	    			}
            	    		
        	    			// BILLING CYCLE SYNC
        	    			if (type.isSupportsBillingCycleSync()) {
        	    				
        	    				String billingCycleName = mspServiceLocation.getBillingCycle();
        	    				
        	    				boolean added = multispeakMeterService.updateDeviceGroup(billingCycleName, billingCycleDeviceGroupParent, meterNumber, meter, BILLING_CYCLE_LOG_STRING, mspVendor);
        	    				if (added) {
        	    					progress.incrementBillingCycleChangeCount();
        	    				} else {
        	    					progress.incrementBillingCycleNoChangeCount();
        	    				}
        	    			}
            	    		
        	    			// increment meters processed
        	    			progress.incrementMetersProcessedCount();
        	    		}
    				}
    			}
    		}
    	};

    	progress = new MultispeakDeviceGroupSyncProgress(type);
    	
    	// run
    	Runnable runner = new Runnable() {
			@Override
			public void run() {
				try {
					mspObjectDao.getAllMspServiceLocations(mspVendor, callback);
				} catch (Exception e) {
					progress.setException(e);
				}
			}
		};
		
		scheduledExecutor.execute(runner);
	}

	
	@Resource(name="globalScheduledExecutor")
	public void setScheduledExecutor(ScheduledExecutor scheduledExecutor) {
		this.scheduledExecutor = scheduledExecutor;
	}
	
    @Autowired
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
		this.mspObjectDao = mspObjectDao;
	}
    
    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
		this.multispeakFuncs = multispeakFuncs;
	}
    
    @Autowired
    public void setMultispeakDao(MultispeakDao multispeakDao) {
		this.multispeakDao = multispeakDao;
	}
	
	@Autowired
	public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
	
	@Autowired
	public void setMultispeakMeterService(MultispeakMeterService multispeakMeterService) {
		this.multispeakMeterService = multispeakMeterService;
	}
	
	@Autowired
	public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
	
	@Autowired
	public void setPersistedSystemValueDao(PersistedSystemValueDao persistedSystemValueDao) {
		this.persistedSystemValueDao = persistedSystemValueDao;
	}
}
