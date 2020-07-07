package com.cannontech.rest.api.point;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPointArchiveType;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockStatusControlType;
import com.cannontech.rest.api.point.request.MockStatusPoint;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class StatusPointApiTest extends PointApiTestBase {

    @Override
    @BeforeClass
    public void setUp() {
        pointType = MockPointType.Status;
        mockPointBase = (MockStatusPoint) PointHelper.buildPoint(pointType);
    }


    /**
     * Test case to validate invalid State Id and validates valid error message in
     * response
     */
    @Test
    public void statusPoint_1_InvalidInitialState() {
        MockStatusPoint mockStatusPoint = (MockStatusPoint) PointHelper.buildPoint(pointType);

        mockStatusPoint.setInitialState(5);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockStatusPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "initialState", "InitialState 5 is not valid value for State GroupId " + mockStatusPoint.getStateGroupId()+ "."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Status Point cannot be created with invalid Archive type and validates valid error
     * message in response
     */
    @Test
    public void statusPoint_2_InvalidArchiveType() {
        MockStatusPoint mockStatusPoint = (MockStatusPoint) PointHelper.buildPoint(pointType);

        mockStatusPoint.setArchiveType(MockPointArchiveType.ON_TIMER);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockStatusPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                                                       "archiveType",
                                                       "ArchiveType ON_TIMER is not valid for Point Type Status."),
                   "Expected code in response is not correct");
    }
    
    
    /**
     * Test case to validate Status Point cannot be created with invalid control Offset and validates valid error
     * message in response
     */
    @Test
    public void statusPoint_3_InvalidDefaultControlOffset() {
        MockStatusPoint mockStatusPoint = (MockStatusPoint) PointHelper.buildPoint(pointType);

        mockStatusPoint.getPointStatusControl().setControlOffset(5);
        mockStatusPoint.getPointStatusControl().setControlType(MockStatusControlType.NONE);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockStatusPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                                                       "pointStatusControl.controlOffset",
                                                       "Control Offset must be 0 when Control Type is None."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate open command length (valid length <101 ) and validates valid error
     * message in response
     */
    @Test
    public void statusPoint_4_InvalidOpenCommandLength() {
        MockStatusPoint mockStatusPoint = (MockStatusPoint) PointHelper.buildPoint(pointType);

        mockStatusPoint.getPointStatusControl()
                       .setOpenCommand("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        mockStatusPoint.getPointStatusControl().setControlType(MockStatusControlType.NORMAL);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockStatusPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                                                       "pointStatusControl.openCommand",
                                                       "Exceeds maximum length of 100."),
                   "Expected code in response is not correct");
    }
    
    
    /**
     * Test case to validate close command length (valid length <101 ) and validates valid error
     * message in response
     */
    @Test
    public void statusPoint_5_InvalidCloseCommandLength() {
        MockStatusPoint mockStatusPoint = (MockStatusPoint) PointHelper.buildPoint(pointType);

        mockStatusPoint.getPointStatusControl()
                       .setCloseCommand("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        mockStatusPoint.getPointStatusControl().setControlType(MockStatusControlType.NORMAL);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockStatusPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                                                       "pointStatusControl.closeCommand",
                                                       "Exceeds maximum length of 100."),
                   "Expected code in response is not correct");
    }
    
}
