package com.cannontech.cc.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.cc.model.CurtailmentProgramType;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ProgramService {

    List<Program> getProgramList(LiteYukonUser yukonUser);

    List<ProgramType> getProgramTypeList(LiteYukonUser yukonUser);

    Set<Group> getUnassignedGroups(Program program);

    Program getProgramById(Integer programId);

    CurtailmentProgramType getProgramType(int programId);

    boolean isEventsExistForProgram(Program program);

    void saveProgram(Program program, Collection<ProgramParameter> programParameters,
                            List<Group> assignedGroups,
                            Set<LiteNotificationGroup> assignedNotificationGroups);

    void deleteProgram(Program program);

    List<Program> getProgramList(ProgramType programType);

    List<AvailableProgramGroup> getAvailableProgramGroups(Program program);

    Set<LiteNotificationGroup> getAssignedNotificationGroups(Program program);

    Set<Group> getAssignedGroups(Program program);

}