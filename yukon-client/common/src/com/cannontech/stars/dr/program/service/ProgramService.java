package com.cannontech.stars.dr.program.service;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface ProgramService {
    
    public boolean hasProgramAccess(CustomerAccount customerAccount, Program program);

    public boolean hasProgramAccess(CustomerAccount customerAccount, List<Appliance> appliances, Program program);
    
    public Program getByProgramName(String programName, YukonEnergyCompany yukonEnergyCompany) ;    
}
