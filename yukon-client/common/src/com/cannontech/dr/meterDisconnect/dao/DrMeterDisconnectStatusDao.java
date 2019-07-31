package com.cannontech.dr.meterDisconnect.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.joda.time.Instant;

import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.dr.meterDisconnect.DrMeterEventStatus;

public interface DrMeterDisconnectStatusDao {
    
    /**
     * Create a new event for the program, or return the ID of an existing event with the specified start and end time.
     * @param startTime Start time for the event.
     * @param expectedEndTime The expected end time for the event, if it runs to completion.
     * @param programPaoId the ID of the program controlled in this event.
     * @return the event ID.
     */
    int createEvent(Instant startTime, Instant expectedEndTime, int programPaoId);
    
    /**
     * Add device entries to the event, with the default initial status of NOT_SENT.
     */
    void addDevicesToEvent(int eventId, Collection<Integer> deviceIds);
    
    /**
     * Update the status of devices in an event. All devices with the specified current status are updated to a new 
     * status.
     * 
     * @param status the new status of the selected devices.
     */
    void updateControlStatus(int eventId, DrMeterControlStatus status, Instant timestamp, 
                             DrMeterControlStatus currentStatus);
    
    /**
     * Update the status of a collection of devices in an event.
     * @param status the new status of the selected devices.
     */
    void updateControlStatus(int eventId, DrMeterControlStatus status, Instant timestamp, Collection<Integer> deviceIds);
    
    /**
     * @return the ID of the active event for the specified program, if there is one.
     */
    Optional<Integer> findActiveEventForProgram(int programId);
    
    /**
     * @return the ID of the active event for the specified device, if there is one.
     */
    Optional<Integer> findActiveEventForDevice(int deviceId);
    
    /**
     * Retrieve the statuses for all devices in the most recent event on the specified program.
     * @param controlStatuses if present, only devices with the specified statuses will be retrieved.
     * @return The list of meter statuses for the event.
     */
    List<DrMeterEventStatus> getAllCurrentStatusForLatestProgramEvent(int programId, 
                                                                      Collection<DrMeterControlStatus> controlStatuses);
    
    /**
     * Retrieve all device statuses that have been reported for the specified event. This includes the complete history
     * of statuses that devices have gone through in this event, not just the current (or last) status.
     * @return The complete historic list of meter statuses for the event.
     */
    List<DrMeterEventStatus> getAllStatusForEvent(int eventId);
    
}
