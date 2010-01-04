package com.cannontech.stars.dr.enrollment.service;

import java.util.Date;
import java.util.List;

import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.enrollment.model.EnrolledDevicePrograms;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;

public interface EnrollmentHelperService {

    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user);
    
    /**
     * Return EnrolledDevicePrograms (serial number + list of program names) objects for a given account.
     * Program names are the programs' PAO names.
     * startDate and stopDate are optional, if provided, only those devices enrolled in at least one program within the date range are included.
     * If only the start date is provided, then only those devices enrolled after the provided date up through the current date will be returned. 
     * If only the stop date is provided, then only those devices enrolled from the system origin date up to the provided stop date will be returned. 
     * If no dates are provided (nulls), then all currently enrolled devices will be returned. 
     * @param accountNumber
     * @param startDate
     * @param stopDate
     * @param user
     * @return
     * @throws AccountNotFoundException
     */
    public List<EnrolledDevicePrograms> getEnrolledDeviceProgramsByAccountNumber(String accountNumber, Date startDate, Date stopDate, LiteYukonUser user) throws AccountNotFoundException;
}
