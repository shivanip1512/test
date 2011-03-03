package com.cannontech.stars.dr.displayable.dao;

import java.util.List;

import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;

public interface DisplayableEnrollmentDao {

    /**
     * Get a list of all DisplayableEnrollment for a given customer. An instance
     * of "DisplayableEnrollment" is basically an appliance category with
     * customer specific enrollment information included.
     * @param accountId The customer account id.
     * @return the list of possible enrollments
     */
    public List<DisplayableEnrollment> find(int accountId);

    /**
     * Get a list of all programs (instances of "DisplayableEnrollmentProgram"
     * which include customer specific enrollment information) which a given
     * customer is enrolled in.
     * @param accountId The account id of the customer to check.
     * @return The list of enrolled programs.
     */
    public List<DisplayableEnrollmentProgram> findEnrolledPrograms(int accountId);

    /**
     * Get a specific program (which may or may not be enrolled) given a
     * customer and program id.
     * @param accountId the account id for which enrollment information is to be
     *            retrieved
     * @param assignedProgramId the assigned (STARS) program id
     * @return the enrollment program
     */
    public DisplayableEnrollmentProgram getProgram(int accountId, int programId);

    /**
     * Returns the DisplayableEnrollmentProgram with filtered inventory list based on the programs pao type
     */
    public DisplayableEnrollmentProgram getFilteredDisplayableEnrollmentProgram(int accountId, int programId);
    
}