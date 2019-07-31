package com.cannontech.dr.meterDisconnect.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.joda.time.Instant;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.dr.meterDisconnect.DrMeterEventStatus;

/**
 * Service for logging the control status of disconnect meters in DR events, and retrieving those statuses.
 */
public interface DrMeterDisconnectStatusService {
    
    /**
     * Create a new event for the program, or return the ID of an existing event with the specified start and end time.
     * @param startTime Start time for the event.
     * @param expectedEndTime The expected end time for the event, if it runs to completion.
     * @param programPaoId the ID of the program controlled in this event.
     * @param meters a collection of meters to be tracked in this event.
     * @return the event ID.
     */
    int initializeEvent(Instant startTime, Instant expectedEndTime, int programPaoId, Collection<SimpleDevice> meters);
    
    /**
     * Log a restore being sent for the specified event. This will update the status of all actively controlling meters.
     */
    void restoreSent(Instant restoreTime, int eventId);
    
    /**
     * Update the status of a collection of devices in an event.
     * @param status the new status of the selected devices.
     */
    void updateControlStatus(int eventId, DrMeterControlStatus status, Instant timestamp, Collection<Integer> deviceIds);
    
    /**
     * Update all devices in the event that are waiting for a control response to reflect a timeout occurring.
     */
    void updateAllControlTimeout(int eventId);
    
    /**
     * Update all devices in the event that are waiting for a restore response to reflect a timeout occurring.
     */
    void updateAllRestoreTimeout(int eventId);
    
    /**
     * Retrieve the statuses for all devices in the most recent event on the specified program.
     * @param controlStatuses if present, only devices with the specified statuses will be retrieved.
     * @return The list of meter statuses for the event.
     */
    List<DrMeterEventStatus> getAllCurrentStatusForLatestProgramEvent(int programId,
                                                                      Collection<DrMeterControlStatus> controlStatuses);
    
    /**
     * @return The event ID of the currently active event for the specified program, if there is one.
     */
    Optional<Integer> findActiveEventForProgram(int programId);
    
    /**
     * @return The event ID of the currently active event for the specified device, if there is one.
     */
    Optional<Integer> findActiveEventForDevice(int deviceId);
}
