package com.cannontech.stars.dr.enrollment.dao;

import java.util.List;

import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public interface EnrollmentDao {

    public List<ProgramEnrollment> getActiveEnrollmentsByAccountId(int accountId);
        
}