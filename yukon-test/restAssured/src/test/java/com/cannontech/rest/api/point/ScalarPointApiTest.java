package com.cannontech.rest.api.point;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockPointUnit;
import com.cannontech.rest.api.point.request.MockScalarPoint;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public abstract class ScalarPointApiTest extends PointApiTestBase {

    /**
     * Test case to validate Point cannot be created with invalid Decimal Places
     * and validates valid error message in response
     */
    @Test
    public void scalarPoint_01_InvalidDecimalPlaces() {
        MockScalarPoint mockScalarPoint = (MockScalarPoint) PointHelper.buildPoint(pointType);

        mockScalarPoint.getPointUnit().setDecimalPlaces(12);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockScalarPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointUnit.decimalPlaces", "Must be between 0 and 10."),
                "Expected code in response is not correct");
    }

    
    /**
     * Test case to validate Point cannot be created with Invalid Uom Id and gets valid error message
     * in response
     */
    @Test
    public void scalarPoint_02_InvalidUomId() {
        MockScalarPoint mockScalarPoint = (MockScalarPoint) PointHelper.buildPoint(pointType);

        MockPointUnit pointUnit = MockPointUnit.builder().uomId(100).build();
        mockScalarPoint.setPointUnit(pointUnit);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockScalarPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointUnit.uomId", "Uom Id does not exist."),
                "Expected code in response is not correct");
    }

}
