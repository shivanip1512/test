package com.cannontech.web.amr.meterEventsReport.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.amr.meter.service.impl.MeterEventStatusTypeGroupings;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MeterEventsReportFilterBackingBean extends ListBackingBean {

    private DeviceCollection deviceCollection;
    private Instant toInstant = new Instant();
    private Instant fromInstant = new Instant().minus(Duration.standardDays(7));
    private Map<BuiltInAttribute, Boolean> meterEventTypesMap;
    private boolean onlyAbnormalEvents = true;
    private boolean onlyLatestEvent = true;
    private boolean includeDisabledPaos;

    public MeterEventsReportFilterBackingBean() {
        meterEventTypesMap = Maps.newHashMapWithExpectedSize(MeterEventStatusTypeGroupings.getAll().size());
        for (BuiltInAttribute attr : MeterEventStatusTypeGroupings.getAll()) {
            meterEventTypesMap.put(attr, false);
        }
    }

    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }

    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public Instant getToInstant() {
        return toInstant;
    }

    public void setToInstant(Instant toInstant) {
        this.toInstant = toInstant;
    }

    public Instant getFromInstant() {
        return fromInstant;
    }

    public void setFromInstant(Instant fromInstant) {
        this.fromInstant = fromInstant;
    }

    public Map<BuiltInAttribute, Boolean> getMeterEventTypesMap() {
        return meterEventTypesMap;
    }

    public void setMeterEventTypesMap(Map<BuiltInAttribute, Boolean> meterEventTypesMap) {
        this.meterEventTypesMap = meterEventTypesMap;
    }

    public void setEventTypesAllTrue() {
        for (Entry<BuiltInAttribute, Boolean> event : meterEventTypesMap.entrySet()) {
            event.setValue(true);
        }
    }

    public int getNumSelectedEventTypes() {
        int result = 0;
        for (Entry<BuiltInAttribute, Boolean> event : meterEventTypesMap.entrySet()) {
            if (event.getValue() == true) {
                result++;
            }
        }
        return result;
    }
    
    public List<String> getEnabledEventTypeStrings() {
        List<String> events = Lists.newArrayList();
        for(Entry<BuiltInAttribute, Boolean> event : meterEventTypesMap.entrySet()) {
            if (event.getValue()) {
                events.add(event.getKey().toString());
            }
        }
        Collections.sort(events);
        return events;
    }

    public boolean isOnlyAbnormalEvents() {
        return onlyAbnormalEvents;
    }

    public void setOnlyAbnormalEvents(boolean onlyAbnormalEvents) {
        this.onlyAbnormalEvents = onlyAbnormalEvents;
    }

    public boolean isOnlyLatestEvent() {
        return onlyLatestEvent;
    }

    public void setOnlyLatestEvent(boolean onlyLatestEvent) {
        this.onlyLatestEvent = onlyLatestEvent;
    }

    public boolean isIncludeDisabledPaos() {
        return includeDisabledPaos;
    }

    public void setIncludeDisabledPaos(boolean includeDisabledPaos) {
        this.includeDisabledPaos = includeDisabledPaos;
    }

}
