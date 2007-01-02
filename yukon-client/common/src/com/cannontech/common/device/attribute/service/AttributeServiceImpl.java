package com.cannontech.common.device.attribute.service;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.AttributeSource;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LitePoint;

public class AttributeServiceImpl implements AttributeService {

    private DeviceDefinitionDao deviceDefinitionDao = null;
    private PointDao pointDao = null;

    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public double getCurrentValue(AttributeSource source, Attribute attribute) {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getCurrentStateText(AttributeSource source, Attribute attribute) {
        // TODO Auto-generated method stub
        return null;
    }

    public LitePoint getPointForAttribute(DeviceBase device, Attribute attribute) {

        PointTemplate pointTemplate = deviceDefinitionDao.getPointTemplateForAttribute(device,
                                                                                       attribute);

        int pointId = pointDao.getPointIDByDeviceID_Offset_PointType(device.getPAObjectID(),
                                                                     pointTemplate.getOffset(),
                                                                     pointTemplate.getType());

        return pointDao.getLitePoint(pointId);
    }

    public Set<Attribute> getAvailableAttributes(DeviceBase device) {
        return deviceDefinitionDao.getAvailableAttributes(device);
    }

    public Set<Attribute> getAllExistingAtributes(DeviceBase device) {

        Set<Attribute> attributes = new HashSet<Attribute>();

        Set<Attribute> availableAttribute = this.getAvailableAttributes(device);
        for (Attribute attribute : availableAttribute) {
            try {
                this.getPointForAttribute(device, attribute);
                attributes.add(attribute);
            } catch (NotFoundException e) {
                // point doesn't exist for current attribute
            }
        }

        return attributes;
    }

}
