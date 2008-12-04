package com.cannontech.yukon.api.stars;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.impl.EnrollmentHelperServiceImpl;
import com.cannontech.user.YukonUserContext;

class EnrollmentHelperEndpointServiceMock extends EnrollmentHelperServiceImpl {

    @Override
    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, YukonUserContext yukonUserContext) {
        doEnrollment(enrollmentHelper.getProgramName());
    }
    
    private void doEnrollment(String programEnrollmentItem) {
        if (programEnrollmentItem.equalsIgnoreCase("NOT_FOUND")){
            throw new NotFoundException("The program name supplied does not exist.");
        }
        if (programEnrollmentItem.equalsIgnoreCase("ILLEGAL_ARGUMENT")){
            throw new NotFoundException("The load group supplied does not belong to the program supplied.");
        }
        if (programEnrollmentItem.equalsIgnoreCase("DUPLICATE_ENROLLMENT")){
            throw new NotFoundException("The enrollment name supplied causes a duplicate enrollment.");
        }
    }
}