package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.google.common.collect.Table;

public interface UnitMeasureDao {

    /**
     * Returns a list of all available lite unit measure objects
     */
    List<LiteUnitMeasure> getLiteUnitMeasures();
    
    /**
     * Returns the unit of measure information for a given point id.
     * If the point does not have a unit of measure defined (status
     * points and system points don't), this method will return null).
     * @param pointID
     * @return LiteUnitMeasure or null if it doesn't exist
     */
    LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID);

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint
     * @param pointID int
     */
    LiteUnitMeasure getLiteUnitMeasure(int uomid);
    
    /**
     * Method to get a unit of measure by name
     * @param uomName - Name of unit of measure
     */
    LiteUnitMeasure getLiteUnitMeasure(String uomName);

    /**
     * Returns a mapping of paoId and point to a LiteUnitMeasure for all paos in list
     */
    Table<Integer, PointIdentifier, LiteUnitMeasure> getLiteUnitMeasureByPaoIdAndPoint(List<? extends YukonPao> paos);

}