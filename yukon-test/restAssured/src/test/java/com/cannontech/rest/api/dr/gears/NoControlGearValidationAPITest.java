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
import com.cannontech.rest.api.gear.fields.MockNoControlGearFields;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class NoControlGearValidationAPITest {
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
        mockLoadProgram.getGears().get(0).setGearName("GearNameLenghthMoreThan30Characters");

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
        MockNoControlGearFields mockNoControlGearFields = (MockNoControlGearFields) mockLoadProgram.getGears().get(0).getFields();

        mockNoControlGearFields.setWhenToChangeFields(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields", "When To Change Fields is required."),
                   "Expected Error not found:" + "When To Change Fields is required.");
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
        gearTypes.add(MockGearControlMethod.NoControl);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM,
                                                                                     loadGroups,
                                                                                     gearTypes,
                                                                                     programConstraint.getId());
        return loadProgram;
    }
}
