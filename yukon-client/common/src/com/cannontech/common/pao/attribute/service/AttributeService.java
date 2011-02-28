package com.cannontech.common.pao.attribute.service;

import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.MappableAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.data.lite.LitePoint;

public interface AttributeService {

    /**
     * Method to get the lite point for the given pao for the given attribute
     * @param pao - Pao to get point for
     * @param attribute - Attribute to get point for
     * @return The point for the given attribute
     * @throws IllegalUseOfAttribute
     */
    public LitePoint getPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;

    /**
     * Method to get the paoPointIdentifier for the given deivce for the given attribute.
     * @param pao - Pao to get point for
     * @param attribute - Attribute to get point for
     * @return The paoPointIdentifier for the given attribute and pao
     * @throws IllegalUseOfAttribute if nothing is mapped for a mappable attribute
     * @throws IllegalArgumentException if the pao does not have that attribute
     */
    public PaoPointIdentifier getPaoPointIdentifierForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;

    /**
     * Method to get a set of attributes available for the given pao
     * @param pao - The pao to get attributes for
     * @return An immutable set of all attributes available for this pao
     */
    public Set<Attribute> getAvailableAttributes(YukonPao pao);

    /**
     * Method to get a set of all attributes for which points exist for a given
     * pao
     * @param pao - Pao to get points for
     * @return A set of attributes (returns a new copy each time the method is
     *         called)
     */
    public Set<Attribute> getAllExistingAttributes(YukonPao pao);
    
    /**
     * Returns the set of Mappable Attributes defined for the given 
     * PaoType 
     * @param paoType
     * @return
     */
    public Set<MappableAttribute> getMappableAttributes(PaoType paoType);
    
    public Attribute resolveAttributeName(String name);
    
    /**
     * Method used to determine if a pao supports a given attribute
     * @param pao - Pao in question
     * @param attribute - Attribute to determine support for
     * @return True if the pao supports the attribute
     */
    public boolean isAttributeSupported(YukonPao pao, Attribute attribute);

    /**
     * Method used to determine if a point exists on a pao for a given attribute
     * @param pao - Pao in question
     * @param attribute - Attribute to determine if point exists
     * @return True if the point exists on the pao
     */
    public boolean pointExistsForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;
    
    public PaoPointTemplate getPaoPointTemplateForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;
    
    /**
     * Method used to create a point for the pao and given attribute
     * @param pao - Pao to create point for
     * @param attribute - Attribute the point will represent
     */
    public void createPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;

    public boolean isPointAttribute(PaoPointIdentifier paoPointIdentifier, Attribute usage);
    
    /**
     * Returns a set of attributes for which "reading" makes sense. 
     * 
     * (Currently this is designed to exclude "profile" attributes.)
     */
    public Set<Attribute> getReadableAttributes();
    
    public SqlFragmentSource getAttributeLookupSql(Attribute attribute);
}
