package com.cannontech.stars.dr.enrollment.dao;

import java.util.List;

import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public interface EnrollmentDao {

    public List<ProgramEnrollment> getActiveEnrollmentsByAccountId(int accountId);

    /**
     * Method to get a list of all the programs a device is currently enrolled in
     * @param inventoryId - Inventory in question
     * @return List of enrolled programs
     */
    public List<Program> getCurrentlyEnrolledProgramsByInventoryId(int inventoryId);
        
}