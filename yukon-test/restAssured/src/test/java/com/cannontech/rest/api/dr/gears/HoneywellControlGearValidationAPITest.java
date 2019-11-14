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
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockHoneywellCycleGearFields;
import com.cannontech.rest.api.gear.fields.MockWhenToChange;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class HoneywellControlGearValidationAPITest {

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
     * Test case to validate, Load Program cannot be created with Control Percent less than min value
     */
    @Test
    public void gearValidation_03_ControlPercentLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.setControlPercent(-2);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.controlPercent",
                "Must be between 0 and 100."), "Expected Error not found: Must be between 0 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Control Percent greater than max value
     */
    @Test
    public void gearValidation_04_ControlPercentGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.setControlPercent(101);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.controlPercent",
                "Must be between 0 and 100."), "Expected Error not found: Must be between 0 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Invalid Cycle Period
     */
    @Test
    public void gearValidation_05_InvalidCyclePeriod() {

        mockLoadProgram = buildMockLoadProgram();
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.setCyclePeriodInMinutes(2);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.cyclePeriodInMinutes",
                "Invalid Cycle Period value."), "Expected Error not found: Invalid Cycle Period value.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Capacity Reduction less than min value
     */
    @Test
    public void gearValidation_06_CapacityReductionLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.setCapacityReduction(-2);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.setCapacityReduction(101);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.setWhenToChangeFields(null);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Priority);
        mockHoneywellCycleGearFields.getWhenToChangeFields().setChangePriority(-1);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Priority);
        mockHoneywellCycleGearFields.getWhenToChangeFields().setChangePriority(100000);

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
    public void gearValidation_11_WhenToChangeDurationInMinutesLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Duration);
        mockHoneywellCycleGearFields.getWhenToChangeFields().setChangeDurationInMinutes(-1);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Duration);
        mockHoneywellCycleGearFields.getWhenToChangeFields().setChangeDurationInMinutes(100000);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        mockHoneywellCycleGearFields.getWhenToChangeFields().setTriggerNumber(0);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        mockHoneywellCycleGearFields.getWhenToChangeFields().setTriggerNumber(100000);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        mockHoneywellCycleGearFields.getWhenToChangeFields().setTriggerOffset(-100005.000000);

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
        MockHoneywellCycleGearFields mockHoneywellCycleGearFields = (MockHoneywellCycleGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        mockHoneywellCycleGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        mockHoneywellCycleGearFields.getWhenToChangeFields().setTriggerOffset(100005.000000);

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

        MockLoadGroupBase loadGroupHoneywell = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_HONEYWELL);
        loadGroupHoneywell.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupHoneywell);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.HoneywellCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_HONEYWELL_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());

        return loadProgram;
    }

}