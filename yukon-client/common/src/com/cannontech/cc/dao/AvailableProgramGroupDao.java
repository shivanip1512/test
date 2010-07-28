package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;

public interface AvailableProgramGroupDao {
    public List<AvailableProgramGroup> getAllForProgram(Program program);
    public void deleteFor(Group object);
    public void deleteFor(Program object);
    public void save(AvailableProgramGroup object);
    public void delete(AvailableProgramGroup object);
}
