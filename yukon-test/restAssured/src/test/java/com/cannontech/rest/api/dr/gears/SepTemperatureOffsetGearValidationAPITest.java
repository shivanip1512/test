package com.cannontech.rest.api.dr.gears;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockMode;
import com.cannontech.rest.api.gear.fields.MockSepTemperatureOffsetGearFields;
import com.cannontech.rest.api.gear.fields.MockTemperatureMeasureUnit;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class SepTemperatureOffsetGearValidationAPITest {
    private MockLoadProgram mockLoadProgram = null;

    /**
     * Test case to validate, Load Program cannot be created with Blank Gear Name
     */
    @Test
    public void gearValidation_01_BlankGearName() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getGears().get(0).setGearName("");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
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
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", "Exceeds maximum length of 30."),
                   "Expected Error not found:" + "Exceeds maximum length of 30.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change field as blank
     */
    @Test
    public void gearValidation_03_WhenToChangeFieldAsBlank() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setWhenToChangeFields(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields", "When To Change Fields is required."),
                   "Expected Error not found:" + "When To Change Fields is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Capacity Reduction less than min value
     */
    @Test
    public void gearValidation_04_CapacityReductionLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setCapacityReduction(-2);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.capacityReduction", "Must be between 0 and 100."),
                   "Expected Error not found: Must be between 0 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Capacity Reduction greater than max value
     */
    @Test
    public void gearValidation_05_CapacityReductionGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setCapacityReduction(101);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.capacityReduction", "Must be between 0 and 100."),
                   "Expected Error not found: Must be between 0 and 100.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Offset value less than min value for Fahrenheit
     */

    @Test
    public void gearValidation_06_HeatingOffsetLessThanMinValueForFahrenheit() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setMode(MockMode.HEAT);
        mockSepTemperatureOffsetGearFields.setCelsiusOrFahrenheit(MockTemperatureMeasureUnit.FAHRENHEIT);
        mockSepTemperatureOffsetGearFields.setOffset(0.0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.offset", "Must be between 0.1 and 77.7"),
                   "Expected Error not found: Must be between 0.1 and 77.7");
    }

    /**
     * Test case to validate, Load Program cannot be created with Offset value greater than max value for Fahrenheit
     */
    @Test
    public void gearValidation_07_HeatingOffsetGreaterThanMaxValueForFahrenheit() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setMode(MockMode.HEAT);
        mockSepTemperatureOffsetGearFields.setCelsiusOrFahrenheit(MockTemperatureMeasureUnit.FAHRENHEIT);
        mockSepTemperatureOffsetGearFields.setOffset(77.8);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.offset", "Must be between 0.1 and 77.7"),
                   "Expected Error not found: Must be between 0.1 and 77.7");
    }

    /**
     * Test case to validate, Load Program cannot be created with Offset value less than min value for Celsius
     */

    @Test
    public void gearValidation_08_HeatingOffsetLessThanMinValueForCelsius() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setMode(MockMode.HEAT);
        mockSepTemperatureOffsetGearFields.setCelsiusOrFahrenheit(MockTemperatureMeasureUnit.CELSIUS);
        mockSepTemperatureOffsetGearFields.setOffset(0.0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.offset", "Must be between 0.1 and 25.4"),
                   "Expected Error not found: Must be between 0.1 and 25.4");
    }

    /**
     * Test case to validate, Load Program cannot be created with Offset value greater than max value for Celsius
     */
    @Test
    public void gearValidation_09_HeatingOffsetGreaterThanMaxValueForCelsius() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();

        mockSepTemperatureOffsetGearFields.setMode(MockMode.HEAT);
        mockSepTemperatureOffsetGearFields.setCelsiusOrFahrenheit(MockTemperatureMeasureUnit.CELSIUS);
        mockSepTemperatureOffsetGearFields.setOffset(25.5);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.offset", "Must be between 0.1 and 25.4"),
                   "Expected Error not found: Must be between 0.1 and 25.4");
    }

    /**
     * Test case to validate, Load Program cannot be created with Criticality value less than min value
     */

    @Test
    public void gearValidation_10_CriticalityLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setCriticality(0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.criticality", "Must be between 1 and 15."),
                   "Expected Error not found: Must be between 1 and 15.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Criticality value greater than max value
     */
    @Test
    public void gearValidation_11_CriticalityGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setCriticality(16);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.criticality", "Must be between 1 and 15."),
                   "Expected Error not found: Must be between 1 and 15.");
    }

    /**
     * Test case to validate, Load Program cannot be created with How to Stop Control field as Blank
     */
    @Test
    public void gearValidation_12_HowToStopControlAsBlank() {

        mockLoadProgram = buildMockLoadProgram();
        MockSepTemperatureOffsetGearFields mockSepTemperatureOffsetGearFields = (MockSepTemperatureOffsetGearFields) mockLoadProgram.getGears()
                                                                                                                                    .get(0)
                                                                                                                                    .getFields();
        mockSepTemperatureOffsetGearFields.setHowToStopControl(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.howToStopControl", "How To Stop Control is required."),
                   "Expected Error not found: How To Stop Control is required.");
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
        gearTypes.add(MockGearControlMethod.SepTemperatureOffset);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM,
                                                                                     loadGroups,
                                                                                     gearTypes,
                                                                                     programConstraint.getId());
        return loadProgram;
    }
}
