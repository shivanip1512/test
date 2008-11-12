package com.cannontech.stars.dr.enrollment.service;

import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.user.YukonUserContext;

public interface EnrollmentHelperService {

    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, YukonUserContext yukonUserContext);
    
}
