package com.cannontech.web.amr.meterEventsReport.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.cannontech.amr.meter.service.impl.MeterEventStatusTypeGroupings;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MeterEventsReportFilterBackingBean extends ListBackingBean {

    private DeviceCollection deviceCollection;
    private Date toDate = new Date();
    private Date fromDate = new DateTime().minus(Duration.standardDays(7)).toDate();
    private Map<BuiltInAttribute, Boolean> meterEventTypesMap;
    private boolean onlyActiveEvents = true;
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

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
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

    public boolean isOnlyActiveEvents() {
        return onlyActiveEvents;
    }

    public void setOnlyActiveEvents(boolean onlyActiveEvents) {
        this.onlyActiveEvents = onlyActiveEvents;
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
