package com.cannontech.rest.api.dr.gears;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockCycleCountSendType;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockHowToStopControl;
import com.cannontech.rest.api.gear.fields.MockTimeRefreshGearFields;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class TimeRefreshGearValidationApiTest {

    private MockLoadProgram mockLoadProgram = null;
    private MockTimeRefreshGearFields mockTimeRefreshGearFields = null;

    @BeforeMethod
    public void setup() {
        MockLoadGroupBase loadGroupEcobee = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
        loadGroupEcobee.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupEcobee);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.TimeRefresh);
        mockLoadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());
        mockTimeRefreshGearFields = (MockTimeRefreshGearFields) mockLoadProgram.getGears()
                .get(0).getFields();
    }

    /**
     * Test case to validate, Load Program cannot be created with blank Gear name
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_01_BlankGearName() {

        String errorMsg = "Gear Name is required.";
        mockLoadProgram.getGears().get(0).setGearName("");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Refresh Shed Type value
     * and validates proper error message in response
     * Valid values for 'Invalid Refresh Shed Type' field are Dynamic Shed Time & Fixed Shed Time
     */
    @Test
    public void timeRefreshGear_02_InvalidRefreshShedType() {

        String errorMsg = "Invalid Refresh Shed Time value.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.CountDown);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.refreshShedTime", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Shed Time
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_03_InvalidShedTime() {

        String errorMsg = "Invalid Shed Time value.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setShedTime(50);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.shedTime", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Command Resend Rate
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_04_InvalidCommandResendRate() {

        String errorMsg = "Invalid Command Resend Rate value.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setSendRate(130);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.sendRate", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Number of groups
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_05_InvalidNumberOfGroups() {

        String errorMsg = "Must be between 0 and 25.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setNumberOfGroups(26);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.numberOfGroups", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Group selection method
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_06_InvalidGroupSelectionMethod() {

        String errorMsg = "Group Selection Method is required.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setGroupSelectionMethod(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.groupSelectionMethod", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Ramp in percentage
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_07_RampInPercentageGreaterThanMaxValue() {

        String errorMsg = "Must be between 0 and 100.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setRampInPercent(101);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampInPercent", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Ramp in percentage
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_08_RampInPercentageLessThanMinValue() {

        String errorMsg = "Must be between 0 and 100.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setRampInPercent(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampInPercent", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Ramp in Interval
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_09_RampIntervalGreaterThanMaxValue() {

        String errorMsg = "Must be between -99,999 and 99,999.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setRampInPercent(10);
        mockTimeRefreshGearFields.setRampInIntervalInSeconds(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampInIntervalInSeconds", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Ramp in Interval
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_10_RampIntervalLessThanMinValue() {

        String errorMsg = "Must be between -99,999 and 99,999.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setRampInPercent(10);
        mockTimeRefreshGearFields.setRampInIntervalInSeconds(-100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampInIntervalInSeconds", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Stop Command Repeat
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_11_StopCommandGreaterThanMaxValue() {

        String errorMsg = "Must be between 0 and 5.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setStopCommandRepeat(6);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.stopCommandRepeat", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Stop Command Repeat
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_12_StopCommandLessThanMinValue() {

        String errorMsg = "Must be between 0 and 5.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setStopCommandRepeat(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.stopCommandRepeat", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with invalid Ramp out percentage
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_13_RampOutPercentLessThanMinValue() {

        String errorMsg = "Must be between 0 and 100.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setHowToStopControl(MockHowToStopControl.RampOutRestore);
        mockTimeRefreshGearFields.setRampOutPercent(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampOutPercent", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Ramp out percentage
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_14_RampOutPercentGreaterThanMaxValue() {

        String errorMsg = "Must be between 0 and 100.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setHowToStopControl(MockHowToStopControl.RampOutRestore);
        mockTimeRefreshGearFields.setRampOutPercent(101);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampOutPercent", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Rampout interval
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_15_RampOutIntervalGreaterThanMaxValue() {

        String errorMsg = "Must be between -99,999 and 99,999.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setHowToStopControl(MockHowToStopControl.RampOutRestore);
        mockTimeRefreshGearFields.setRampOutPercent(10);
        mockTimeRefreshGearFields.setRampInIntervalInSeconds(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampInIntervalInSeconds", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Rampout interval
     * and validates proper error message in response
     */
    @Test
    public void timeRefreshGear_16_RampOutIntervalLessThanMinValue() {

        String errorMsg = "Must be between -99,999 and 99,999.";
        mockTimeRefreshGearFields.setRefreshShedTime(MockCycleCountSendType.FixedShedTime);
        mockTimeRefreshGearFields.setHowToStopControl(MockHowToStopControl.RampOutRestore);
        mockTimeRefreshGearFields.setRampOutPercent(10);
        mockTimeRefreshGearFields.setRampInIntervalInSeconds(-100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampInIntervalInSeconds", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

}
