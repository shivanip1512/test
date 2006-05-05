package com.cannontech.cc.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.AvailableProgramGroupDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.ProgramTypeDao;
import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

public class ProgramService {
    private ProgramDao programDao;
    private ProgramTypeDao programTypeDao;
    private GroupDao groupDao;
    private AvailableProgramGroupDao availableProgramGroupDao;
    private ProgramParameterDao programParameterDao;
    
    public ProgramService() {
        super();
    }

    public ProgramDao getProgramDao() {
        return programDao;
    }

    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    @Transactional
    public List<Program> getProgramList(LiteYukonUser yukonUser) {
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(yukonUser);
        
        List<Program> programs = 
            programDao.getProgramsForEnergyCompany(energyCompany.getEnergyCompanyID());
        
        return programs;
    }
    
    @Transactional
    public List<ProgramType> getProgramTypeList(LiteYukonUser yukonUser) {
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(yukonUser);
        List<ProgramType> programTypes = 
            programTypeDao.getAllProgramTypes(energyCompany.getEnergyCompanyID());
        
        return programTypes;
    }
    
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

    public Program getProgram(Integer programId) {
        return programDao.getForId(programId);
    }
    
    public ProgramTypeDao getProgramTypeDao() {
        return programTypeDao;
    }

    public void setProgramTypeDao(ProgramTypeDao programTypeDao) {
        this.programTypeDao = programTypeDao;
    }

    @Transactional
    public void saveProgram(Program program, 
                            Collection<ProgramParameter> programParameters, 
                            List<Group> assignedGroups) {
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
    }

    public Program createNewProgram(LiteYukonUser user) {
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(user);
        Program newProgram = new Program();
        List<ProgramType> programTypes = 
            programTypeDao.getAllProgramTypes(energyCompany.getEnergyCompanyID());
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

    @Transactional
    public void deleteProgram(Program program) {
        List<AvailableProgramGroup> allForProgram = availableProgramGroupDao.getAllForProgram(program);
        for (AvailableProgramGroup group : allForProgram) {
            availableProgramGroupDao.delete(group);
        }
        programParameterDao.deleteAllForProgram(program);
        programDao.delete(program);
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Transactional
    public void deleteProgramGroup(AvailableProgramGroup toDelete) {
        availableProgramGroupDao.delete(toDelete);
    }

    public AvailableProgramGroupDao getAvailableProgramGroupDao() {
        return availableProgramGroupDao;
    }

    public void setAvailableProgramGroupDao(
            AvailableProgramGroupDao availableProgramGroupDao) {
        this.availableProgramGroupDao = availableProgramGroupDao;
    }

    public List<Program> getProgramList(ProgramType programType) {
        return programDao.getProgramsForType(programType);
    }

    public List<AvailableProgramGroup> getAvailableProgramGroups(Program program) {
        return availableProgramGroupDao.getAllForProgram(program);
    }

    public TimeZone getTimeZone(Program program) {
        int energyCompanyId = program.getProgramType().getEnergyCompanyId();
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(energyCompanyId);
        
        return EnergyCompanyFuncs.getEnergyCompanyTimeZone(energyCompany);
    }
    
    public ProgramParameterDao getProgramParameterDao() {
        return programParameterDao;
    }

    public void setProgramParameterDao(ProgramParameterDao programParameterDao) {
        this.programParameterDao = programParameterDao;
    }

}
