package com.cannontech.common.search.criteria;

import com.cannontech.database.data.point.PointUnits;

public class KWCriteria extends PointDeviceCriteriaAdapter {
    Integer[] unitOfMeasureIds = new Integer[] {PointUnits.UOMID_KW};
    public KWCriteria() {
        super();
        
    }
    
    public Integer[] getUnitOfMeasureIds() {
        return unitOfMeasureIds;
    }

}
