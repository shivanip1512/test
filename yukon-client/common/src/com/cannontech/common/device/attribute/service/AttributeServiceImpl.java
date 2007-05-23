package com.cannontech.common.device.attribute.service;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class AttributeServiceImpl implements AttributeService {

    private DeviceDefinitionDao deviceDefinitionDao = null;
    private PointService pointService = null;

    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

    public LitePoint getPointForAttribute(LiteYukonPAObject device, Attribute attribute) {

        PointTemplate pointTemplate = deviceDefinitionDao.getPointTemplateForAttribute(device,
                                                                                       attribute);
        return pointService.getPointForDevice(device, pointTemplate);
    }

    public Set<Attribute> getAvailableAttributes(LiteYukonPAObject device) {
        return deviceDefinitionDao.getAvailableAttributes(device);
    }

    public Set<Attribute> getAllExistingAtributes(LiteYukonPAObject device) {

        Set<Attribute> attributes = new HashSet<Attribute>();

        Set<Attribute> availableAttribute = this.getAvailableAttributes(device);
        for (Attribute attribute : availableAttribute) {
            try {
                PointTemplate template = deviceDefinitionDao.getPointTemplateForAttribute(device,
                                                                                          attribute);
                if (pointService.pointExistsForDevice(device, template)) {
                    attributes.add(attribute);
                }
            } catch (NotFoundException e) {
                // point doesn't exist for current attribute
            }
        }

        return attributes;
    }

}
