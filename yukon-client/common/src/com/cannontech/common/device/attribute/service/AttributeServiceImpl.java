package com.cannontech.common.device.attribute.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.dao.DataAccessException;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;

public class AttributeServiceImpl implements AttributeService {

    private DeviceDefinitionDao deviceDefinitionDao = null;
    private PointService pointService = null;

    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

    public LitePoint getPointForAttribute(YukonDevice device, Attribute attribute) {

        PointTemplate pointTemplate = deviceDefinitionDao.getPointTemplateForAttribute(device,
                                                                                       attribute);
        return pointService.getPointForDevice(device, pointTemplate);
    }

    public Set<Attribute> getAvailableAttributes(YukonDevice device) {
        return deviceDefinitionDao.getAvailableAttributes(device);
    }

    public Set<Attribute> getAllExistingAtributes(YukonDevice device) {

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
    
    public Attribute resolveAttributeName(String name) {
        // some day this should also "lookup" user defined attributes
        return BuiltInAttribute.valueOf(name);
    }

    public boolean isAttributeSupported(YukonDevice device, Attribute attribute) {

        try {
            deviceDefinitionDao.getPointTemplateForAttribute(device, attribute);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean pointExistsForAttribute(YukonDevice device, Attribute attribute) {

        if (isAttributeSupported(device, attribute)) {
            PointTemplate template = deviceDefinitionDao.getPointTemplateForAttribute(device,
                                                                                      attribute);

            return pointService.pointExistsForDevice(device, template);
        }

        throw new IllegalArgumentException("Device: " + device + " does not support attribute: " + attribute.getKey());
    }

    public void createPointForAttribute(YukonDevice device, Attribute attribute) {

        boolean pointExists = this.pointExistsForAttribute(device, attribute);
        if (!pointExists) {
            PointTemplate template = deviceDefinitionDao.getPointTemplateForAttribute(device,
                                                                                      attribute);
            PointBase point = pointService.createPoint(device.getDeviceId(), template);
            try {
                Transaction t = Transaction.createTransaction(Transaction.INSERT, point);
                t.execute();
            } catch (TransactionException e) {
                throw new DataAccessException("Could not create point for device: " + device,
                                              e) {
                };
            }

        }
    }

}
