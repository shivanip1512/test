package com.cannontech.stars.dr.program.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public class ProgramEnrollmentServiceImplTest {
    private static ProgramEnrollmentServiceImpl service = new ProgramEnrollmentServiceImpl();

    @Test
    public void test_isUnenrollment() {
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
        assertFalse((boolean)ReflectionTestUtils.invokeMethod(service, "isUnenrollment", originalEnrollments, newRequests, 4));

        // Config flow
        newRequests.add(enrollmentOne);
        newRequests.add(enrollmentTwo);
        newRequests.add(enrollmentThree);
        originalEnrollments.add(enrollmentFour);
        assertFalse((boolean)ReflectionTestUtils.invokeMethod(service, "isUnenrollment", originalEnrollments, newRequests, 4));

        // Unenrollment flow 1
        newRequests.clear();
        originalEnrollments.clear();
        originalEnrollments.add(enrollmentOne);
        originalEnrollments.add(enrollmentTwo);
        originalEnrollments.add(enrollmentThree);
        newRequests.add(enrollmentOne);
        newRequests.add(enrollmentTwo);
        assertTrue((boolean)ReflectionTestUtils.invokeMethod(service, "isUnenrollment", originalEnrollments, newRequests, 3));

        // unenrollment flow 2 : Multiple program
        originalEnrollments.add(enrollmentThree);
        newRequests.add(enrollmentThree);
        assertTrue((boolean)ReflectionTestUtils.invokeMethod(service, "isUnenrollment", originalEnrollments, newRequests, 3));

    }

    @Test
    public void test_checkDeviceStatus() {
        InventoryBaseDao inventoryBaseDao = EasyMock.createMock(InventoryBaseDao.class);
        EasyMock.expect(inventoryBaseDao.getDeviceStatus(1)).andStubReturn(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL);
        EasyMock.expect(inventoryBaseDao.getDeviceStatus(2)).andStubReturn(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL);
        EasyMock.replay(inventoryBaseDao);
        ReflectionTestUtils.setField(service, "inventoryBaseDao", inventoryBaseDao);
        Arrays.stream(HardwareType.values()).filter(HardwareType::isEnrollable).forEach(type -> {
            if (type.isItron() || type.isEcobee() || type.isHoneywell()) {
                assertTrue((boolean) ReflectionTestUtils.invokeMethod(service, "checkDeviceStatus", 1, type));
                assertTrue((boolean) ReflectionTestUtils.invokeMethod(service, "checkDeviceStatus", 2, type));
            } else {
                assertTrue((boolean) ReflectionTestUtils.invokeMethod(service, "checkDeviceStatus", 1, type));
                assertFalse((boolean) ReflectionTestUtils.invokeMethod(service, "checkDeviceStatus", 2, type));
            }
        });
    }

}
