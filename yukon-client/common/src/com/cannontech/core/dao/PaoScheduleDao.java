package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;

public interface PaoScheduleDao {
    public PaoScheduleAssignment getScheduleAssignmentByEventId(Integer eventId);

    public List<PaoScheduleAssignment> getAllScheduleAssignments();

    public List<PAOSchedule> getAllPaoScheduleNames();

    public boolean assignCommand(List<PaoScheduleAssignment> param);

    public boolean assignCommand(PaoScheduleAssignment param);

    public boolean unassignCommandByEventId(int eventId);

    public boolean updateAssignment(PaoScheduleAssignment assignment);

    public boolean delete(int scheduleId);

    public boolean deletePaoScheduleAssignmentsByScheduleId(int scheduleId);

    public int add(String name, boolean disabled);
}
