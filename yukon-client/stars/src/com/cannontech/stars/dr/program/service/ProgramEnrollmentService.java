package com.cannontech.stars.dr.program.service;

import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResult;
import com.cannontech.user.YukonUserContext;

public interface ProgramEnrollmentService {

    public void removeNonEnrolledPrograms(List<Program> programs, Map<Integer,List<ControlHistory>> controlHistoryMap);
    
    public boolean isProgramEnrolled(int customerAccountId, int inventoryId, int programId);
    
    public ProgramEnrollmentResult applyEnrollmentRequests(CustomerAccount customerAccount, 
            List<ProgramEnrollmentRequest> requests, YukonUserContext yukonUserContext);
    
}
