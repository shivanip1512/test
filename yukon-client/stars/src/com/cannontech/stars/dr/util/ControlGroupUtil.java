package com.cannontech.stars.dr.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public final class ControlGroupUtil {
    private static Comparator<LMHardwareControlGroup> enrollmentComparator;
    private static Comparator<LMHardwareControlGroup> optOutComparator;
    
    private ControlGroupUtil() {}
    
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
    
}
