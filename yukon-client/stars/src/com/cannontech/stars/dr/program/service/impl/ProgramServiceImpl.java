package com.cannontech.stars.dr.program.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class ProgramServiceImpl implements ProgramService {
    private ApplianceDao applianceDao;
    private ProgramDao programDao;

    @Override
    public boolean hasProgramAccess(final CustomerAccount customerAccount, final Program program) {
        final List<Appliance> appliances = applianceDao.getAssignedAppliancesByAccountId(customerAccount.getAccountId());
        return hasProgramAccess(customerAccount, appliances, program);
    }
    
    @Override
    public boolean hasProgramAccess(final CustomerAccount customerAccount, final List<Appliance> appliances,
        final Program program) {
        
        final int programId = program.getProgramId();
        
        for (final Appliance appliance : appliances) {
            int applianceProgramId = appliance.getProgramId(); 
            if (programId == applianceProgramId) return true;
        }
        
        return false;
    }
    
    @Override
    public Program getByProgramName(String programName, YukonEnergyCompany yukonEnergyCompany) {
        
        Program program = null;
        try {
            program = programDao.getByProgramName(programName, yukonEnergyCompany.getEnergyCompanyId());
        } catch (ProgramNotFoundException e) {
            /*
             * Since we couldn't find the program by the program name lets try
             * finding the program by its alternate name.
             */
            program = programDao.getByAlternateProgramName(programName, yukonEnergyCompany.getEnergyCompanyId());
        }
        return program;
    }

    // DI Setters
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
}
