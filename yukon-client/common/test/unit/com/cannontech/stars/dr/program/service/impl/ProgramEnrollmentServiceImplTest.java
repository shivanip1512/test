package com.cannontech.stars.dr.program.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public class ProgramEnrollmentServiceImplTest {
    private static ProgramEnrollmentServiceImpl service;

    @Test
    public void test_isUnenrollment() {
        service = new ProgramEnrollmentServiceImpl();
        List<ProgramEnrollment> originalEnrollments = new ArrayList<ProgramEnrollment>();
        List<ProgramEnrollment> newRequests = new ArrayList<ProgramEnrollment>();

        ProgramEnrollment enrollmentOne = new ProgramEnrollment();
        enrollmentOne.setInventoryId(1);
        ProgramEnrollment enrollmentTwo = new ProgramEnrollment();
        enrollmentTwo.setInventoryId(2);
        ProgramEnrollment enrollmentThree = new ProgramEnrollment();
        enrollmentThree.setInventoryId(3);
        ProgramEnrollment enrollmentFour = new ProgramEnrollment();
        enrollmentFour.setInventoryId(4);

        // Enrollment flow
        originalEnrollments.add(enrollmentOne);
        originalEnrollments.add(enrollmentTwo);
        originalEnrollments.add(enrollmentThree);
        newRequests.add(enrollmentFour);
        assertFalse(ReflectionTestUtils.invokeMethod(service, "isUnenrollment", originalEnrollments, newRequests, 4));

        // Config flow
        newRequests.add(enrollmentOne);
        newRequests.add(enrollmentTwo);
        newRequests.add(enrollmentThree);
        originalEnrollments.add(enrollmentFour);
        assertFalse(ReflectionTestUtils.invokeMethod(service, "isUnenrollment", originalEnrollments, newRequests, 4));

        // Unenrollment flow 1
        newRequests.clear();
        originalEnrollments.clear();
        originalEnrollments.add(enrollmentOne);
        originalEnrollments.add(enrollmentTwo);
        originalEnrollments.add(enrollmentThree);
        newRequests.add(enrollmentOne);
        newRequests.add(enrollmentTwo);
        assertTrue(ReflectionTestUtils.invokeMethod(service, "isUnenrollment", originalEnrollments, newRequests, 3));

        // unenrollment flow 2 : Multiple program
        originalEnrollments.add(enrollmentThree);
        newRequests.add(enrollmentThree);
        assertTrue(ReflectionTestUtils.invokeMethod(service, "isUnenrollment", originalEnrollments, newRequests, 3));

    }

}
