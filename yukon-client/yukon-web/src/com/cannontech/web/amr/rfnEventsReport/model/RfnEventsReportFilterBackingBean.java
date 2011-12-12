package com.cannontech.web.amr.rfnEventsReport.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RfnEventsReportFilterBackingBean extends ListBackingBean {

    private DeviceCollection deviceCollection;
    private Date toDate = new Date();
    private Date fromDate = new DateTime().minus(Duration.standardDays(7)).toDate();
    private Map<BuiltInAttribute, Boolean> rfnEventTypesMap;
    private boolean onlyActiveEvents;
    private boolean onlyLatestEvent;
    private boolean includeDisabledPaos;

    public RfnEventsReportFilterBackingBean() {
        setDescending(true);
        rfnEventTypesMap =
            Maps.newHashMapWithExpectedSize(BuiltInAttribute.getRfnEventStatusTypes().size());
        for (BuiltInAttribute attr : BuiltInAttribute.getRfnEventStatusTypes()) {
            rfnEventTypesMap.put(attr, false);
        }
        setItemsPerPage(10);
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

    public Map<BuiltInAttribute, Boolean> getRfnEventTypesMap() {
        return rfnEventTypesMap;
    }

    public void setRfnEventTypesMap(Map<BuiltInAttribute, Boolean> rfnEventTypesMap) {
        this.rfnEventTypesMap = rfnEventTypesMap;
    }

    public void setEventTypesAllTrue() {
        for (Entry<BuiltInAttribute, Boolean> event : rfnEventTypesMap.entrySet()) {
            event.setValue(true);
        }
    }

    public int getNumSelectedEventTypes() {
        int result = 0;
        for (Entry<BuiltInAttribute, Boolean> event : rfnEventTypesMap.entrySet()) {
            if (event.getValue() == true) {
                result++;
            }
        }
        return result;
    }
    
    public List<String> getEnabledEventTypeStrings() {
        List<String> events = Lists.newArrayList();
        for(Entry<BuiltInAttribute, Boolean> event : rfnEventTypesMap.entrySet()) {
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
