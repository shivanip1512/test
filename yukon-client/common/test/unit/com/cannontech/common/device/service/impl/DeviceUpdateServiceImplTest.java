package com.cannontech.common.device.service.impl;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.device.service.DeviceUpdateService.PointToTemplate;
import com.cannontech.common.device.service.DeviceUpdateService.PointsToProcess;
import com.cannontech.common.device.service.DeviceUpdateServiceImpl;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest.MockEmptyDeviceDefinitionDao;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.impl.PointCreationServiceImpl;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.data.device.RfnMeterBase;
import com.cannontech.database.data.point.PointBase;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class DeviceUpdateServiceImplTest extends TestCase {

    private DeviceUpdateServiceImpl service = null;

    @Override
    @Before
    public void setUp() throws Exception {
        PointDao pointDao = new MockPointDao();
        service = new DeviceUpdateServiceImpl();
        PointCreationServiceImpl pointCreationService = new PointCreationServiceImpl();
        ReflectionTestUtils.setField(pointDao, "pointCreationService", pointCreationService);
        ReflectionTestUtils.setField(service, "pointDao", pointDao);
        ReflectionTestUtils.setField(service, "paoDefinitionDao",
            PaoDefinitionDaoImplTest.getTestPaoDefinitionDao(new MockEmptyDeviceDefinitionDao()));
    }

    @Test
    public void testPointTransferMCTtoMCT() {
        
        MCT410IL mct410IL = new MCT410IL();
        mct410IL.setDeviceID(1); 

        PointsToProcess mctToMct = service.getPointsToProccess(mct410IL, PaoType.MCT430S4);

        // add
        assertTrue("1 point to add", mctToMct.getPointsToAdd().size() == 1);
        assertPointToAdd(mctToMct.getPointsToAdd(), "Blink Count");

        // transfer
        assertTrue("2 points to transfer", mctToMct.getPointsToTransfer().values().size() == 2);
        assertPointToTransfer(mctToMct.getPointsToTransfer(), 2, "Blink Count", "IED Blink Count");
        assertPointToTransfer(mctToMct.getPointsToTransfer(), 3, "kWh", "Total kWh");

        // delete
        assertTrue("1 point to delete", mctToMct.getPointsToDelete().size() == 1);
        assertPointToDelete(mctToMct.getPointsToDelete(), "Voltage");
    }

    @Test
    public void testPointTransferMCTtoRFN() {
        
        MCT410IL mct410IL = new MCT410IL();
        mct410IL.getDevice().setDeviceID(1);

        PointsToProcess mctToRfn = service.getPointsToProccess(mct410IL, PaoType.RFN420FL);

        // add
        assertTrue("3 point to add", mctToRfn.getPointsToAdd().size() == 3);
        assertPointToAdd(mctToRfn.getPointsToAdd(), "Blink Count");
        assertPointToAdd(mctToRfn.getPointsToAdd(), "Received kWh");
        assertPointToAdd(mctToRfn.getPointsToAdd(), "Outage Count");

        // transfer
        assertTrue("2 points to transfer", mctToRfn.getPointsToTransfer().values().size() == 2);
        assertPointToTransfer(mctToRfn.getPointsToTransfer(), 2, "Blink Count", "Total Outage Count");
        assertPointToTransfer(mctToRfn.getPointsToTransfer(), 3, "kWh", "Delivered kWh");

        // delete
        assertTrue("1 point to delete", mctToRfn.getPointsToDelete().size() == 1);
        assertPointToDelete(mctToRfn.getPointsToDelete(), "Voltage");
    }

    @Test
    public void testPointTransferRFNtoMCT() {
        
        RfnMeterBase rfn420FL = new RfnMeterBase(PaoType.RFN420FL);
        rfn420FL.getDevice().setDeviceID(2);

        // transfer
        PointsToProcess rfnToMct = service.getPointsToProccess(rfn420FL, PaoType.MCT430S4);
        assertTrue("3 points to transfer", rfnToMct.getPointsToTransfer().values().size() == 3);
        assertPointToTransfer(rfnToMct.getPointsToTransfer(), 2, "Delivered kWh", "Total kWh");
        assertPointToTransfer(rfnToMct.getPointsToTransfer(), 4, "Blink Count", "Blink Count");
        assertPointToTransfer(rfnToMct.getPointsToTransfer(), 6, "Total Outage Count", "IED Blink Count");

        // delete
        assertTrue("2 points to delete", rfnToMct.getPointsToDelete().size() == 2);
        assertPointToDelete(rfnToMct.getPointsToDelete(), "Received kWh");
        assertPointToDelete(rfnToMct.getPointsToDelete(), "Outage Count");
    }

    private void assertPointToDelete(Set<PointBase> pointsToDelete, String pointName) {
        Iterable<PointBase> pointToDelete = Iterables.filter(pointsToDelete, new Predicate<PointBase>() {
            @Override
            public boolean apply(PointBase t) {
                return t.getPoint().getPointName().equals(pointName);
            }
        });
        assertTrue(pointName + " should be deleted", pointToDelete.iterator().hasNext());
    }

    private void assertPointToAdd(Set<PointTemplate> templates, String pointName) {
        Iterable<PointTemplate> template = Iterables.filter(templates, new Predicate<PointTemplate>() {
            @Override
            public boolean apply(PointTemplate t) {
                return t.getName().equals(pointName);
            }
        });
        assertTrue(pointName + " should be added", template.iterator().hasNext());
    }

    private void assertPointToTransfer(Map<Integer, PointToTemplate> pointsToTransfer, int pointId, String from,
            String to) {
        PointToTemplate pointToTransfer = pointsToTransfer.get(pointId);
        assertEquals(from + " should be transfered", pointToTransfer.getPoint().getPoint().getPointName(), from);
        assertEquals(from + " should be transfered to " + to, pointToTransfer.getTemplate().getName(), to);
    }
}
