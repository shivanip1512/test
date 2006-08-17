package com.cannontech.common.search.criteria;

import com.cannontech.database.data.point.PointUnits;

public class CCVoltCriteria extends PointDeviceCriteriaAdapter {
    Integer[] unitOfMeasureIds = PointUnits.CAP_CONTROL_VOLTS_UOMIDS;
    public CCVoltCriteria() {
        super();
    }

    public Integer[] getUnitOfMeasureIds() {
        return unitOfMeasureIds;
    }
}
