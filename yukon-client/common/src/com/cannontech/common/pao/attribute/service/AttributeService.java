package com.cannontech.common.pao.attribute.service;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.MappableAttribute;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
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
     * Method to get the PaoPointIdentifier for the given PAO for the given attribute.
     * 
     * This method will not require a database lookup for non-mapped Attributes.
     * 
     * @param pao - Pao to get point for
     * @param attribute - Attribute to get point for
     * @return The paoPointIdentifier for the given attribute and pao
     * @throws IllegalUseOfAttribute if nothing is mapped for a mappable attribute
     * @throws IllegalArgumentException if the pao does not have that attribute
     */
    public PaoPointIdentifier getPaoPointIdentifierForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;
    
    /**
     * Method to get the PaoPointIdentifier for the given PAO for the given attribute.
     * 
     * This is a temporary method designed to be used by code that wishes to assert
     * that it does not support mapped attributes. This is useful because attributes
     * will be redefined in the 5.4 scope to no longer support the concept of "mapped".
     * At that time, this method and the one above can be merged.
     * 
     * @param pao - Pao to get point for
     * @param attribute - Attribute to get point for
     * @return The paoPointIdentifier for the given attribute and pao
     * @throws IllegalUseOfAttribute if nothing is mapped for a mappable attribute
     * @throws IllegalArgumentException if the pao does not have that attribute
     */
    public PaoPointIdentifier getPaoPointIdentifierForNonMappedAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;
    
    
    /**
     * This method returns a list of PaoMultiPointIdentifier objects for of the passed in PAO that has a point
     * for at least one of the specified attributes.
     * 
     * Like the above method, this is a transitional method that assumes attributes are unmapped.
     * @param devices
     * @param attributes
     * @return list of PaoMultiPointIdentifiers
     */
    public List<PaoMultiPointIdentifier> findPaoMultiPointIdentifiersForNonMappedAttributes(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes);
    
    /**
     * This method returns a list of PaoMultiPointIdentifier objects for of the passed in PAO that has a point
     * for at least one of the specified attributes.
     * 
     * Like the above method, this is a transitional method that assumes attributes are unmapped.
     * @param devices
     * @param attributes
     * @return list of PaoMultiPointIdentifiers
     * @throws IllegalUseOfAttribute if nothing is mapped for a mappable attribute
     */
    public List<PaoMultiPointIdentifier> getPaoMultiPointIdentifiersForNonMappedAttributes(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes) throws IllegalUseOfAttribute;
    
    /**
     * Method to get a set of attributes available for the given pao
     * @param pao - The pao to get attributes for
     * @return An immutable set of all attributes available for this pao
     */
    public Set<Attribute> getAvailableAttributes(YukonPao pao);
    
    /**
     * Method to get a set of attributes available for the given PaoType
     * @param paoType - The PaoType to get attributes for
     * @return An immutable set of all attributes available for this paoType
     */
    public Set<Attribute> getAvailableAttributes(PaoType paoType);

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

    /**
     * This method will determine if the point identified by the PaoPointIdentifier is an
     * Attribute of the given type for the PAO identified by the PaoPointIdentifier. There is
     * nothing wrong with asking this question, but it would be more correct to call the other
     * version of this method that breaks the PAO and the Point into two parameters.
     */
    public boolean isPointAttribute(PaoPointIdentifier paoPointIdentifier, Attribute attribute);
    
    /**
     * Returns a set of attributes for which "reading" makes sense. 
     * 
     * (Currently this is designed to exclude "profile" attributes.)
     */
    public Set<Attribute> getReadableAttributes();
    
    /**
     * Returns the Sql that gives you the paObjectId and pointId based on a
     * passed in Attribute
     * @param attribute
     */
    public SqlFragmentSource getAttributeLookupSql(Attribute attribute);

    /**
     * Returns a list of all the devices in a given DeviceGroup that support the given Attribute.
     * This method works recursively on each child group of the requested group.
     * @param group
     * @param attribute
     * @return
     */
    public List<SimpleDevice> getDevicesInGroupThatSupportAttribute(DeviceGroup group, Attribute attribute);

}
