package com.cannontech.stars.dr.program.service;

import java.security.InvalidParameterException;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.google.common.collect.ListMultimap;

public interface ProgramEnrollmentService {

    public void removeNonEnrolledPrograms(List<Program> programs, ListMultimap<Integer,ControlHistory> controlHistoryMap);
    
    public boolean isProgramEnrolled(int customerAccountId, int inventoryId, int programId);
    
    public ProgramEnrollmentResultEnum applyEnrollmentRequests(CustomerAccount customerAccount, 
                                                               List<ProgramEnrollment> programEnrollments, 
                                                               LiteYukonUser user);
    
    public List<LiteStarsLMHardware> applyEnrollmentRequests(CustomerAccount customerAccount, 
            List<ProgramEnrollment> programEnrollments, LiteInventoryBase liteInv, LiteYukonUser user) 
            throws WebClientException;

    /**
     * Sends the device configuration for ZigBee devices throught the iDigi rest web service.
     * @param deviceId
     * @throws {@link DigiWebServiceException} {@link InvalidParameterException}
     */
    public void sendZigbeeConfigMessage(int deviceId) throws DigiWebServiceException, InvalidParameterException;
    
}