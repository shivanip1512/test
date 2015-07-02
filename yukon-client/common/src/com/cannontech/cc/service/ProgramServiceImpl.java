package com.cannontech.cc.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.AvailableProgramGroupDao;
import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramNotificationGroupDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.ProgramTypeDao;
import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public class ProgramServiceImpl implements ProgramService {
    @Autowired private ProgramDao programDao;
    @Autowired private ProgramTypeDao programTypeDao;
    @Autowired private GroupDao groupDao;
    @Autowired private AvailableProgramGroupDao availableProgramGroupDao;
    @Autowired private ProgramParameterDao programParameterDao;
    @Autowired private BaseEventDao baseEventDao;
    @Autowired private ProgramNotificationGroupDao programNotificationGroupDao;
    @Autowired private EnergyCompanyDao ecDao;
    
    private Comparator<AvailableProgramGroup> onGroup = new Comparator<AvailableProgramGroup>() {
        @Override
        public int compare(AvailableProgramGroup o1, AvailableProgramGroup o2) {
            return o1.getGroup().compareTo(o2.getGroup());
        }
    };
    
    public ProgramServiceImpl() {
        super();
    }


    @Override
    @Transactional
    public List<Program> getProgramList(LiteYukonUser yukonUser) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(yukonUser);

        List<Program> programs =
            programDao.getProgramsForEnergyCompany(energyCompany.getId());

        Comparator<Program> comp = new Comparator<Program>() {
            @Override
            public int compare(Program o1, Program o2) {
                CompareToBuilder builder = new CompareToBuilder();
                builder.append(o1.getProgramType().getName(), o2.getProgramType().getName());
                builder.append(o1, o2);
                return builder.toComparison();
            };
        };
        Collections.sort(programs, comp);
        return programs;
    }

    @Override
    @Transactional
    public List<ProgramType> getProgramTypeList(LiteYukonUser yukonUser) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(yukonUser);
        List<ProgramType> programTypes =
            programTypeDao.getAllProgramTypes(energyCompany.getId());

        return programTypes;
    }

    @Override
    public Set<Group> getUnassignedGroups(Program program) {
        Set<Group> selectedGroups = new HashSet<Group>();
        Set<Group> unassignedGroups = new HashSet<Group>();
        List<AvailableProgramGroup> allForProgram = availableProgramGroupDao.getAllForProgram(program);
        for (AvailableProgramGroup apg : allForProgram) {
            selectedGroups.add(apg.getGroup());
        }
        Integer energyCompanyId = program.getProgramType().getEnergyCompanyId();
        List<Group> groupsForEnergyCompany = groupDao.getGroupsForEnergyCompany(energyCompanyId);
        for (Group group : groupsForEnergyCompany) {
            if (!selectedGroups.contains(group)) {
                unassignedGroups.add(group);
            }
        }
        return unassignedGroups;
    }

    public Set<Group> getAllGroups(Integer energyCompanyId) {
        return new TreeSet<Group>(groupDao.getGroupsForEnergyCompany(energyCompanyId));
    }

    @Override
    public Program getProgram(Integer programId) {
        return programDao.getForId(programId);
    }

    public ProgramTypeDao getProgramTypeDao() {
        return programTypeDao;
    }

    public void setProgramTypeDao(ProgramTypeDao programTypeDao) {
        this.programTypeDao = programTypeDao;
    }

    @Override
    public boolean isEventsExistForProgram(Program program) {
        List<BaseEvent> allForProgram = baseEventDao.getAllForProgram(program);
        boolean result = !allForProgram.isEmpty();
        return result;
    }

    @Override
    @Transactional
    public void saveProgram(Program program,
                            Collection<ProgramParameter> programParameters,
                            List<Group> assignedGroups,
                            Set<LiteNotificationGroup> assignedNotificationGroups) {
        programDao.save(program);
        for (ProgramParameter parameter : programParameters) {
            programParameterDao.save(parameter);
        }
        ArrayList<Group> requiredGroups = new ArrayList<Group>(assignedGroups);
        List<AvailableProgramGroup> existingGroups =
            availableProgramGroupDao.getAllForProgram(program);

        // check for ones that are already in the database
        for (AvailableProgramGroup apg : existingGroups) {
            if (requiredGroups.contains(apg.getGroup())) {
                // currently mapped, remove from list so it's ignored below
                requiredGroups.remove(apg.getGroup());
            } else {
                // currently mapped, but no longer needed
                availableProgramGroupDao.delete(apg);
            }
        }

        // now add the remaining
        for (Group group : requiredGroups) {
            AvailableProgramGroup apg = new AvailableProgramGroup();
            apg.setGroup(group);
            apg.setProgram(program);
            availableProgramGroupDao.save(apg);
        }

        // save notification groups
        programNotificationGroupDao.setNotificationGroupsForProgram(program, assignedNotificationGroups);
    }

    public Program createNewProgram(LiteYukonUser user) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(user);
        Program newProgram = new Program();
        List<ProgramType> programTypes = programTypeDao.getAllProgramTypes(energyCompany.getId());
        // the new program must have some program type assigned
        // for now, default to the first one in the DB
        newProgram.setProgramType(programTypes.get(0));

        //newProgram.setEnergyCompanyId(energyCompany.getEnergyCompanyID());
        return newProgram;
    }

    public void addProgramAndGroup(Program program, Group group) {
        AvailableProgramGroup apg = new AvailableProgramGroup();
        apg.setGroup(group);
        apg.setProgram(program);
        availableProgramGroupDao.save(apg);
    }

    @Override
    @Transactional
    public void deleteProgram(Program program) {
        programDao.delete(program);
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public AvailableProgramGroupDao getAvailableProgramGroupDao() {
        return availableProgramGroupDao;
    }

    public void setAvailableProgramGroupDao(
            AvailableProgramGroupDao availableProgramGroupDao) {
        this.availableProgramGroupDao = availableProgramGroupDao;
    }

    @Override
    public List<Program> getProgramList(ProgramType programType) {
        return programDao.getProgramsForType(programType);
    }

    @Override
    public List<AvailableProgramGroup> getAvailableProgramGroups(Program program) {
        List<AvailableProgramGroup> allForProgram = availableProgramGroupDao.getAllForProgram(program);
        Collections.sort(allForProgram, onGroup);
        return allForProgram;
    }

    @Override
    public Set<LiteNotificationGroup> getAssignedNotificationGroups(Program program) {
        return programNotificationGroupDao.getNotificationGroupsForProgram(program);
    }
}
