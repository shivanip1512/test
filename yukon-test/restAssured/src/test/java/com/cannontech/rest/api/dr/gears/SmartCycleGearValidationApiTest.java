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
import com.cannontech.rest.api.gear.fields.MockSmartCycleGearFields;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class SmartCycleGearValidationApiTest {

    private MockLoadProgram mockLoadProgram = null;
    private MockSmartCycleGearFields mockTSmartCycleGearFields = null;

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
        gearTypes.add(MockGearControlMethod.SmartCycle);
        mockLoadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());
        mockTSmartCycleGearFields = (MockSmartCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();
    }

    /**
     * Test case to validate smart cycle gear field Cycle Period for boundary value negative scenario
     * and validates proper error message in response
     */
    @Test
    public void smartCycleGear_01_CyclePeriodLessThanMinVal() {

        String errorMsg = "Must be between 1 and 945.";

        mockTSmartCycleGearFields.setCyclePeriodInMinutes(0);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.cyclePeriodInMinutes", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate smart cycle gear field Cycle Period for boundary value negative scenario
     * and validates proper error message in response
     */
    @Test
    public void smartCycleGear_02_CyclePeriodGreaterThanMaxVal() {

        String errorMsg = "Must be between 1 and 945.";

        mockTSmartCycleGearFields.setCyclePeriodInMinutes(946);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.cyclePeriodInMinutes", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate smart cycle gear field CycleCountSendType with invalid value
     * and validates proper error message in response
     */
    @Test
    public void smartCycleGear_03_InvalidCycleCountSendType() {

        String errorMsg = "Invalid Cycle Count Send Type value.";

        mockTSmartCycleGearFields.setCycleCountSendType(MockCycleCountSendType.DynamicShedTime);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.cycleCountSendType", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate smart cycle gear field MaxCycleCount for boundary value negative scenario
     * and validates proper error message in response
     */
    @Test
    public void smartCycleGear_04_MaxCycleCountGreaterThanLimit() {

        String errorMsg = "Must be between 0 and 63.";

        mockTSmartCycleGearFields.setMaxCycleCount(64);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.maxCycleCount", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate smart cycle gear field MaxCycleCount for boundary value negative scenario
     * and validates proper error message in response
     */
    @Test
    public void smartCycleGear_05_MaxCycleCountLessThanLimit() {

        String errorMsg = "Must be between 0 and 63.";

        mockTSmartCycleGearFields.setMaxCycleCount(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.maxCycleCount", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate smart cycle gear field StartingPeriodCount for boundary value negative scenario
     * and validates proper error message in response
     */
    @Test
    public void smartCycleGear_06_StartingPeriodCountGreaterThanLimit() {

        String errorMsg = "Must be between 1 and 63.";

        mockTSmartCycleGearFields.setStartingPeriodCount(64);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.startingPeriodCount", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate smart cycle gear field StartingPeriodCount for boundary value negative scenario
     * and validates proper error message in response
     */
    @Test
    public void smartCycleGear_07_StartingPeriodCountLessThanLimit() {

        String errorMsg = "Must be between 1 and 63.";

        mockTSmartCycleGearFields.setStartingPeriodCount(0);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.startingPeriodCount", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate smart cycle gear field CommandResendRate for boundary value negative scenario
     * and validates proper error message in response
     * Range - 0 to 2700 seconds
     */
    @Test
    public void smartCycleGear_08_CommandResendRateLessThanLimit() {

        String errorMsg = "Invalid Command Resend Rate value.";

        mockTSmartCycleGearFields.setSendRate(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.sendRate", errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate smart cycle gear field CommandResendRate for boundary value negative scenario
     * and validates proper error message in response
     * Range - 0 to 2700 seconds
     */
    @Test
    public void smartCycleGear_09_CommandResendRateGreaterThanLimit() {

        String errorMsg = "Invalid Command Resend Rate value.";

        mockTSmartCycleGearFields.setSendRate(2701);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.sendRate", errorMsg),
                "Expected Error not found: " + errorMsg);
    }
}
