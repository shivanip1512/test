package com.cannontech.common.events.model;

import com.cannontech.common.util.predicate.Predicate;

public class AnyEventLogPredicate implements Predicate<EventLog> {
    
    public boolean evaluate(EventLog eventLog) {
        return true;
    }
}
