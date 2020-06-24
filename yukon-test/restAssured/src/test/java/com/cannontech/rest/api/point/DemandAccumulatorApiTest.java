package com.cannontech.rest.api.point;

import org.testng.annotations.BeforeClass;

import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockAccumulatorPoint;

public class DemandAccumulatorApiTest extends ScalarPointApiTest {

    @Override
    @BeforeClass
    public void setUp() {
        pointType = MockPointType.DemandAccumulator;
        mockPointBase = (MockAccumulatorPoint) PointHelper.buildPoint(pointType);
    }
}
