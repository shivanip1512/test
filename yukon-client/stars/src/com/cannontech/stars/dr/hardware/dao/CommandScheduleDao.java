package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.CommandSchedule;

public interface CommandScheduleDao {
    public CommandSchedule getById(int commandScheduleId);

    public List<CommandSchedule> getAll();

    public List<CommandSchedule> getAllEnabled();

    public void save(CommandSchedule schedule);

    int delete(int scheduleId);

    public void disable(int scheduleId);
    
    public void enable(int scheduleId);
}
