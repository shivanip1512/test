package com.cannontech.common.pao.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;

/**
 * Class which provides functionality for manipulating points
 */
public interface PointService {

    /**
     * Method to get an existing point for a device based on a template
     * @param device - Device to get point for
     * @param template - Template of point to get
     * @return - Existing point
     */
    LitePoint getPointForPao(YukonPao pao, PointIdentifier pointIdentifier)throws NotFoundException;
    LitePoint getPointForPao(PaoPointIdentifier paoPointIdentifier)throws NotFoundException;

    /**
     * Method to determine if a point exists for the given device
     * @param device - Device
     * @param template - Template of point to determine existence
     * @return True if point exists for the device
     */
    boolean pointExistsForPao(YukonPao pao, PointIdentifier pointIdentifier);
    boolean pointExistsForPao(PaoPointIdentifier devicePointIdentifier);


    PreviousReadings getPreviousReadings(LitePoint lp);

    /**
     * Returns the count of devices in the passed in DeviceGroup that have an
     * existing point matching Attribute AND state group
     * 
     * @param group
     * @param attribute
     * @param stateGroup
     * @return
     */
    int getCountOfGroupAttributeStateGroup(DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup);

    /**
     * Returns the paoIds of all devices in the passed in DeviceGroup that have
     * an existing point matching Attribute AND state group
     * 
     * @param group
     * @param attribute
     * @param stateGroup
     * @return
     */
    List<Integer> getPaoIdsForGroupAttributeStateGroup(DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup);
    List<Integer> getPaoIdsForGroupAttributeStateGroupId(DeviceGroup group, Attribute attribute, Integer stateGroupId);

    /**
     * 
     * @param group
     * @param attribute
     * @param stateGroup	LiteStateGroup
     * @return
     */
    List<Integer> findDeviceIdsInGroupWithAttributePointStateGroup(DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup);
    /**
     * 
     * @param group
     * @param attribute
     * @param stateGroupId	Integer
     * @return
     */
    List<Integer> findDeviceIdsInGroupWithAttributePointStateGroupId(DeviceGroup group, Attribute attribute, Integer stateGroupId);

    /**
     * 
     * @param group
     * @param attribute
     * @param limitToRowCount   int     Use this as this query's execution time can grow considerably
     *                                  depending on the size of the Device Group and all their attributes.
     * @return
     */
    int getCountDevicesInGroupWithAttributePoint(DeviceGroup group, Attribute attribute);
    int getCountDevicesInGroupWithAttributePoint(DeviceGroup group, Attribute attribute, int limitToRowCount);

    /**
     * Out of the given Device Group, this returns only IDs which have both the given attribute and 
     * the respective point for the attribute.
     * 
     * @param group         DeviceGroup
     * @param attribute     Attribute
     * @return
     */
    List<Integer> findDeviceIdsInGroupWithAttributePoint(DeviceGroup group, Attribute attribute);
    
    /**
     * Returns a {@link LinkedHashMap} of point type to point list for all points on the pao.
     * The map keys (point types) are sorted alphabetically by their localized name.
     * Each map value (List of LitePoints) is also sorted alphabetically by point name. 
     */
    Map<PointType, List<LitePoint>> getSortedPointTree(int paoId, MessageSourceAccessor accessor);
}