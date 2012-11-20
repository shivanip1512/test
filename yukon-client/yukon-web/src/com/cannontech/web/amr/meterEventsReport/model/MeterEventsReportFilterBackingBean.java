package com.cannontech.web.amr.meterEventsReport.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import com.cannontech.amr.meter.service.impl.MeterEventStatusTypeGroupings;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeNameComparator;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MeterEventsReportFilterBackingBean extends ListBackingBean {
    
    final private static int NUM_DAYS_PREVIOUS = 7;

    private DeviceCollection deviceCollection;
    private Instant fromInstant = new Instant().minus(Duration.standardDays(NUM_DAYS_PREVIOUS));
    private Instant toInstant = new Instant();
    private Map<BuiltInAttribute, Boolean> meterEventTypesMap;
    private boolean onlyAbnormalEvents = true;
    private boolean onlyLatestEvent = true;
    private boolean includeDisabledPaos;
    
    public MeterEventsReportFilterBackingBean() {
        this(null);
    }

    public MeterEventsReportFilterBackingBean(YukonUserContext userContext) {
        if (userContext != null) {
            LocalDate localDate = new LocalDate(userContext.getJodaTimeZone());
            fromInstant = TimeUtil.toMidnightAtBeginningOfDay(localDate, userContext.getJodaTimeZone()).minus(Duration.standardDays(NUM_DAYS_PREVIOUS));
            toInstant = TimeUtil.toMidnightAtEndOfDay(localDate, userContext.getJodaTimeZone());
        }
        
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

    /**
     * Returns our "ToInstant" value with 1 day subtracted. We need this since our "to" Instant
     * is set to END_OF_DAY (00:00:00 of the next day - which doesn't look right in the UI)
     */
    public Instant getToInstantDisplayable() {
        return toInstant.minus(Duration.standardDays(1));
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

    public Range<Instant> getRange() {
        return Clusivity.INCLUSIVE_INCLUSIVE.makeRange(fromInstant, toInstant);
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

    public Set<Attribute> getEnabledEventTypes() {
        SortedSet<Attribute> events = Sets.newTreeSet(AttributeNameComparator.attributeComparator());
        for(Entry<BuiltInAttribute, Boolean> event : meterEventTypesMap.entrySet()) {
            if (event.getValue()) {
                events.add(event.getKey());
            }
        }
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
