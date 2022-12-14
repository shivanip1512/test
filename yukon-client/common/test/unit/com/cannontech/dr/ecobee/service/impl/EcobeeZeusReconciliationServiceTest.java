package com.cannontech.dr.ecobee.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeZeusGroupDeviceMapping;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationReport;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class EcobeeZeusReconciliationServiceTest {

    @Test
    public void test_generateReport() {

        Multimap<Integer, String> yukonGroupToDevicesMap = ArrayListMultimap.create();
        yukonGroupToDevicesMap.put(1, "T1");
        yukonGroupToDevicesMap.put(2, "T2");
        yukonGroupToDevicesMap.put(4, "T4");

        List<String> allYukonSerialNumbers = new ArrayList<>(Arrays.asList("T1", "T2", "T3", "T4", "T5"));
        List<EcobeeZeusGroupDeviceMapping> ecobeeGroupDeviceMapping = getEcobeeDeviceMapping();

        EcobeeZeusGroupDao ecobeeZeusGroupDao = createNiceMock(EcobeeZeusGroupDao.class);
        ecobeeZeusGroupDao.getGroupMapping(EasyMock.anyObject());
        expectLastCall().andAnswer(() -> {
            List<String> yukonGroupIds = new ArrayList<>(Arrays.asList("G1", "G2", "G4"));
            return yukonGroupIds;
        }).anyTimes();

        ecobeeZeusGroupDao.getAllEcobeeGroupToSerialNumberMapping();
        expectLastCall().andAnswer(() -> {
            Multimap<String, String> allEcobeeGroupToSerialNumberMapping = ArrayListMultimap.create();
            allEcobeeGroupToSerialNumberMapping.put("T1", "G1");
            allEcobeeGroupToSerialNumberMapping.put("T3", "G2");
            return allEcobeeGroupToSerialNumberMapping;
        }).anyTimes();
        EcobeeZeusReconciliationService ecobeeZeusReconciliationService = new EcobeeZeusReconciliationServiceImpl();

        replay(ecobeeZeusGroupDao);
        ReflectionTestUtils.setField(ecobeeZeusReconciliationService, "ecobeeZeusGroupDao", ecobeeZeusGroupDao);

        EcobeeZeusReconciliationReport report = ReflectionTestUtils.invokeMethod(ecobeeZeusReconciliationService,
                "generateReport", yukonGroupToDevicesMap, allYukonSerialNumbers, ecobeeGroupDeviceMapping);

        for (EcobeeZeusDiscrepancyType type : EcobeeZeusDiscrepancyType.values()) {
            if (type == EcobeeZeusDiscrepancyType.MISSING_GROUP) {
                Collection<EcobeeZeusDiscrepancy> missingGroup = report.getErrors(type);
                assertTrue(missingGroup.size() == 1, "Group did not match ");
                missingGroup.stream().forEach(e -> {
                    assertTrue(e.getCorrectPath().equals("G3"), "Missing group in ecobee ");

                });
            } else if (type == EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP) {
                Collection<EcobeeZeusDiscrepancy> extraneousGroup = report.getErrors(type);
                assertTrue(extraneousGroup.size() == 1, "Group did not match ");
                extraneousGroup.stream().forEach(e -> {
                    assertTrue(e.getCurrentPath().equals("G4"), "Missing Group in Yukon ");

                });
            } else if (type == EcobeeZeusDiscrepancyType.MISSING_DEVICE) {
                Collection<EcobeeZeusDiscrepancy> missingDevice = report.getErrors(type);
                assertTrue(missingDevice.size() == 2, "Group did not match ");
                missingDevice.stream().forEach(e -> {
                    assertTrue(e.getSerialNumber().equals("T4") || e.getSerialNumber().equals("T5"), "Missing devices in ecobee ");

                });
            } else if (type == EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE) {
                Collection<EcobeeZeusDiscrepancy> extraneousDevice = report.getErrors(type);
                assertTrue(extraneousDevice.size() == 1, "Group did not match ");
                extraneousDevice.stream().forEach(e -> {
                    assertTrue(e.getSerialNumber().equals("T6"), "Missing devices in Yukon ");

                });
            } else if (type == EcobeeZeusDiscrepancyType.MISLOCATED_DEVICE) {
                Collection<EcobeeZeusDiscrepancy> mislocatedDevices = report.getErrors(type);
                assertTrue(mislocatedDevices.size() == 2, "Group did not match ");
                mislocatedDevices.stream().forEach(e -> {
                    assertTrue(e.getSerialNumber().equals("T2") || e.getSerialNumber().equals("T6"), "Misslocated devices ");

                });
            }

        }

    }

    private List<EcobeeZeusGroupDeviceMapping> getEcobeeDeviceMapping() {
        List<EcobeeZeusGroupDeviceMapping> ecobeeGroupDeviceMapping = new ArrayList<>();
        EcobeeZeusGroupDeviceMapping mapping0 = new EcobeeZeusGroupDeviceMapping("G0",
                new ArrayList<String>(Arrays.asList()));
        ecobeeGroupDeviceMapping.add(mapping0);

        EcobeeZeusGroupDeviceMapping mapping1 = new EcobeeZeusGroupDeviceMapping("G1",
                new ArrayList<String>(Arrays.asList("T1", "T2")));
        mapping1.setParentGroupId("G0");
        ecobeeGroupDeviceMapping.add(mapping1);
        EcobeeZeusGroupDeviceMapping mapping2 = new EcobeeZeusGroupDeviceMapping("G2",
                new ArrayList<String>(Arrays.asList("T3")));
        mapping2.setParentGroupId("G0");
        ecobeeGroupDeviceMapping.add(mapping2);
        EcobeeZeusGroupDeviceMapping mapping3 = new EcobeeZeusGroupDeviceMapping("G3",
                new ArrayList<String>(Arrays.asList("T6")));
        mapping3.setParentGroupId("G0");
        ecobeeGroupDeviceMapping.add(mapping3);

        return ecobeeGroupDeviceMapping;
    }
}
