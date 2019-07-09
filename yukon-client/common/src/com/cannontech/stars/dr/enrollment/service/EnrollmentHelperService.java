package com.cannontech.stars.dr.enrollment.service;

import java.util.Date;
import java.util.List;

import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.model.EnrolledDevicePrograms;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEventLoggingData;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelperHolder;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.user.YukonUserContext;

public interface EnrollmentHelperService {
    /**
     * Update the program enrollments to match the given list for the given
     * account. This list is considered to be a list of updates so any
     * enrollments not in this list will be left alone.
     * <p>
     * It is acceptable (and common) to have an enrollment in this list which is
     * already in place and unchanged. For that enrollment, no action will take
     * place.
     */
    public void updateProgramEnrollments(
            List<ProgramEnrollment> programEnrollments, int accountId,
            YukonUserContext userContext);

    public EnrollmentEventLoggingData getEventLoggingData(ProgramEnrollment programEnrollment);

	public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user);

    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user,
            CustomerAccount ca);

    public void doEnrollment(EnrollmentHelperHolder enrollmentHelperHolder, EnrollmentEnum enrollmentEnum, LiteYukonUser user);

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

    /**
     * Find all meters on the account and prepare them for enrollment by adding the LMHardwareBase table
     * if it does not already exist.
     */
    void makeDisconnectMetersOnAccountEnrollable(int accountId);
}
