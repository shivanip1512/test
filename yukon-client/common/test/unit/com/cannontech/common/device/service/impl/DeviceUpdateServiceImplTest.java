package com.cannontech.common.device.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.device.service.DeviceUpdateService.PointToTemplate;
import com.cannontech.common.device.service.DeviceUpdateService.PointsToProcess;
import com.cannontech.common.device.service.DeviceUpdateServiceImpl;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.common.pao.definition.loader.DefinitionLoaderServiceImpl;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.impl.PointCreationServiceImpl;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.data.device.RfnMeterBase;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.pao.YukonPAObject;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class DeviceUpdateServiceImplTest {

    @Autowired private DefinitionLoaderServiceImpl loaderServiceImpl;
    private DeviceUpdateServiceImpl service;

    @Before
    public void setUp() {
        PointDao pointDao = new MockPointDao();
        service = new DeviceUpdateServiceImpl();
        PaoDefinitionDao paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();
        PointCreationServiceImpl pointCreationServiceImpl = new PointCreationServiceImpl();
        ReflectionTestUtils.setField(pointDao, "pointCreationService", pointCreationServiceImpl);
        ReflectionTestUtils.setField(service, "pointDao", pointDao);
        ReflectionTestUtils.setField(service, "paoDefinitionDao", paoDefinitionDao);
    }

    @Test
    public void testPointTransferMCTtoMCT() {
        YukonPAObject ypo = new YukonPAObject();
        ypo.setPaObjectID(1);
        ypo.setPaoType(PaoType.MCT410IL);
        MCT410IL mct410il = new MCT410IL();

        ReflectionTestUtils.setField(mct410il, "yukonPAObject", ypo);
        PointsToProcess mctToMct = service.getPointsToProccess(mct410il, PaoType.MCT430S4);
        assertPointToAdd(mctToMct.getPointsToAdd(), "Blink Count");

        assertPointToTransfer(mctToMct.getPointsToTransfer(), 2, "Blink Count", "IED Blink Count");

        assertPointToDelete(mctToMct.getPointsToDelete(), "Voltage");
    }

    @Test
    public void testPointTransferMCTtoRFN() {

        YukonPAObject ypo = new YukonPAObject();
        ypo.setPaObjectID(1);
        ypo.setPaoType(PaoType.MCT410IL);
        MCT410IL mct410IL = new MCT410IL();
        ReflectionTestUtils.setField(mct410IL, "yukonPAObject", ypo);

        PointsToProcess mctToRfn = service.getPointsToProccess(mct410IL, PaoType.RFN420FL);

        // add
        assertPointToAdd(mctToRfn.getPointsToAdd(), "Blink Count");
        assertPointToAdd(mctToRfn.getPointsToAdd(), "RF Demand Reset Status");
        assertPointToAdd(mctToRfn.getPointsToAdd(), "Outage Count");

        // transfer
        assertPointToTransfer(mctToRfn.getPointsToTransfer(), 2, "Blink Count", "Total Outage Count");
        assertPointToTransfer(mctToRfn.getPointsToTransfer(), 3, "kWh", "Delivered kWh");

        // delete
        assertPointToDelete(mctToRfn.getPointsToDelete(), "Comm Status");
    }

    @Test
    public void testPointTransferRFNtoMCT() {

        YukonPAObject ypo = new YukonPAObject();
        ypo.setPaObjectID(2);
        ypo.setPaoType(PaoType.RFN420FL);
        RfnMeterBase rfn420FL = new RfnMeterBase(PaoType.RFN420FL);
        ReflectionTestUtils.setField(rfn420FL, "yukonPAObject", ypo);

        PointsToProcess rfnToMct = service.getPointsToProccess(rfn420FL, PaoType.MCT430S4);

        // transfer
        assertPointToTransfer(rfnToMct.getPointsToTransfer(), 6, "Total Outage Count", "IED Blink Count");

        // delete
        assertPointToDelete(rfnToMct.getPointsToDelete(), "Received kWh");
        assertPointToDelete(rfnToMct.getPointsToDelete(), "Outage Count");
    }

    private void assertPointToAdd(Set<PointTemplate> templates, String pointName) {
        Collection<PointTemplate> template = Collections2.filter(templates, new Predicate<PointTemplate>() {
            @Override
            public boolean apply(PointTemplate t) {
                return t.getName().equals(pointName);
            }
        });
        Assert.assertEquals(pointName + " should be added", 1, template.size());
    }

    private void assertPointToTransfer(Map<Integer, PointToTemplate> pointsToTransfer, int pointId, String from,
            String to) {
        PointToTemplate pointToTransfer = pointsToTransfer.get(pointId);
        Assert.assertEquals(from + " should be transfered", from, pointToTransfer.getPoint().getPoint().getPointName());
        Assert.assertEquals(from + " should be transfered to " + to, to, pointToTransfer.getTemplate().getName());
    }

    private void assertPointToDelete(Set<PointBase> pointsToDelete, String pointName) {
        Collection<PointBase> pointToDelete = Collections2.filter(pointsToDelete, new Predicate<PointBase>() {
            @Override
            public boolean apply(PointBase t) {
                return t.getPoint().getPointName().equals(pointName);
            }
        });
        Assert.assertEquals(pointName + " should be deleted", 1, pointToDelete.size());
    }

}
