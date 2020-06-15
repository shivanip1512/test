package com.cannontech.rest.api.point;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockAnalogControlType;
import com.cannontech.rest.api.common.model.MockPointArchiveType;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockAnalogPoint;
import com.cannontech.rest.api.point.request.MockFdrDirection;
import com.cannontech.rest.api.point.request.MockFdrInterfaceType;
import com.cannontech.rest.api.point.request.MockPointBase;
import com.cannontech.rest.api.point.request.MockPointUnit;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class AnalogPointApiTest {
    MockPointBase analogPoint = null;

    @BeforeClass
    public void setUp() {
        analogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);
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
        assertTrue("Name Should be : " + analogPoint.getPointName(),
                analogPoint.getPointName().equals(analogPointDetail.getPointName()));
        assertTrue("Point Type Should be : " + analogPoint.getPointType(),
                analogPoint.getPointType().equals(analogPointDetail.getPointType()));
        assertTrue("Point Offset Should be : " + analogPoint.getPointOffset(),
                analogPoint.getPointOffset().equals(analogPointDetail.getPointOffset()));

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
     * Test case to validate Analog Point cannot be created as name with null and gets valid error message
     * in response
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
     * Test case to validate Analog Point cannot be created with name having more than 60 characters and
     * validates valid error message in response
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
     * Test case to validate Analog Point cannot be created with Invalid Pao Id and gets valid error message
     * in response
     */
    @Test
    public void analogPoint_06_InvalidPaoId() {
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
     * Test case to validate Analog Point cannot be created with Invalid Uom Id and gets valid error message
     * in response
     */
    @Test
    public void analogPoint_07_InvalidUomId() {
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
     * Test case to validate Analog Point cannot be cannot be created with name having special characters and validates
     * valid error message in response
     */
    @Test
    public void analogPoint_08_NameWithSpecialChars() {
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
     * Test case to validate Analog Point cannot be cannot be created with name having special characters and validates
     * valid error message in response
     */
    @Test
    public void analogPoint_09_NullPointOffset() {
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

    /**
     * Test case to validate Analog Point cannot be created with invalid Update Style and validates
     * valid error message in response
     */
    @Test
    public void analogPoint_10_InvalidUpdateStyle() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getStaleData().setUpdateStyle(3);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "staleData.updateStyle", "Update Style must be 0 or 1."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with invalid Archive Interval
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_11_InvalidArchiveInterval() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.setArchiveType(MockPointArchiveType.ON_CHANGE);
        mockAnalogPoint.setArchiveInterval(60);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "archiveInterval",
                        "Archive Interval must be 0 when Archive Data type is None, On Change, or On Update."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with invalid Decimal Places
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_12_InvalidDecimalPlaces() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getPointUnit().setDecimalPlaces(12);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointUnit.decimalPlaces", "Must be between 0 and 10."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with invalid State Id
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_13_InvalidStateId() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.setStateGroupId(500);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "stateGroupId", "State Group Id 500 does not exist."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with invalid Control Offset
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_14_InvalidControlOffset() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

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
    public void analogPoint_15_InvalidControlInhibited() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

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
    
    /**
     * Test case to validate Analog Point cannot be created with invalid Condition Value
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_16_InvalidConditionValue() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getAlarming().getAlarmTableList().get(0).setCondition("Test");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "alarming.alarmTableList[0].condition",
                        "Invalid Condition value."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with invalid NotificationId
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_17_InvalidNotificationId() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getAlarming().setNotificationGroupId(987898);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "alarming.notificationGroupId",
                "Notification GroupId does not exist."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Analog Point cannot be created with Condition value as a Blank
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_18_ConditionBlank() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getAlarming().getAlarmTableList().get(0).setCondition(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "alarming.alarmTableList[0].condition",
                        "Condition is required."),
                "Expected code in response is not correct");
    }

    
    /**
     * Test case to validate Analog Point cannot be created with Category Out Of Range
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_19_CategoryOutOfRange() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getAlarming().getAlarmTableList().get(0).setCategory("Category 1222");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "alarming.alarmTableList[0].category",
                        "Invalid Category value."),
                "Expected code in response is not correct");
    }
  
    /**
     * Test case to validate Analog Point cannot be created with missing Translation field
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_20_MissingTranslationField() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getFdrList().get(0).setTranslation(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "fdrList[0].translation",
                        "Missing Translation property Category, Remote, Point for ACS Interface."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate Analog Point cannot be created with invalid Translation Property
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_21_InvalidTranslationProperty() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getFdrList().get(0).setFdrInterfaceType(MockFdrInterfaceType.ACSMULTI);
        
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "fdrList[0].translation",
                        "Missing Translation property Destination/Source for ACSMULTI Interface."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate Analog Point cannot be created with missing DirectionField
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_22_MissingDirectionField() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getFdrList().get(0).setDirection(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "fdrList[0].direction",
                        "SEND, SEND_FOR_CONTROL, RECEIVE, RECEIVE_FOR_CONTROL Directions are supported by the ACS Interface."),
                "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate Analog Point cannot be created when Interface Type is missing
     * and validates valid error message in response
     */
    @Test
    public void analogPoint_23_MissingInterfaceType() {
        MockAnalogPoint mockAnalogPoint = (MockAnalogPoint) PointHelper.buildPoint(MockPointType.Analog);

        mockAnalogPoint.getFdrList().get(0).setFdrInterfaceType(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockAnalogPoint);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "fdrList[0].fdrInterfaceType",
                        "Interface is required."),
                "Expected code in response is not correct");
    }
}