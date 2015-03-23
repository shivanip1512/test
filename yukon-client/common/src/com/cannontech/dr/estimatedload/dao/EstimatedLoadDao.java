package com.cannontech.dr.estimatedload.dao;

import java.util.List;

import com.cannontech.dr.estimatedload.EstimatedLoadApplianceCategoryInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.GearNotFoundException;

public interface EstimatedLoadDao {

    /** Returns the appliance category id and average appliance load value in kW for a given LM program 
     * @throws EstimatedLoadException */
    public EstimatedLoadApplianceCategoryInfo getAcIdAndAverageKwLoadForLmProgram(int lmProgramId)
            throws EstimatedLoadException;

    /** Returns the gear id of a specific gear number for an LM program. 
     * @throws EstimatedLoadException */
    public int getGearIdForProgramAndGearNumber(int lmProgramId, int gearNumber) throws GearNotFoundException;

    /** This method finds the LM program ids of any LM program that also has devices which are enrolled
     * enrolled in the LM program id passed in.  The intention of the method is to find all LM programs that
     * share enrollments with the passed in programId.  This information is used to determine which
     * controlling programs will influence the kW Savings Now estimated load value.
     */
    List<Integer> findOtherEnrolledProgramsForDevicesInProgram(int programId);

    /**
     * This method determines the number of devices which are enrolled in two separate programs. Additionally,
     * a list of previously considered programs can be passed in in order to exclude any devices which have already
     * been considered in previous calculations.
     * 
     * @param calculatingProgramId The program id of the program for which estimated load values are being calculated.
     * @param controllingProgramId The program id of another currently controlling program with overlapping enrollment.
     * @param previousControllingProgramIds A list of previous program ids whose overlapping enrollments have already 
     * been considered and need to be excluded from the current count.
     * @return An integer representing the number of current active overlapping enrollments.
     */
    int getOverlappingEnrollmentSize(int calculatingProgramId, int controllingProgramId,
            List<Integer> previousControllingProgramIds);
}
