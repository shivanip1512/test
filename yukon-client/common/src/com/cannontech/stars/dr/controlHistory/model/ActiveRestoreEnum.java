package com.cannontech.stars.dr.controlHistory.model;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public enum ActiveRestoreEnum implements DatabaseRepresentationSource{
    NEW_CONTROL("N"),       // This is the first entry for any new control.
    CONTINUE_CONTROL("C"),  // Previous command was repeated extending the current control interval.
    TIMED_RESTORE("T"),     // Control terminated because of an active restore or terminate command being sent..
    MANUAL_RESTORE("M"),    // The control was terminated because of an active restore or terminate command being sent.
    OVERRIDE_CONTROL("O"),  // Control terminated because a new command of a different nature was sent to this group.
    CONTROL_ADJUST("A"),    // Control accounting was adjusted by user.
    LOG_TIMER("L"),         // This is a timed log entry.  Nothing exciting happened in this interval.
    PERIOD_TRANSITION("P"), // Control was active as we crossed a control history boundary.  This log denotes the last log in the previous interval. 
    DISPATCH_SHUTDOWN("S"), // Control was active as dispatch shutdown.  This entry will be used to resume control.
    ;

    private String databaseRepresentation;
    
    private static final ImmutableSet<ActiveRestoreEnum> controlPeriodStartedEnums = Sets.immutableEnumSet(NEW_CONTROL);
    private static final ImmutableSet<ActiveRestoreEnum> controlPeriodStoppedEnums = Sets.immutableEnumSet(TIMED_RESTORE, MANUAL_RESTORE, OVERRIDE_CONTROL);
    
    
    private ActiveRestoreEnum(String databaseRepresentation) {
        this.databaseRepresentation = databaseRepresentation;
    }
    
    
    public static ImmutableSet<ActiveRestoreEnum> getActiveRestoreStartEntries() {
        return controlPeriodStartedEnums;
    }
    
    public static ImmutableSet<ActiveRestoreEnum> getActiveRestoreStopEntries() {
        return controlPeriodStoppedEnums;
    }
    
    public static ActiveRestoreEnum getActiveRestoreByDatabaseRepresentation(String databaseValue){
        ActiveRestoreEnum[] activeRestoreValues = ActiveRestoreEnum.values();
        
        for (ActiveRestoreEnum activeRestore : activeRestoreValues) {
            if (activeRestore.databaseRepresentation.equals(databaseValue)) {
                return activeRestore;
            }
        }
            
        return null;
            
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return databaseRepresentation;
    }
    
}