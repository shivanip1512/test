package com.cannontech.common.pao.attribute.service;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.model.MappableAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.attribute.lookup.MappedAttributeDefinition;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class AttributeServiceImpl implements AttributeService {

    private DBPersistentDao dbPersistentDao = null;
    private PaoDefinitionDao paoDefinitionDao = null;
    private PointService pointService = null;
    
    private Set<Attribute> readableAttributes;
    {
    	EnumSet<BuiltInAttribute> nonProfiledAttributes = EnumSet.noneOf(BuiltInAttribute.class);
    	for (BuiltInAttribute attribute : BuiltInAttribute.values()) {
    		if (!attribute.isProfile()) nonProfiledAttributes.add(attribute);
    	}
    	// could consider other factors and handle user defined attributes in the future
    	readableAttributes = ImmutableSet.<Attribute>copyOf(nonProfiledAttributes);
    }

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

    public LitePoint getPointForAttribute(YukonPao pao, Attribute attribute) {
        try {
            PaoPointIdentifier paoPointIdentifier = getPaoPointIdentifierForAttribute(pao, attribute);

            LitePoint litePoint = pointService.getPointForPao(paoPointIdentifier);
        
            return litePoint;
        } catch (NotFoundException nfe) {
            throw new IllegalUseOfAttribute("Illegal use of attribute: " + attribute.getDescription());
        }
    }

    public PaoPointIdentifier getPaoPointIdentifierForAttribute(YukonPao pao, Attribute attribute) {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
        try {
        PaoPointIdentifier paoPointIdentifier = attributeDefinition.getPointIdentifier(pao);
        return paoPointIdentifier;
        } catch (NotFoundException nfe) {
            throw new IllegalUseOfAttribute("Illegal use of attribute: " + attribute.getDescription());
        }
    }
    
    public Set<Attribute> getAvailableAttributes(YukonPao pao) {
        Set<Attribute> result = Sets.newHashSet();
        
        // first add type-based attributes
        Set<AttributeDefinition> definedAttributes = paoDefinitionDao.getDefinedAttributes(pao.getPaoIdentifier().getPaoType());
        for (AttributeDefinition attributeDefinition : definedAttributes) {
            result.add(attributeDefinition.getAttribute());
        }
        
        return result;
    }

    public Set<Attribute> getAllExistingAttributes(YukonPao pao) {
        Set<Attribute> result = Sets.newHashSet();
        Set<Attribute> availableAttribute = this.getAvailableAttributes(pao);
        
        for (final Attribute attribute : availableAttribute) {
            try {
                getPointForAttribute(pao, attribute);
                result.add(attribute);
            } catch (IllegalUseOfAttribute ignore) { }
        }
        return result;
    }
    
    public Attribute resolveAttributeName(String name) {
        // some day this should also "lookup" user defined attributes
        return BuiltInAttribute.valueOf(name);
    }

    public boolean isAttributeSupported(YukonPao pao, Attribute attribute) {
        boolean result = getAvailableAttributes(pao).contains(attribute);
        return result;
    }

    public boolean pointExistsForAttribute(YukonPao pao, Attribute attribute) {

        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        if (isAttributeSupported(pao, builtInAttribute)) {
            AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
            PaoPointIdentifier paoPointIdentifier = attributeDefinition.getPointIdentifier(pao);

            return pointService.pointExistsForPao(paoPointIdentifier);
        }

        throw new IllegalArgumentException("Pao: " + pao + " does not support attribute: " + attribute.getKey());
    }

    public PaoPointTemplate getPaoPointTemplateForAttribute(YukonPao pao, Attribute attribute) {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
        if (attributeDefinition.isPointTemplateAvailable()) {
            return attributeDefinition.getPointTemplate(pao);
        }
        throw new IllegalUseOfAttribute("Cannot create " + attribute + " on " + pao);
    }
    
    public void createPointForAttribute(YukonPao pao, Attribute attribute) {
        

        boolean pointExists = this.pointExistsForAttribute(pao, attribute);
        if (!pointExists) {
            PaoPointTemplate paoPointTemplate = getPaoPointTemplateForAttribute(pao, attribute);
            PointBase point = pointService.createPoint(paoPointTemplate.getPaoIdentifier().getPaoId(), paoPointTemplate.getPointTemplate());
            try {
                dbPersistentDao.performDBChange(point, Transaction.INSERT);
            } catch (PersistenceException e) {
                // TODO this should throw a different exception
                throw new DataAccessException("Could not create point for pao: " + pao, e) {};
            }

        }
    }
    
    @Override
    public boolean isPointAttribute(PaoPointIdentifier paoPointIdentifier, Attribute attribute) {
        // the following could probably be optimized, but it is technically correct
        
        PaoIdentifier paoIdentifier = paoPointIdentifier.getPaoIdentifier();
        try {
            PaoPointIdentifier pointForAttribute = getPaoPointIdentifierForAttribute(paoIdentifier, attribute);
            boolean result = pointForAttribute.equals(paoPointIdentifier);
            return result;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Set<MappableAttribute> getMappableAttributes(PaoType paoType) {
        Set<MappableAttribute> result = Sets.newHashSet();
        
        Set<AttributeDefinition> definedAttributes = paoDefinitionDao.getDefinedAttributes(paoType);
        for (AttributeDefinition attributeDefinition : definedAttributes) {
            if( attributeDefinition instanceof MappedAttributeDefinition) {
                MappedAttributeDefinition mappedAttributeDefinition = (MappedAttributeDefinition) attributeDefinition;
                result.add(new MappableAttribute(mappedAttributeDefinition.getAttribute(), mappedAttributeDefinition.getPointType()));
            }
        }
        
        return result;
    }
    
    @Override
    public Set<Attribute> getReadableAttributes() {
    	return readableAttributes;
    }

}