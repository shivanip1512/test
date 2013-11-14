package com.cannontech.dr.estimatedload.dao;

import java.util.List;

import com.cannontech.dr.estimatedload.EstimatedLoadApplianceCategoryInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadException;

public interface EstimatedLoadDao {

    /** Returns the appliance category id and average appliance load value in kW for a given LM program 
     * @throws EstimatedLoadException */
    public EstimatedLoadApplianceCategoryInfo getAcIdAndAverageKwLoadForLmProgram(int lmProgramId)
            throws EstimatedLoadException;

    /** Returns the gear id of a specific gear number for an LM program. 
     * @throws EstimatedLoadException */
    public Integer getCurrentGearIdForProgram(int lmProgramId, int gearNumber) throws EstimatedLoadException;

    /** This method finds the LM program ids of any LM program that also has devices which are enrolled
     * enrolled in the LM program id passed in.  The intention of the method is to find all LM programs that
     * share enrollments with the passed in programId.  This information is used to determine which
     * controlling programs will influence the kW Savings Now estimated load value.
     */
    List<Integer> findOtherEnrolledProgramsForDevicesInProgram(int programId);
}
