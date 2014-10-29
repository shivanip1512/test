package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteUnitMeasure;

public interface UnitMeasureDao {

    /**
     * Returns a list of all available lite unit measure objects
     * @return
     */
    public List<LiteUnitMeasure> getLiteUnitMeasures();
    
    /**
     * Returns the unit of measure information for a given point id.
     * If the point does not have a unit of measure defined (status
     * points and system points don't), this method will return null).
     * @param pointID
     * @return LiteUnitMeasure or null if it doesn't exist
     */
    public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID);

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint
     * @param pointID int
     */
    public LiteUnitMeasure getLiteUnitMeasure(int uomid);
    
    /**
     * Method to get a unit of measure by name
     * @param uomName - Name of unit of measure
     * @return - A unit of measure
     */
    public LiteUnitMeasure getLiteUnitMeasure(String uomName);

    LiteUnitMeasure getLiteUnitMeasureByPaoIdAndPointOffset(int paoId, int pointOffset);

}