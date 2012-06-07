package com.cannontech.stars.dr.controlHistory.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.ReadableInstant;

import com.cannontech.stars.dr.controlHistory.model.ControlHistory;
import com.cannontech.stars.dr.controlHistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlHistory.model.ControlHistoryStatus;
import com.cannontech.stars.dr.controlHistory.service.ControlHistoryService;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

public class ControlHistoryServiceImpl implements ControlHistoryService {
    private static final Comparator<ControlHistoryEvent> eventComparator;
    
    static {
        
        eventComparator = new Comparator<ControlHistoryEvent>() {
            @Override
            public int compare(ControlHistoryEvent o1, ControlHistoryEvent o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        };
        
    }
    
    @Override
    public boolean containsOnlyNotEnrolledHistory(final List<ControlHistory> controlHistoryList) {
        if (controlHistoryList == null) return false;
        
        for (final ControlHistory controlHistory : controlHistoryList) {
            ControlHistoryStatus status = controlHistory.getCurrentStatus();
            if (!status.equals(ControlHistoryStatus.NOT_ENROLLED)) return false;
        }
        return true;
    }
    
    public Map<Integer, Duration> 
                calculateTotalDuration(final ListMultimap<Integer, ControlHistory> controlHistoryMap) {
        final Map<Integer, Duration> resultMap = Maps.newHashMap();
        
        for (final Integer programId : controlHistoryMap.keySet()) {
            List<ControlHistory> controlHistoryList = controlHistoryMap.get(programId);
            Duration programTotalDuration = Duration.ZERO;
            
            if (controlHistoryList == null) {
                resultMap.put(programId, programTotalDuration);
                continue;
            }
            
            programTotalDuration = calculateTotalDuration(controlHistoryList);
            resultMap.put(programId, programTotalDuration);
        }
        
        return resultMap;
    }
    
    public Duration calculateTotalDuration(final List<ControlHistory> controlHistoryList) {
        Duration programTotalDuration = Duration.ZERO;
        
        final List<ControlHistoryEvent> programEventList = new ArrayList<ControlHistoryEvent>();
        for (final ControlHistory controlHistory : controlHistoryList) {
            programEventList.addAll(controlHistory.getCurrentHistory());
        }
        
        // Sort the Collection by the startDate starting with the first Event.
        Collections.sort(programEventList, eventComparator);

        ReadableInstant lastStartDate = null;
        ReadableInstant lastEndDate = null;
        
        for (final ControlHistoryEvent event : programEventList) {
            final ReadableInstant eventStartDate = event.getStartDate();
            final ReadableInstant eventEndDate = event.getEndDate();
            
            if (lastStartDate == null && lastEndDate == null){
                lastStartDate = eventStartDate;
                lastEndDate = eventEndDate;
            }
            
            // Found time gap, process previous event and reset startDate/endDate.
            if (eventStartDate.isAfter(lastEndDate)) {
                Duration difference =
                    new Duration(lastStartDate,lastEndDate);
                programTotalDuration = programTotalDuration.toDuration().plus(difference);
                
                lastStartDate = eventStartDate;
                lastEndDate = eventEndDate;
            }
            
            // Increasing the endDate for the last Event.
            if (eventEndDate.isAfter(lastEndDate)) {
                lastEndDate = eventEndDate;
            }    
        }
        
        // Process the event that may have been missed in the last iteration.
        if (lastStartDate != null && lastEndDate != null) {
            Duration difference = 
                new Duration(lastStartDate, lastEndDate);
            programTotalDuration = programTotalDuration.toDuration().plus(difference);
        }
        
        return programTotalDuration;
    }
    
}
