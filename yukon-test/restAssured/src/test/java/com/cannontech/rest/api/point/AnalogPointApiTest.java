package com.cannontech.rest.api.point;

import static org.junit.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockAnalogPoint;
import com.cannontech.rest.api.point.request.MockPointBase;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class AnalogPointApiTest {
    MockPointBase analogPoint = null;

    @BeforeClass
    public void setUp() {
        analogPoint = (MockPointBase) PointHelper.buildPoint(MockPointType.Analog);
    }

    @Test
    public void analogPoint_01_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", analogPoint);
        String pointId = createResponse.asString();
        context.setAttribute(PointHelper.CONTEXT_POINT_ID, pointId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Point Id should not be Null", pointId != null);
    }

    @Test(dependsOnMethods = { "analogPoint_01_Create" })
    public void analogPoint_02_Get(ITestContext context) {

        Log.info("Point Id of ANALOG_POINT point created is : "
                + context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getPoint",
                context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());

        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockAnalogPoint analogPointDetail = getResponse.as(MockAnalogPoint.class);

        assertTrue("poaId Should be : " + analogPoint.getPaoId(), analogPoint.getPaoId().equals(analogPointDetail.getPaoId()));
        assertTrue("Name Should be : " + analogPoint.getPointName(), analogPoint.getPointName().equals(analogPointDetail.getPointName()));
        assertTrue("Point Type Should be : " + analogPoint.getPointType(), analogPoint.getPointType().equals(analogPointDetail.getPointType()));
        assertTrue("Point Offset Should be : " + analogPoint.getPointOffset(), analogPoint.getPointOffset().equals(analogPointDetail.getPointOffset()));
    }

}