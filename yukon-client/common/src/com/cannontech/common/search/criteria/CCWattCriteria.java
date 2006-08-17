package com.cannontech.common.search.criteria;

import com.cannontech.database.data.point.PointUnits;

public class CCWattCriteria extends PointDeviceCriteriaAdapter {
    Integer[] unitOfMeasureIds = PointUnits.CAP_CONTROL_WATTS_UOMIDS;
    public CCWattCriteria() {
        super();
    }
    
    public Integer[] getUnitOfMeasureIds() {
        return unitOfMeasureIds;
    }

}
