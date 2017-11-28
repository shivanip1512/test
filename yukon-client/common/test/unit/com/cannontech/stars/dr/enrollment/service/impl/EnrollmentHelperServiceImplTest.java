package com.cannontech.stars.dr.enrollment.service.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public class EnrollmentHelperServiceImplTest {

    private EnrollmentHelperEndpointServiceMock enrollmentHelperService;
    
    @Test
    public void testAddProgramEnrollment() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();
        
        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(10, 20, false, 34, 40, 50, 1);
        List<ProgramEnrollment> programEnrollments = 
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, true);

        Assert.assertEquals(5, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollment));
    }
    
    @Test
    public void testAddProgramEnrollmentUpdateLoadGroup() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();
        
        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(11, 0, false, 31, 47, 51, 0);
        ProgramEnrollment newProgramEnrollmentTest = new ProgramEnrollment(11, 0, true, 31, 47, 51, 0);
        List<ProgramEnrollment> programEnrollments = 
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, true);

        Assert.assertEquals(4, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollmentTest));
        Assert.assertEquals(47, programEnrollments.get(1).getLmGroupId());
        Assert.assertEquals(41, programEnrollments.get(3).getLmGroupId());
    }

    @Test
    public void testAddProgramEnrollmentUpdateLoadGroupGre() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();
        
        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(11, 0, false, 31, 47, 51, 0);
        ProgramEnrollment newProgramEnrollmentTest = new ProgramEnrollment(11, 0, true, 31, 47, 51, 0);
        List<ProgramEnrollment> programEnrollments = 
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, true);

        Assert.assertEquals(4, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollmentTest));
        Assert.assertEquals(47, programEnrollments.get(1).getLmGroupId());
        Assert.assertEquals(41, programEnrollments.get(3).getLmGroupId());
    }
    
    @Test
    public void testAddProgramEnrollmentUpdateLoadProgram() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();
        
        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(11, 42, false, 31, 47, 53, 0);
        ProgramEnrollment newProgramEnrollmentTest = new ProgramEnrollment(11, 42, true, 31, 47, 53, 0);
        List<ProgramEnrollment> programEnrollments = 
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, true);

        Assert.assertEquals(4, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollmentTest));
        Assert.assertEquals(53, programEnrollments.get(1).getAssignedProgramId());
        Assert.assertEquals(47, programEnrollments.get(1).getLmGroupId());
    }
    
    @Test
    public void testAddProgramEnrollmentWithSeasonLoad() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();
        
        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(10, 20, false, 31, 46, 55, 0);
        List<ProgramEnrollment> programEnrollments = 
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, true, true);

        Assert.assertEquals(5, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollment));
        Assert.assertTrue(programEnrollments.contains(enrollmentHelperService.getEnrollmentTwo()));
    }
 
    @Test
    public void testRemoveProgramEnrollment() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();
        
        ProgramEnrollment removedProgramEnrollment = new ProgramEnrollment(11, 0, true, 31, 41, 51, 0);
        List<ProgramEnrollment> programEnrollments = 
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.removeProgramEnrollment(programEnrollments, removedProgramEnrollment);

        Assert.assertEquals(3, programEnrollments.size());
        Assert.assertFalse(programEnrollments.contains(removedProgramEnrollment));
    }
    
    @Test
    public void testRemoveProgramEnrollmentNotFound() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();
        
        ProgramEnrollment removedProgramEnrollment = new ProgramEnrollment(13, 0, true, 31, 41, 51, 0);
        List<ProgramEnrollment> programEnrollments = 
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        try {
            enrollmentHelperService.removeProgramEnrollment(programEnrollments, removedProgramEnrollment);
        } catch (NotFoundException nfe) {
            return;
        }
        Assert.fail();
    }

    /**
     * In this case , considering different appliance category (isMultipleProgramsPerCategoryAllowed = false)
     */
    @Test
    public void testAddProgramEnrollmentCase_1() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();

        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(12, 20, false, 34, 40, 50, 1);
        List<ProgramEnrollment> programEnrollments =
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, false);

        Assert.assertEquals(5, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollment));
    }

    /**
     * In this case , considering same appliance category, different program, same relay
     * (isMultipleProgramsPerCategoryAllowed = false)
     */

    @Test
    public void testAddProgramEnrollmentCase_2() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();

        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(10, 20, false, 30, 100, 55, 1);
        List<ProgramEnrollment> programEnrollments =
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, false);

        Assert.assertEquals(4, programEnrollments.size());
        Assert.assertEquals(100, programEnrollments.get(0).getLmGroupId());
    }
    
    /**
     * In this case , considering same appliance category, different program, different relay
     * (isMultipleProgramsPerCategoryAllowed = false)
     */

    @Test
    public void testAddProgramEnrollmentCase_3() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();

        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(10, 20, false, 30, 100, 55, 2);
        List<ProgramEnrollment> programEnrollments =
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, false);

        Assert.assertEquals(4, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollment));
    }

    /**
     * In this case , considering same appliance category, same program and different inventory
     * (isMultipleProgramsPerCategoryAllowed = false)
     */

    @Test
    public void testAddProgramEnrollmentCase_4() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();

        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(10, 20, false, 34, 100, 50, 1);
        List<ProgramEnrollment> programEnrollments =
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, false);

        Assert.assertEquals(5, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollment));
    }
    
    /**
     * In this case , considering same appliance category, different program and different inventory
     * (isMultipleProgramsPerCategoryAllowed = false)
     */

    @Test
    public void testAddProgramEnrollmentCase_5() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();

        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(10, 20, false, 34, 40, 67, 1);
        List<ProgramEnrollment> programEnrollments =
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, false);

        Assert.assertEquals(5, programEnrollments.size());
        Assert.assertTrue(programEnrollments.contains(newProgramEnrollment));
    }
    

    /**
     * In this case , considering same appliance category, same program and same inventory
     * It will update load group name & relay (isMultipleProgramsPerCategoryAllowed = false)
     */

    @Test
    public void testAddProgramEnrollmentCase_6() throws Exception {

        enrollmentHelperService = new EnrollmentHelperEndpointServiceMock();

        ProgramEnrollment newProgramEnrollment = new ProgramEnrollment(10, 20, false, 30, 41, 50, 3);
        List<ProgramEnrollment> programEnrollments =
            enrollmentHelperService.enrollmentDao.getActiveEnrollmentsByAccountId(0);

        enrollmentHelperService.addProgramEnrollment(programEnrollments, newProgramEnrollment, false, false);

        Assert.assertEquals(4, programEnrollments.size());
        Assert.assertEquals(41, programEnrollments.get(0).getLmGroupId());
        Assert.assertEquals(3, programEnrollments.get(0).getRelay());
    }

}

