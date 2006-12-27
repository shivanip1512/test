package com.cannontech.common.device.definition.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointService;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.point.PointBase;

/**
 * Implementation class for DeviceDefinitionService
 */
public class DeviceDefinitionServiceImpl implements DeviceDefinitionService {

    private DeviceDefinitionDao deviceDefinitionDao = null;
    private PointService pointService = null;

    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

    public List<PointBase> createDefaultPointsForDevice(DeviceBase device) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        Set<PointTemplate> pointTemplates = deviceDefinitionDao.getInitPointTemplates(device);
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointService.createPoint(device.getPAObjectID(), template));
        }

        return pointList;
    }

    public void createAllPointsForDevice(DeviceBase device) {
        Set<PointTemplate> pointTemplates = deviceDefinitionDao.getAllPointTemplates(device);
        for (PointTemplate template : pointTemplates) {
            pointService.createPoint(device.getPAObjectID(), template);
        }
    }

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return deviceDefinitionDao.getDeviceDisplayGroupMap();
    }
}
