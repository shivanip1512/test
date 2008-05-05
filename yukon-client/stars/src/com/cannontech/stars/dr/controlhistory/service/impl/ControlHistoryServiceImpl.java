package com.cannontech.stars.dr.controlhistory.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryStatus;
import com.cannontech.stars.dr.controlhistory.service.ControlHistoryService;

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
    
    public Map<Integer, Integer> calculateTotalDuration(final Map<Integer, List<ControlHistory>> controlHistoryMap) {
        final Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>(controlHistoryMap.size());
        
        for (final Integer programId : controlHistoryMap.keySet()) {
            List<ControlHistory> controlHistoryList = controlHistoryMap.get(programId);
            int programTotalDuration = 0;
            
            if (controlHistoryList == null) {
                resultMap.put(programId, programTotalDuration);
                continue;
            }
            
            programTotalDuration = calculateTotalDuration(controlHistoryList);
            resultMap.put(programId, programTotalDuration);
        }
        
        return resultMap;
    }
    
    public Integer calculateTotalDuration(final List<ControlHistory> controlHistoryList) {
        int programTotalDuration = 0;
        
        final List<ControlHistoryEvent> programEventList = new ArrayList<ControlHistoryEvent>();
        for (final ControlHistory controlHistory : controlHistoryList) {
            programEventList.addAll(controlHistory.getCurrentHistory());
        }
        
        // Sort the Collection by the startDate starting with the first Event.
        Collections.sort(programEventList, eventComparator);

        Date lastStartDate = null;
        Date lastEndDate = null;
        
        for (final ControlHistoryEvent event : programEventList) {
            final Date eventStartDate = event.getStartDate();
            final Date eventEndDate = event.getEndDate();
            
            if (lastStartDate == null && lastEndDate == null){
                lastStartDate = eventStartDate;
                lastEndDate = eventEndDate;
            }
            
            // Found time gap, process previous event and reset startDate/endDate.
            if (eventStartDate.after(lastEndDate)) {
                long diff = getDiffInSeconds(lastStartDate, lastEndDate);
                programTotalDuration += diff;
                
                lastStartDate = eventStartDate;
                lastEndDate = eventEndDate;
            }
            
            // Increasing the endDate for the last Event.
            if (eventEndDate.after(lastEndDate)) {
                lastEndDate = eventEndDate;
            }    
        }
        
        // Process the event that may have been missed in the last iteration.
        if (lastStartDate != null && lastEndDate != null) {
            long diff = getDiffInSeconds(lastStartDate, lastEndDate);
            programTotalDuration += diff;
        }
        
        return programTotalDuration;
    }
    
    private long getDiffInSeconds(Date startDate, Date endDate) {
        long diff = (endDate.getTime() - startDate.getTime()) / 1000;
        return diff;
    }
    
}
