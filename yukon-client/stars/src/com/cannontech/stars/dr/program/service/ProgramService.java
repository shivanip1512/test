package com.cannontech.stars.dr.program.service;

import java.util.List;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.model.Program;

public interface ProgramService {
    
    public boolean hasProgramAccess(CustomerAccount customerAccount, Program program);

    public boolean hasProgramAccess(CustomerAccount customerAccount, List<Appliance> appliances, Program program);
    
}
