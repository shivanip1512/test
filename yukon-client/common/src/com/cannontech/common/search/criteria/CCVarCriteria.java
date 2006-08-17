package com.cannontech.common.search.criteria;

import com.cannontech.database.data.point.PointUnits;

public class CCVarCriteria extends PointDeviceCriteriaAdapter {
    Integer[] unitOfMeasureIds = PointUnits.CAP_CONTROL_VAR_UOMIDS;
    public CCVarCriteria() {
        super();
    }

    public Integer[] getUnitOfMeasureIds() {
        return unitOfMeasureIds;
    }
}
