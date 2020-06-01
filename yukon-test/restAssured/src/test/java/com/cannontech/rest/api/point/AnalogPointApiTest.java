package com.cannontech.rest.api.point;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockAnalogPoint;
import com.cannontech.rest.api.point.request.MockPointBase;
import com.cannontech.rest.api.point.request.MockPointUnit;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

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

        assertTrue("paoId Should be : " + analogPoint.getPaoId(), analogPoint.getPaoId().equals(analogPointDetail.getPaoId()));
        assertTrue("Name Should be : " + analogPoint.getPointName(), analogPoint.getPointName().equals(analogPointDetail.getPointName()));
        assertTrue("Point Type Should be : " + analogPoint.getPointType(), analogPoint.getPointType().equals(analogPointDetail.getPointType()));
        assertTrue("Point Offset Should be : " + analogPoint.getPointOffset(), analogPoint.getPointOffset().equals(analogPointDetail.getPointOffset()));
    }

    /**
     * Test case to validate Analog Point cannot be created as name with null and gets valid error message in response
     */
    @Test
    public void analogPoint_03_NameCannotBeNull() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(MockPointType.Analog);

        mockPointBase.setPointName(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointName", "pointName is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with name having more than 60 characters and validates valid error
     * message in response
     */
    @Test
    public void analogPoint_04_NameGreaterThanMaxLength() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(MockPointType.Analog);

        mockPointBase.setPointName("Poinmdmdldslldlkdlkdddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointName", "Exceeds maximum length of 60."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created as paoId with null and gets valid error message
     * in response
     */
    @Test
    public void analogPoint_05_NullPaoId() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(MockPointType.Analog);

        mockPointBase.setPaoId(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "paoId", "paoId is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with Invalid Pao Id and gets valid error message in response
     */
    @Test
    public void analogPoint_05_InvalidPaoId() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(MockPointType.Analog);

        mockPointBase.setPaoId(99999999);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "paoId", "Pao Id does not exist."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with Invalid Uom Id and gets valid error message in response
     */
    @Test
    public void analogPoint_06_InvalidUomId() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(MockPointType.Analog);

        MockPointUnit pointUnit = MockPointUnit.builder().uomId(100).build();
        mockPointBase.setPointUnit(pointUnit);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointUnit.uomID", "Uom Id does not exist."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with name having special characters and validates valid error message
     * in response
     */
    @Test
    public void analogPoint_07_NameWithSpecialChars() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(MockPointType.Analog);

        mockPointBase.setPointName("Test,//Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "pointName",
                        "Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be cannot be created from null value of point offset and validates valid error
     * message in response
     */
    @Test
    public void analogPoint_07_NullPointOffset() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(MockPointType.Analog);

        mockPointBase.setPointOffset(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "pointOffset", "pointOffset is required."),
                "Expected code in response is not correct");
    }
}