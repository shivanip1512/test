package com.cannontech.stars.dr.program.service;

import java.util.List;

import com.cannontech.common.device.commands.exception.SystemConfigurationException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlHistory.model.ControlHistory;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.google.common.collect.ListMultimap;

public interface ProgramEnrollmentService {

    public void removeNonEnrolledPrograms(List<Program> programs, ListMultimap<Integer,ControlHistory> controlHistoryMap);

    public ProgramEnrollmentResultEnum applyEnrollmentRequests(CustomerAccount customerAccount,
                                                               List<ProgramEnrollment> programEnrollments,
                                                               LiteYukonUser user);

    public List<LiteLmHardwareBase> applyEnrollmentRequests(CustomerAccount customerAccount,
            List<ProgramEnrollment> programEnrollments, LiteInventoryBase liteInv, LiteYukonUser user) throws SystemConfigurationException;


}