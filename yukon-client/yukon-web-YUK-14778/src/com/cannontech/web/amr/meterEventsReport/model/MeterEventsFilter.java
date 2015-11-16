package com.cannontech.web.amr.meterEventsReport.model;

import java.util.Collections;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class MeterEventsFilter {
    private Instant fromInstant;
    private Instant toInstant;
    private Set<? extends Attribute> attributes = Collections.emptySet();
    private boolean onlyAbnormalEvents;
    private boolean onlyLatestEvent;
    private boolean includeDisabledPaos;

    public MeterEventsFilter() {
    }

    public MeterEventsFilter(Instant fromInstant, Instant toInstant, Set<? extends Attribute> attributes,
            boolean onlyAbnormalEvents, boolean onlyLatestEvent, boolean includeDisabledPaos) {
        this.fromInstant = fromInstant;
        this.toInstant = toInstant;
        this.attributes = attributes;
        this.onlyAbnormalEvents = onlyAbnormalEvents;
        this.onlyLatestEvent = onlyLatestEvent;
        this.includeDisabledPaos = includeDisabledPaos;
    }

    public Instant getFromInstant() {
        return fromInstant;
    }

    public void setFromInstant(Instant fromInstant) {
        this.fromInstant = fromInstant;
    }

    public Instant getToInstant() {
        return toInstant;
    }

    public void setToInstant(Instant toInstant) {
        this.toInstant = toInstant;
    }

    public Set<? extends Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<BuiltInAttribute> attributes) {
        this.attributes = attributes;
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
