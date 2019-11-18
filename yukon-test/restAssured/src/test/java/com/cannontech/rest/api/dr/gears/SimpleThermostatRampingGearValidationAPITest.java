package com.cannontech.rest.api.dr.gears;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockSimpleThermostatRampingGearFields;
import com.cannontech.rest.api.gear.fields.MockWhenToChange;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class SimpleThermostatRampingGearValidationAPITest {
    private MockLoadProgram mockLoadProgram = null;

    @BeforeMethod
    public void setup() {
        mockLoadProgram = buildMockLoadProgram();

    }

    /**
     * Test case to validate, Load Program cannot be created with Blank Gear Name
     */
    @Test
    public void gearValidation_01_BlankGearName() {

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

        mockLoadProgram.getGears().get(0).setGearName("GearNameLenghthMoreThan30Characters");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", "Exceeds maximum length of 30."),
                "Expected Error not found: Exceeds maximum length of 30.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Random Start Time less than min value
     */
    @Test
    public void gearValidation_03_RandomStartTimeLessThanMinValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears() .get(0).getFields();
         
        simpleThermostatRampingGearFields.setRandomStartTimeInMinutes(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.randomStartTimeInMinutes",
                "Must be between 0 and 120."), "Expected Error not found: Must be between 0 and 120.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Random Start Time greater than max value
     */
    @Test
    public void gearValidation_04_RandomStartTimeGreaterThanMaxValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();
    
        simpleThermostatRampingGearFields.setRandomStartTimeInMinutes(121);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.randomStartTimeInMinutes",
                "Must be between 0 and 120."), "Expected Error not found: Must be between 0 and 120.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Temperature less than min value
     */
    @Test
    public void gearValidation_05_TempLessThanMinValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();
    
        simpleThermostatRampingGearFields.setPreOpTemp(-25);
     
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.preOpTemp", "Must be between -20 and 20."),
                "Expected Error not found: Must be between -20 and 20.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Temperature greater than max value
     */
    @Test
    public void gearValidation_06_TempGreaterThanMaxValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setPreOpTemp(21);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.preOpTemp", "Must be between -20 and 20."),
                "Expected Error not found: Must be between -20 and 20.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Time greater than max value
     */

    @Test
    public void gearValidation_07_TimeGreaterThanMaxValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setPreOpTimeInMinutes(301);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.preOpTimeInMinutes",
                "Must be between 0 and 300."), "Expected Error not found: Must be between 0 and 300.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Time less than min value
     */
    @Test
    public void gearValidation_08_TimeLessThanMinValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();
         
        simpleThermostatRampingGearFields.setPreOpTimeInMinutes(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.preOpTimeInMinutes",
                "Must be between 0 and 300."), "Expected Error not found: Must be between 0 and 300.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Hold greater than max value
     */

    @Test
    public void gearValidation_09_HoldGreaterThanMaxValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setPreOpHoldInMinutes(301);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.preOpHoldInMinutes",
                "Must be between 0 and 300."), "Expected Error not found: Must be between 0 and 300.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Hold less than min value
     */
    @Test
    public void gearValidation_10_HoldLessThanMinValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setPreOpHoldInMinutes(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.preOpHoldInMinutes",
                "Must be between 0 and 300."), "Expected Error not found: Must be between 0 and 300.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Max Runtime Greater Than Max Value
     */
    @Test
    public void gearValidation_11_MaxRuntimeGreaterThanMaxValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setMaxRuntimeInMinutes(1440);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                "gears[0].fields.maxRuntimeInMinutes", "Must be between 240 and 1,439."),
                "Must be between 240 and 1,439.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Max Runtime In Minutes less than min value
     */
    @Test
    public void gearValidation_12_MaxRuntimeLessThanMinValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setMaxRuntimeInMinutes(239);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                "gears[0].fields.maxRuntimeInMinutes", "Must be between 240 and 1,439."),
                "Expected Error not found: Must be between 240 and 1,439.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Ramp Out Time Greater Than Max Value
     */

    @Test
    public void gearValidation_13_RampOutTimeGreaterThanMaxValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setRampOutTimeInMinutes(301);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampOutTimeInMinutes",
                "Must be between 0 and 300."), "Expected Error not found: Must be between 0 and 300.");

    }

    /**
     * Test case to validate, Load Program cannot be created with Ramp Out Time less than min value
     */
    @Test
    public void gearValidation_14_RampOutTimeLessThanMinValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setRampOutTimeInMinutes(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampOutTimeInMinutes",
                "Must be between 0 and 300."), "Expected Error not found: Must be between 0 and 300.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Ramp per Hour greater than max value
     */
    @Test
    public void gearValidation_15_RampHourGreaterThanMaxValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setRampPerHour((float) 10);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampPerHour",
                "Must be between -9.9 and 9.9."), "Expected Error not found: Must be between -9.9 and 9.9.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Ramp per Hour less than min value
     */
    @Test
    public void gearValidation_16_RampHourLessThanMinValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setRampPerHour((float) -10);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.rampPerHour",
                "Must be between -9.9 and 9.9."), "Expected Error not found: Must be between -9.9 and 9.9.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Max greater than max value
     */
    @Test

    public void gearValidation_17_MaxGreaterThanMaxValue() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setMax(21);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.max", "Must be between 0 and 20."),
                "Expected Error not found: Must be between 0 and 20.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Max less than min value
     */
    @Test
    public void gearValidation_18_MaxLessThanMinValue() {
        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setMax(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.max", "Must be between 0 and 20."),
                "Expected Error not found: Must be between 0 and 20.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Field is Blank
     */
    @Test
    public void gearValidation_19_WhenToChangeFieldAsBlank() {

        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.setWhenToChangeFields(null);

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
    public void gearValidation_20_WhenToChangePriorityLessThanMinValue() {
        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Priority);
        simpleThermostatRampingGearFields.getWhenToChangeFields().setChangePriority(-1);

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
    public void gearValidation_21_WhenToChangePriorityGreaterThanMaxValue() {
        MockSimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (MockSimpleThermostatRampingGearFields) mockLoadProgram
                .getGears().get(0).getFields();

        simpleThermostatRampingGearFields.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Priority);
        simpleThermostatRampingGearFields.getWhenToChangeFields().setChangePriority(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.changePriority",
                "Must be between 0 and 9,999."), "Expected Error not found: Must be between 0 and 9,999.");
    }

    /**
     * This is to build Mock LoadProgram payload to be used for negative scenarios test cases
     */
    public MockLoadProgram buildMockLoadProgram() {

        MockLoadGroupBase loadGroupEcobee = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
        loadGroupEcobee.setId(333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupEcobee);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SimpleThermostatRamping);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());

        return loadProgram;
    }

}
