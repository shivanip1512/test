package com.cannontech.multispeak.service.impl;

import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncTypeProcessorType;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Maps;

public abstract class MultispeakDeviceGroupSyncServiceBase {

    @Autowired public PersistedSystemValueDao persistedSystemValueDao;

    protected MultispeakDeviceGroupSyncProgress progress = null;

    public MultispeakDeviceGroupSyncProgress getProgress() {
        return progress;
    };

    /**
     * Get a map of Instants of last completed sync for each processor type.
     */

    // LAST SYNC INSTANTS
    public Map<MultispeakDeviceGroupSyncTypeProcessorType, Instant> getLastSyncInstants() {

        Instant lastSubstationInstant =
            persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.MSP_SUBSTATION_DEVICE_GROUP_SYNC_LAST_COMPLETED);
        Instant lastBillingCycleInstant =
            persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.MSP_BILLING_CYCLE_DEVICE_GROUP_SYNC_LAST_COMPLETED);

        Map<MultispeakDeviceGroupSyncTypeProcessorType, Instant> lastSyncInstantsMap = Maps.newLinkedHashMap();
        lastSyncInstantsMap.put(MultispeakDeviceGroupSyncTypeProcessorType.SUBSTATION, lastSubstationInstant);
        lastSyncInstantsMap.put(MultispeakDeviceGroupSyncTypeProcessorType.BILLING_CYCLE, lastBillingCycleInstant);
        return lastSyncInstantsMap;
    }

    abstract public void startSyncForType(MultispeakDeviceGroupSyncType type, YukonUserContext userContext);
}
