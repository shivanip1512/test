package com.cannontech.common.events.model;

import com.cannontech.common.util.predicate.Predicate;

public enum EventLogPredicateEnum {

    ANY(new AnyEventLogPredicate());
    
    private Predicate<EventLog> predicate;
    
    private EventLogPredicateEnum(Predicate<EventLog> predicate) {
        this.predicate = predicate;
    }
    
    public Predicate<EventLog> getPredicate() {
        return predicate;
    }
}
