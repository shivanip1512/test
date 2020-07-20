package com.cannontech.rest.api.point;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPointArchiveType;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockFdrInterfaceType;
import com.cannontech.rest.api.point.request.MockPointBase;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;

public abstract class PointApiTestBase {

    MockPointBase mockPointBase;
    MockPointType pointType;

    abstract public void setUp();

    @Test
    public void point_01_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        String pointId = createResponse.path(PointHelper.CONTEXT_POINT_ID).toString();
        context.setAttribute(PointHelper.CONTEXT_POINT_ID, pointId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Point Id should not be Null", pointId != null);
    }

    @Test(dependsOnMethods = { "point_01_Create" })
    public void point_02_Update(ITestContext context) {
        Log.startTestCase("point_02_Update");
        String pointName = "Test Point Update";
        context.setAttribute("PointName_Update", pointName);

        String pointId = context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString();
        mockPointBase.setPointName(pointName);

        ExtractableResponse<?> getResponse = ApiCallHelper.patch("updatePoint", mockPointBase, pointId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        Log.endTestCase("point_02_Update");
    }

    @Test(dependsOnMethods = { "point_02_Update" })
    public void point_03_Delete(ITestContext context) {
        Log.startTestCase("point_03_Delete");
        ExtractableResponse<?> response = ApiCallHelper.delete("deletePoint", context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        Log.startTestCase("point_03_Delete");
    }

    /**
     * Test case to validate Point cannot be cannot be created with name having special characters and validates
     * valid error message in response
     */
    @Test
    public void point_04_NameWithSpecialChars() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.setPointName("Test,//Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointName", "Name must not contain any of the following characters: / \\ , ' \" |."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be cannot be created with name having special characters and validates
     * valid error message in response
     */
    @Test
    public void point_05_NullPointOffset() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.setPointOffset(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointOffset", "Point Offset is required."), "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created as name with null and gets valid error message in response
     */
    @Test
    public void point_06_NameCannotBeNull() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.setPointName(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointName", "Point Name is required."), "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with name having more than 60 characters and validates valid
     * error message in response
     */
    @Test
    public void point_07_NameGreaterThanMaxLength() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.setPointName("Poinmdmdldslldlkdlkdddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointName", "Exceeds maximum length of 60."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created as paoId with null and gets valid error message in response
     */
    @Test
    public void point_08_NullPaoId() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.setPaoId(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "paoId", "PaoId is required."), "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with Invalid Pao Id and gets valid error message in response
     */
    @Test
    public void point_09_InvalidPaoId() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.setPaoId(99999999);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        
        assertTrue(ValidationHelper.validateFieldError(createResponse, "paoId", String.format("%,d", 99999999) + " does not exist."),
        "Expected code in response is not correct");
        
    }

    /**
     * Test case to validate Point cannot be created with invalid State Id and validates valid error message in
     * response
     */
    @Test
    public void point_10_InvalidStateId() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.setStateGroupId(500);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "stateGroupId", "State Group Id 500 does not exist."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with invalid Update Style and validates valid error message
     * in response
     */
    @Test
    public void point_11_InvalidUpdateStyle() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getStaleData().setUpdateStyle(3);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "staleData.updateStyle", "Update Style must be 0 or 1."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with invalid Archive Interval and validates valid error
     * message in response
     */
    @Test
    public void point_12_InvalidArchiveInterval() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.setArchiveType(MockPointArchiveType.ON_CHANGE);
        mockPointBase.setArchiveInterval(60);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                                                       "archiveInterval",
                                                       "Archive Interval must be 0 when Archive Data type is None, On Change, or On Update."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with invalid Condition Value and validates valid error
     * message in response
     */
    @Test
    public void point_13_InvalidConditionValue() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getAlarming().getAlarmTableList().get(0).setCondition("Test");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "alarming.alarmTableList[0].condition", "Invalid Condition value."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with invalid NotificationId and validates valid error
     * message in response
     */
    @Test
    public void point_14_InvalidNotificationId() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getAlarming().setNotificationGroupId(987898);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "alarming.notificationGroupId", "Notification GroupId does not exist."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with Condition value as a Blank and validates valid error
     * message in response
     */
    @Test
    public void point_15_ConditionBlank() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getAlarming().getAlarmTableList().get(0).setCondition(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "alarming.alarmTableList[0].condition", "Condition is required."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with Category Out Of Range and validates valid error message
     * in response
     */
    @Test
    public void point_16_CategoryOutOfRange() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getAlarming().getAlarmTableList().get(0).setCategory("Category 1222");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "alarming.alarmTableList[0].category", "Invalid Category value."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with missing Translation field and validates valid error
     * message in response
     */
    @Test
    public void point_17_MissingTranslationField() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getFdrList().get(0).setTranslation(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                                                       "fdrList[0].translation",
                                                       "Missing Translation property Category, Remote, Point for ACS Interface."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created with invalid Translation Property and validates valid error
     * message in response
     */
    @Test
    public void point_18_InvalidTranslationProperty() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getFdrList().get(0).setFdrInterfaceType(MockFdrInterfaceType.ACSMULTI);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                                                       "fdrList[0].translation",
                                                       "Missing Translation property Destination/Source for ACSMULTI Interface."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to Point cannot be created with missing DirectionField and validates valid error
     * message in response
     */
    @Test
    public void point_19_MissingDirectionField() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getFdrList().get(0).setDirection(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                                                       "fdrList[0].direction",
                                                       "SEND, SEND_FOR_CONTROL, RECEIVE, RECEIVE_FOR_CONTROL Directions are supported by the ACS Interface."),
                   "Expected code in response is not correct");
    }

    /**
     * Test case to validate Point cannot be created when Interface Type is missing and validates valid error
     * message in response
     */
    @Test
    public void point_20_MissingInterfaceType() {
        MockPointBase mockPointBase = (MockPointBase) PointHelper.buildPoint(pointType);

        mockPointBase.getFdrList().get(0).setFdrInterfaceType(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockPointBase);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "fdrList[0].fdrInterfaceType", "Interface is required."),
                   "Expected code in response is not correct");
    }
    
    /**
    * Test case for retrieve all points associated with paoId
    */
   @Test
   public void getPoints(ITestContext context) {
       RequestSpecification requestSpecification = ApiCallHelper.getHeader()
                                                                .queryParam("types", MockPointType.CalcAnalog)
                                                                .queryParam("pointNames", "TestCalcAnalog")
                                                                .queryParam("page", 1)
                                                                .queryParam("itemsPerPage", "50")
                                                                .queryParam("dir", "asc")
                                                                .queryParam("sort", "pointName");
       String paoId = ApiCallHelper.getProperty("paoId");
       Log.info("Point Id to retrive all points : " + paoId);

       ExtractableResponse<?> getResponse = ApiCallHelper.get("getPoints", paoId, requestSpecification);

       assertTrue("Status code should be 200", getResponse.statusCode() == 200);
   }
}
