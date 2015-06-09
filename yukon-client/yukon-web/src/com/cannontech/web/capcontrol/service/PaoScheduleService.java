package com.cannontech.web.capcontrol.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.pao.PaoScheduleAssignment;

public interface PaoScheduleService {
    
    /**
     * For a given schedule id, 
     * @return a Map of assigned device names to their command names
     * 
     */
    Map<String, Collection<String>> getDeviceToCommandMapForSchedule(int id);

    /**
     * Finds schedule assignments for the given command and/or schedule
     * 
     * @param command - Command name. If null, empty, or "All", no filtering will be done
     * @param schedule - Schedule name. If null, empty, or "All", no filtering will be done
     */
    List<PaoScheduleAssignment> getAssignmentsByFilter(String command, String schedule);

    /**
     * Deletes a schedule by id and sends db change messages for any assigned paos
     */
    void delete(int id);
    
    /**
     * Enumeration of possible statuses when attempting to assign a command
     *
     */
    enum AssignmentStatus { SUCCESS, NO_DEVICES, DUPLICATE, INVALID }
    
    /**
     * Assigns command to schedule for a list of pao ids, and sends out DB updates for each assigned object.
     * @return {@link AssignmentStatus#SUCCESS} when this operation is successful,
     * other {@link AssignmentStatus} values for error reasons. 
     */
    AssignmentStatus assignCommand(int scheduleId, ScheduleCommand cmd, List<Integer> paoIds);
    
    /**
     * Removes an assignment by id (eventId is the primary key for ScheduleAssignment)
     * @param eventId
     * @return
     */
    boolean unassignCommand(int eventId);
    
    /**
     * Sends start command for the provided assignment
     * 
     * @return true for successful excecution, false for error cases
     */
    boolean sendStartCommand(PaoScheduleAssignment assignment, LiteYukonUser user);
    
    /**
     * Sends stop command for the provided assignment
     * 
     * @return true for successful excecution, false for error cases
     */
    boolean sendStop(int deviceId, LiteYukonUser user);
    
    /**
     * Sends out Stop commands for all schedules meeting the filtered criterion.
     * 
     * @param command - Command name. If null, empty, or "All", no filtering will be done
     * @param schedule - Command name. If null, empty, or "All", no filtering will be done
     * @param user
     * @return number of successful commands sent out
     */
    int sendStopCommands(String command, String schedule, LiteYukonUser user);
    
}