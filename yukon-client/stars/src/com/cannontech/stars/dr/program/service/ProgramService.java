package com.cannontech.stars.dr.program.service;

import java.util.List;

import com.cannontech.common.search.SearchResult;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.model.Program;

public interface ProgramService {
    
    public boolean hasProgramAccess(CustomerAccount customerAccount, Program program);

    public boolean hasProgramAccess(CustomerAccount customerAccount, List<Appliance> appliances, Program program);
    
    public Program getByProgramName(String programName, LiteStarsEnergyCompany energyCompany) ;    

    public SearchResult<Program> filterPrograms(Integer applianceCategoryId);
}
