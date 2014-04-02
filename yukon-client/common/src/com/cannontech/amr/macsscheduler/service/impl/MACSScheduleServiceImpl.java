package com.cannontech.amr.macsscheduler.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.macs.message.OverrideRequest;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.yukon.IMACSConnection;

public class MACSScheduleServiceImpl implements MACSScheduleService<Schedule> {
    private IMACSConnection connection;

    public void start(final Schedule schedule, final Date startDate, final Date stopDate) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        Validate.notNull(startDate, "startDate cannot be null");
        Validate.notNull(stopDate, "stopDate cannot be null");

        if (startDate.after(stopDate)) {
            throw new java.lang.IllegalArgumentException("startDate cannot be after stopDate");
        }

        schedule.setUpdatingState(true);
        connection.sendStartStopSchedule(schedule, startDate, stopDate, OverrideRequest.OVERRIDE_START);
    }

    public void stop(final Schedule schedule, final Date stopDate) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        Validate.notNull(stopDate, "stopDate cannot be null");

        schedule.setUpdatingState(true);
        connection.sendStartStopSchedule(schedule, stopDate, stopDate, OverrideRequest.OVERRIDE_STOP);
    }
    
    public void enable(final Schedule schedule) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        
        schedule.setUpdatingState(true);
        connection.sendEnableDisableSchedule(schedule);
    }
    
    public void disable(final Schedule schedule) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        
        schedule.setUpdatingState(true);
        connection.sendEnableDisableSchedule(schedule);
    }

    public Schedule getById(final int scheduleId) throws NotFoundException,IOException {
        for (final Schedule schedule : connection.retrieveSchedules()) {
            if (schedule.getId() == scheduleId) return schedule;
        }
        throw new NotFoundException("Schedule with ID " + scheduleId + " not found!");
    }

    public List<Schedule> getAll() throws IOException {
        return Arrays.asList(connection.retrieveSchedules());
    }

    public void setConnection(final IMACSConnection connection) {
        this.connection = connection;
    }

}
