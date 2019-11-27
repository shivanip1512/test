package com.cannontech.rest.api.dr.gears;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockEcobeeSetpointGearFields;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockWhenToChange;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class EcobeeSetpointGearValidationAPITest {

    private MockLoadProgram mockLoadProgram = null;

    /**
     * Test case to validate, Load Program cannot be created with Blank Gear Name
     */
    @Test
    public void gearValidation_01_BlankGearName() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getGears().get(0).setGearName("");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", "Gear Name is required."),
                "Expected Error not found: Gear Name is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Gear Name length more than 30 characters
     */
    @Test
    public void gearValidation_02_GearNameGreaterThanThirtyChar() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getGears().get(0).setGearName("GearNameLenghthMoreThan30Characters");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", "Exceeds maximum length of 30."),
                "Expected Error not found: Exceeds maximum length of 30.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Setpoint Offset less than min value
     */
    @Test
    public void gearValidation_03_SetpointOffsetLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.setSetpointOffset(-11);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.setpointOffset",
                "Must be between -10 and 10."), "Expected Error not found: Must be between -10 and 10.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Setpoint Offset greater than max value
     */
    @Test
    public void gearValidation_04_ControlPercentGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.setSetpointOffset(11);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.setpointOffset",
                "Must be between -10 and 10."), "Expected Error not found: Must be between -10 and 10.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Cycle Period
     */
    @Test
    public void gearValidation_05_BlankMandatory() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.setMandatory(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.mandatory",
                "Mandatory is required."), "Expected Error not found: Mandatory is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Capacity Reduction less than min value
     */
    @Test
    public void gearValidation_06_CapacityReductionLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.setCapacityReduction(-2);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.capacityReduction",
                "Must be between 0 and 100."), "Expected Error not found: Must be between 0 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Capacity Reduction greater than max value
     */
    @Test
    public void gearValidation_07_CapacityReductionGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.setCapacityReduction(101);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.capacityReduction",
                "Must be between 0 and 100."), "Expected Error not found: Must be between 0 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change field as blank
     */
    @Test
    public void gearValidation_08_WhenToChangeFieldAsBlank() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.setWhenToChangeFields(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields",
                        "When To Change Fields is required."),
                "Expected Error not found: When To Change Fields is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Priority less than min value
     */
    @Test
    public void gearValidation_09_WhenToChangePriorityLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Priority);
        mockEcobeeSetpointGearFields.getWhenToChangeFields().setChangePriority(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.changePriority",
                "Must be between 0 and 9,999."), "Expected Error not found: Must be between 0 and 9,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Priority greater than max value
     */
    @Test
    public void gearValidation_10_WhenToChangePriorityGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Priority);
        mockEcobeeSetpointGearFields.getWhenToChangeFields().setChangePriority(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.changePriority",
                "Must be between 0 and 9,999."), "Expected Error not found: Must be between 0 and 9,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Duration In Minutes less than min value
     */
    @Test
    public void gearValidation_11_DurationInMinutesLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Duration);
        mockEcobeeSetpointGearFields.getWhenToChangeFields().setChangeDurationInMinutes(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                "gears[0].fields.whenToChangeFields.changeDurationInMinutes", "Must be between 0 and 99,999."),
                "Expected Error not found: Must be between 0 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Duration In Minutes greater than max value
     */
    @Test
    public void gearValidation_12_ChangeDurationInMinutesGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Duration);
        mockEcobeeSetpointGearFields.getWhenToChangeFields().setChangeDurationInMinutes(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                "gears[0].fields.whenToChangeFields.changeDurationInMinutes", "Must be between 0 and 99,999."),
                "Expected Error not found: Must be between 0 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Number and Offset as blank
     */
    @Test
    public void gearValidation_13_TriggerNumberAndOffsetAsBlank() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerNumber",
                "Trigger Number is required."), "Expected Error not found: Trigger Number is required.");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerOffset",
                "Trigger Offset is required."), "Expected Error not found: Trigger Offset is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Number less than min value
     */
    @Test
    public void gearValidation_14_TriggerNumberLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        mockEcobeeSetpointGearFields.getWhenToChangeFields().setTriggerNumber(0);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerNumber",
                "Must be between 1 and 99,999."), "Expected Error not found: Must be between 1 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Number greater than max value
     */
    @Test
    public void gearValidation_15_TriggerNumberGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        mockEcobeeSetpointGearFields.getWhenToChangeFields().setTriggerNumber(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerNumber",
                "Must be between 1 and 99,999."), "Expected Error not found: Must be between 1 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Offset less than min value
     */
    @Test
    public void gearValidation_16_TriggerOffsetLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        mockEcobeeSetpointGearFields.getWhenToChangeFields().setTriggerOffset(-100005.000000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerOffset",
                        "Must be between -100,000 and 100,000."),
                "Expected Error not found: Must be between -100,000 and 100,000.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Offset greater than max value
     */
    @Test
    public void gearValidation_17_TriggerOffsetGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockEcobeeSetpointGearFields mockEcobeeSetpointGearFields = (MockEcobeeSetpointGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockEcobeeSetpointGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        mockEcobeeSetpointGearFields.getWhenToChangeFields().setTriggerOffset(100005.000000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerOffset",
                        "Must be between -100,000 and 100,000."),
                "Expected Error not found: Must be between -100,000 and 100,000.");
    }

    /**
     * This is to build Mock LoadProgram payload to be used for negative scenarios test cases
     */
    public MockLoadProgram buildMockLoadProgram() {

        MockLoadGroupBase loadGroupEcobee = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
        loadGroupEcobee.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupEcobee);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());

        return loadProgram;
    }

}