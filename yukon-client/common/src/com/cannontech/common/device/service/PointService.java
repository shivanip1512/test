package com.cannontech.common.device.service;

import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;

/**
 * Class which provides functionality for manipulating and creating points
 */
public interface PointService {

    /**
     * Method to create a point
     * @param type - Type of point
     * @param name - Name of point
     * @param paoId - Id of parent device
     * @param offset - Offset of point
     * @param multiplier - Multiplier for point
     * @param unitOfMeasure - Unit of measure for point
     * @param stateGroupId - State group id for point
     * @return A new point of the given type
     */
    public abstract PointBase createPoint(int type, String name, int paoId, int offset,
            double multiplier, int unitOfMeasure, int stateGroupId);

    /**
     * Method to create a point based on a point template
     * @param paoId - Id of the pao object to create the point for
     * @param template - Template for point to be created
     * @return A new point for the pao object based on the template
     */
    public abstract PointBase createPoint(int paoId, PointTemplate template);

    /**
     * Method to get an existing point for a device based on a template
     * @param device - Device to get point for
     * @param template - Template of point to get
     * @return - Existing point
     */
    public abstract LitePoint getPointForDevice(LiteYukonPAObject device, PointTemplate template);

    /**
     * Method to determine if a point exists for the given device
     * @param device - Device
     * @param template - Template of point to determine existance
     * @return True if point exists for the device
     */
    public abstract boolean pointExistsForDevice(LiteYukonPAObject device, PointTemplate template);

}