package com.cannontech.web.api.schedules.service;

import com.cannontech.common.schedules.model.HolidaySchedule;

public interface HolidayScheduleService {
    
    /**
     * Create a Holiday schedule
     */
    public HolidaySchedule create(HolidaySchedule holidaySchedule);

}
