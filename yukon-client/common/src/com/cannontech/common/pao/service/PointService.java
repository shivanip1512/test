package com.cannontech.common.pao.service;

import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
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
     * @param decimalPlaces - Number of decimal places
     * @return A new point of the given type
     */
    public abstract PointBase createPoint(int type, String name, int paoId, int offset,
            double multiplier, int unitOfMeasure, int stateGroupId, int decimalplaces);

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
    public abstract LitePoint getPointForPao(YukonPao pao, PointIdentifier pointIdentifier)throws NotFoundException;
    public abstract LitePoint getPointForPao(PaoPointIdentifier paoPointIdentifier)throws NotFoundException;

    /**
     * Method to determine if a point exists for the given device
     * @param device - Device
     * @param template - Template of point to determine existence
     * @return True if point exists for the device
     */
    public abstract boolean pointExistsForPao(YukonPao pao, PointIdentifier pointIdentifier);
    public abstract boolean pointExistsForPao(PaoPointIdentifier devicePointIdentifier);
    
    
    public PreviousReadings getPreviousReadings(LitePoint lp);

}