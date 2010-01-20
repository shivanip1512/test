package com.cannontech.common.pao.attribute.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.google.common.collect.Sets;

public class AttributeServiceImpl implements AttributeService {

    private DBPersistentDao dbPersistentDao = null;
    private PaoDefinitionDao paoDefinitionDao = null;
    private PointService pointService = null;

    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Required
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }

    @Required
    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

    public LitePoint getPointForAttribute(YukonDevice device, Attribute attribute) {

        // if "extra device" functionality exists, look up attribute based on that
        
        // otherwise, fallback to the type-based device definition lookup
    	PaoPointIdentifier devicePointIdentifier = getPaoPointIdentifierForAttribute(device, attribute);

        LitePoint litePoint = pointService.getPointForDevice(devicePointIdentifier);
        return litePoint;
    }

    public PaoPointIdentifier getPaoPointIdentifierForAttribute(YukonDevice device, Attribute attribute) {

        // if "extra device" functionality exists, look up attribute based on that
        
        // otherwise, fallback to the type-based device definition lookup
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(device.getPaoIdentifier().getPaoType(), builtInAttribute);
        PaoPointIdentifier devicePointIdentifier = attributeDefinition.getPointIdentifier(device);
        return devicePointIdentifier;
    }
    
    public Set<Attribute> getAvailableAttributes(YukonDevice device) {
        Set<Attribute> result = Sets.newHashSet();
        
        // first add type-based attributes
        Set<AttributeDefinition> definedAttributes = paoDefinitionDao.getDefinedAttributes(device.getPaoIdentifier().getPaoType());
        for (AttributeDefinition attributeDefinition : definedAttributes) {
            result.add(attributeDefinition.getAttribute());
        }
        
        // if "extra device" functionality exists, add those attributes to result here
        
        return result;
    }

    public Set<Attribute> getAllExistingAttributes(YukonDevice device) {
        // as written this method is "extra device" safe 
        Set<Attribute> result = Sets.newHashSet();
        Set<Attribute> availableAttribute = this.getAvailableAttributes(device);
        
        for (final Attribute attribute : availableAttribute) {
            try {
                getPointForAttribute(device, attribute);
                result.add(attribute);
            } catch (NotFoundException ignore) { }
        }
        return result;
    }
    
    public Attribute resolveAttributeName(String name) {
        // some day this should also "lookup" user defined attributes
        return BuiltInAttribute.valueOf(name);
    }

    public boolean isAttributeSupported(YukonDevice device, Attribute attribute) {
        boolean result = getAvailableAttributes(device).contains(attribute);
        return result;
    }

    public boolean pointExistsForAttribute(YukonDevice device, Attribute attribute) {

        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        if (isAttributeSupported(device, builtInAttribute)) {
            AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(device.getPaoIdentifier().getPaoType(), builtInAttribute);
            PaoPointIdentifier devicePointIdentifier = attributeDefinition.getPointIdentifier(device);

            return pointService.pointExistsForDevice(devicePointIdentifier);
        }

        throw new IllegalArgumentException("Device: " + device + " does not support attribute: " + attribute.getKey());
    }

    public PaoPointTemplate getDevicePointTemplateForAttribute(YukonDevice device, Attribute attribute) {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(device.getPaoIdentifier().getPaoType(), builtInAttribute);
        if (attributeDefinition.isPointTemplateAvailable()) {
            return attributeDefinition.getPointTemplate(device);
        }
        throw new IllegalUseOfAttribute("Cannot create " + attribute + " on " + device);
    }
    
    public void createPointForAttribute(YukonDevice device, Attribute attribute) {
        

        boolean pointExists = this.pointExistsForAttribute(device, attribute);
        if (!pointExists) {
            PaoPointTemplate devicePointTemplate = getDevicePointTemplateForAttribute(device, attribute);
            PointBase point = pointService.createPoint(devicePointTemplate.getPaoIdentifier().getPaoId(), devicePointTemplate.getPointTemplate());
            try {
                dbPersistentDao.performDBChange(point, Transaction.INSERT);
            } catch (PersistenceException e) {
                // TODO this should throw a different exception
                throw new DataAccessException("Could not create point for device: " + device, e) {};
            }

        }
    }
    
    @Override
    public boolean isPointAttribute(PaoPointIdentifier paoPointIdentifier, Attribute attribute) {
        // the following could probably be optimized, but it is technically correct
        
        PaoIdentifier paoIdentifier = paoPointIdentifier.getPaoIdentifier();
        try {
            PaoPointIdentifier pointForAttribute = getPaoPointIdentifierForAttribute(new SimpleDevice(paoIdentifier), attribute);
            boolean result = pointForAttribute.equals(paoPointIdentifier);
            return result;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
