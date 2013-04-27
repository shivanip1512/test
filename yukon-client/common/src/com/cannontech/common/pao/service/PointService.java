package com.cannontech.common.pao.service;

import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;

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
    public LitePoint getPointForPao(YukonPao pao, PointIdentifier pointIdentifier)throws NotFoundException;
    public LitePoint getPointForPao(PaoPointIdentifier paoPointIdentifier)throws NotFoundException;

    /**
     * Method to determine if a point exists for the given device
     * @param device - Device
     * @param template - Template of point to determine existence
     * @return True if point exists for the device
     */
    public boolean pointExistsForPao(YukonPao pao, PointIdentifier pointIdentifier);
    public boolean pointExistsForPao(PaoPointIdentifier devicePointIdentifier);


    public PreviousReadings getPreviousReadings(LitePoint lp);

    /**
     * Returns the count of devices in the passed in DeviceGroup that have an
     * existing point matching Attribute AND state group
     * 
     * @param group
     * @param attribute
     * @param stateGroup
     * @return
     */
    public int getCountOfGroupAttributeStateGroup(DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup);

    /**
     * Returns the paoIds of all devices in the passed in DeviceGroup that have
     * an existing point matching Attribute AND state group
     * 
     * @param group
     * @param attribute
     * @param stateGroup
     * @return
     */
    public List<Integer> getPaoIdsForGroupAttributeStateGroup(DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup);
    public List<Integer> getPaoIdsForGroupAttributeStateGroupId(DeviceGroup group, Attribute attribute, Integer stateGroupId);

    /**
     * 
     * @param group
     * @param attribute
     * @param stateGroup	LiteStateGroup
     * @return
     */
    public List<Integer> findDeviceIdsInGroupWithAttributePointStateGroup(DeviceGroup group, Attribute attribute, LiteStateGroup stateGroup);
    /**
     * 
     * @param group
     * @param attribute
     * @param stateGroupId	Integer
     * @return
     */
    public List<Integer> findDeviceIdsInGroupWithAttributePointStateGroupId(DeviceGroup group, Attribute attribute, Integer stateGroupId);

    /**
     * 
     * @param group
     * @param attribute
     * @param limitToRowCount
     *            int Use this as this query's execution time can grow
     *            considerably depending on the size of the Device Group and all
     *            their attributes.
     * @return
     */
    public int countDevicesInGroupWithAttributePoint(DeviceGroup group, Attribute attribute);
    public int countDevicesInGroupWithAttributePoint(DeviceGroup group, Attribute attribute, int limitToRowCount);

    /**
     * 
     * @param group
     * @param attribute
     * @return
     */
    public List<Integer> findDeviceIdsInGroupWithAttributePoint(DeviceGroup group, Attribute attribute);
}