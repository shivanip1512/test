package com.cannontech.amr.macsscheduler.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

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

        OverrideRequest startRequest = buildRequest(OverrideRequest.OVERRIDE_START, schedule.getId(), startDate);
        OverrideRequest stopRequest = buildRequest(OverrideRequest.OVERRIDE_STOP, schedule.getId(), stopDate);

        connection.write(startRequest);
        connection.write(stopRequest);
    }

    public void stop(final Schedule schedule, final Date stopDate) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        Validate.notNull(stopDate, "stopDate cannot be null");

        OverrideRequest stopRequest = buildRequest(OverrideRequest.OVERRIDE_STOP, schedule.getId(), stopDate);

        connection.write(stopRequest);
    }

    public Schedule getById(final int scheduleId) throws NotFoundException,IOException {
        connection.sendRetrieveAllSchedules();
        for (final Schedule schedule : connection.retrieveSchedules()) {
            if (schedule.getId() == scheduleId) return schedule;
        }
        throw new NotFoundException("Schedule with ID " + scheduleId + " not found!");
    }

    public List<Schedule> getAll() throws IOException {
        connection.sendRetrieveAllSchedules();
        return Arrays.asList(connection.retrieveSchedules());
    }

    private OverrideRequest buildRequest(final int type, final int scheduleId, final Date date) {
        final OverrideRequest request = new OverrideRequest();
        request.setSchedId(scheduleId);
        request.setAction(type);

        if (type == OverrideRequest.OVERRIDE_START) request.setStart(date);
        if (type == OverrideRequest.OVERRIDE_STOP) request.setStop(date);

        return request;
    }

    public void setConnection(final IMACSConnection connection) {
        this.connection = connection;
    }

}
