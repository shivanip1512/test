package com.cannontech.rest.api.point;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockAnalogControlType;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockAnalogPoint;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class AnalogPointApiTest extends ScalarPointApiTest {

    @BeforeClass
    public void setUp() {
        pointType = MockPointType.Analog;
        mockPointBase = (MockAnalogPoint) PointHelper.buildPoint(pointType);
    }

    @Test(dependsOnMethods = { "point_01_Create" })
    public void analogPoint_01_Get(ITestContext context) {

        Log.info("Point Id of ANALOG_POINT point created is : "
                + context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getPoint",
                context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());

        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockAnalogPoint analogPointDetail = getResponse.as(MockAnalogPoint.class);

        assertTrue("paoId Should be : " + mockPointBase.getPaoId(), mockPointBase.getPaoId().equals(analogPointDetail.getPaoId()));
        assertTrue("Name Should be : " + mockPointBase.getPointName(),
                   mockPointBase.getPointName().equals(analogPointDetail.getPointName()));
        assertTrue("Point Type Should be : " + mockPointBase.getPointType(),
                   mockPointBase.getPointType().equals(analogPointDetail.getPointType()));
        assertTrue("Point Offset Should be : " + mockPointBase.getPointOffset(),
                   mockPointBase.getPointOffset().equals(analogPointDetail.getPointOffset()));

        assertTrue("Archive Type Should be : " + analogPointDetail.getArchiveType(),
                analogPointDetail.getArchiveType().equals(analogPointDetail.getArchiveType()));
        assertTrue("Timing Group Should be : " + analogPointDetail.getTimingGroup(),
                analogPointDetail.getTimingGroup().equals(analogPointDetail.getTimingGroup()));
        assertTrue("Update Style Should be : " + analogPointDetail.getStaleData().getUpdateStyle(),
                analogPointDetail.getStaleData().getUpdateStyle().equals(analogPointDetail.getStaleData().getUpdateStyle()));
        assertTrue("State Group Id Should be : " + analogPointDetail.getStateGroupId(),
                analogPointDetail.getStateGroupId().equals(analogPointDetail.getStateGroupId()));
        assertTrue("Control Type Should be : " + analogPointDetail.getPointAnalogControl().getControlType(), analogPointDetail
                .getPointAnalogControl().getControlType().equals(analogPointDetail.getPointAnalogControl().getControlType()));

    }

    /**
     * Test case to validate Analog Point cannot be created with invalid Control Offset
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_02_InvalidControlOffset() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(pointType);

        mockAnalogPoint.getPointAnalogControl().setControlType(MockAnalogControlType.NONE);
        mockAnalogPoint.getPointAnalogControl().setControlOffset(76);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "pointAnalogControl.controlOffset",
                        "Control Offset must be 0 when Control Type is None."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with invalid Control Inhibited
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_03_InvalidControlInhibited() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(pointType);

        mockAnalogPoint.getPointAnalogControl().setControlType(MockAnalogControlType.NONE);
        mockAnalogPoint.getPointAnalogControl().setControlInhibited(true);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "pointAnalogControl.controlInhibited",
                        "Control Inhibited must be false when Control type is None."),
                "Expected code in response is not correct");
    }


}