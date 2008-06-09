package com.cannontech.stars.dr.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;

public final class ControlGroupUtil {
    private static Comparator<LMHardwareControlGroup> enrollmentComparator;
    private static Comparator<LMHardwareControlGroup> optOutComparator;
    
    private ControlGroupUtil() {
        
    }
    
    static {
        
        enrollmentComparator = new Comparator<LMHardwareControlGroup>() {
            @Override
            public int compare(LMHardwareControlGroup o1, LMHardwareControlGroup o2) {
                Date d1 = o1.getGroupEnrollStart();
                Date d2 = o2.getGroupEnrollStart();
                if (hasBothNullDates(d1, d2)) return 0;
                return d1.compareTo(d2);
            }

        };
        
        optOutComparator = new Comparator<LMHardwareControlGroup>() {
            @Override
            public int compare(LMHardwareControlGroup o1, LMHardwareControlGroup o2) {
                Date d1 = o1.getOptOutStart();
                Date d2 = o2.getOptOutStart();
                if (hasBothNullDates(d1, d2)) return 0;
                return d1.compareTo(d2);
            }
        };
        
    }
    
    public static final boolean isControlledToday(final List<LMHardwareControlGroup> controlGroupList, 
            final Map<Integer, StarsLMControlHistory> controlHistoryMap) {
        
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            StarsLMControlHistory controlHistory = controlHistoryMap.get(controlGroup.getLmGroupId());
            if (controlHistory == null) continue;
            boolean result = controlHistory.getControlSummary().getDailyTime() != 0;
            if (result) return true;
        }
        
        return false;
    } 
    
    public static final boolean isControlling(final List<LMHardwareControlGroup> controlGroupList, 
            final Map<Integer, StarsLMControlHistory> controlHistoryMap) {
        
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            StarsLMControlHistory controlHistory = controlHistoryMap.get(controlGroup.getLmGroupId());
            if (controlHistory == null) continue;
            boolean result = controlHistory.getBeingControlled();
            if (result) return true;    
        }
        
        return false;
    }
    
    /**
     * You are enrolled ONLY IF the enrollment entry has not been closed, i.e. no stop date (null). 
     * It is a current enrollment if there is a startDate which is in the past and no stop date.
     */
    public static final boolean isEnrolled(final List<LMHardwareControlGroup> controlGroupList, final Date now) {
        // sort the list by enrollment start date
        Collections.sort(controlGroupList, enrollmentComparator);
        
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            Date start = controlGroup.getGroupEnrollStart();
            Date stop = controlGroup.getGroupEnrollStop();
            
            boolean hasStartInPastWithNullStop = hasStartInPastWithNullStop(start, stop, now);
            if (hasStartInPastWithNullStop) return true;
        }    
        return false;
    }
    
    public static final boolean isOptedOut(final List<LMHardwareControlGroup> outList, final Date now) {
        // sort the list by optout start date
        Collections.sort(outList, optOutComparator);
        
        for (final LMHardwareControlGroup controlGroup : outList) {
            Date start = controlGroup.getOptOutStart();
            Date stop = controlGroup.getOptOutStop();
            
            boolean hasStartInPastWithNullStop = hasStartInPastWithNullStop(start, stop, now);
            if (hasStartInPastWithNullStop) return true;
        }
        return false;
    }
    
    private static boolean hasStartInPastWithNullStop(Date start, Date stop, Date now) {
        boolean result = (start != null && start.before(now) && stop == null);
        return result;
    }
    
    private static boolean hasBothNullDates(Date start, Date stop) {
        boolean result = (start == null && stop == null);
        return result;
    }
    

    
}
