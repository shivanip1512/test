package com.cannontech.stars.dr.program.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramService;

public class ProgramServiceImpl implements ProgramService {
    private ApplianceDao applianceDao;
    
    @Override
    public boolean hasProgramAccess(final CustomerAccount customerAccount, final Program program) {
        final List<Appliance> appliances = applianceDao.getByAccountId(customerAccount.getAccountId());
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
    
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

}
