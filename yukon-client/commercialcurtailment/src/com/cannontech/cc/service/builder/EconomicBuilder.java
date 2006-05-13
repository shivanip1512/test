package com.cannontech.cc.service.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.cc.model.Program;

public class EconomicBuilder extends EventBuilderBase {
    private EconomicEvent event = null;
    private EconomicEventPricing eventRevision = null;
    private List<EconomicEventPricingWindow> prices = Collections.emptyList();
    private List<EconomicEventParticipant> participantList = 
        new ArrayList<EconomicEventParticipant>();
    
    private Integer numberOfWindows = new Integer(0);
    private TimeZone timeZone;
    private Program program;
    
    public Integer getNumberOfWindows() {
        return numberOfWindows;
    }
    public void setNumberOfWindows(Integer duration) {
        this.numberOfWindows = duration;
    }
    public void setTimeZone(TimeZone tz) {
        timeZone = tz;
    }
    public TimeZone getTimeZone() {
        return timeZone;
    }
    public Program getProgram() {
        return program;
    }
    public void setProgram(Program program) {
        this.program = program;
    }
    public List<EconomicEventPricingWindow> getPrices() {
        return prices;
    }
    public void setPrices(List<EconomicEventPricingWindow> prices) {
        this.prices = prices;
    }
    public EconomicEvent getEvent() {
        return event;
    }
    public void setEvent(EconomicEvent event) {
        this.event = event;
    }
    public EconomicEventPricing getEventRevision() {
        return eventRevision;
    }
    public void setEventRevision(EconomicEventPricing eventRevision) {
        this.eventRevision = eventRevision;
    }
    public List<EconomicEventParticipant> getParticipantList() {
        return participantList;
    }
    public void setParticipantList(List<EconomicEventParticipant> participantList) {
        this.participantList = participantList;
    }
    
}
