package com.cannontech.multispeak.service.impl.v5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.msp.beans.v5.commontypes.SubstationRef;
import com.cannontech.msp.beans.v5.multispeak.ElectricLocationFields;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.dao.v5.MultispeakGetAllServiceLocationsCallback;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.impl.MultispeakDeviceGroupSyncServiceBase;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessorType;
import com.cannontech.multispeak.service.v5.MultispeakDeviceGroupSyncTypeProcessor;
import com.cannontech.multispeak.service.v5.MultispeakMeterService;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MultispeakDeviceGroupSyncServiceImpl extends MultispeakDeviceGroupSyncServiceBase {

    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MeterDao meterDao;

    @Resource(name = "globalScheduledExecutor") private ScheduledExecutor scheduledExecutor;

    private Map<MultispeakDeviceGroupSyncTypeProcessorType, MultispeakDeviceGroupSyncTypeProcessor> processorMap;
    private Logger log = YukonLogManager.getLogger(MultispeakDeviceGroupSyncServiceImpl.class);

    private static final String SUBSTATION_SYNC_LOG_STRING = "SubstationDeviceGroupSync";
    private static final String BILLING_CYCLE_LOG_STRING = "BillingCycleDeviceGroupSync";
 
    @PostConstruct
    public void init() {
        processorMap = Maps.newLinkedHashMap();
        processorMap.put(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION, new SubstationSyncTypeProcessor());
        processorMap.put(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE, new BillingCycleSyncTypeProcessor());
    }

    // START
    @Override
    public void startSyncForType(MultispeakDeviceGroupSyncType type, YukonUserContext userContext) {

        log.debug("Multispeak device group sync started. type =  " + type);
        final MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());

        // MultispeakGetAllServiceLocationsCallback
        // processes the list of msp meters as they are retrieved from the cis vendor
        final MultispeakGetAllServiceLocationsCallback callback = new MultispeakGetAllServiceLocationsCallback() {

            // FINISH
            @Override
            public void finish() {

                // set last completed persisted system value
                Set<MultispeakDeviceGroupSyncTypeProcessorType> processorsTypes = type.getProcessorTypes();
                for (MultispeakDeviceGroupSyncTypeProcessorType processorType : processorsTypes) {

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

                    List<ElectricMeter> mspMeters =
                        mspObjectDao.getMspMetersByServiceLocation(mspServiceLocation, mspVendor);

                    // msp meter map
                    log.debug("Handling msp meter list of size " + mspMeters.size() + " for Service Location: "
                        + mspServiceLocation.getObjectGUID());
                    ImmutableMap<String, ElectricMeter> mspMeterMap =
                        Maps.uniqueIndex(mspMeters, new Function<ElectricMeter, String>() {
                            @Override
                            public String apply(ElectricMeter device) {
                                return device.getPrimaryIdentifier().getValue();
                            }
                        });

                    // yukon meter map
                    List<String> meterNumberList = new ArrayList<String>(mspMeterMap.keySet());
                    List<YukonMeter> yukonMeters = meterDao.getMetersForMeterNumbers(meterNumberList);
                    ImmutableMap<String, YukonMeter> yukonMeterMap =
                        Maps.uniqueIndex(yukonMeters, new Function<YukonMeter, String>() {
                            @Override
                            public String apply(YukonMeter meter) {
                                return meter.getMeterNumber();
                            }
                        });

                    log.debug("Found " + yukonMeters.size() + " yukon meters for Service Location: "
                        + mspServiceLocation.getObjectGUID());

                    // loop per msp meter
                    for (ElectricMeter mspMeter : mspMeterMap.values()) {

                        // kill before processing another meter if canceled
                        if (progress.isCanceled()) {
                            return;
                        }

                        // lookup yukon meter
                        String meterNumber = mspMeter.getPrimaryIdentifier().getValue();
                        if (!yukonMeterMap.containsKey(meterNumber)) {

                            log.debug("No Yukon meter found for meter number " + meterNumber
                                + " from Service Location " + mspServiceLocation.getObjectGUID());
                            continue;
                        }
                        YukonMeter yukonMeter = yukonMeterMap.get(meterNumber);

                        // process
                        Set<MultispeakDeviceGroupSyncTypeProcessorType> processorsTypes = type.getProcessorTypes();
                        for (MultispeakDeviceGroupSyncTypeProcessorType processorType : processorsTypes) {

                            MultispeakDeviceGroupSyncTypeProcessor processor = processorMap.get(processorType);
                            boolean added = processor.processMeterSync(mspVendor, mspMeter, yukonMeter);

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
                    log.error("Error occured during MSP device group sync.", e);
                    progress.setException(e);
                }
            }
        };

        scheduledExecutor.execute(runner);
    }

    // PROCESSORS
    private class SubstationSyncTypeProcessor implements MultispeakDeviceGroupSyncTypeProcessor {

        @Override
        public boolean processMeterSync(MultispeakVendor mspVendor, ElectricMeter mspMeter, YukonMeter yukonMeter) {

            boolean added = false;
            ElectricLocationFields electricLocationFields = mspMeter.getElectricLocationFields();
            if (electricLocationFields != null) {
                SubstationRef substationRef = electricLocationFields.getSubstationRef();
                if (substationRef != null) {
                    String substationName = substationRef.getSubstationName();
                    if (StringUtils.isNotBlank(substationName)) {
                        added =
                            multispeakMeterService.updateSubstationGroup(substationName, yukonMeter.getMeterNumber(),
                                yukonMeter, SUBSTATION_SYNC_LOG_STRING, mspVendor);
                    }
                }
            }
            return added;
        }

        @Override
        public PersistedSystemValueKey getPersistedSystemValueKey() {
            return PersistedSystemValueKey.MSP_SUBSTATION_DEVICE_GROUP_SYNC_LAST_COMPLETED;
        }
    }

    private class BillingCycleSyncTypeProcessor implements MultispeakDeviceGroupSyncTypeProcessor {

        @Override
        public boolean processMeterSync(MultispeakVendor mspVendor, ElectricMeter mspMeter, YukonMeter yukonMeter) {

            boolean added = false;
            String billingCycleName = mspMeter.getBillingCycle();
            if (StringUtils.isNotBlank(billingCycleName)) {
                added =
                    multispeakMeterService.updateBillingCyle(billingCycleName, yukonMeter.getMeterNumber(), yukonMeter,
                        BILLING_CYCLE_LOG_STRING, mspVendor);
            }
            return added;
        }

        @Override
        public PersistedSystemValueKey getPersistedSystemValueKey() {
            return PersistedSystemValueKey.MSP_BILLING_CYCLE_DEVICE_GROUP_SYNC_LAST_COMPLETED;
        }
    }

}
