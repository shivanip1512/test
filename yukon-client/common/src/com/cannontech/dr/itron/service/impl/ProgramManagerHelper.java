package com.cannontech.dr.itron.service.impl;

import java.util.List;

import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.SetServicePointEnrollmentRequest;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.ServicePointEnrollmentType;

public class ProgramManagerHelper {
    
    public static SetServicePointEnrollmentRequest buildEnrollmentRequest(String accountNumber, List<Long> itronProgramIds) {
        SetServicePointEnrollmentRequest servicePointRequest = new SetServicePointEnrollmentRequest();
        ServicePointEnrollmentType type = new ServicePointEnrollmentType();
        type.setUtilServicePointID(accountNumber);
        type.getProgramIDs().addAll(itronProgramIds);
        servicePointRequest.getEnrolls().add(type);
        return servicePointRequest;
    }
}
