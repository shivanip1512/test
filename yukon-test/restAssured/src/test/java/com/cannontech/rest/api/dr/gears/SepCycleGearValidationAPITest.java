package com.cannontech.rest.api.dr.gears;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockSepCycleGearFields;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class SepCycleGearValidationAPITest {
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
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", "Gear Name is required."),
                   "Expected Error not found:" + "Gear Name is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Gear Name length more than 30 characters
     */
    @Test
    public void gearValidation_02_GearNameGreaterThanThirtyChar() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getGears().get(0).setGearName("GearNameLenghthMoreThan30Charac");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", "Exceeds maximum length of 30."),
                   "Expected Error not found:" + "Exceeds maximum length of 30.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Control Percent less than min value
     */
    @Test
    public void gearValidation_03_ControlPercentLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setControlPercent(4);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.controlPercent", "Must be between 5 and 100."),
                   "Expected Error not found:" + "Must be between 5 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Control Percent greater than max value
     */
    @Test
    public void gearValidation_04_ControlPercentGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setControlPercent(101);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.controlPercent", "Must be between 5 and 100."),
                   "Expected Error not found:" + "Must be between 5 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Criticality less than min value
     */
    @Test
    public void gearValidation_05_CriticalityLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setCriticality(0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.criticality", "Must be between 1 and 15."),
                   "Expected Error not found:" + "Must be between 1 and 15.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Criticality greater than max value
     */
    @Test
    public void gearValidation_06_CriticalityGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setCriticality(16);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.criticality", "Must be between 1 and 15."),
                   "Expected Error not found:" + "Must be between 1 and 15.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Group Capacity Reduction less than min value
     */
    @Test
    public void gearValidation_07_CapacityReductionLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setCapacityReduction(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.capacityReduction", "Must be between 0 and 100."),
                   "Expected Error not found: Must be between 0 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Group Capacity Reduction Greater Than Max value
     */
    @Test
    public void gearValidation_08_CapacityReductionGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setCapacityReduction(101);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.capacityReduction", "Must be between 0 and 100."),
                   "Expected Error not found: Must be between 0 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change field as blank
     */
    @Test
    public void gearValidation_09_WhenToChangeFieldAsBlank() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setWhenToChangeFields(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields", "When To Change Fields is required."),
                   "Expected Error not found: When To Change Fields is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with How to Stop Control field as Blank
     */
    @Test
    public void gearValidation_10_HowToStopControlAsBlank() {
        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setHowToStopControl(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.howToStopControl", "How To Stop Control is required."),
                   "Expected Error not found: How To Stop Control is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Ramp In field as Blank
     */
    @Test
    public void gearValidation_11_RampInAsBlank() {
        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setRampIn(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampIn", "Ramp In is required."),
                   "Expected Error not found: Ramp In is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Ramp Out field as Blank
     */
    @Test
    public void gearValidation_12_RampOutAsBlank() {
        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setRampOut(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampOut", "Ramp Out is required."),
                   "Expected Error not found: Ramp Out is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with True Cycle field as Blank
     */
    @Test
    public void gearValidation_13_TrueCycleAsBlank() {
        mockLoadProgram = buildMockLoadProgram();
        MockSepCycleGearFields mockSepCycleGearFields = (MockSepCycleGearFields) mockLoadProgram.getGears().get(0).getFields();
        mockSepCycleGearFields.setTrueCycle(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.trueCycle", "True Cycle or Adaptive Algorithm is required."),
                   "Expected Error not found: True Cycle or Adaptive Algorithm is required.");
    }    
    
    /**
     * This is to build Mock LoadProgram payload to be used for negative scenarios test cases
     */
    public MockLoadProgram buildMockLoadProgram() {

        MockLoadGroupBase loadGroupDigiSep = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        loadGroupDigiSep.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupDigiSep);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SepCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM,
                                                                                     loadGroups,
                                                                                     gearTypes,
                                                                                     programConstraint.getId());
        return loadProgram;
    }
}
