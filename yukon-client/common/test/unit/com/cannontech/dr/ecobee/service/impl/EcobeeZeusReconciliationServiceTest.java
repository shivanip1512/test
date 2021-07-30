package com.cannontech.dr.ecobee.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeZeusGroupDeviceMapping;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public class EcobeeZeusReconciliationServiceTest {
    List<EcobeeZeusDiscrepancy> errorsList = new ArrayList<EcobeeZeusDiscrepancy>();
    EcobeeZeusReconciliationService ecobeeZeusReconciliationService;
    List<EcobeeZeusGroupDeviceMapping> ecobeeGroupDeviceMapping;

    @BeforeEach
    public void setup() {
        errorsList.clear();
        ecobeeZeusReconciliationService = new EcobeeZeusReconciliationServiceImpl();
        ecobeeGroupDeviceMapping = new ArrayList<EcobeeZeusGroupDeviceMapping>();
    }

    @Test
    public void test_checkForParentGroupId() {
        // Number of parent group Id should be exactly 1.
        ecobeeGroupDeviceMapping = new ArrayList<EcobeeZeusGroupDeviceMapping>();
        EcobeeZeusGroupDeviceMapping mapping0 = new EcobeeZeusGroupDeviceMapping("G0", new ArrayList<String>(Arrays.asList()));
        mapping0.setParentGroupId("1");
        ecobeeGroupDeviceMapping.add(mapping0);
        List<String> yukonGroupIds = new ArrayList<String>();
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousGroup",
                ecobeeGroupDeviceMapping, yukonGroupIds, errorsList);
        assertTrue(errorsList.size() == 0, "No root group found for the mapping");
    }

    @Test
    public void test_NoChildGroupsAndNoYukonGroups() {
        ecobeeGroupDeviceMapping = new ArrayList<EcobeeZeusGroupDeviceMapping>();
        EcobeeZeusGroupDeviceMapping mapping0 = new EcobeeZeusGroupDeviceMapping("G0", new ArrayList<String>(Arrays.asList()));
        ecobeeGroupDeviceMapping.add(mapping0);
        List<String> yukonGroupIds = new ArrayList<String>();
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousGroup",
                ecobeeGroupDeviceMapping, yukonGroupIds, errorsList);
        assertTrue(errorsList.size() == 0, "Both Yukon and Ecobee has no groups.");
    }

    @Test
    public void test_checkForMissingGroup() {

        // Missing group should happen when Yukon has groups but Ecobee doesn't have.
        ecobeeGroupDeviceMapping = new ArrayList<EcobeeZeusGroupDeviceMapping>();
        EcobeeZeusGroupDeviceMapping mapping0 = new EcobeeZeusGroupDeviceMapping("G0", new ArrayList<String>(Arrays.asList()));
        ecobeeGroupDeviceMapping.add(mapping0);
        EcobeeZeusGroupDeviceMapping mapping1 = new EcobeeZeusGroupDeviceMapping("G1", new ArrayList<String>(Arrays.asList()));
        mapping1.setParentGroupId("G0");
        ecobeeGroupDeviceMapping.add(mapping1);

        List<String> yukonGroupIds = new ArrayList<>(Arrays.asList("G1", "G2"));

        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousGroup",
                ecobeeGroupDeviceMapping, yukonGroupIds, errorsList);
        assertTrue(errorsList.size() == 1, "Number of missing group should be 1");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_GROUP, "Type should be MISSING_GROUP");
            assertTrue(error.getCorrectPath().equals("G2"), "Missing group should be G2");
        });

        errorsList.clear();
        yukonGroupIds.add("G3");
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousGroup",
                ecobeeGroupDeviceMapping, yukonGroupIds, errorsList);
        assertTrue(errorsList.size() == 2, "Number of missing group should be 2");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_GROUP, "Type should be MISSING_GROUP");
            assertTrue(error.getCorrectPath().equals("G2") || error.getCorrectPath().equals("G3"),
                    "Missing group should be G2 or G3");
        });
    }

    @Test
    public void test_checkForExtraneousGroup() {
        // Extraneous group should happen when Ecobee has groups but Yukon doesn't have.
        ecobeeGroupDeviceMapping = new ArrayList<EcobeeZeusGroupDeviceMapping>();
        EcobeeZeusGroupDeviceMapping mapping0 = new EcobeeZeusGroupDeviceMapping("G0", new ArrayList<String>(Arrays.asList()));
        ecobeeGroupDeviceMapping.add(mapping0);
        EcobeeZeusGroupDeviceMapping mapping1 = new EcobeeZeusGroupDeviceMapping("G1", new ArrayList<String>(Arrays.asList()));
        mapping1.setParentGroupId("G0");
        ecobeeGroupDeviceMapping.add(mapping1);
        EcobeeZeusGroupDeviceMapping mapping2 = new EcobeeZeusGroupDeviceMapping("G2", new ArrayList<String>(Arrays.asList()));
        mapping2.setParentGroupId("G0");
        ecobeeGroupDeviceMapping.add(mapping2);

        List<String> yukonGroupIds = new ArrayList<>(Arrays.asList("G1"));

        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousGroup",
                ecobeeGroupDeviceMapping, yukonGroupIds, errorsList);
        assertTrue(errorsList.size() == 1, "Number of Extraneous group should be 1");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP, "Type should be EXTRANEOUS_GROUP");
            assertTrue(error.getCurrentPath().equals("G2"), "Extraneous group should be G2");
        });

        errorsList.clear();
        EcobeeZeusGroupDeviceMapping mapping3 = new EcobeeZeusGroupDeviceMapping("G3", new ArrayList<String>(Arrays.asList()));
        mapping3.setParentGroupId("G0");
        ecobeeGroupDeviceMapping.add(mapping3);
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousGroup",
                ecobeeGroupDeviceMapping, yukonGroupIds, errorsList);
        assertTrue(errorsList.size() == 2, "Number of Extraneous group should be 2");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP, "Type should be EXTRANEOUS_GROUP");
            assertTrue(error.getCurrentPath().equals("G2") || error.getCurrentPath().equals("G3"),
                    "Extraneous group should be G2 or G3");
        });
    }

    @Test
    public void test_checkForMissingDevices() {
        List<String> yukonSerialNumbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
        Set<String> ecobeeSerialNumbers = new HashSet<String>(Arrays.asList("1", "2", "3", "4", "5"));

        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumbers, errorsList);
        assertTrue(errorsList.size() == 0, "Number of missing device should be 0");

        yukonSerialNumbers.add("6");
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumbers, errorsList);
        assertTrue(errorsList.size() == 1, "Number of missing device should be 1");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_DEVICE, "Type should be MISSING_DEVICE");
            assertTrue(error.getSerialNumber().equals("6"), "Missing device should be 6");
        });
    }

    public void test_checkForExtraneousDevices() {
        List<String> yukonSerialNumbers = new ArrayList<>(Arrays.asList("1", "2", "3"));
        Set<String> ecobeeSerialNumbers = new HashSet<String>(Arrays.asList("1", "2", "3", "4", "5"));

        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumbers, errorsList);
        assertTrue(errorsList.size() == 2, "Number of missing device should be 2");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_DEVICE, "Type should be MISSING_DEVICE");
            assertTrue(error.getSerialNumber().equals("4") || error.getSerialNumber().equals("5"),
                    "Missing device should be 4 or 5");
        });
    }

    @Test
    public void test_checkForMisLocatedDevices_1() {
        // Test case for no misloacted device
        ecobeeGroupDeviceMapping = new ArrayList<EcobeeZeusGroupDeviceMapping>();
        // Root group
        EcobeeZeusGroupDeviceMapping mapping0 = new EcobeeZeusGroupDeviceMapping("G0", new ArrayList<String>(Arrays.asList()));
        ecobeeGroupDeviceMapping.add(mapping0);
        // Ecobee group : G1
        EcobeeZeusGroupDeviceMapping mapping1 = new EcobeeZeusGroupDeviceMapping("G1", new ArrayList<String>(Arrays.asList()));
        mapping1.setParentGroupId("G0");
        List<String> thermostatNumbers = new ArrayList<>(Arrays.asList("1", "2", "3"));
        mapping1.setThermostatsSerialNumber(thermostatNumbers);
        ecobeeGroupDeviceMapping.add(mapping1);

        EcobeeZeusGroupDao ecobeeZeusGroupDao = createNiceMock(EcobeeZeusGroupDao.class);

        ecobeeZeusGroupDao.getInventoryIdsForZeusGroupID("G1");
        expectLastCall().andAnswer(() -> {
            List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3));
            return numbers;
        }).anyTimes();

        replay(ecobeeZeusGroupDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "ecobeeZeusGroupDao", ecobeeZeusGroupDao);

        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMisLocatedDevices", ecobeeGroupDeviceMapping,
                errorsList);
        assertTrue(errorsList.size() == 0, "Number of mislocated device should be 0");
    }

    @Test
    public void test_checkForMisLocatedDevices_2() {
        // Test case for misloacted device : Available in Ecobee but not in Yukon
        ecobeeGroupDeviceMapping = new ArrayList<EcobeeZeusGroupDeviceMapping>();
        // Root group
        EcobeeZeusGroupDeviceMapping mapping0 = new EcobeeZeusGroupDeviceMapping("G0", new ArrayList<String>(Arrays.asList()));
        ecobeeGroupDeviceMapping.add(mapping0);
        // Ecobee group : G1
        EcobeeZeusGroupDeviceMapping mapping1 = new EcobeeZeusGroupDeviceMapping("G1", new ArrayList<String>(Arrays.asList()));
        mapping1.setParentGroupId("G0");
        List<String> thermostatNumbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
        mapping1.setThermostatsSerialNumber(thermostatNumbers);
        ecobeeGroupDeviceMapping.add(mapping1);

        EcobeeZeusGroupDao ecobeeZeusGroupDao = createNiceMock(EcobeeZeusGroupDao.class);

        ecobeeZeusGroupDao.getInventoryIdsForZeusGroupID("G1");
        expectLastCall().andAnswer(() -> {
            List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3));
            return numbers;
        }).anyTimes();

        replay(ecobeeZeusGroupDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "ecobeeZeusGroupDao", ecobeeZeusGroupDao);

        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMisLocatedDevices", ecobeeGroupDeviceMapping,
                errorsList);
        assertTrue(errorsList.size() == 2, "Number of mislocated device should be 2");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISLOCATED_DEVICE, "Type should be MISLOCATED_DEVICE");
            assertTrue(error.getSerialNumber().equals("4") || error.getSerialNumber().equals("5"),
                    "Mislocated devices should be 4 or 5");
            assertTrue(error.getCurrentPath().equals("G1"), "Current Group should be G1");
            assertTrue(StringUtils.isEmpty(error.getCorrectPath()),
                    "Correct Group should be empty as Yukon will remove these devices.");
        });

    }

    @Test
    public void test_checkForMisLocatedDevices_3() {

        // Test case for misloacted device : Available in Yukon but not in Ecobee
        ecobeeGroupDeviceMapping = new ArrayList<EcobeeZeusGroupDeviceMapping>();
        // Root group
        EcobeeZeusGroupDeviceMapping mapping0 = new EcobeeZeusGroupDeviceMapping("G0", new ArrayList<String>(Arrays.asList()));
        ecobeeGroupDeviceMapping.add(mapping0);
        // Ecobee group : G1
        EcobeeZeusGroupDeviceMapping mapping1 = new EcobeeZeusGroupDeviceMapping("G1", new ArrayList<String>(Arrays.asList()));
        mapping1.setParentGroupId("G0");
        List<String> thermostatNumbers = new ArrayList<>(Arrays.asList("1", "2", "3"));
        mapping1.setThermostatsSerialNumber(thermostatNumbers);
        ecobeeGroupDeviceMapping.add(mapping1);

        EcobeeZeusGroupDao ecobeeZeusGroupDao = createNiceMock(EcobeeZeusGroupDao.class);
        LmHardwareBaseDao lmHardwareBaseDao = createNiceMock(LmHardwareBaseDao.class);

        ecobeeZeusGroupDao.getInventoryIdsForZeusGroupID("G1");
        expectLastCall().andAnswer(() -> {
            List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
            return numbers;
        }).anyTimes();

        ecobeeZeusGroupDao.getProgramIdForZeusGroup("G1");
        expectLastCall().andAnswer(() -> {
            return 111;
        }).anyTimes();

        lmHardwareBaseDao.getBySerialNumber("4");
        expectLastCall().andAnswer(() -> {
            LMHardwareBase base = new LMHardwareBase();
            base.setInventoryId(4);
            return base;
        }).anyTimes();

        lmHardwareBaseDao.getBySerialNumber("5");
        expectLastCall().andAnswer(() -> {
            LMHardwareBase base = new LMHardwareBase();
            base.setInventoryId(5);
            return base;
        }).anyTimes();

        ecobeeZeusGroupDao.getLmGroupForInventory(4, 111);
        expectLastCall().andAnswer(() -> {
            return 44;
        }).anyTimes();

        ecobeeZeusGroupDao.getLmGroupForInventory(5, 111);
        expectLastCall().andAnswer(() -> {
            return 55;
        }).anyTimes();

        ecobeeZeusGroupDao.getZeusGroupId(44, 4, 111);
        expectLastCall().andAnswer(() -> {
            return "YG4";
        }).anyTimes();

        ecobeeZeusGroupDao.getZeusGroupId(55, 5, 111);
        expectLastCall().andAnswer(() -> {
            return "YG5";
        }).anyTimes();

        replay(ecobeeZeusGroupDao);
        replay(lmHardwareBaseDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "ecobeeZeusGroupDao", ecobeeZeusGroupDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "lmHardwareBaseDao", lmHardwareBaseDao);

        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMisLocatedDevices", ecobeeGroupDeviceMapping,
                errorsList);
        assertTrue(errorsList.size() == 2, "Number of mislocated device should be 2");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISLOCATED_DEVICE, "Type should be MISLOCATED_DEVICE");
            assertTrue(error.getSerialNumber().equals("4") || error.getSerialNumber().equals("5"),
                    "Mislocated devices should be 4 or 5");
            assertTrue(error.getCurrentPath().equals("G1"), "Current Group should be G1");
            assertTrue(error.getCorrectPath().equals("YG4") || error.getCorrectPath().equals("YG5"),
                    "Current Group should be YG4 or YG5");
        });
    }
}
