package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.google.common.collect.Table;

public interface UnitMeasureDao {

    /**
     * Returns the unit of measure information for a given point id.
     * If the point does not have a unit of measure defined (status
     * points and system points don't), this method will return null).
     * @param pointID
     * @return Unit of measure, or null if it doesn't exist
     */
    UnitOfMeasure getUnitMeasureByPointID(int pointID);
    
    /**
     * Returns a mapping of paoId and point to a UnitOfMeasure for all paos in list
     */
    Table<Integer, PointIdentifier, UnitOfMeasure> getUnitMeasureByPaoIdAndPoint(List<? extends YukonPao> paos);

}