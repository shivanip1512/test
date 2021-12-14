package com.cannontech.dr.ecobee.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeZeusGroupDeviceMapping;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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

   public void prepareYukonData(Multimap<Integer, String> yukonGroupToDevicesMap, List<LMHardwareBase> devices,
            LmHardwareBaseDao lmHardwareBaseDao2, LoadGroupDao loadGroupDao, EcobeeZeusGroupDao ecobeeZeusGroupDao) {
       
       
        yukonGroupToDevicesMap.put(100, "1");
        yukonGroupToDevicesMap.put(100, "2");
        yukonGroupToDevicesMap.put(100, "3");
        yukonGroupToDevicesMap.put(100, "4");
        yukonGroupToDevicesMap.put(100, "5");

        LMHardwareBase device1 = new LMHardwareBase();
        device1.setInventoryId(11);
        devices.add(0, device1);
        LMHardwareBase device2 = new LMHardwareBase();
        device2.setInventoryId(12);
        devices.add(1, device2);
        LMHardwareBase device3 = new LMHardwareBase();
        device3.setInventoryId(13);
        devices.add(2, device3);
        LMHardwareBase device4 = new LMHardwareBase();
        device4.setInventoryId(14);
        devices.add(3, device4);
        LMHardwareBase device5 = new LMHardwareBase();
        device5.setInventoryId(15);
        devices.add(4, device5);
        LMHardwareBase device6 = new LMHardwareBase();
        device6.setInventoryId(16);
        devices.add(5, device6);

        LmHardwareBaseDao lmHardwareBaseDao = EasyMock.createNiceMock(LmHardwareBaseDao.class);
        EasyMock.expect(lmHardwareBaseDao.getBySerialNumber("1")).andStubReturn(devices.get(0));
        EasyMock.expect(lmHardwareBaseDao.getBySerialNumber("2")).andStubReturn(devices.get(1));
        EasyMock.expect(lmHardwareBaseDao.getBySerialNumber("3")).andStubReturn(devices.get(2));
        EasyMock.expect(lmHardwareBaseDao.getBySerialNumber("4")).andStubReturn(devices.get(3));
        EasyMock.expect(lmHardwareBaseDao.getBySerialNumber("5")).andStubReturn(devices.get(4));
        EasyMock.expect(lmHardwareBaseDao.getBySerialNumber("6")).andStubReturn(devices.get(5));
        EasyMock.replay(lmHardwareBaseDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "lmHardwareBaseDao", lmHardwareBaseDao);

        EasyMock.expect(loadGroupDao.getProgramIdsByGroupId(100)).andStubReturn(List.of(1000));
        EasyMock.replay(loadGroupDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "loadGroupDao", loadGroupDao);

        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 11, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 12, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 13, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 14, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 15, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 16, 1000)).andStubReturn("200");
        
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 11, 1000)).andStubReturn("201");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 12, 1000)).andStubReturn("201");
        EasyMock.replay(ecobeeZeusGroupDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "ecobeeZeusGroupDao", ecobeeZeusGroupDao);

    }

    public Multimap<String, String> prepareEcobeeData() {
        Multimap<String, String> ecobeeSerialNumberToGroupMapping = ArrayListMultimap.create();
        ecobeeSerialNumberToGroupMapping.put("1", "200");
        ecobeeSerialNumberToGroupMapping.put("2", "200");
        ecobeeSerialNumberToGroupMapping.put("3", "200");
        ecobeeSerialNumberToGroupMapping.put("4", "200");
        ecobeeSerialNumberToGroupMapping.put("5", "200");
        ecobeeSerialNumberToGroupMapping.put("1", "205");
        ecobeeSerialNumberToGroupMapping.put("2", "205");
        ecobeeSerialNumberToGroupMapping.put("1", "210");
        ecobeeSerialNumberToGroupMapping.put("1", "215");
        return ecobeeSerialNumberToGroupMapping;
    }

    @Test public void runTestCasesForMissingDevice() throws RestClientException, EcobeeAuthenticationException {
        
        List<String> yukonSerialNumbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
        Multimap<Integer, String> yukonGroupToDevicesMap = ArrayListMultimap.create();
        List<LMHardwareBase> devices = new ArrayList<LMHardwareBase>();
        LmHardwareBaseDao lmHardwareBaseDao = EasyMock.createNiceMock(LmHardwareBaseDao.class);
        LoadGroupDao loadGroupDao = EasyMock.createNiceMock(LoadGroupDao.class);
        EcobeeZeusGroupDao ecobeeZeusGroupDao = EasyMock.createNiceMock(EcobeeZeusGroupDao.class);
        
        prepareYukonData(yukonGroupToDevicesMap, devices, lmHardwareBaseDao, loadGroupDao, ecobeeZeusGroupDao);

        Multimap<String, String> ecobeeSerialNumberToGroupMapping = prepareEcobeeData();

        test_checkForMissingDevices_1(yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, yukonGroupToDevicesMap);
        test_checkForMissingDevices_2(yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, yukonGroupToDevicesMap);
        test_checkForMissingDevices_3(yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, yukonGroupToDevicesMap);
        test_checkForMissingDevices_4(yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, yukonGroupToDevicesMap);
        test_checkForMissingDevices_5(yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, yukonGroupToDevicesMap);
    }
   
    public void test_checkForMissingDevices_1(List<String> yukonSerialNumbers, Multimap<String, String> ecobeeSerialNumberToGroupMapping, Multimap<Integer, String> yukonGroupToDevicesMap) throws RestClientException, EcobeeAuthenticationException {
        
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, errorsList, yukonGroupToDevicesMap);
        assertTrue(errorsList.size() == 0, "Number of missing device should be 0");
        
        errorsList.clear();
    }
       
    public void test_checkForMissingDevices_2(List<String> yukonSerialNumbers, Multimap<String, String> ecobeeSerialNumberToGroupMapping, Multimap<Integer, String> yukonGroupToDevicesMap) throws RestClientException, EcobeeAuthenticationException {
        
        ecobeeSerialNumberToGroupMapping.put("6", "220");
        yukonGroupToDevicesMap.put(100, "6");
        yukonSerialNumbers.add("6");
        
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, errorsList, yukonGroupToDevicesMap);
        assertTrue(errorsList.size() == 1, "Number of missing device should be 1");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_DEVICE, "Type should be MISSING_DEVICE");
            assertTrue(error.getSerialNumber().equals("6"), "Missing device should be 6");
            assertTrue(error.getCorrectPath().equals("100"));
        });
        
        ecobeeSerialNumberToGroupMapping.remove("6", "220");
        errorsList.clear();
    }

    
    public void test_checkForMissingDevices_3(List<String> yukonSerialNumbers, Multimap<String, String> ecobeeSerialNumberToGroupMapping, Multimap<Integer, String> yukonGroupToDevicesMap) throws RestClientException, EcobeeAuthenticationException {
        
        EcobeeZeusCommunicationService communicationService = EasyMock.createMock(EcobeeZeusCommunicationService.class);
        EasyMock.expect(communicationService.isDeviceEnrolled("6")).andStubReturn(false);
        EasyMock.expect(communicationService.retrieveThermostatGroupID()).andStubReturn("10000");
        EasyMock.replay(communicationService);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "communicationService", communicationService);
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, errorsList, yukonGroupToDevicesMap);
        assertTrue(errorsList.size() == 1, "Number of missing device should be 1");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_DEVICE, "Type should be MISSING_DEVICE");
            assertTrue(error.getSerialNumber().equals("6"), "Missing device should be 6");
            assertTrue(error.getCorrectPath().equals("10000"));
        });
        errorsList.clear();
    }

    
    public void test_checkForMissingDevices_4(List<String> yukonSerialNumbers, Multimap<String, String> ecobeeSerialNumberToGroupMapping, Multimap<Integer, String> yukonGroupToDevicesMap) throws RestClientException, EcobeeAuthenticationException {

        EcobeeZeusCommunicationService communicationService = EasyMock.createMock(EcobeeZeusCommunicationService.class);
        EasyMock.expect(communicationService.isDeviceEnrolled("6")).andStubReturn(true);
        EasyMock.expect(communicationService.retrieveThermostatGroupID()).andStubReturn("10000");
        EasyMock.replay(communicationService);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "communicationService", communicationService);
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, errorsList, yukonGroupToDevicesMap);
        assertTrue(errorsList.size() == 1, "Number of missing device should be 1");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_DEVICE, "Type should be MISSING_DEVICE");
            assertTrue(error.getSerialNumber().equals("6"), "Missing device should be 6");
            assertTrue(error.getCorrectPath().equals("100"));
        });
        errorsList.clear();
    }
    
    public void test_checkForMissingDevices_5(List<String> yukonSerialNumbers, Multimap<String, String> ecobeeSerialNumberToGroupMapping, Multimap<Integer, String> yukonGroupToDevicesMap) throws RestClientException, EcobeeAuthenticationException {
        
        EcobeeZeusGroupDao ecobeeZeusGroupDao = EasyMock.createNiceMock(EcobeeZeusGroupDao.class);
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 11, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 12, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 13, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 14, 1000)).andStubReturn("200");
        EasyMock.expect(ecobeeZeusGroupDao.getZeusGroupId(100, 15, 1000)).andStubReturn("200");
        EasyMock.replay(ecobeeZeusGroupDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "ecobeeZeusGroupDao", ecobeeZeusGroupDao);

        ecobeeSerialNumberToGroupMapping.put("6", "220");
        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, errorsList, yukonGroupToDevicesMap);
        assertTrue(errorsList.size() == 1, "Number of missing device should be 1");
        errorsList.forEach(error -> {
            assertTrue(error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_DEVICE, "Type should be MISSING_DEVICE");
            assertTrue(error.getSerialNumber().equals("6"), "Missing device should be 6");
            assertTrue(error.getCorrectPath().equals("100"));
        });
        
        ecobeeSerialNumberToGroupMapping.remove("6", "220");
        errorsList.clear();
    }
    

    public void test_checkForExtraneousDevices() {
        List<String> yukonSerialNumbers = new ArrayList<>(Arrays.asList("1", "2", "3"));
        // Set<String> ecobeeSerialNumbers = new HashSet<String>(Arrays.asList("1", "2", "3", "4", "5"));
        Multimap<String, String> ecobeeSerialNumberToGroupMapping = ArrayListMultimap.create();
        Multimap<Integer, String> yukonGroupToDevicesMap = ArrayListMultimap.create();

        ecobeeSerialNumberToGroupMapping.put("1", "200");
        ecobeeSerialNumberToGroupMapping.put("2", "200");
        ecobeeSerialNumberToGroupMapping.put("3", "200");
        ecobeeSerialNumberToGroupMapping.put("4", "200");
        ecobeeSerialNumberToGroupMapping.put("5", "200");

        ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService, "checkForMissingAndExtraneousDevices",
                yukonSerialNumbers, ecobeeSerialNumberToGroupMapping, errorsList, yukonGroupToDevicesMap);

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
        LmHardwareBaseDao lmHardwareBaseDao = createNiceMock(LmHardwareBaseDao.class);

        ecobeeZeusGroupDao.getInventoryIdsForZeusGroupID("G1");
        expectLastCall().andAnswer(() -> {
            List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3));
            return numbers;
        }).anyTimes();
        
        lmHardwareBaseDao.getSerialNumberForInventoryId(1);
        expectLastCall().andAnswer(() -> {
            return "1";
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(2);
        expectLastCall().andAnswer(() -> {
            return "2";
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(3);
        expectLastCall().andAnswer(() -> {
            return "3";
        }).anyTimes();

        replay(ecobeeZeusGroupDao);
        replay(lmHardwareBaseDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "ecobeeZeusGroupDao", ecobeeZeusGroupDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "lmHardwareBaseDao", lmHardwareBaseDao);

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
        LmHardwareBaseDao lmHardwareBaseDao = createNiceMock(LmHardwareBaseDao.class);

        ecobeeZeusGroupDao.getInventoryIdsForZeusGroupID("G1");
        expectLastCall().andAnswer(() -> {
            List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3));
            return numbers;
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(1);
        expectLastCall().andAnswer(() -> {
            return "1";
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(2);
        expectLastCall().andAnswer(() -> {
            return "2";
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(3);
        expectLastCall().andAnswer(() -> {
            return "3";
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
            List<Integer> inventoryIds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
            return inventoryIds;
        }).anyTimes();

        ecobeeZeusGroupDao.getProgramIdForZeusGroup("G1");
        expectLastCall().andAnswer(() -> {
            return 111;
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(1);
        expectLastCall().andAnswer(() -> {
            return "1";
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(2);
        expectLastCall().andAnswer(() -> {
            return "2";
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(3);
        expectLastCall().andAnswer(() -> {
            return "3";
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(4);
        expectLastCall().andAnswer(() -> {
            return "4";
        }).anyTimes();

        lmHardwareBaseDao.getSerialNumberForInventoryId(5);
        expectLastCall().andAnswer(() -> {
            return "5";
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
            // As Yukon has 2 extra devices, Yukon must enroll the devices to correct path.Current path is required in case of
            // unenrollment.
            assertTrue(StringUtils.isEmpty(error.getCurrentPath()), "Current Group should be empty");
            assertTrue(error.getCorrectPath().equals("YG4") || error.getCorrectPath().equals("YG5"),
                    "Current Group should be YG4 or YG5");
        });
    }
}
