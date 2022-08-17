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
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockHowToStopControl;
import com.cannontech.rest.api.gear.fields.MockRotationGearFields;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class RotationGearValidationApitTest {

    private MockLoadProgram mockLoadProgram = null;
    private MockRotationGearFields mockRotationGearFields = null;

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
        gearTypes.add(MockGearControlMethod.Rotation);
        mockLoadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());
        mockRotationGearFields = (MockRotationGearFields) mockLoadProgram.getGears()
                .get(0).getFields();
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Shed Type value
     * and validates proper error message in response
     */
    @Test
    public void rotationGear_01_InvalidShedType() {

        String errorMsg = "Invalid Shed Time value.";
        mockRotationGearFields.setShedTime(28801);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.shedTime",
                        errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Number of groups
     * and validates proper error message in response
     */
    @Test
    public void rotationGear_02_InvalidNumberOfGroups() {

        String errorMsg = "Must be between 0 and 25.";
        mockRotationGearFields.setNumberOfGroups(26);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.numberOfGroups",
                        errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Command Resend Rate
     * and validates proper error message in response
     */
    @Test
    public void rotationGear_03_InvalidCommandResendRate() {

        String errorMsg = "Invalid Command Resend Rate value.";
        mockRotationGearFields.setSendRate(130);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.sendRate",
                        errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Group selection method
     * and validates proper error message in response
     */
    @Test
    public void rotationGear_04_InvalidGroupSelectionMethod() {

        String errorMsg = "Group Selection Method is required.";
        mockRotationGearFields.setGroupSelectionMethod(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.groupSelectionMethod",
                        errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Capacity Reduction Value
     * and validates proper error message in response
     */
    @Test
    public void rotationGear_05_CapacityReductionLessThanMinValue() {

        String errorMsg = "Must be between 0 and 100.";
        mockRotationGearFields.setCapacityReduction(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.capacityReduction",
                        errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Capacity Reduction Value
     * and validates proper error message in response
     */
    @Test
    public void rotationGear_06_CapacityReductionGreaterThanMinValue() {

        String errorMsg = "Must be between 0 and 100.";
        mockRotationGearFields.setCapacityReduction(101);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.capacityReduction",
                        errorMsg),
                "Expected Error not found: " + errorMsg);
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid HowToStopControl
     * and validates proper error message in response
     */
    @Test
    public void rotationGear_07_InvalidHowToStopControl() {

        String errorMsg = "Invalid How To Stop Control value.";
        mockRotationGearFields.setHowToStopControl(MockHowToStopControl.RampOutRestore);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.howToStopControl",
                        errorMsg),
                "Expected Error not found: " + errorMsg);
    }
}
