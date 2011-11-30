package com.cannontech.common.pao.service;

import com.cannontech.common.pao.definition.model.CalcPointBase;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;

public interface PointCreationService {

	/**
     * Method to create a point
     * @param type - Type of point
     * @param name - Name of point
     * @param paoId - Id of parent device
     * @param offset - Offset of point
     * @param multiplier - Multiplier for point
     * @param unitOfMeasure - Unit of measure for point
     * @param stateGroupId - State group id for point
     * @param initialState - starting state for point
     * @param decimalPlaces - Number of decimal places
     * @param pointArchiveType - Type of archiving
     * @param pointArchiveInterval - Interval of archiving
     * @param calcPoint - Calc Point attributes
     * @return A new point of the given type
     */
    public PointBase createPoint(int type, String name, int paoId, int offset,
            double multiplier, int unitOfMeasure, int stateGroupId, int initialState, int decimalplaces, ControlType controlType, PointArchiveType pointArchiveType, PointArchiveInterval pointArchiveInterval, CalcPointBase calcPoint);

    /**
     * Method to create a point based on a point template
     * @param paoId - Id of the pao object to create the point for
     * @param template - Template for point to be created
     * @return A new point for the pao object based on the template
     */
    public PointBase createPoint(int paoId, PointTemplate template);
}
