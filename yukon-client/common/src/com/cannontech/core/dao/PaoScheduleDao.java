package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;

public interface PaoScheduleDao {
    
    /**
     * Returns the assignment with the given event id
     * 
     * @param eventId
     * @throws NotFoundException if the command was not found.
     * @return
     */
    public PaoScheduleAssignment getScheduleAssignmentByEventId(Integer eventId);

    /**
     * Returns the assignments with the given schedule id
     * 
     * @param scheduleId
     * @return list of assignments or empty list if there are none.
     */
    public List<PaoScheduleAssignment> getScheduleAssignmentByScheduleId(Integer scheduleId);
    
    public List<PaoScheduleAssignment> getAllScheduleAssignments();

    public List<PAOSchedule> getAllPaoScheduleNames();

    public boolean assignCommand(List<PaoScheduleAssignment> param);

    public boolean assignCommand(PaoScheduleAssignment param);

    public boolean unassignCommandByEventId(int eventId);

    public boolean updateAssignment(PaoScheduleAssignment assignment);

    public boolean delete(int scheduleId);

    public boolean deletePaoScheduleAssignmentsByScheduleId(int scheduleId);

    public int add(String name, boolean disabled);

    /**
     * Checks if there is already a schedule of the same name
     * @return true if there is a conflict
     */
    public boolean isUniqueName(String name);
}
