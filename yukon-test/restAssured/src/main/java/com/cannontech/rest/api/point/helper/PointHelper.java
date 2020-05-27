package com.cannontech.rest.api.point.helper;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.ApiUtils;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.request.MockPointBase;
import com.cannontech.rest.api.point.request.MockPointUnit;

public class PointHelper {
    public final static String CONTEXT_POINT_ID = "pointId";

    public final static MockPointBase buildPoint(MockPointType pointType) {
        MockPointBase point = null;
        Integer paoId = Integer.valueOf(ApiCallHelper.getProperty("meterNumber"));
        Integer pointOffset = Integer.valueOf(ApiCallHelper.getProperty("pointOffset"));
        Integer uomId = Integer.valueOf(ApiCallHelper.getProperty("uomId"));
        String name = ApiUtils.buildFriendlyNameForPoint(pointType, "POINT", " Test Point");
        MockPointUnit pointUnit  = MockPointUnit.builder().uomId(uomId).build();
        switch (pointType) {
        case Analog:
            point = MockPointBase.builder()
                    .paoId(paoId)
                    .pointName(name)
                    .pointType(pointType.name())
                    .pointOffset(pointOffset)
                    .pointUnit(pointUnit)
                    .build();
            break;
        default:
            break;
        }
        return point;
    }

}