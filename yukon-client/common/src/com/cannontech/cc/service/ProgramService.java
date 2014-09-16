package com.cannontech.cc.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ProgramService {


    @Transactional
    public List<Program> getProgramList(LiteYukonUser yukonUser);

    @Transactional
    public List<ProgramType> getProgramTypeList(LiteYukonUser yukonUser);

    public Set<Group> getUnassignedGroups(Program program);

    public Program getProgram(Integer programId);

    public boolean isEventsExistForProgram(Program program);

    @Transactional
    public void saveProgram(Program program, Collection<ProgramParameter> programParameters,
                            List<Group> assignedGroups,
                            Set<LiteNotificationGroup> assignedNotificationGroups);

    @Transactional
    public void deleteProgram(Program program);

    public List<Program> getProgramList(ProgramType programType);

    public List<AvailableProgramGroup> getAvailableProgramGroups(Program program);

    public Set<LiteNotificationGroup> getAssignedNotificationGroups(Program program);

}