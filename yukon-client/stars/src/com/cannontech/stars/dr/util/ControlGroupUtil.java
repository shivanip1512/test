package com.cannontech.stars.dr.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public final class ControlGroupUtil {
    private static Comparator<LMHardwareControlGroup> enrollmentComparator;
    private static Comparator<LMHardwareControlGroup> optOutComparator;
    
    private ControlGroupUtil() {
        
    }
    
    static {
        
        enrollmentComparator =
            Ordering.natural().nullsFirst().onResultOf(new Function<LMHardwareControlGroup, Instant>() {
                public Instant apply(LMHardwareControlGroup lmHardwarecontrolGroup) {
                    return lmHardwarecontrolGroup.getGroupEnrollStart();
                }
            });
        
        optOutComparator = 
            Ordering.natural().nullsFirst().onResultOf(new Function<LMHardwareControlGroup, Instant>() {
                public Instant apply(LMHardwareControlGroup lmHardwarecontrolGroup) {
                    return lmHardwarecontrolGroup.getOptOutStart();
                }
            });
        
    }

    public static final boolean isControlledToday(final List<LMHardwareControlGroup> controlGroupList, 
            final Map<Integer, StarsLMControlHistory> controlHistoryMap) {
        
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            StarsLMControlHistory controlHistory = controlHistoryMap.get(controlGroup.getLmGroupId());
            if (controlHistory == null || controlHistory.getControlHistoryCount() == 0) continue;
            boolean result = !controlHistory.getControlSummary().getDailyTime().isEqual(Duration.ZERO);
            if (result) return true;
        }
        
        return false;
    } 
    
    public static final boolean isControlling(final List<LMHardwareControlGroup> controlGroupList, 
            final Map<Integer, StarsLMControlHistory> controlHistoryMap) {
        
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            StarsLMControlHistory controlHistory = controlHistoryMap.get(controlGroup.getLmGroupId());
            if (controlHistory == null || controlHistory.getControlHistoryCount() == 0) continue;
            boolean result = controlHistory.getBeingControlled();
            if (result) return true;    
        }
        
        return false;
    }
    
    /**
     * You are enrolled ONLY IF the enrollment entry has not been closed, i.e. no stop date (null). 
     * It is a current enrollment if there is a startDate which is in the past and no stop date.
     */
    public static final boolean isEnrolled(final List<LMHardwareControlGroup> controlGroupList, 
                                               final ReadableInstant now) {
        // sort the list by enrollment start date
        Collections.sort(controlGroupList, enrollmentComparator);
        
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            ReadableInstant start = controlGroup.getGroupEnrollStart();
            ReadableInstant stop = controlGroup.getGroupEnrollStop();
            
            boolean hasStartInPastWithNullStop = hasStartInPastWithNullStop(start, stop, now);
            if (hasStartInPastWithNullStop) return true;
        }    
        return false;
    }
    
    public static final boolean isOptedOut(final List<LMHardwareControlGroup> outList, final ReadableInstant now) {
        // sort the list by optout start date
        Collections.sort(outList, optOutComparator);
        
        for (final LMHardwareControlGroup controlGroup : outList) {
            ReadableInstant start = controlGroup.getOptOutStart();
            ReadableInstant stop = controlGroup.getOptOutStop();
            
            boolean hasStartInPastWithNullStop = hasStartInPastWithNullStop(start, stop, now);
            if (hasStartInPastWithNullStop) return true;
        }
        return false;
    }
    
    private static boolean hasStartInPastWithNullStop(ReadableInstant start, 
                                                         ReadableInstant stop,
                                                         ReadableInstant now) {
        boolean result = (start != null && 
                           start.isBefore(now) && 
                           stop == null);
        return result;
    }
    
    private static boolean hasBothNullDates(ReadableInstant start, ReadableInstant stop) {
        boolean result = (start == null && 
                           stop == null);
        return result;
    }
    

    
}
