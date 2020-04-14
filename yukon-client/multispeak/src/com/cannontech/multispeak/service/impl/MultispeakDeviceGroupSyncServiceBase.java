package com.cannontech.multispeak.service.impl;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessor;
import com.cannontech.multispeak.service.MultispeakSyncType;
import com.cannontech.multispeak.service.MultispeakSyncTypeProcessorType;
import com.cannontech.multispeak.service.v5.MultispeakMeterService;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Maps;

public abstract class MultispeakDeviceGroupSyncServiceBase {

    @Autowired public PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private MultispeakMeterService multispeakMeterService;

    protected MultispeakDeviceGroupSyncProgress progress = null;
    protected Map<MultispeakSyncTypeProcessorType, MultispeakDeviceGroupSyncTypeProcessor> processorMap;
    
    private static final String SUBSTATION_SYNC_LOG_STRING = "SubstationDeviceGroupSync";
    private static final String BILLING_CYCLE_LOG_STRING = "BillingCycleDeviceGroupSync";
 
    @PostConstruct
    public void init() {
        processorMap = Maps.newLinkedHashMap();
        processorMap.put(MultispeakSyncTypeProcessorType.SUBSTATION, new SubstationSyncTypeProcessor());
        processorMap.put(MultispeakSyncTypeProcessorType.BILLING_CYCLE, new BillingCycleSyncTypeProcessor());
    }

    public MultispeakDeviceGroupSyncProgress getProgress() {
        return progress;
    };

    /**
     * Get a map of Instants of last completed sync for each processor type.
     */

    // LAST SYNC INSTANTS
    public Map<MultispeakSyncTypeProcessorType, Instant> getLastSyncInstants() {

        Instant lastSubstationInstant =
            persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.MSP_SUBSTATION_DEVICE_GROUP_SYNC_LAST_COMPLETED);
        Instant lastBillingCycleInstant =
            persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.MSP_BILLING_CYCLE_DEVICE_GROUP_SYNC_LAST_COMPLETED);

        Map<MultispeakSyncTypeProcessorType, Instant> lastSyncInstantsMap = Maps.newLinkedHashMap();
        lastSyncInstantsMap.put(MultispeakSyncTypeProcessorType.SUBSTATION, lastSubstationInstant);
        lastSyncInstantsMap.put(MultispeakSyncTypeProcessorType.BILLING_CYCLE, lastBillingCycleInstant);
        return lastSyncInstantsMap;
    }

    abstract public void startSyncForType(MultispeakSyncType type, YukonUserContext userContext);
    
    // PROCESSORS
    private class SubstationSyncTypeProcessor implements MultispeakDeviceGroupSyncTypeProcessor {

        @Override
        public boolean processMeterSync(MultispeakVendor mspVendor, String substationName, YukonMeter yukonMeter) {

            boolean added = false;
            if (StringUtils.isNotBlank(substationName)) {
                added =
                    multispeakMeterService.updateSubstationGroup(substationName, yukonMeter.getMeterNumber(),
                        yukonMeter, SUBSTATION_SYNC_LOG_STRING, mspVendor);
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
        public boolean processMeterSync(MultispeakVendor mspVendor, String billingCycleName, YukonMeter yukonMeter) {

            boolean added = false;
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
