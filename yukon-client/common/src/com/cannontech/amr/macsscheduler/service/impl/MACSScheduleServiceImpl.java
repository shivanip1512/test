package com.cannontech.amr.macsscheduler.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.messaging.message.macs.OverrideRequestMessage;
import com.cannontech.messaging.message.macs.ScheduleMessage;
import com.cannontech.yukon.IMACSConnection;

public class MACSScheduleServiceImpl implements MACSScheduleService<ScheduleMessage> {
    private IMACSConnection connection;

    public void start(final ScheduleMessage schedule, final Date startDate, final Date stopDate) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        Validate.notNull(startDate, "startDate cannot be null");
        Validate.notNull(stopDate, "stopDate cannot be null");

        if (startDate.after(stopDate)) {
            throw new java.lang.IllegalArgumentException("startDate cannot be after stopDate");
        }

        schedule.setUpdatingState(true);
        connection.sendStartStopSchedule(schedule, startDate, stopDate, OverrideRequestMessage.OVERRIDE_START);
    }

    public void stop(final ScheduleMessage schedule, final Date stopDate) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        Validate.notNull(stopDate, "stopDate cannot be null");

        schedule.setUpdatingState(true);
        connection.sendStartStopSchedule(schedule, stopDate, stopDate, OverrideRequestMessage.OVERRIDE_STOP);
    }
    
    public void enable(final ScheduleMessage schedule) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        
        schedule.setUpdatingState(true);
        connection.sendEnableDisableSchedule(schedule);
    }
    
    public void disable(final ScheduleMessage schedule) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        
        schedule.setUpdatingState(true);
        connection.sendEnableDisableSchedule(schedule);
    }

    public ScheduleMessage getById(final int scheduleId) throws NotFoundException,IOException {
        for (final ScheduleMessage schedule : connection.retrieveSchedules()) {
            if (schedule.getId() == scheduleId) return schedule;
        }
        throw new NotFoundException("Schedule with ID " + scheduleId + " not found!");
    }

    public List<ScheduleMessage> getAll() throws IOException {
        return Arrays.asList(connection.retrieveSchedules());
    }

    public void setConnection(final IMACSConnection connection) {
        this.connection = connection;
    }

}
