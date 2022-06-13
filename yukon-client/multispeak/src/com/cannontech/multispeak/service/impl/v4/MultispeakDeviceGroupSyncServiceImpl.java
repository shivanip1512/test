package com.cannontech.multispeak.service.impl.v4;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.msp.beans.v4.ElectricLocationFields;
import com.cannontech.msp.beans.v4.ElectricMeter;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.dao.v4.MultispeakSyncProcessCallback;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessor;
import com.cannontech.multispeak.service.MultispeakSyncType;
import com.cannontech.multispeak.service.MultispeakSyncTypeProcessorType;
import com.cannontech.multispeak.service.impl.MultispeakDeviceGroupSyncServiceBase;
import com.cannontech.multispeak.service.v4.MultispeakMeterService;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MultispeakDeviceGroupSyncServiceImpl extends MultispeakDeviceGroupSyncServiceBase {

    
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MeterDao meterDao;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    
    @Resource(name = "globalScheduledExecutor") private ScheduledExecutor scheduledExecutor;
        
        private Logger log = YukonLogManager.getLogger(MultispeakDeviceGroupSyncServiceImpl.class);

        // START
        @Override
        public void startSyncForType(final MultispeakSyncType type, final YukonUserContext userContext) {
                
                log.debug("Multispeak device group sync started. type =  " + type);
                final MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());

        // MultispeakGetAllServiceLocationsCallback
        // processes the list of msp meters as they are retrieved from the cis vendor
        final MultispeakSyncProcessCallback callback = new MultispeakSyncProcessCallback() {
                
                // FINISH
                @Override
                public void finish() {
                        
                        // set last completed persisted system value
                        Set<MultispeakSyncTypeProcessorType> processorsTypes = type.getProcessorTypes();
                        for (MultispeakSyncTypeProcessorType processorType : processorsTypes) {
                                
                                MultispeakDeviceGroupSyncTypeProcessor processor = processorMap.get(processorType);
                                persistedSystemValueDao.setValue(processor.getPersistedSystemValueKey(), new Instant());
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
                                log.debug("Handling of remaining msp Service Locations skipped due to cancellation.");
                                return;
                        }
                        
                        log.debug("Handling msp ServiceLocation list of size " + mspServiceLocations.size());
                        
                        // loop per service location
                        for (ServiceLocation mspServiceLocation : mspServiceLocations) {
                                
                                List<MspMeter> mspMeters = mspObjectDao.getMspMetersByServiceLocation(mspServiceLocation, mspVendor);
                                
                                // msp meter map
                                log.debug("Handling msp meter list of size " + mspMeters.size() + " for Service Location: " + mspServiceLocation.getObjectID());
                                ImmutableMap<String, MspMeter> mspMeterMap = Maps.uniqueIndex(mspMeters,  new Function<MspMeter, String>() {
                                @Override
                                public String apply(MspMeter device) {
                                        return device.getMeterNo();
                                }
                        });
                                
                                // yukon meter map
                                List<String> meterNumberList = new ArrayList<String>(mspMeterMap.keySet());
                        List<YukonMeter> yukonMeters = meterDao.getMetersForMeterNumbers(meterNumberList);
                        ImmutableMap<String, YukonMeter> yukonMeterMap = Maps.uniqueIndex(yukonMeters,  new Function<YukonMeter, String>() {
                                @Override
                                public String apply(YukonMeter meter) {
                                        return meter.getMeterNumber();
                                }
                        });
                        
                        log.debug("Found " + yukonMeters.size() + " yukon meters for Service Location: " + mspServiceLocation.getObjectID());
                                
                        // loop per msp meter
                                for (MspMeter mspMeter : mspMeterMap.values()) {
                                    
                                        // kill before processing another meter if canceled
                                if (progress.isCanceled()) {
                                        return;
                                }
                                
                                // lookup yukon meter
                                String meterNumber = mspMeter.getMeterNo();
                        if (!yukonMeterMap.containsKey(meterNumber)) {

                            log.debug("No Yukon meter found for meter number " + meterNumber + " from Service Location "
                                    + mspServiceLocation.getObjectID());
                            continue;
                        }
                        YukonMeter yukonMeter = yukonMeterMap.get(meterNumber);

                        // process
                        Set<MultispeakSyncTypeProcessorType> processorsTypes = type.getProcessorTypes();
                        for (MultispeakSyncTypeProcessorType processorType : processorsTypes) {

                            MultispeakDeviceGroupSyncTypeProcessor processor = processorMap.get(processorType);
                            String deviceGroupSyncValue = null;
                            if (processorType.equals(MultispeakSyncTypeProcessorType.SUBSTATION)
                                    && mspMeter instanceof ElectricMeter) {
                                ElectricMeter electricMeter = (ElectricMeter) mspMeter;
                                ElectricLocationFields electricLocationFields = electricMeter.getElectricLocationFields();

                                if (electricLocationFields != null && electricLocationFields.getSubstationName() != null &&
                                        !StringUtils.isBlank(electricLocationFields.getSubstationName())) {
                                    deviceGroupSyncValue = electricLocationFields.getSubstationName();
                                }
                            } else if (processorType.equals(MultispeakSyncTypeProcessorType.BILLING_CYCLE)) {
                                deviceGroupSyncValue = mspMeter.getBillingCycle();
                            }
                            boolean added = processor.processMeterSync(mspVendor, deviceGroupSyncValue, yukonMeter);

                            if (added) {
                                progress.incrementChangeCount(processorType);
                            } else {
                                progress.incrementNoChangeCount(processorType);
                            }
                        }

                        // increment meters processed
                                progress.incrementMetersProcessedCount();
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
                                        log.error("Error occurred during MSP device group sync.", e);
                                        progress.setException(e);
                                }
                        }
                };
                
                scheduledExecutor.execute(runner);
    }

}
