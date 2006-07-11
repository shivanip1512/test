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
     * @param pointID
     * @return LiteUnitMeasure
     */
    public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID);

    /**
     * Insert the method's description here.
     * Creation date: (3/26/2001 9:41:59 AM)
     * @return com.cannontech.database.data.lite.LitePoint
     * @param pointID int
     */
    public LiteUnitMeasure getLiteUnitMeasure(int uomid);

}