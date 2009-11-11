package com.cannontech.dr.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.dr.service.DemandResponseService;

public class DemandResponseServiceImpl implements DemandResponseService {
    private SystemDateFormattingService systemDateFormattingService;

    @Override
    public int getTimeSlotsForTargetCycle(Date stopTime, Date startTime) {
        Calendar stopCal = systemDateFormattingService.getSystemCalendar();
        stopCal.setTime(stopTime);
        stopCal.set(Calendar.MINUTE, 0);
        stopCal.set(Calendar.SECOND, 0);
        stopCal.set(Calendar.MILLISECOND, 0);
        Calendar startCal = systemDateFormattingService.getSystemCalendar();
        startCal.setTime (startTime);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        int timeSlots = (int) ((stopCal.getTimeInMillis()
                - startCal.getTimeInMillis())/(60*60*1000)) + 1;

        // see if we roll over on the last hour
        Calendar tempCal = systemDateFormattingService.getSystemCalendar();
        tempCal.setTime(stopTime);
        int newHr = tempCal.get(Calendar.HOUR_OF_DAY);
        if (newHr > stopCal.get(Calendar.HOUR_OF_DAY)) {
            timeSlots++;
        }
        return Math.min(timeSlots, 24);
    }

    @Autowired
    public void setSystemDateFormattingService(
            SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }
}
