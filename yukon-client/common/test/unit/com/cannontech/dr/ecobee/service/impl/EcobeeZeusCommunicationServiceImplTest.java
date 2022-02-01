package com.cannontech.dr.ecobee.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;

public class EcobeeZeusCommunicationServiceImplTest {
    private static EcobeeZeusCommunicationServiceImpl service = new EcobeeZeusCommunicationServiceImpl();
    
    @Test
    public void test_getSuitableGroupForEnrolment() {
        
        EcobeeZeusGroupService ecobeeZeusGroupService = EasyMock.createMock(EcobeeZeusGroupService.class);
        
        EasyMock.expect(ecobeeZeusGroupService.getDeviceCount("1234")).andStubReturn(4000);
        EasyMock.expect(ecobeeZeusGroupService.getDeviceCount("2345")).andStubReturn(4899);
        EasyMock.expect(ecobeeZeusGroupService.getDeviceCount("3456")).andStubReturn(4900);
        EasyMock.expect(ecobeeZeusGroupService.getDeviceCount("4567")).andStubReturn(5000);
        EasyMock.replay(ecobeeZeusGroupService);
        
        ReflectionTestUtils.setField(service, "ecobeeZeusGroupService", ecobeeZeusGroupService);
        
        List<String> groupIDs= new ArrayList<String>();
        groupIDs.add("1234");
        groupIDs.add("2345");
        assertTrue(ReflectionTestUtils.invokeMethod(service, "getSuitableGroupForEnrollment", groupIDs).equals("1234"), "Group ID must be 1234");
        groupIDs.clear();
        groupIDs.add("3456");
        groupIDs.add("4567");
        assertTrue(StringUtils.isEmpty(ReflectionTestUtils.invokeMethod(service, "getSuitableGroupForEnrollment", groupIDs)), "Group ID must be empty");
        groupIDs.clear();
        groupIDs.add("3456");
        groupIDs.add("4567");
        groupIDs.add("2345");
        assertTrue(ReflectionTestUtils.invokeMethod(service, "getSuitableGroupForEnrollment", groupIDs).equals("2345"), "Group Id must be 2345");
        
    }

    @Test
    public void test_getNewGroupName() {
        assertTrue(ReflectionTestUtils.invokeMethod(service, "getNewGroupName", "1234", 1234, 222).equals("1234_1_222"),
                "Group name must be 1234_1_222");
        assertTrue(ReflectionTestUtils.invokeMethod(service, "getNewGroupName", "1234_1_222", 1234, 222).equals("1234_2_222"),
                "Group name must be 1234_1_222");
        assertTrue(ReflectionTestUtils.invokeMethod(service, "getNewGroupName", "1234_2_222", 1234, 222).equals("1234_3_222"),
                "Group name must be 1234_1_222");
    }

}