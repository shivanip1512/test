package com.cannontech.common.device.attribute.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.service.PointService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;

public class AttributeServiceImpl implements AttributeService {

    private DBPersistentDao dbPersistentDao = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    private PointService pointService = null;
    private PointDao pointDao;

    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Required
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    @Required
    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

    @Required
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    public LitePoint getPointForAttribute(YukonDevice device, Attribute attribute) {

        PointTemplate pointTemplate = deviceDefinitionDao.getPointTemplateForAttribute(device,
                                                                                       attribute);
        return pointService.getPointForDevice(device, pointTemplate.getDevicePointIdentifier());
    }

    public Set<Attribute> getAvailableAttributes(YukonDevice device) {
        return deviceDefinitionDao.getAvailableAttributes(device);
    }

    public Set<Attribute> getAllExistingAttributes(YukonDevice device) {

        Set<Attribute> attributes = new HashSet<Attribute>();
        Set<Attribute> availableAttribute = this.getAvailableAttributes(device);
        List<LitePoint> pointList = pointDao.getLitePointsByPaObjectId(device.getDeviceId()); 
        
        for (final Attribute attribute : availableAttribute) {
            try {
                PointTemplate template = deviceDefinitionDao.getPointTemplateForAttribute(device,
                                                                                          attribute);
                for (final LitePoint point : pointList) {
                    if ((point.getPointOffset() == template.getOffset()) &&
                        (point.getPointType() == template.getType())) attributes.add(attribute);
                }
            } catch (NotFoundException ignore) { }
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

            return pointService.pointExistsForDevice(device, template.getDevicePointIdentifier());
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
                dbPersistentDao.performDBChange(point, Transaction.INSERT);
            } catch (PersistenceException e) {
                throw new DataAccessException("Could not create point for device: " + device, e) {
                };
            }

        }
    }

}
