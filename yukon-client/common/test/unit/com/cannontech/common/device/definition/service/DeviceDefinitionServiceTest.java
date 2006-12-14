package com.cannontech.common.device.definition.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.MCT470;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.spring.YukonSpringHook;

public class DeviceDefinitionServiceTest extends TestCase {

    DeviceDefinitionService service = null;

    public void setUp() {

        service = (DeviceDefinitionService) YukonSpringHook.getBean("deviceService");
    }

    public void testCreateDefaultPointsForDevice() {

        DeviceBase device = null;
        // MCT470
        device = this.createNewDevice(DeviceTypes.MCT470);

        List<PointBase> expectedPoints = this.getPointUtilPointList(device);
        Set<PointBase> expectedPointsSet = new HashSet<PointBase>();
        expectedPointsSet.addAll(expectedPoints);

        List<PointBase> actualPoints = service.createDefaultPointsForDevice(device);
        Set<PointBase> actualPointsSet = new HashSet<PointBase>();
        actualPointsSet.addAll(actualPoints);

        assertEquals("MCT470 points don't match", expectedPointsSet, actualPointsSet);
    }

    private List<PointBase> getPointUtilPointList(DeviceBase device) {

        List<PointBase> pointList = new ArrayList<PointBase>();

        MultiDBPersistent pointPersistant = (MultiDBPersistent) PointUtil.generatePointsForMCT(device);
        for (Object object : pointPersistant.getDBPersistentVector()) {
            if (object instanceof PointBase) {
                pointList.add((PointBase) object);
            }
        }

        return pointList;
    }

    private DeviceBase createNewDevice(int deviceType) {
        DeviceBase device = null;

        switch (deviceType) {

        case DeviceTypes.MCT470:
            device = new MCT470();
            break;
        }

        device.setDeviceID(1);
        device.setDeviceType(PAOGroups.getPAOTypeString(deviceType));
        return device;
    }

}
