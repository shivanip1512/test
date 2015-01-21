package com.cannontech.core.schedule.dao;

import java.util.List;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.schedule.model.PaoSchedule;
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
    PaoScheduleAssignment getScheduleAssignmentByEventId(Integer eventId);

    /**
     * Returns the assignments with the given schedule id
     * 
     * @param scheduleId
     * @return list of assignments or empty list if there are none.
     */
    List<PaoScheduleAssignment> getScheduleAssignmentByScheduleId(Integer scheduleId);
    
    List<PaoScheduleAssignment> getAllScheduleAssignments();

    List<PAOSchedule> getAllPaoScheduleNames();

    boolean assignCommand(List<PaoScheduleAssignment> param);

    boolean assignCommand(PaoScheduleAssignment param);

    boolean unassignCommandByEventId(int eventId);

    boolean updateAssignment(PaoScheduleAssignment assignment);

    boolean delete(int scheduleId);

    boolean deletePaoScheduleAssignmentsByScheduleId(int scheduleId);

    int add(String name, boolean disabled);

    /**
     * Checks if there is already a schedule of the same name
     * @return true if there is a conflict
     */
    boolean doesNameExist(String name);
    
    List<PaoSchedule> getAll();

    PaoSchedule getForId(int id);

    PaoSchedule findForName(String name);

    void save(PaoSchedule schedule);

}