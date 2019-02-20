package com.cannontech.common.pao.attribute.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.BiMap;
import com.google.common.collect.Multimap;

public interface AttributeService {

    /**
     * Method to get the lite point for the given pao for the given attribute
     * 
     * @param pao - Pao to get point for
     * @param attribute - Attribute to get point for
     * @return The point for the given attribute
     * @throws IllegalUseOfAttribute
     */
    LitePoint getPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;

    /**
     * Method to find the lite point for the given pao for the given attribute
     * Returns a value of null if a point cannot be found for the attribute.
     * Returns a value of null if an attribute is not supported by pao.
     * @param pao - Pao to get point for
     * @param attribute - Attribute to get point for
     * @return The point for the given attribute or null (if point doesn't exist or attribute not supported).
     */
    LitePoint findPointForAttribute(YukonPao pao, Attribute attribute);

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
    PaoPointIdentifier getPaoPointIdentifierForAttribute(YukonPao pao, Attribute attribute)
            throws IllegalUseOfAttribute;

    /**
     * This method returns a PaoPointIdentifier object if the passed in PAO has a point identifier for the specified attribute.
     * 
     * @return an optional PaoPointIdentifier
     */
    Optional<PaoPointIdentifier> findPaoPointIdentifierForAttribute(PaoIdentifier pao, BuiltInAttribute attribute);


    List<PointIdentifier> findPointsForDevicesAndAttribute(Iterable<? extends YukonPao> devices, Attribute attributes);

    /**
     * Method to get a set of attributes available for the given pao
     * 
     * @param pao - The pao to get attributes for
     * @return An immutable set of all attributes available for this pao
     */
    Set<Attribute> getAvailableAttributes(YukonPao pao);

    /**
     * Method to get a set of attributes available for the given PaoType
     * 
     * @param paoType - The PaoType to get attributes for
     * @return An immutable set of all attributes available for this paoType
     */
    Set<Attribute> getAvailableAttributes(PaoType paoType);

    /**
     * Method to get a set of all attributes from the set of desired attributes for a given pao.
     * *** This will hit the database for each desiredAttribute. Future may be to optimize this method to only
     * do one db hit.
     * 
     * @param pao
     * @param attributes
     * @return A set of attributes (from desiredAttributes) that exist for a given pao.
     */
    Set<Attribute> getExistingAttributes(YukonPao pao, Set<? extends Attribute> desiredAttributes);

    Attribute resolveAttributeName(String name);

    /**
     * Method used to determine if a pao supports a given attribute
     * 
     * @param pao - Pao in question
     * @param attribute - Attribute to determine support for
     * @return True if the pao supports the attribute
     */
    boolean isAttributeSupported(YukonPao pao, Attribute attribute);

    /**
     * Method used to determine if a point exists on a pao for a given attribute
     * 
     * @param pao - Pao in question
     * @param attribute - Attribute to determine if point exists
     * @return True if the point exists on the pao
     */
    boolean pointExistsForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;

    PaoPointTemplate getPaoPointTemplateForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;

    /**
     * Method used to create a point for the pao and given attribute
     * 
     * @param pao - Pao to create point for
     * @param attribute - Attribute the point will represent
     * @return A boolean that is true if the point was created and false if it was not created or already existed.
     */
    boolean createPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute;

    /**
     * This method will determine if the point identified by the PaoPointIdentifier is an
     * Attribute of the given type for the PAO identified by the PaoPointIdentifier. There is
     * nothing wrong with asking this question, but it would be more correct to call the other
     * version of this method that breaks the PAO and the Point into two parameters.
     */
    boolean isPointAttribute(PaoPointIdentifier paoPointIdentifier, Attribute attribute);

    /**
     * Returns a set of attributes for which "reading" makes sense.
     *
     * (Currently this is designed to exclude "profile" attributes.)
     */
    Set<Attribute> getReadableAttributes();

    /**
     * Returns a set of attributes for which "reading" makes sense.
     *
     * (Includes "profile" attributes.)
     */
    Set<Attribute> getAdvancedReadableAttributes();

    /**
     * Returns a list of all the devices in a given DeviceGroup that support the given Attribute.
     * This method works recursively on each child group of the requested group.
     * 
     * @param group
     * @param attribute
     * @return
     */
    List<SimpleDevice> getDevicesInGroupThatSupportAttribute(DeviceGroup group, Attribute attribute);

    /**
     * Reverse lookup of BuiltInAttribute based on Pao and Point Identifier from set of possible attributes.
     * Will return empty set if no attributes in possible set matches the pao point identifier definition.
     * Used primarily by MultispeakMeterService to retrieve readable attributes.
     * 
     * @param paoTypePointIdentifier
     * @param possibleMatches
     * @return BuiltInAttribute
     */
    Set<BuiltInAttribute> findAttributesForPoint(PaoTypePointIdentifier paoTypePointIdentifier,
            Set<? extends Attribute> possibleMatches);

    /**
     * Creates a map of AttributeGroup to list of BuiltInAttribute. The resulting map contains only
     * those attributes that are in the 'attributes' collection that is passed in.
     *
     * @param attributes The collection of Attribute objects that will be made into a grouped attribute map.
     * @param userContext Used to sort the attributes and AttributeGroup option groups according to the user's
     *        locale.
     */
    Map<AttributeGroup, List<BuiltInAttribute>> getGroupedAttributeMapFromCollection(
            Collection<? extends Attribute> attributes, YukonUserContext userContext);

    /**
     * Returns a comparator for attributes resolved name
     *
     * @param context
     * @return
     */
    Comparator<Attribute> getNameComparator(YukonUserContext context);

    /**
     * Resolves all BuiltInAttributes and returns a sorted map
     *
     * @param bins
     * @param context
     * @return
     */
    SortedMap<BuiltInAttribute, String> resolveAllToString(Set<BuiltInAttribute> bins, YukonUserContext context);

    PaoTypePointIdentifier getPaoTypePointIdentifierForAttribute(PaoType type, Attribute attribute)
            throws IllegalUseOfAttribute;

    /**
     * Return a list of state groups for the named device group and attribute.
     * Different device types can have different points mapped to the same attribute
     * so that even for a unique attribute it is possible to have multiple state groups.
     * 
     * @since 5.6.4
     *
     * @param groupName Must be findable in the database, eg. "/Group1 Meters"
     * @param attribute
     *
     * @return List<LiteStateGroup> The list of state groups or an empty list.
     */
    List<LiteStateGroup> findStateGroups(String groupName, BuiltInAttribute attribute);

    /**
     * Return a list of state groups for the devices and attribute.
     * Different device types can have different points mapped to the same attribute
     * so that even for a unique attribute it is possible to have multiple state groups.
     * 
     * @since 6.2.0
     *
     * @return List<LiteStateGroup> The list of state groups or an empty list.
     */
    List<LiteStateGroup> findStateGroups(List<SimpleDevice> devices, BuiltInAttribute attribute);

    /**
     * Returns the list of point ids that map to the attribute and pao type for each device.
     * Ignores devices that do not support the attribute.
     */
    List<Integer> getPointIds(Iterable<SimpleDevice> devices, BuiltInAttribute attribute);

    /**
     * Returns a map of {@link PaoIdentifier} to {@link LitePoint} for the point that maps
     * to the attribute and pao type for each device.
     * Ignores devices that do not support the attribute.
     */
    BiMap<PaoIdentifier, LitePoint> getPoints(Iterable<? extends YukonPao> devices, BuiltInAttribute attribute);

    /**
     * This method returns a list of PaoMultiPointIdentifier objects for of the passed in PAO that has a point
     * for at least one of the specified attributes. This method also returns a list of devices that doesn't
     * support one of the attributes.
     */
    PaoMultiPointIdentifierWithUnsupported findPaoMultiPointIdentifiersForAttributesWithUnsupported(
            Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes);
    
    /**
     * This method returns a list of PaoMultiPointIdentifier objects for of the passed in PAO that has a point
     * for at least one of the specified attributes.
     *
     * Like the above method, this is a transitional method that assumes attributes are unmapped.
     */
    List<PaoMultiPointIdentifier> findPaoMultiPointIdentifiersForAttributes(Iterable<? extends YukonPao> devices,
            Set<? extends Attribute> attributes);

    /**
     * Returns list of devices and supported attributes.
     * 
     * @param group - contains devices
     * @param attributes - attributes to check
     * @param deviceIds - If null checks all the devices in a group otherwise checks only devices listed.
     */
    Multimap<BuiltInAttribute, SimpleDevice> getDevicesInGroupThatSupportAttribute(DeviceGroup group,
            List<BuiltInAttribute> attributes, List<Integer> deviceIds);
}